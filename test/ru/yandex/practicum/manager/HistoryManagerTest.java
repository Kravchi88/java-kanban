package ru.yandex.practicum.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;

import java.util.LinkedList;
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
        List<Task> expectedTasks = new LinkedList<>();

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

        assertEquals(expectedTasks, historyManager.getHistory(), "Wrong addition to searh history");
    }

    @Test
    void addToHistory_shouldKeepNoMoreThan10Elements() {
        int historyLimit = 10;

        for (int i = 1; i <= 15; i++) {
            Task task = new Task();
            task.setId(i);
            historyManager.addToHistory(task);
        }

        assertEquals(historyLimit, historyManager.getHistory().size());
    }

    @Test
    void getHistory_shouldNotReturnNull() {
        assertNotNull(historyManager.getHistory(), "Search history does not exist");
    }
}