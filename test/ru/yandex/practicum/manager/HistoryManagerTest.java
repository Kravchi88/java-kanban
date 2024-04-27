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

        Task task = new Task();
        task.setId(1);
        expectedTasks.add(task);
        historyManager.addToHistory(task);

        Epic epic = new Epic();
        epic.setId(2);
        expectedTasks.add(epic);
        historyManager.addToHistory(epic);

        Subtask subtask = new Subtask();
        subtask.setId(3);
        expectedTasks.add(subtask);
        historyManager.addToHistory(subtask);

        assertEquals(expectedTasks, historyManager.getHistory(), "Wrong addition to search history");
    }

    @Test
    void addToHistory_shouldNotAddDuplicates() {
        List<Task> expectedTasks = new ArrayList<>();

        Task task1 = new Task();
        task1.setId(1);
        Task task2 = new Task();
        task2.setId(2);

        expectedTasks.add(task2);
        expectedTasks.add(task1);

        historyManager.addToHistory(task1);
        historyManager.addToHistory(task2);
        historyManager.addToHistory(task1);

        assertEquals(expectedTasks, historyManager.getHistory(), "Wrong addition to search history");
    }

    @Test
    void getHistory_shouldNotReturnNull() {
        assertNotNull(historyManager.getHistory(), "Search history does not exist");
    }
}