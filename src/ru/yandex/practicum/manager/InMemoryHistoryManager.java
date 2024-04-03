package ru.yandex.practicum.manager;

import ru.yandex.practicum.tasks.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> searchHistory = new LinkedList<>();

    @Override
    public void addToSearchHistory(Task task) {
        if (searchHistory.size() == 10) {
            searchHistory.remove(0);
        }
        searchHistory.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return searchHistory;
    }
}
