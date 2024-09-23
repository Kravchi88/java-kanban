package ru.yandex.practicum.manager;

import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;
import ru.yandex.practicum.tasks.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected final Set<Task> prioritizedTasks = new TreeSet<>(comparator());

    private final HistoryManager historyManager = Managers.getDefaultHistory();

    protected int idSequence = 0;

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public Set<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    private Comparator<Task> comparator() {
        return (o1, o2) -> {
            if (o1.getStartTime() == null && o2.getStartTime() == null) {
                return 0;
            } else if (o1.getStartTime() == null) {
                return -1;
            } else if (o2.getStartTime() == null) {
                return 1;
            }

            int timeComparison = o1.getStartTime().compareTo(o2.getStartTime());
            if (timeComparison != 0) {
                return timeComparison;
            }

            return Integer.compare(o1.getId(), o2.getId());
        };
    }

    protected void updateTree(Task task, boolean isDelete) {
        if (isDelete || task.getStartTime() == null) {
            prioritizedTasks.remove(task);
            return;
        }

        Optional<Task> existingTaskOpt = prioritizedTasks.stream()
                .filter(existingTask -> existingTask.getId() == task.getId())
                .findFirst();

        existingTaskOpt.ifPresent(prioritizedTasks::remove);

        prioritizedTasks.add(task);
    }

    private boolean hasTimeConflict(Task t1) {
        if (t1.getStartTime() == null || t1.getDuration() == null) {
            return false;
        }
        return getPrioritizedTasks().stream()
                .filter(t2 -> t1.getId() != t2.getId())
                .filter(t2 -> !(t1 instanceof Subtask && t2 instanceof Epic && ((Subtask) t1).getEpicId() == t2.getId()))
                .anyMatch(t2 -> t1.getEndTime().isAfter(t2.getStartTime()) && t1.getStartTime().isBefore(t2.getEndTime()));
    }

    @Override
    public Task createTask(Task task) {
        if (hasTimeConflict(task)) {
            return null;
        }
        task.setId(++idSequence);
        tasks.put(idSequence, task);
        updateTree(task, false);

        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        epic.setId(++idSequence);
        epics.put(idSequence, epic);
        setEpicTimeFields(epic);
        updateTree(epic, false);

        return epic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        if (hasTimeConflict(subtask)) {
            return null;
        }
        subtask.setId(++idSequence);
        subtasks.put(idSequence, subtask);
        updateTree(subtask, false);

        Epic epic = epics.get(subtask.getEpicId());
        epic.getSubtaskIds().add(subtask.getId());
        epic.setStatus(getEpicStatus(epic));
        setEpicTimeFields(epic);
        updateTree(epic, false);

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
        if (tasks.get(id) == null) {
            return;
        }
        updateTree(tasks.get(id), true);
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void removeEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic == null) {
            return;
        }
        for (int subtaskId : epic.getSubtaskIds()) {
            updateTree(subtasks.get(subtaskId), true);
            subtasks.remove(subtaskId);
            historyManager.remove(subtaskId);
        }
        updateTree(epic, true);
        epics.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void removeSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask == null) {
            return;
        }
        Epic epic = epics.get(subtask.getEpicId());
        epic.getSubtaskIds().remove(Integer.valueOf(subtask.getId()));
        epic.setStatus(getEpicStatus(epic));
        updateTree(epic, true);
        setEpicTimeFields(epic);
        if (!epic.getSubtaskIds().isEmpty()) {
            updateTree(epic, false);
        }
        updateTree(subtask, true);
        subtasks.remove(subtask.getId());
        historyManager.remove(id);
    }

    @Override
    public void removeTasks() {
        tasks.keySet().forEach(historyManager::remove);
        tasks.values().forEach(task -> updateTree(task, true));
        tasks.clear();
    }

    @Override
    public void removeEpics() {
        epics.keySet().forEach(historyManager::remove);
        subtasks.keySet().forEach(historyManager::remove);
        epics.values().forEach(epic -> updateTree(epic, true));
        subtasks.values().forEach(subtask -> updateTree(subtask, true));
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void removeSubtasks() {
        subtasks.keySet().forEach(historyManager::remove);
        subtasks.values().forEach(subtask -> updateTree(subtask, true));
        subtasks.clear();
        epics.values().forEach(epic -> {
            epic.getSubtaskIds().clear();
            epic.setStatus(TaskStatus.NEW);
            updateTree(epic, true);
            setEpicTimeFields(epic);
        });
    }

    @Override
    public Task updateTask(Task newTask) {
        if (hasTimeConflict(newTask)) {
            throw new RuntimeException();
        }
        tasks.put(newTask.getId(), newTask);
        updateTree(newTask, false);

        return newTask;
    }

    @Override
    public Epic updateEpic(Epic newEpic) {
        Epic oldEpic = epics.get(newEpic.getId());
        newEpic.setSubtaskIds(oldEpic.getSubtaskIds());
        setEpicTimeFields(newEpic);
        updateTree(newEpic, false);
        epics.put(newEpic.getId(), newEpic);

        return newEpic;
    }

    @Override
    public Subtask updateSubtask(Subtask newSubtask) {
        if (hasTimeConflict(newSubtask)) {
            throw new RuntimeException();
        }
        Subtask oldSubtask = subtasks.get(newSubtask.getId());
        newSubtask.setEpicId(oldSubtask.getEpicId());
        updateTree(newSubtask, false);
        subtasks.put(newSubtask.getId(), newSubtask);

        // change epic status and time
        Epic epic = epics.get(newSubtask.getEpicId());
        epic.setStatus(getEpicStatus(epic));
        setEpicTimeFields(epic);
        updateTree(epic, false);

        return newSubtask;
    }

    @Override
    public List<Subtask> getSubtasksByEpicId(int epicId) {
        Epic epic = epics.get(epicId);
        List<Integer> subtaskIds = epic.getSubtaskIds();
        return subtasks.values().stream()
                .filter(subtask -> subtaskIds.contains(subtask.getId()))
                .collect(Collectors.toList());
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

    private void setEpicTimeFields(Epic epic) {
        List<Subtask> subtaskList = getSubtasksByEpicId(epic.getId());

        if (subtaskList.isEmpty()) {
            epic.setDuration(null);
            epic.setStartTime(null);
            epic.setEndTime(null);
            return;
        }

        epic.setDuration(getEpicDuration(subtaskList));
        epic.setStartTime(getEpicStartTime(subtaskList));
        epic.setEndTime(getEpicEndTime(subtaskList));
    }

    private Duration getEpicDuration(List<Subtask> subtaskList) {
        return subtaskList.stream()
                .map(Subtask::getDuration)
                .filter(Objects::nonNull)
                .reduce(Duration.ZERO, Duration::plus);
    }

    private LocalDateTime getEpicStartTime(List<Subtask> subtaskList) {
        Optional<LocalDateTime> epicStartTimeOptional = subtaskList.stream()
                .map(Subtask::getStartTime)
                .filter(Objects::nonNull)
                .min(Comparator.naturalOrder());

        return epicStartTimeOptional.orElse(null);
    }

    private LocalDateTime getEpicEndTime(List<Subtask> subtaskList) {
        Optional<LocalDateTime> epicEndTimeOptional = subtaskList.stream()
                .map(Subtask::getEndTime)
                .filter(Objects::nonNull)
                .max(Comparator.naturalOrder());

        return epicEndTimeOptional.orElse(null);
    }
}
