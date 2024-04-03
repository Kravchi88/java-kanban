package ru.yandex.practicum.manager;

import ru.yandex.practicum.tasks.Task;

import java.util.List;

public interface HistoryManager {
    void addToSearchHistory(Task task);

    List<Task> getHistory();
}
