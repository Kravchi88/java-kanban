package ru.yandex.practicum.manager;

import org.jetbrains.annotations.NotNull;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;
import ru.yandex.practicum.tasks.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskManager {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();

    private int idSequence = 0;

    public Task createTask(@NotNull Task task) {
        task.setId(++idSequence);
        tasks.put(idSequence, task);

        return task;
    }

    public Epic createEpic(@NotNull Epic epic) {
        epic.setId(++idSequence);
        epics.put(idSequence, epic);

        return epic;
    }

    public Subtask createSubtask(@NotNull Subtask subtask) {
        subtask.setId(++idSequence);
        subtasks.put(idSequence, subtask);

        Epic epic = epics.get(subtask.getEpicId());
        epic.getSubtaskIds().add(subtask.getId());
        epic.setStatus(getEpicStatus(epic));

        return subtask;
    }

    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public List<Task> getEpics() {
        return new ArrayList<>(epics.values());
    }

    public List<Task> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public void removeTasks() {
        tasks.clear();
    }

    public void removeEpics() {
        epics.clear();
        subtasks.clear();
    }

    public void removeSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtaskIds().clear();
            epic.setStatus(TaskStatus.NEW);
        }
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    public void removeTaskById(int id) {
        tasks.remove(id);
    }

    public void removeEpicById(int id) {
        Epic epic = epics.get(id);
        for (int subtaskId : epic.getSubtaskIds()) {
            subtasks.remove(subtaskId);
        }
        epics.remove(epic.getId());
    }

    public void removeSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        Epic epic = epics.get(subtask.getEpicId());
        epic.getSubtaskIds().remove(Integer.valueOf(subtask.getId()));
        epic.setStatus(getEpicStatus(epic));
        subtasks.remove(subtask.getId());
    }

    public Task updateTask(@NotNull Task newTask) {
        tasks.put(newTask.getId(), newTask);

        return newTask;
    }

    public Epic updateEpic(@NotNull Epic newEpic) {
        Epic oldEpic = epics.get(newEpic.getId());
        newEpic.setSubtaskIds(oldEpic.getSubtaskIds());
        epics.put(newEpic.getId(), newEpic);

        return newEpic;
    }

    public Subtask updateSubtask(@NotNull Subtask newSubtask) {
        Subtask oldSubtask = subtasks.get(newSubtask.getId());
        newSubtask.setEpicId(oldSubtask.getEpicId());
        subtasks.put(newSubtask.getId(), newSubtask);

        // change epic status
        Epic epic = epics.get(newSubtask.getEpicId());
        epic.setStatus(getEpicStatus(epic));

        return newSubtask;
    }

    public List<Subtask> getSubtasksByEpicId(int epicId) {
        List<Subtask> epicSubtasks = new ArrayList<>();
        Epic epic = epics.get(epicId);
        for (int key : epic.getSubtaskIds()) {
            epicSubtasks.add(subtasks.get(key));
        }

        return epicSubtasks;
    }

    private TaskStatus getEpicStatus(Epic epic) {
        TaskStatus[] subtaskStatuses = new TaskStatus[epic.getSubtaskIds().size()];
        int i = 0;
        for (int subtaskId : epic.getSubtaskIds()) {
            subtaskStatuses[i] = subtasks.get(subtaskId).getStatus();
            i++;
        }
        int countNew = 0, countDone = 0;
        for (TaskStatus status : subtaskStatuses) {
            if (status == TaskStatus.NEW) {
                countNew++;
            } else if (status == TaskStatus.DONE) {
                countDone++;
            } else {
                return TaskStatus.IN_PROGRESS;
            }
        }
        if (countNew == subtaskStatuses.length) {
            return TaskStatus.NEW;
        } else if (countDone == subtaskStatuses.length) {
            return TaskStatus.DONE;
        } else {
            return TaskStatus.IN_PROGRESS;
        }
    }
}
