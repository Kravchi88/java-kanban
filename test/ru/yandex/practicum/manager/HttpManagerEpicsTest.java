package ru.yandex.practicum.manager;

import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import com.google.gson.Gson;
import ru.yandex.practicum.HttpTaskServer;
import ru.yandex.practicum.tasks.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpManagerEpicsTest {
    private final InMemoryTaskManager manager;
    private final HttpTaskServer taskServer;
    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .serializeNulls()
            .setPrettyPrinting()
            .create();

    public HttpManagerEpicsTest() throws IOException {
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
    public void testCreateEpic() throws IOException, InterruptedException {
        // 201 created
        Epic epic = epic();
        String epicJson = gson.toJson(epic);

        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        List<Epic> epicsFromManager = manager.getEpics();
        assertNotNull(epicsFromManager);
        assertEquals(1, epicsFromManager.size());
        assertEquals("Test Epic", epicsFromManager.get(0).getName());
    }

    @Test
    public void testUpdateEpic() throws IOException, InterruptedException {
        // 201 updated
        Epic epic = epic();
        manager.createEpic(epic);

        epic.setName("Updated Epic");
        String epicJson = gson.toJson(epic);

        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        List<Epic> epicsFromManager = manager.getEpics();
        assertEquals(1, epicsFromManager.size());
        assertEquals("Updated Epic", epicsFromManager.get(0).getName());

        // 404 not found
        Epic nonExistentEpic = new Epic();
        nonExistentEpic.setId(999);
        nonExistentEpic.setName("Non-existent Epic");
        nonExistentEpic.setDescription("This epic does not exist in the manager");

        String nonExistentEpicJson = gson.toJson(nonExistentEpic);
        HttpRequest nonExistentRequest = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(nonExistentEpicJson)).build();
        HttpResponse<String> nonExistentResponse = client.send(nonExistentRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, nonExistentResponse.statusCode());
    }

    @Test
    public void testGetEpicById() throws IOException, InterruptedException {
        // 200 success get
        Epic epic = epic();
        manager.createEpic(epic);

        URI url = URI.create("http://localhost:8080/epics/" + epic.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        Epic epicFromResponse = gson.fromJson(response.body(), Epic.class);
        assertNotNull(epicFromResponse);
        assertEquals(epic.getName(), epicFromResponse.getName());

        // 404 not found
        URI nonExistentUrl = URI.create("http://localhost:8080/epics/999");
        HttpRequest nonExistentRequest = HttpRequest.newBuilder().uri(nonExistentUrl).GET().build();
        HttpResponse<String> nonExistentResponse = client.send(nonExistentRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, nonExistentResponse.statusCode());
    }

    @Test
    public void testGetEpics() throws IOException, InterruptedException {
        // 200 success get
        Epic epic = epic();
        manager.createEpic(epic);

        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        List<Epic> epicsFromResponse = gson.fromJson(response.body(), List.class);
        assertNotNull(epicsFromResponse);
        assertEquals(1, epicsFromResponse.size());
    }

    @Test
    public void testGetSubtasksByEpic() throws IOException, InterruptedException {
        Epic epic = epic();
        manager.createEpic(epic);
        Subtask subtask = new Subtask();
        subtask.setName("Test Subtask");
        subtask.setDescription("Testing subtask");
        subtask.setStatus(TaskStatus.NEW);
        subtask.setEpicId(epic.getId());
        subtask.setDuration(Duration.ofMinutes(30));
        subtask.setStartTime(LocalDateTime.now());
        manager.createSubtask(subtask);

        // 200 success get
        URI url = URI.create("http://localhost:8080/epics/" + epic.getId() + "/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        List<Subtask> subtasksFromResponse = gson.fromJson(response.body(), List.class);
        assertNotNull(subtasksFromResponse);
        assertEquals(1, subtasksFromResponse.size());

        // 404 not found
        URI nonExistentUrl = URI.create("http://localhost:8080/epics/999");
        HttpRequest nonExistentRequest = HttpRequest.newBuilder().uri(nonExistentUrl).GET().build();
        HttpResponse<String> nonExistentResponse = client.send(nonExistentRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, nonExistentResponse.statusCode());
    }

    @Test
    public void testRemoveEpics() throws IOException, InterruptedException {
        // 200 success remove
        Epic epic = epic();
        manager.createEpic(epic);

        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(manager.getEpics().isEmpty());
    }

    @Test
    public void testRemoveEpicById() throws IOException, InterruptedException {
        // 200 success remove
        Epic epic = epic();
        manager.createEpic(epic);

        URI url = URI.create("http://localhost:8080/epics/" + epic.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(manager.getEpics().isEmpty());

        // 404 not found
        URI nonExistentUrl = URI.create("http://localhost:8080/epics/999");
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
}
