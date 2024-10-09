package ru.yandex.practicum.handlers;

import com.sun.net.httpserver.HttpExchange;
import ru.yandex.practicum.manager.TaskManager;

import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler {
    private final TaskManager manager;

    public HistoryHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        if (!(pathParts.length == 2) || !(exchange.getRequestMethod().equals("GET"))) {
            sendNotFound(exchange);
        }
        sendOk(exchange, convertToJson(manager.getHistory()));
    }
}
