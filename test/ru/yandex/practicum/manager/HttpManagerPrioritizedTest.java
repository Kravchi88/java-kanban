package ru.yandex.practicum.manager;

import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import com.google.gson.Gson;
import ru.yandex.practicum.HttpTaskServer;
import ru.yandex.practicum.tasks.DurationAdapter;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.LocalDateTimeAdapter;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;
import ru.yandex.practicum.tasks.TaskStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpManagerPrioritizedTest {
    private final InMemoryTaskManager manager;
    private final HttpTaskServer taskServer;
    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .serializeNulls()
            .setPrettyPrinting()
            .create();

    public HttpManagerPrioritizedTest() throws IOException {
        manager = new InMemoryTaskManager();
        taskServer = new HttpTaskServer(manager);
    }

    @BeforeEach
    public void setUp() {
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void testGetPrioritizedTasks() throws IOException, InterruptedException {
        Task task1 = createTask("Task 1", LocalDateTime.now().plusDays(2));
        Task task2 = createTask("Task 2", LocalDateTime.now().plusDays(1));
        manager.createTask(task1);
        manager.createTask(task2);

        Epic epic = createEpic();
        manager.createEpic(epic);

        Subtask subtask1 = createSubtask(epic.getId(), LocalDateTime.now().plusHours(6));
        Subtask subtask2 = createSubtask(epic.getId(), LocalDateTime.now().plusDays(3));
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        List<?> prioritizedTasks = gson.fromJson(response.body(), List.class);
        assertNotNull(prioritizedTasks);
        assertEquals(5, prioritizedTasks.size());

        assertEquals(epic.getId(), ((Number) ((java.util.Map<?, ?>) prioritizedTasks.get(0)).get("id")).intValue());
        assertEquals(subtask1.getId(), ((Number) ((java.util.Map<?, ?>) prioritizedTasks.get(1)).get("id")).intValue());
        assertEquals(task2.getId(), ((Number) ((java.util.Map<?, ?>) prioritizedTasks.get(2)).get("id")).intValue());
        assertEquals(task1.getId(), ((Number) ((java.util.Map<?, ?>) prioritizedTasks.get(3)).get("id")).intValue());
        assertEquals(subtask2.getId(), ((Number) ((java.util.Map<?, ?>) prioritizedTasks.get(4)).get("id")).intValue());
    }

    @Test
    public void testGetEmptyPrioritizedTasks() throws IOException, InterruptedException {
        // Отправляем запрос на получение задач по приоритету, когда задач нет
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        List<?> prioritizedTasks = gson.fromJson(response.body(), List.class);
        assertNotNull(prioritizedTasks);
        assertTrue(prioritizedTasks.isEmpty()); // Должен вернуться пустой список
    }

    private Task createTask(String name, LocalDateTime startTime) {
        Task task = new Task();
        task.setName(name);
        task.setDescription("Task description");
        task.setStatus(TaskStatus.NEW);
        task.setDuration(Duration.ofMinutes(30));
        task.setStartTime(startTime);
        return task;
    }

    private Epic createEpic() {
        Epic epic = new Epic();
        epic.setName("Test Epic");
        epic.setDescription("Epic description");
        return epic;
    }

    private Subtask createSubtask(int epicId, LocalDateTime startTime) {
        Subtask subtask = new Subtask();
        subtask.setName("Test Subtask");
        subtask.setDescription("Subtask description");
        subtask.setStatus(TaskStatus.IN_PROGRESS);
        subtask.setEpicId(epicId);
        subtask.setDuration(Duration.ofMinutes(45));
        subtask.setStartTime(startTime);
        return subtask;
    }
}