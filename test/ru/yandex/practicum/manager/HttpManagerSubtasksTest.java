package ru.yandex.practicum.manager;

import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import com.google.gson.Gson;
import ru.yandex.practicum.HttpTaskServer;
import ru.yandex.practicum.tasks.DurationAdapter;
import ru.yandex.practicum.tasks.LocalDateTimeAdapter;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
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

public class HttpManagerSubtasksTest {
    private final InMemoryTaskManager manager;
    private final HttpTaskServer taskServer;
    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .serializeNulls()
            .setPrettyPrinting()
            .create();

    public HttpManagerSubtasksTest() throws IOException {
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
    public void testCreateSubtask() throws IOException, InterruptedException {
        Epic epic = epic();
        manager.createEpic(epic);

        // 201 created
        Subtask subtask = subtask(epic.getId());
        String subtaskJson = gson.toJson(subtask);

        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        List<Subtask> subtasksFromManager = manager.getSubtasks();
        assertNotNull(subtasksFromManager);
        assertEquals(1, subtasksFromManager.size());
        assertEquals("Test Subtask", subtasksFromManager.get(0).getName());

        // 406 time conflict
        Subtask conflictingSubtask = subtask(epic.getId());
        conflictingSubtask.setName("Conflicting Subtask");
        conflictingSubtask.setStartTime(subtask.getStartTime());

        String conflictingSubtaskJson = gson.toJson(conflictingSubtask);
        HttpRequest conflictRequest = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(conflictingSubtaskJson)).build();
        HttpResponse<String> conflictResponse = client.send(conflictRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, conflictResponse.statusCode());
    }

    @Test
    public void testUpdateSubtask() throws IOException, InterruptedException {
        Epic epic = epic();
        manager.createEpic(epic);
        Subtask subtask1 = subtask(epic.getId());
        manager.createSubtask(subtask1);

        Subtask subtask2 = new Subtask();
        subtask2.setName("Second Subtask");
        subtask2.setDescription("A subtask with different time");
        subtask2.setEpicId(epic.getId());
        subtask2.setStatus(TaskStatus.NEW);
        subtask2.setStartTime(subtask1.getStartTime().plusHours(2));
        subtask2.setDuration(Duration.ofMinutes(30));
        manager.createSubtask(subtask2);

        // 201 updated
        subtask1.setName("Updated Subtask");
        String subtaskJson = gson.toJson(subtask1);

        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        List<Subtask> subtasksFromManager = manager.getSubtasks();
        assertEquals(2, subtasksFromManager.size());
        assertEquals("Updated Subtask", subtasksFromManager.get(0).getName());

        // 406 time conflict
        subtask1.setStartTime(subtask2.getStartTime());
        String conflictingSubtaskJson = gson.toJson(subtask1);
        HttpRequest conflictUpdateRequest = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(conflictingSubtaskJson)).build();
        HttpResponse<String> conflictUpdateResponse = client.send(conflictUpdateRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, conflictUpdateResponse.statusCode());

        // 404 not found
        Subtask nonExistentSubtask = new Subtask();
        nonExistentSubtask.setId(999);
        nonExistentSubtask.setEpicId(epic.getId());
        nonExistentSubtask.setName("Non-existent Subtask");
        nonExistentSubtask.setDescription("This subtask does not exist in the manager");
        nonExistentSubtask.setStatus(TaskStatus.NEW);
        nonExistentSubtask.setStartTime(LocalDateTime.now().plusHours(1));
        nonExistentSubtask.setDuration(Duration.ofMinutes(30));

        String nonExistentSubtaskJson = gson.toJson(nonExistentSubtask);
        HttpRequest nonExistentRequest = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(nonExistentSubtaskJson)).build();
        HttpResponse<String> nonExistentResponse = client.send(nonExistentRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, nonExistentResponse.statusCode());
    }

    @Test
    public void testGetSubtaskById() throws IOException, InterruptedException {
        Epic epic = epic();
        manager.createEpic(epic);
        Subtask subtask = subtask(epic.getId());
        manager.createSubtask(subtask);

        // 200 success get
        URI url = URI.create("http://localhost:8080/subtasks/" + subtask.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        Subtask subtaskFromResponse = gson.fromJson(response.body(), Subtask.class);
        assertNotNull(subtaskFromResponse);
        assertEquals(subtask.getName(), subtaskFromResponse.getName());

        // 404 not found
        URI nonExistentUrl = URI.create("http://localhost:8080/subtasks/999");
        HttpRequest nonExistentRequest = HttpRequest.newBuilder().uri(nonExistentUrl).GET().build();
        HttpResponse<String> nonExistentResponse = client.send(nonExistentRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, nonExistentResponse.statusCode());
    }

    @Test
    public void testGetSubtasks() throws IOException, InterruptedException {
        Epic epic = epic();
        manager.createEpic(epic);
        Subtask subtask = subtask(epic.getId());
        manager.createSubtask(subtask);

        // 200 success get
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        List<Subtask> subtasksFromResponse = gson.fromJson(response.body(), List.class);
        assertNotNull(subtasksFromResponse);
        assertEquals(1, subtasksFromResponse.size());
    }

    @Test
    public void testRemoveSubtasks() throws IOException, InterruptedException {
        Epic epic = epic();
        manager.createEpic(epic);
        Subtask subtask = subtask(epic.getId());
        manager.createSubtask(subtask);

        // 200 success remove
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(manager.getSubtasks().isEmpty());
    }

    @Test
    public void testRemoveSubtaskById() throws IOException, InterruptedException {
        Epic epic = epic();
        manager.createEpic(epic);
        Subtask subtask = subtask(epic.getId());
        manager.createSubtask(subtask);

        // 200 success remove
        URI url = URI.create("http://localhost:8080/subtasks/" + subtask.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(manager.getSubtasks().isEmpty());

        // 404 not found
        URI nonExistentUrl = URI.create("http://localhost:8080/subtasks/999");
        HttpRequest nonExistentRequest = HttpRequest.newBuilder().uri(nonExistentUrl).DELETE().build();
        HttpResponse<String> nonExistentResponse = client.send(nonExistentRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, nonExistentResponse.statusCode());
    }

    private Epic epic() {
        Epic epic = new Epic();
        epic.setName("Test Epic");
        epic.setDescription("Testing epic");
        return epic;
    }

    private Subtask subtask(int epicId) {
        Subtask subtask = new Subtask();
        subtask.setName("Test Subtask");
        subtask.setDescription("Testing subtask");
        subtask.setStatus(TaskStatus.NEW);
        subtask.setEpicId(epicId);
        subtask.setDuration(Duration.ofMinutes(30));
        subtask.setStartTime(LocalDateTime.now());
        return subtask;
    }
}