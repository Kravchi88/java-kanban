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
    void shouldSerializeTasks() throws IOException {
        File expected = new File("test/files/expectedSerialize.csv");
        File actual = new File("test/files/actualSerialize.csv");
        FileBackedTaskManager manager = new FileBackedTaskManager("test/files/actualSerialize.csv");

        FileWriter writerExpected = new FileWriter(expected);
        writerExpected.write("1,TASK,Task1,NEW,Description task1,30,2024-08-31T10:30\n" +
                "2,EPIC,Epic2,DONE,Description epic2,10,2024-08-31T11:00\n" +
                "3,SUBTASK,Sub Task2,DONE,Description sub task3,10,2024-08-31T11:00,2");
        writerExpected.close();

        FileWriter writerActual = new FileWriter(actual);
        writerActual.write(task().toCsvLine() + "\n");
        writerActual.write(epic().toCsvLine() + "\n");
        writerActual.write(subtask().toCsvLine() + "\n");
        writerActual.close();

        StringBuilder expectedString = new StringBuilder();
        BufferedReader bufferedReaderExpected = new BufferedReader(new FileReader(expected));
        while (bufferedReaderExpected.ready()) {
            expectedString.append(bufferedReaderExpected.readLine());
        }

        StringBuilder actualString = new StringBuilder();
        BufferedReader bufferedReaderActual = new BufferedReader(new FileReader(actual));
        while (bufferedReaderActual.ready()) {
            actualString.append(bufferedReaderActual.readLine());
        }

        assertEquals(expectedString.toString(), actualString.toString());
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
}
