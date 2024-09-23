package ru.yandex.practicum.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;
import ru.yandex.practicum.tasks.TaskStatus;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;


class FileBackedTaskManagerTest {
    private TaskManager taskManager;
    private final Path path = Paths.get("test/files/test.csv");

    @BeforeEach
    void beforeEach() {
        taskManager = FileBackedTaskManager.loadFromFile(path.toFile());
    }

    @Test
    void shouldSerializeTasks() {
        taskManager.createTask(task1());
        taskManager.createTask(task2());
        taskManager.createEpic(epic1());
        taskManager.createSubtask(subtask1());
        taskManager.createSubtask(subtask2());
        taskManager.createSubtask(subtask3());

        taskManager.getTaskById(1);

        taskManager.removeSubtaskById(4);

        taskManager.updateSubtask(subtask3Upd());

        TaskManager newTaskManager = FileBackedTaskManager.loadFromFile(path.toFile());

        assertEquals(taskManager.getTasks(), newTaskManager.getTasks());
        assertEquals(taskManager.getEpics(), newTaskManager.getEpics());
        assertEquals(taskManager.getSubtasks(), newTaskManager.getSubtasks());
        assertEquals(taskManager.getPrioritizedTasks(), newTaskManager.getPrioritizedTasks());

        /*try (FileOutputStream fos = new FileOutputStream(path.toString())) {
            // just to clear the file
        } catch (IOException e) {
            System.out.println("Failed to clear the file" + e.getMessage());
        }*/
    }

    @Test
    void shouldDeserializeTasks() throws IOException {
        File file = new File("test/files/fileToRead.csv");
        FileBackedTaskManager manager = new FileBackedTaskManager("test/files/fileToRead.csv");

        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        Task actualTask = manager.fromString(bufferedReader.readLine());
        Epic actualEpic = (Epic) manager.fromString(bufferedReader.readLine());
        Subtask actualSubtask = (Subtask) manager.fromString(bufferedReader.readLine());

        assertEquals(task(), actualTask);
        assertEquals(epic(), actualEpic);
        assertEquals(subtask(), actualSubtask);
    }

    Task task() {
        Task task = new Task();
        task.setId(1);
        task.setName("Task1");
        task.setDescription("Description task1");
        task.setStatus(TaskStatus.NEW);
        task.setDuration(Duration.ofMinutes(30));
        task.setStartTime(LocalDateTime.of(2024,8, 31, 10, 30));
        return task;
    }

    Epic epic() {
        Epic epic = new Epic();
        epic.setId(2);
        epic.setName("Epic2");
        epic.setDescription("Description epic2");
        epic.setStatus(TaskStatus.DONE);
        epic.setDuration(Duration.ofMinutes(10));
        epic.setStartTime(LocalDateTime.of(2024,8, 31, 11,0));
        return epic;
    }

    Subtask subtask() {
       Subtask subtask = new Subtask();
       subtask.setId(3);
       subtask.setName("Sub Task2");
       subtask.setDescription("Description sub task3");
       subtask.setStatus(TaskStatus.DONE);
       subtask.setEpicId(2);
       subtask.setDuration(Duration.ofMinutes(10));
       subtask.setStartTime(LocalDateTime.of(2024,8, 31, 11,0));
       return subtask;
    }

    Task task1() {
        Task task = new Task();
        task.setStatus(TaskStatus.NEW);
        task.setDuration(Duration.ofMinutes(30));
        task.setStartTime(LocalDateTime.of(2024,1, 1, 10, 30));
        return task;
    }

    Task task2() {
        Task task = new Task();
        task.setStatus(TaskStatus.NEW);
        task.setDuration(Duration.ofMinutes(60));
        task.setStartTime(LocalDateTime.of(2024,1, 3, 11, 0));
        return task;
    }

    Epic epic1() {
        Epic epic = new Epic();
        return epic;
    }

    Subtask subtask1() {
        Subtask subtask = new Subtask();
        subtask.setEpicId(3);
        subtask.setStatus(TaskStatus.NEW);
        subtask.setDuration(Duration.ofMinutes(120));
        subtask.setStartTime(LocalDateTime.of(2024,1, 2, 12, 0));
        return subtask;
    }

    Subtask subtask2() {
        Subtask subtask = new Subtask();
        subtask.setEpicId(3);
        subtask.setStatus(TaskStatus.NEW);
        subtask.setDuration(Duration.ofMinutes(20));
        subtask.setStartTime(LocalDateTime.of(2024,1, 2, 15, 0));
        return subtask;
    }

    Subtask subtask3() {
        Subtask subtask = new Subtask();
        subtask.setEpicId(3);
        subtask.setStatus(TaskStatus.NEW);
        subtask.setDuration(Duration.ofMinutes(10));
        subtask.setStartTime(LocalDateTime.of(2024,1, 2, 16, 0));
        return subtask;
    }

    Subtask subtask3Upd() {
        Subtask subtask = new Subtask();
        subtask.setId(6);
        subtask.setEpicId(3);
        subtask.setStatus(TaskStatus.IN_PROGRESS);
        subtask.setDuration(Duration.ofMinutes(40));
        subtask.setStartTime(LocalDateTime.of(2024,1, 2, 16, 0));
        return subtask;
    }
}
