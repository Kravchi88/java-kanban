package ru.yandex.practicum;

import ru.yandex.practicum.manager.FileBackedTaskManager;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;
import ru.yandex.practicum.tasks.TaskStatus;

import java.io.File;

public class Main {
    public static void main(String[] args) {

        File file = new File("resources/data.csv");
        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(file);

        Task task = new Task();
        task.setName("task2");
        task.setStatus(TaskStatus.NEW);
        task.setDescription("Base task description");
        manager.createTask(task);

        Epic epic = new Epic();
        epic.setName("epic2");
        epic.setDescription("Base epic description");
        manager.createEpic(epic);

        Subtask subtask1 = new Subtask();
        subtask1.setName("subtask2");
        subtask1.setDescription("Base subtask description");
        subtask1.setStatus(TaskStatus.IN_PROGRESS);
        subtask1.setEpicId(2);
        manager.createSubtask(subtask1);

    }
}
