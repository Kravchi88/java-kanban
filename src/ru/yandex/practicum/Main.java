package ru.yandex.practicum;

import ru.yandex.practicum.manager.TaskManager;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        Task task1 = taskManager.createTask(new Task());
        taskManager.createTask(new Task());
        Epic epic1 = (Epic) taskManager.createTask(new Epic());
        Subtask subtask1 = (Subtask) taskManager.createTask(new Subtask());
        Subtask subtask2 = (Subtask) taskManager.createTask(new Subtask());
        Epic epic2 = (Epic) taskManager.createTask(new Epic());
        Subtask subtask3 = (Subtask) taskManager.createTask(new Subtask());

        System.out.println(taskManager.getAllTasks());
        System.out.println();

        task1 = taskManager.updateTask(new Task());

        taskManager.updateTask(new Epic());

        subtask1 = (Subtask) taskManager.updateTask(new Subtask());

        subtask3 = (Subtask) taskManager.updateTask(new Subtask());

        System.out.println(taskManager.getAllTasks());
        System.out.println();

        taskManager.updateTask(new Task());

        taskManager.updateTask(new Subtask());

        subtask2 = (Subtask) taskManager.updateTask(new Subtask());

        taskManager.updateTask(new Subtask());

        System.out.println(taskManager.getAllTasks());
        System.out.println();

        taskManager.updateTask(new Subtask());

        System.out.println(taskManager.getAllTasks());
        System.out.println();

        System.out.println(taskManager.getSubtasksByEpic(epic1));
        System.out.println();

        taskManager.removeTaskById(epic1.getId());
        taskManager.removeTaskById(subtask3.getId());

        System.out.println(taskManager.getAllTasks());
    }
}
