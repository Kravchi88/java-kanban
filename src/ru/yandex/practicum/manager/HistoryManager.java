package ru.yandex.practicum.manager;

import ru.yandex.practicum.tasks.Task;

import java.util.List;

public interface HistoryManager<T extends Task> {
    void addToHistory(T instance);
    void removeFromHistory(int id);
    List<T> getHistory();
}
