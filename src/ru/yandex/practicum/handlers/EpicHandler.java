package ru.yandex.practicum.handlers;

import com.sun.net.httpserver.HttpExchange;
import ru.yandex.practicum.exceptions.TaskConflictException;
import ru.yandex.practicum.exceptions.TaskNotFoundException;
import ru.yandex.practicum.manager.TaskManager;
import ru.yandex.practicum.tasks.Epic;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class EpicHandler extends BaseHttpHandler {
    private final TaskManager manager;

    public EpicHandler(TaskManager manager) {
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
            sendOk(exchange, convertToJson(manager.getEpics()));
            return;
        }

        if (pathParts.length == 3) {
            try {
                int id = Integer.parseInt(pathParts[2]);
                sendOk(exchange, convertToJson(manager.getEpicById(id)));
            } catch (NumberFormatException e) {
                sendBadRequest(exchange);
            } catch (TaskNotFoundException e) {
                sendNotFound(exchange);
            } catch (Exception e) {
                sendError(exchange);
            }
            return;
        }

        if (pathParts.length == 4 && pathParts[3].equals("subtasks")) {
            try {
                int id = Integer.parseInt(pathParts[2]);
                sendOk(exchange, convertToJson(manager.getSubtasksByEpicId(id)));
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
            Epic epic = convertFromJson(json, Epic.class);

            if (epic.getId() == 0) {
                manager.createEpic(epic);
                sendCreated(exchange, convertToJson(epic));
            } else {
                manager.updateEpic(epic);
                sendCreated(exchange, convertToJson(epic));
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
            manager.removeEpics();
            sendOk(exchange, "All epics deleted successfully.");
            return;
        }

        if (pathParts.length == 3) {
            try {
                int id = Integer.parseInt(pathParts[2]);
                manager.removeEpicById(id);
                sendOk(exchange, "Epic deleted successfully.");
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