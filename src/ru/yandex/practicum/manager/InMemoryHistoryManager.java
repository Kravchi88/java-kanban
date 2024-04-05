package ru.yandex.practicum.manager;

import ru.yandex.practicum.tasks.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> history = new LinkedList<>();
    private static final int MAX_CAPACITY = 10;

    @Override
    public void addToHistory(Task task) {
        if (history.size() == MAX_CAPACITY) {
            history.remove(0);
        }
        history.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }
}
