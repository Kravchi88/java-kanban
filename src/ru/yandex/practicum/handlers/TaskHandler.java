package ru.yandex.practicum.handlers;

import com.sun.net.httpserver.HttpExchange;
import ru.yandex.practicum.exceptions.TaskConflictException;
import ru.yandex.practicum.exceptions.TaskNotFoundException;
import ru.yandex.practicum.manager.TaskManager;
import ru.yandex.practicum.tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class TaskHandler extends BaseHttpHandler {
    private final TaskManager manager;

    public TaskHandler(TaskManager manager) {
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
            sendOk(exchange, convertToJson(manager.getTasks()));
            return;
        }

        if (pathParts.length == 3) {
            try {
                int id = Integer.parseInt(pathParts[2]);
                sendOk(exchange, convertToJson(manager.getTaskById(id)));
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
            Task task = convertFromJson(json, Task.class);

            if (task.getId() == 0) {
                manager.createTask(task);
                sendCreated(exchange, convertToJson(task));
            } else {
                manager.updateTask(task);
                sendCreated(exchange, convertToJson(task));
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
            manager.removeTasks();
            sendOk(exchange, "All tasks deleted successfully.");
            return;
        }

        if (pathParts.length == 3) {
            try {
                int id = Integer.parseInt(pathParts[2]);
                manager.removeTaskById(id);
                sendOk(exchange, "Task deleted successfully.");
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
