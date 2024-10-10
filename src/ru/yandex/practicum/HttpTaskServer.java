package ru.yandex.practicum;

import com.sun.net.httpserver.HttpServer;
import ru.yandex.practicum.handlers.*;
import ru.yandex.practicum.manager.Managers;
import ru.yandex.practicum.manager.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private final HttpServer server;
    private final TaskManager manager;

    public HttpTaskServer(TaskManager manager) throws IOException {
        this.manager = manager;
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        createContexts();
    }

    private void createContexts() {
        server.createContext("/tasks", new TaskHandler(manager));
        server.createContext("/epics", new EpicHandler(manager));
        server.createContext("/subtasks", new SubtaskHandler(manager));
        server.createContext("/history", new HistoryHandler(manager));
        server.createContext("/prioritized", new PrioritizedHandler(manager));
    }

    public void start() {
        server.start();
        System.out.println("HTTP server started on port " + PORT);
    }

    public void stop() {
        server.stop(0);
        System.out.println("HTTP server stopped.");
    }

    public static void main(String[] args) {
        try {
            TaskManager manager = Managers.getDefaultSave();
            HttpTaskServer server = new HttpTaskServer(manager);
            server.start();
        } catch (IOException e) {
            System.err.println("Failed to start HTTP server: " + e.getMessage());
        }
    }
}