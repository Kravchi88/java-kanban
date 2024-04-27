package ru.yandex.practicum.manager;

import ru.yandex.practicum.tasks.Task;

import java.util.List;

public interface HistoryManager {
    void addToHistory(Task task);
    void removeFromHistory(int id);
    List<Task> getHistory();
}
