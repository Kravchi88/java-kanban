package ru.yandex.practicum.manager;

import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import com.google.gson.Gson;
import ru.yandex.practicum.HttpTaskServer;
import ru.yandex.practicum.tasks.DurationAdapter;
import ru.yandex.practicum.tasks.LocalDateTimeAdapter;
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

public class HttpManagerTasksTest {
    private final InMemoryTaskManager manager;
    private final HttpTaskServer taskServer;
    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .serializeNulls()
            .setPrettyPrinting()
            .create();

    public HttpManagerTasksTest() throws IOException {
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
    public void testCreateTask() throws IOException, InterruptedException {
        // 201 created
        Task task = task();
        String taskJson = gson.toJson(task);

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        List<Task> tasksFromManager = manager.getTasks();
        assertNotNull(tasksFromManager);
        assertEquals(1, tasksFromManager.size());
        assertEquals("Test Task", tasksFromManager.get(0).getName());

        // 406 time conflict
        Task conflictingTask = new Task();
        conflictingTask.setName("Conflicting Task");
        conflictingTask.setDescription("This task conflicts with the existing one");
        conflictingTask.setStatus(TaskStatus.NEW);
        conflictingTask.setStartTime(task.getStartTime());
        conflictingTask.setDuration(Duration.ofMinutes(30));

        String conflictingTaskJson = gson.toJson(conflictingTask);
        HttpRequest conflictRequest = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(conflictingTaskJson)).build();
        HttpResponse<String> conflictResponse = client.send(conflictRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, conflictResponse.statusCode());
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {
        // 201 updated
        Task task = task();
        manager.createTask(task);

        task.setName("Updated Task");
        String taskJson = gson.toJson(task);

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        List<Task> tasksFromManager = manager.getTasks();
        assertEquals(1, tasksFromManager.size());
        assertEquals("Updated Task", tasksFromManager.get(0).getName());

        // 406 time conflict
        Task conflictingTask = new Task();
        conflictingTask.setId(2);
        conflictingTask.setName("Conflicting Update");
        conflictingTask.setDescription("This update conflicts with the existing one");
        conflictingTask.setStatus(TaskStatus.NEW);
        conflictingTask.setStartTime(task.getStartTime().plusHours(3));
        conflictingTask.setDuration(Duration.ofMinutes(60));
        manager.createTask(conflictingTask);

        conflictingTask.setStartTime(task.getStartTime());

        String conflictingTaskJson = gson.toJson(conflictingTask);
        HttpRequest conflictUpdateRequest = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(conflictingTaskJson)).build();
        HttpResponse<String> conflictUpdateResponse = client.send(conflictUpdateRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, conflictUpdateResponse.statusCode());

        // 404 not found
        Task nonExistentTask = new Task();
        nonExistentTask.setId(999);
        nonExistentTask.setName("Non-existent Task");
        nonExistentTask.setDescription("This task does not exist in the manager");
        nonExistentTask.setStatus(TaskStatus.NEW);
        nonExistentTask.setStartTime(LocalDateTime.now().plusHours(1));
        nonExistentTask.setDuration(Duration.ofMinutes(30));

        String nonExistentTaskJson = gson.toJson(nonExistentTask);
        HttpRequest nonExistentRequest = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(nonExistentTaskJson)).build();
        HttpResponse<String> nonExistentResponse = client.send(nonExistentRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, nonExistentResponse.statusCode());
    }

    @Test
    public void testGetTaskById() throws IOException, InterruptedException {
        // 200 success get
        Task task = task();
        manager.createTask(task);

        URI url = URI.create("http://localhost:8080/tasks/" + task.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        Task taskFromResponse = gson.fromJson(response.body(), Task.class);
        assertNotNull(taskFromResponse);
        assertEquals(task.getName(), taskFromResponse.getName());

        // 404 not found
        URI nonExistentUrl = URI.create("http://localhost:8080/tasks/999");
        HttpRequest nonExistentRequest = HttpRequest.newBuilder().uri(nonExistentUrl).GET().build();
        HttpResponse<String> nonExistentResponse = client.send(nonExistentRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, nonExistentResponse.statusCode());
    }

    @Test
    public void testGetTasks() throws IOException, InterruptedException {
        // 200 success get
        Task task = task();
        manager.createTask(task);

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        List<Task> tasksFromResponse = gson.fromJson(response.body(), List.class);
        assertNotNull(tasksFromResponse);
        assertEquals(1, tasksFromResponse.size());
    }

    @Test
    public void testRemoveTasks() throws IOException, InterruptedException {
        // 200 success delete
        Task task = task();
        manager.createTask(task);

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(manager.getTasks().isEmpty());
    }

    @Test
    public void testRemoveTaskById() throws IOException, InterruptedException {
        // 200 success delete
        Task task = task();
        manager.createTask(task);

        URI url = URI.create("http://localhost:8080/tasks/" + task.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(manager.getTasks().isEmpty());

        // 404 not found
        URI nonExistentUrl = URI.create("http://localhost:8080/tasks/999");
        HttpRequest nonExistentRequest = HttpRequest.newBuilder().uri(nonExistentUrl).DELETE().build();
        HttpResponse<String> nonExistentResponse = client.send(nonExistentRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, nonExistentResponse.statusCode());
    }

    private Task task() {
        Task task = new Task();
        task.setName("Test Task");
        task.setDescription("Testing task");
        task.setStatus(TaskStatus.NEW);
        task.setDuration(Duration.ofMinutes(30));
        task.setStartTime(LocalDateTime.now());
        return task;
    }
}
