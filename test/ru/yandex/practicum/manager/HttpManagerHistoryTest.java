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

public class HttpManagerHistoryTest {
    private final InMemoryTaskManager manager;
    private final HttpTaskServer taskServer;
    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .serializeNulls()
            .setPrettyPrinting()
            .create();

    public HttpManagerHistoryTest() throws IOException {
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
    public void testGetHistory() throws IOException, InterruptedException {
        Task task = createTask();
        manager.createTask(task);
        Epic epic = createEpic();
        manager.createEpic(epic);
        Subtask subtask1 = createSubtask(epic.getId());
        Subtask subtask2 = createSubtask(epic.getId());
        subtask2.setStartTime(LocalDateTime.now().plusDays(1));
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        manager.getTaskById(task.getId());
        manager.getEpicById(epic.getId());
        manager.getSubtaskById(subtask1.getId());
        manager.getSubtaskById(subtask2.getId());
        manager.getTaskById(task.getId());

        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        List<?> historyFromResponse = gson.fromJson(response.body(), List.class);
        assertNotNull(historyFromResponse);
        assertEquals(4, historyFromResponse.size());

        assertEquals(epic.getId(), ((Number) ((java.util.Map<?, ?>) historyFromResponse.get(0)).get("id")).intValue());
        assertEquals(subtask1.getId(), ((Number) ((java.util.Map<?, ?>) historyFromResponse.get(1)).get("id")).intValue());
        assertEquals(subtask2.getId(), ((Number) ((java.util.Map<?, ?>) historyFromResponse.get(2)).get("id")).intValue());
        assertEquals(task.getId(), ((Number) ((java.util.Map<?, ?>) historyFromResponse.get(3)).get("id")).intValue());
    }

    @Test
    public void testGetEmptyHistory() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        List<?> historyFromResponse = gson.fromJson(response.body(), List.class);
        assertNotNull(historyFromResponse);
        assertTrue(historyFromResponse.isEmpty());
    }

    private Task createTask() {
        Task task = new Task();
        task.setName("Test Task");
        task.setDescription("Task description");
        task.setStatus(TaskStatus.NEW);
        task.setDuration(Duration.ofMinutes(30));
        task.setStartTime(LocalDateTime.now());
        return task;
    }

    private Epic createEpic() {
        Epic epic = new Epic();
        epic.setName("Test Epic");
        epic.setDescription("Epic description");
        return epic;
    }

    private Subtask createSubtask(int epicId) {
        Subtask subtask = new Subtask();
        subtask.setName("Test Subtask");
        subtask.setDescription("Subtask description");
        subtask.setStatus(TaskStatus.IN_PROGRESS);
        subtask.setEpicId(epicId);
        subtask.setDuration(Duration.ofMinutes(15));
        subtask.setStartTime(LocalDateTime.now().plusHours(1));
        return subtask;
    }
}