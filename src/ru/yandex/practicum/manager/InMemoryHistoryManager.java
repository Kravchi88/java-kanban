package ru.yandex.practicum.manager;

import ru.yandex.practicum.tasks.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private static final int HISTORY_LIMIT = 10;

    private final List<Task> history = new LinkedList<>();

    @Override
    public void addToHistory(Task task) {
        if (history.size() == HISTORY_LIMIT) {
            history.remove(0);
        }
        history.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }
}
