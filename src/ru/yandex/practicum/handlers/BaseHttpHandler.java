package ru.yandex.practicum.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.tasks.DurationAdapter;
import ru.yandex.practicum.tasks.LocalDateTimeAdapter;

import java.io.IOException;
import java.io.OutputStream;
import java.time.Duration;
import java.time.LocalDateTime;

public class BaseHttpHandler implements HttpHandler {
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .serializeNulls()
            .setPrettyPrinting()
            .create();

    @Override
    public void handle(HttpExchange exchange) throws IOException {}

    protected void sendOk(HttpExchange exchange, String data) throws IOException {
        sendResponse(exchange, 200, data);
    }

    protected void sendCreated(HttpExchange exchange, String data) throws IOException {
        sendResponse(exchange, 201, data);
    }

    protected void sendBadRequest(HttpExchange exchange) throws IOException {
        sendResponse(exchange, 400, "Bad Request");
    }

    protected void sendNotFound(HttpExchange exchange) throws IOException {
        sendResponse(exchange, 404, "Not Found");
    }

    protected void sendNotAcceptable(HttpExchange exchange) throws IOException {
        sendResponse(exchange, 406, "Not Acceptable");
    }

    protected void sendError(HttpExchange exchange) throws IOException {
        sendResponse(exchange, 500, "Internal Server Error");
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String message) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, message.getBytes().length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(message.getBytes());
        }
    }

    protected String convertToJson(Object data) {
        return gson.toJson(data);
    }

    protected <T> T convertFromJson(String json, Class<T> tClass) throws IOException {
        try {
            return gson.fromJson(json, tClass);
        } catch (JsonSyntaxException e) {
            throw new IOException("Invalid JSON format: " + e.getMessage(), e);
        }
    }
}