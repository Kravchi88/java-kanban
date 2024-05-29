package ru.yandex.practicum.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;
import ru.yandex.practicum.tasks.TaskStatus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class FileBackedTaskManagerTest {
    TaskManager taskManager;
    Path path = Paths.get("test/files/test.csv");

    @BeforeEach
    void beforeEach() {
        taskManager = FileBackedTaskManager.loadFromFile(path.toFile());
    }

    @Test
    void shouldSerializeTasks() {
        File expected = new File("test/files/expectedSerialize.csv");
        File actual = new File("test/files/actualSerialize.csv");
        FileBackedTaskManager manager = new FileBackedTaskManager("test/files/actualSerialize.csv");

        try (FileWriter writer = new FileWriter(expected)) {
            writer.write("1,TASK,Task1,NEW,Description task1\n" +
                    "2,EPIC,Epic2,DONE,Description epic2\n" +
                    "3,SUBTASK,Sub Task2,DONE,Description sub task3,2");
        } catch (IOException e) {
            System.out.println("Exception");
        }

        try (FileWriter writer = new FileWriter(actual)) {
            writer.write(manager.toString(task()) + "\n");
            writer.write(manager.toString(epic()) + "\n");
            writer.write(manager.toString(subtask()) + "\n");
        } catch (IOException e) {
            System.out.println("Exception");
        }

        StringBuilder expectedString = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(expected))) {
            while (bufferedReader.ready()) {
                expectedString.append(bufferedReader.readLine());
            }
        } catch (IOException e){
            System.out.println("Exception");
        }

        StringBuilder actualString = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(actual))) {
            while (bufferedReader.ready()) {
                actualString.append(bufferedReader.readLine());
            }
        } catch (IOException e) {
            System.out.println("Exception");
        }

        assertEquals(expectedString.toString(), actualString.toString());
    }

    @Test
    void shouldDeserializeTasks() {
        File file = new File("test/files/fileToRead.csv");
        FileBackedTaskManager manager = new FileBackedTaskManager("test/files/fileToRead.csv");

        Task actualTask = null;
        Epic actualEpic = null;
        Subtask actualSubtask = null;

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            actualTask = manager.fromString(bufferedReader.readLine());
            actualEpic = (Epic) manager.fromString(bufferedReader.readLine());
            actualSubtask = (Subtask) manager.fromString(bufferedReader.readLine());
        } catch (IOException e) {
            System.out.println("Exception");
        }

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
        return task;
    }

    Epic epic() {
        Epic epic = new Epic();
        epic.setId(2);
        epic.setName("Epic2");
        epic.setDescription("Description epic2");
        epic.setStatus(TaskStatus.DONE);
        return epic;
    }

    Subtask subtask() {
       Subtask subtask = new Subtask();
       subtask.setId(3);
       subtask.setName("Sub Task2");
       subtask.setDescription("Description sub task3");
       subtask.setStatus(TaskStatus.DONE);
       subtask.setEpicId(2);
       return subtask;
    }
}
