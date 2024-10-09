
import com.sun.net.httpserver.HttpServer;
import ru.yandex.practicum.handlers.*;
import ru.yandex.practicum.manager.Managers;
import ru.yandex.practicum.manager.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final int PORT = 8080;

    public static void main(String[] args) throws IOException {
        TaskManager manager = Managers.getDefaultSave();
        HttpServer server = createContexts(HttpServer.create(new InetSocketAddress(PORT), 0), manager);
        server.start();
    }

    private static HttpServer createContexts(HttpServer server, TaskManager manager) {
        server.createContext("/tasks", new TaskHandler(manager));
        server.createContext("/epics", new EpicHandler(manager));
        server.createContext("/subtasks", new SubtaskHandler(manager));
        server.createContext("/history", new HistoryHandler(manager));
        server.createContext("/prioritized", new PrioritizedHandler(manager));
        return server;
    }
}