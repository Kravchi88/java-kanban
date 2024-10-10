package ru.yandex.practicum.handlers;

import com.sun.net.httpserver.HttpExchange;
import ru.yandex.practicum.exceptions.TaskConflictException;
import ru.yandex.practicum.exceptions.TaskNotFoundException;
import ru.yandex.practicum.manager.TaskManager;
import ru.yandex.practicum.tasks.Subtask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class SubtaskHandler extends BaseHttpHandler {
    private final TaskManager manager;

    public SubtaskHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        String requestPath = exchange.getRequestURI().getPath();
        String[] pathParts = parseRequestPath(requestPath);

        switch (requestMethod) {
            case "GET" -> getRequest(exchange, pathParts);
            case "POST" -> postRequest(exchange, pathParts);
            case "DELETE" -> deleteRequest(exchange, pathParts);
            default -> sendBadRequest(exchange);
        }
    }

    private void getRequest(HttpExchange exchange, String[] pathParts) throws IOException {
        if (pathParts.length == 2) {
            sendOk(exchange, convertToJson(manager.getSubtasks()));
            return;
        }

        if (pathParts.length == 3) {
            try {
                int id = Integer.parseInt(pathParts[2]);
                sendOk(exchange, convertToJson(manager.getSubtaskById(id)));
            } catch (NumberFormatException e) {
                sendBadRequest(exchange);
            } catch (TaskNotFoundException e) {
                sendNotFound(exchange);
            } catch (Exception e) {
                sendError(exchange);
            }
            return;
        }

        sendNotFound(exchange);
    }

    private void postRequest(HttpExchange exchange, String[] pathParts) throws IOException {
        if (pathParts.length != 2) {
            sendNotFound(exchange);
            return;
        }

        try {
            InputStream inputStream = exchange.getRequestBody();
            String json = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            Subtask subtask = convertFromJson(json, Subtask.class);

            if (subtask.getId() == 0) {
                manager.createSubtask(subtask);
                sendCreated(exchange, convertToJson(subtask));
            } else {
                manager.updateSubtask(subtask);
                sendCreated(exchange, convertToJson(subtask));
            }
        } catch (TaskNotFoundException e) {
            sendNotFound(exchange);
        } catch (TaskConflictException e) {
            sendNotAcceptable(exchange);
        } catch (Exception e) {
            sendError(exchange);
        }
    }

    private void deleteRequest(HttpExchange exchange, String[] pathParts) throws IOException {
        if (pathParts.length == 2) {
            manager.removeSubtasks();
            sendOk(exchange, "All subtasks deleted successfully.");
            return;
        }

        if (pathParts.length == 3) {
            try {
                int id = Integer.parseInt(pathParts[2]);
                manager.removeSubtaskById(id);
                sendOk(exchange, "Subtask deleted successfully.");
            } catch (NumberFormatException e) {
                sendBadRequest(exchange);
            } catch (TaskNotFoundException e) {
                sendNotFound(exchange);
            } catch (Exception e) {
                sendError(exchange);
            }
            return;
        }

        sendNotFound(exchange);
    }

    private String[] parseRequestPath(String requestPath) {
        return requestPath.split("/");
    }
}