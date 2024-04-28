package ru.yandex.practicum.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


class HistoryManagerTest {
    private HistoryManager historyManager;

    @BeforeEach
    void beforeEach() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void addToHistory_shouldSaveTasks() {
        List<Task> expectedTasks = new ArrayList<>();

        Task task = task(1);
        expectedTasks.add(task);
        historyManager.add(task);

        Epic epic = epic(2);
        expectedTasks.add(epic);
        historyManager.add(epic);

        Subtask subtask = subtask(3, 2);
        expectedTasks.add(subtask);
        historyManager.add(subtask);

        assertEquals(expectedTasks, historyManager.getHistory(), "Wrong addition to search history");
    }

    @Test
    void addToHistory_shouldNotAddDuplicates() {
        List<Task> expectedTasks = new ArrayList<>();

        Task task1 = task(1);
        Task task2 = task(2);

        expectedTasks.add(task2);
        expectedTasks.add(task1);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task1);

        assertEquals(expectedTasks, historyManager.getHistory(), "Wrong addition to search history");
    }

    @Test
    void getHistory_shouldNotReturnNull() {
        assertNotNull(historyManager.getHistory(), "Search history does not exist");
    }

    Task task(int id) {
        Task task = new Task();
        task.setId(id);
        return task;
    }

    Epic epic(int id) {
        Epic epic = new Epic();
        epic.setId(id);
        return epic;
    }

    Subtask subtask(int id, int epicId) {
        Subtask subtask = new Subtask();
        subtask.setId(id);
        subtask.setEpicId(epicId);
        return subtask;
    }
}