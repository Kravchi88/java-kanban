package ru.yandex.practicum.manager;

import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;
import ru.yandex.practicum.tasks.TaskStatus;
import ru.yandex.practicum.tasks.TaskType;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    private final Path filePath;

    public FileBackedTaskManager(String filePath) {
        this.filePath = Paths.get(filePath);
    }

    private void save() {
        try (FileWriter writer = new FileWriter(filePath.toFile())) {
            writer.write("id,type,name,status,description,epic\n");
            for (Task task : tasks.values()) {
                writer.write(toString(task) + "\n");
            }
            for (Epic epic : epics.values()) {
                writer.write(toString(epic) + "\n");
            }
            for (Subtask subtask : subtasks.values()) {
                writer.write(toString(subtask) + "\n");
            }
        } catch (IOException e) {
            System.out.println("Ошибка при записи в файл");
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file.getPath());
        int assignableId = 0;
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            bufferedReader.readLine();
            while (bufferedReader.ready()) {
                String data = bufferedReader.readLine();
                Task task = manager.fromString(data);
                assignableId = Math.max(assignableId, task.getId());
                switch (task.getType()) {
                    case TASK -> manager.tasks.put(task.getId(), task);
                    case EPIC -> {
                        Epic epic = (Epic) task;
                        manager.epics.put(epic.getId(), epic);
                    }
                    case SUBTASK -> {
                        Subtask subtask = (Subtask) task;
                        manager.subtasks.put(subtask.getId(), subtask);
                        manager.epics.get(subtask.getEpicId()).getSubtaskIds().add(subtask.getId());
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка при чтении из файла");
        } finally {
            manager.idSequence = assignableId;
        }
        return manager;
    }

    private String toString(Task task) {
        String[] taskData;
        if (task.getType() == TaskType.SUBTASK) {
            Subtask subtask = (Subtask) task;
            taskData = new String[]{String.valueOf(subtask.getId()),
                    String.valueOf(subtask.getType()), subtask.getName(),
                    String.valueOf(subtask.getStatus()), subtask.getDescription(), String.valueOf(subtask.getEpicId())};
        } else {
            taskData = new String[]{String.valueOf(task.getId()), String.valueOf(task.getType()),
                    task.getName(), String.valueOf(task.getStatus()), task.getDescription()};
        }
        return String.join(",", taskData);
    }

    private Task fromString(String value) {
        String[] taskData = value.split(",");
        switch (taskData[1]) {
            case "TASK" -> {
                Task task = new Task();
                task.setId(Integer.parseInt(taskData[0]));
                task.setName(taskData[2]);
                switch (taskData[3]) {
                    case "NEW" -> task.setStatus(TaskStatus.NEW);
                    case "IN_PROGRESS" -> task.setStatus(TaskStatus.IN_PROGRESS);
                    case "DONE" -> task.setStatus(TaskStatus.DONE);
                }
                task.setDescription(taskData[4]);
                return task;
            }
            case "EPIC" -> {
                Epic epic = new Epic();
                epic.setId(Integer.parseInt(taskData[0]));
                epic.setName(taskData[2]);
                switch (taskData[3]) {
                    case "NEW" -> epic.setStatus(TaskStatus.NEW);
                    case "IN_PROGRESS" -> epic.setStatus(TaskStatus.IN_PROGRESS);
                    case "DONE" -> epic.setStatus(TaskStatus.DONE);
                }
                epic.setDescription(taskData[4]);
                return epic;
            }
            case "SUBTASK" -> {
                Subtask subtask = new Subtask();
                subtask.setId(Integer.parseInt(taskData[0]));
                subtask.setName(taskData[2]);
                switch (taskData[3]) {
                    case "NEW" -> subtask.setStatus(TaskStatus.NEW);
                    case "IN_PROGRESS" -> subtask.setStatus(TaskStatus.IN_PROGRESS);
                    case "DONE" -> subtask.setStatus(TaskStatus.DONE);
                }
                subtask.setDescription(taskData[4]);
                subtask.setEpicId(Integer.parseInt(taskData[5]));
                return subtask;
            }
            default -> {
                return null;
            }
        }
    }

    @Override
    public List<Task> getHistory() {
        List<Task> history = super.getHistory();
        save();
        return history;
    }

    @Override
    public Task createTask(Task task) {
        Task createdTask = super.createTask(task);
        save();
        return createdTask;
    }

    @Override
    public Epic createEpic(Epic epic) {
        Epic createdEpic = super.createEpic(epic);
        save();
        return createdEpic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        Subtask createdSubtask = super.createSubtask(subtask);
        save();
        return createdSubtask;
    }

    @Override
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = super.getEpicById(id);
        save();
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = super.getSubtaskById(id);
        save();
        return subtask;
    }

    @Override
    public List<Task> getTasks() {
        List<Task> tasks = super.getTasks();
        save();
        return tasks;
    }

    @Override
    public List<Epic> getEpics() {
        List<Epic> epics = super.getEpics();
        save();
        return epics;
    }

    @Override
    public List<Subtask> getSubtasks() {
        List<Subtask> subtasks = super.getSubtasks();
        save();
        return subtasks;
    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public void removeEpicById(int id) {
        super.removeEpicById(id);
        save();
    }

    @Override
    public void removeSubtaskById(int id) {
        super.removeSubtaskById(id);
        save();
    }

    @Override
    public void removeTasks() {
        super.removeTasks();
        save();
    }

    @Override
    public void removeEpics() {
        super.removeEpics();
        save();
    }

    @Override
    public void removeSubtasks() {
        super.removeSubtasks();
        save();
    }

    @Override
    public Task updateTask(Task newTask) {
        Task task = super.updateTask(newTask);
        save();
        return task;
    }

    @Override
    public Epic updateEpic(Epic newEpic) {
        Epic epic = super.updateEpic(newEpic);
        save();
        return epic;
    }

    @Override
    public Subtask updateSubtask(Subtask newSubtask) {
        Subtask subtask = super.updateSubtask(newSubtask);
        save();
        return subtask;
    }

    @Override
    public List<Subtask> getSubtasksByEpicId(int epicId) {
        List<Subtask> subtasks = super.getSubtasksByEpicId(epicId);
        save();
        return subtasks;
    }
}
