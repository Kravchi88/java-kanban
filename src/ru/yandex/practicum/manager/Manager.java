package ru.yandex.practicum.manager;

import org.jetbrains.annotations.NotNull;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;
import ru.yandex.practicum.tasks.specifications.Status;
import ru.yandex.practicum.tasks.specifications.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Manager {
    private Map<Integer, Task> tasksMap = new HashMap<>();

    private int availableId = 0;

    public Task createTask(@NotNull Task task) {
        task.setId(++availableId);
        tasksMap.put(availableId, task);

        if (task.getTYPE() == Type.SUBTASK) {
            Subtask subtask = (Subtask) task;
            Epic epic = (Epic) tasksMap.get(subtask.getEpicId());
            epic.getListOfSubtasks().add(subtask.getId());
            return subtask;
        }
        return task;
    }

    public List<Task> getAllTasks() {
        return new ArrayList<>(tasksMap.values());
    }

    public void removeAllTasks() {
        tasksMap.clear();
    }

    public Task getTaskById(int id) {
        return tasksMap.get(id);
    }

    public void removeTaskById(int id){
        if (tasksMap.get(id).getTYPE() == Type.EPIC) {
            Epic epic = (Epic) tasksMap.get(id);
            for (int subtask : epic.getListOfSubtasks()) {
                tasksMap.remove(subtask);
            }
            tasksMap.remove(epic.getId());
            return;
        } else if (tasksMap.get(id).getTYPE() == Type.SUBTASK) {
            Subtask subtask = (Subtask) tasksMap.get(id);
            Epic epic = (Epic) tasksMap.get(subtask.getEpicId());
            epic.getListOfSubtasks().remove(subtask.getId());
            tasksMap.remove(subtask.getId());
            return;
        }
        tasksMap.remove(id);
    }

    public Task updateTask(@NotNull Task task) {
        if (task.getTYPE() == Type.EPIC) {
            Epic newEpic = (Epic) task;
            Epic oldEpic = (Epic) tasksMap.get(task.getId());
            newEpic.setListOfSubtasks(oldEpic.getListOfSubtasks());
            tasksMap.put(newEpic.getId(), newEpic);
            return newEpic;
        } else if (task.getTYPE() == Type.SUBTASK) {
            Subtask newSubtask = (Subtask) task;
            Subtask oldSubtask = (Subtask) tasksMap.get(task.getId());
            newSubtask.setEpicId(oldSubtask.getEpicId());
            tasksMap.put(newSubtask.getId(), newSubtask);

            // change epic status
            Epic epic = (Epic) tasksMap.get(newSubtask.getEpicId());

            if (newSubtask.getStatus() == Status.IN_PROGRESS) {
                epic.setStatus(Status.IN_PROGRESS);
            } else if (newSubtask.getStatus() == Status.DONE) {
                epic.setStatus(Status.IN_PROGRESS);
                for (int key : epic.getListOfSubtasks()) {
                    if (tasksMap.get(key).getStatus() != Status.DONE) {
                        return newSubtask;
                    }
                }
                epic.setStatus(Status.DONE);
            }
            return newSubtask;
        }
        else { // if  (task.getTYPE() == Type.TASK)
            tasksMap.put(task.getId(), task);
            return task;
        }
    }

    public List<Subtask> getSubtasksByEpic(Epic epic) {
        List<Subtask> subtasks = new ArrayList<>();
        for (int key : epic.getListOfSubtasks()) {
            subtasks.add((Subtask) tasksMap.get(key));
        }
        return subtasks;
    }
}
