package ru.yandex.practicum.manager;

import org.jetbrains.annotations.NotNull;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;
import ru.yandex.practicum.tasks.TaskStatus;
import ru.yandex.practicum.tasks.TaskType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskManager {
    private Map<Integer, Task> tasks = new HashMap<>();

    private int idSequence = 0;

    public Task createTask(@NotNull Task task) {
        task.setId(++idSequence);
        tasks.put(idSequence, task);

        return task;
    }

    public Epic createEpic(@NotNull Epic epic) {
        epic.setId(++idSequence);
        tasks.put(idSequence, epic);

        return epic;
    }

    public Subtask createSubtask(@NotNull Subtask subtask) {
        subtask.setId(++idSequence);
        tasks.put(idSequence, subtask);

        Epic epic = (Epic) tasks.get(subtask.getEpicId());
        epic.getSubtaskIds().add(subtask.getId());

        return subtask;
    }

    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public void removeAllTasks() {
        tasks.clear();
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Epic getEpicById(int id) {
        return (Epic) tasks.get(id);
    }

    public Subtask getSubtaskById(int id) {
        return (Subtask) tasks.get(id);
    }

    public void removeTaskById(int id) {
        tasks.remove(id);
    }

    public void removeEpicById(int id) {
        Epic epic = (Epic) tasks.get(id);
        for (int subtask : epic.getSubtaskIds()) {
            tasks.remove(subtask);
        }
        tasks.remove(epic.getId());
    }

    public void removeSubtaskById(int id) {
        Subtask subtask = (Subtask) tasks.get(id);
        Epic epic = (Epic) tasks.get(subtask.getEpicId());
        epic.getSubtaskIds().remove(subtask.getId());
        tasks.remove(subtask.getId());
    }

    public Task updateTask(@NotNull Task newTask) {
        tasks.put(newTask.getId(), newTask);

        return newTask;
    }

    public Epic updateEpic(@NotNull Epic newEpic) {
        Epic oldEpic = (Epic) tasks.get(newEpic.getId());
        newEpic.setSubtaskIds(oldEpic.getSubtaskIds());
        tasks.put(newEpic.getId(), newEpic);

        return newEpic;
    }

    public Subtask updateSubtask(@NotNull Subtask newSubtask) {
        Subtask oldSubtask = (Subtask) tasks.get(newSubtask.getId());
        newSubtask.setEpicId(oldSubtask.getEpicId());
        tasks.put(newSubtask.getId(), newSubtask);

        // change epic status
        Epic epic = (Epic) tasks.get(newSubtask.getEpicId());

        if (newSubtask.getTaskStatus() == TaskStatus.IN_PROGRESS) {
            epic.setTaskStatus(TaskStatus.IN_PROGRESS);
        } else if (newSubtask.getTaskStatus() == TaskStatus.DONE) {
            epic.setTaskStatus(TaskStatus.IN_PROGRESS);
            for (int key : epic.getSubtaskIds()) {
                if (tasks.get(key).getTaskStatus() != TaskStatus.DONE) {
                    return newSubtask;
                }
            }
            epic.setTaskStatus(TaskStatus.DONE);
        }

        return newSubtask;
    }

    public List<Subtask> getSubtasksByEpic(Epic epic) {
        List<Subtask> subtasks = new ArrayList<>();
        for (int key : epic.getSubtaskIds()) {
            subtasks.add((Subtask) tasks.get(key));
        }

        return subtasks;
    }
}
