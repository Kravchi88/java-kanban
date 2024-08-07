package ru.yandex.practicum.manager;

import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;
import ru.yandex.practicum.tasks.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();

    private final HistoryManager historyManager = Managers.getDefaultHistory();

    protected int idSequence = 0;

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public Task createTask(Task task) {
        task.setId(++idSequence);
        tasks.put(idSequence, task);

        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        epic.setId(++idSequence);
        epics.put(idSequence, epic);

        return epic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        subtask.setId(++idSequence);
        subtasks.put(idSequence, subtask);

        Epic epic = epics.get(subtask.getEpicId());
        epic.getSubtaskIds().add(subtask.getId());
        epic.setStatus(getEpicStatus(epic));

        return subtask;
    }

    @Override
    public Task getTaskById(int id) {
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        historyManager.add(subtasks.get(id));
        return subtasks.get(id);
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void removeTaskById(int id) {
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void removeEpicById(int id) {
        Epic epic = epics.get(id);
        for (int subtaskId : epic.getSubtaskIds()) {
            subtasks.remove(subtaskId);
            historyManager.remove(subtaskId);
        }
        epics.remove(epic.getId());
        historyManager.remove(id);
    }

    @Override
    public void removeSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        Epic epic = epics.get(subtask.getEpicId());
        epic.getSubtaskIds().remove(Integer.valueOf(subtask.getId()));
        epic.setStatus(getEpicStatus(epic));
        subtasks.remove(subtask.getId());
        historyManager.remove(id);
    }

    @Override
    public void removeTasks() {
        tasks.keySet().forEach(historyManager::remove);
        tasks.clear();
    }

    @Override
    public void removeEpics() {
        epics.keySet().forEach(historyManager::remove);
        subtasks.keySet().forEach(historyManager::remove);
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void removeSubtasks() {
        subtasks.keySet().forEach(historyManager::remove);
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtaskIds().clear();
            epic.setStatus(TaskStatus.NEW);
        }
    }

    @Override
    public Task updateTask(Task newTask) {
        tasks.put(newTask.getId(), newTask);

        return newTask;
    }

    @Override
    public Epic updateEpic(Epic newEpic) {
        Epic oldEpic = epics.get(newEpic.getId());
        newEpic.setSubtaskIds(oldEpic.getSubtaskIds());
        epics.put(newEpic.getId(), newEpic);

        return newEpic;
    }

    @Override
    public Subtask updateSubtask(Subtask newSubtask) {
        Subtask oldSubtask = subtasks.get(newSubtask.getId());
        newSubtask.setEpicId(oldSubtask.getEpicId());
        subtasks.put(newSubtask.getId(), newSubtask);

        // change epic status
        Epic epic = epics.get(newSubtask.getEpicId());
        epic.setStatus(getEpicStatus(epic));

        return newSubtask;
    }

    @Override
    public List<Subtask> getSubtasksByEpicId(int epicId) {
        List<Subtask> epicSubtasks = new ArrayList<>();
        Epic epic = epics.get(epicId);
        for (int subtaskId : epic.getSubtaskIds()) {
            epicSubtasks.add(subtasks.get(subtaskId));
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
