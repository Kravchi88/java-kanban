package ru.yandex.practicum;

import ru.yandex.practicum.manager.TaskManager;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        taskManager.createTask(new Task());
        taskManager.createTask(new Task());
        taskManager.createEpic(new Epic());
        taskManager.createSubtask(new Subtask());
        taskManager.createSubtask(new Subtask());
        taskManager.createEpic(new Epic());
        taskManager.createSubtask(new Subtask());

        System.out.println(taskManager.getAllTasks());

    }
}
