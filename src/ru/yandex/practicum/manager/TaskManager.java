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

    List<Task> getTasks();

    List<Task> getEpics();

    List<Task> getSubtasks();

    void removeTasks();

    void removeEpics();

    void removeSubtasks();

    Task getTaskById(int id);

    Epic getEpicById(int id);

    Subtask getSubtaskById(int id);

    void removeTaskById(int id);

    void removeEpicById(int id);

    void removeSubtaskById(int id);

    Task updateTask(@NotNull Task newTask);

    Epic updateEpic(@NotNull Epic newEpic);

    Subtask updateSubtask(@NotNull Subtask newSubtask);

    List<Subtask> getSubtasksByEpicId(int epicId);
}
