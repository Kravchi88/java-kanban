package ru.yandex.practicum.manager;

import org.jetbrains.annotations.NotNull;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;

import java.util.List;

public interface TaskManager {
    Task createTask(@NotNull Task task);

    Epic createEpic(@NotNull Epic epic);

    Subtask createSubtask(@NotNull Subtask subtask);

    Task getTaskById(int id);

    Epic getEpicById(int id);

    Subtask getSubtaskById(int id);

    List<Task> getTasks();

    List<Epic> getEpics();

    List<Subtask> getSubtasks();

    void removeTaskById(int id);

    void removeEpicById(int id);

    void removeSubtaskById(int id);

    void removeTasks();

    void removeEpics();

    void removeSubtasks();

    Task updateTask(@NotNull Task newTask);

    Epic updateEpic(@NotNull Epic newEpic);

    Subtask updateSubtask(@NotNull Subtask newSubtask);

    List<Subtask> getSubtasksByEpicId(int epicId);
}
