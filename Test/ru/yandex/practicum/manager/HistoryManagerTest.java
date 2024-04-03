package ru.yandex.practicum.manager;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


class HistoryManagerTest {
    static HistoryManager historyManager;

    @BeforeAll
    static void beforeAll() {
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    void shouldAddToSearchHistoryAllTypesAndReturnLast10Elements() {
        List<Task> expectedTasks = new LinkedList<>();
        for (int i = 6; i <= 15; i++) {
            Task task = new Task();
            task.setId(i);
            expectedTasks.add(task);
        }

        for (int i = 1; i <= 15; i++) {
            if (i % 3 == 0) {
                Task task = new Task();
                task.setId(i);
                historyManager.addToSearchHistory(task);
            } else if (i % 3 == 1) {
                Epic epic = new Epic();
                epic.setId(i);
                historyManager.addToSearchHistory(epic);
            } else {
                Subtask subtask = new Subtask();
                subtask.setId(i);
                historyManager.addToSearchHistory(subtask);
            }
        }

        assertEquals(expectedTasks, historyManager.getHistory(), "Wrong addition to searh history");
    }

    @Test
    void shouldNotReturnNullWhenGetHistory() {
        assertNotNull(historyManager.getHistory(), "Search history does not exist");
    }
}