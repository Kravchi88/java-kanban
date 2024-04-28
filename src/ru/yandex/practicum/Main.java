package ru.yandex.practicum;

import ru.yandex.practicum.manager.Managers;
import ru.yandex.practicum.manager.TaskManager;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;

public class Main {
    public static void main(String[] args) {

        TaskManager taskManager = Managers.getDefault();

        Task task1 = taskManager.createTask(new Task());
        Task task2 = taskManager.createTask(new Task());
        Epic epic1 = taskManager.createEpic(new Epic());
        Subtask subtask1 = new Subtask();
        subtask1.setEpicId(3);
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask();
        subtask2.setEpicId(3);
        taskManager.createSubtask(subtask2);
        Subtask subtask3 = new Subtask();
        subtask3.setEpicId(3);
        taskManager.createSubtask(subtask3);
        Epic epic2 = taskManager.createEpic(new Epic());

        taskManager.getTaskById(1);
        taskManager.getTaskById(1);
        taskManager.getEpicById(3);
        taskManager.getTaskById(1);
        taskManager.getTaskById(2);
        taskManager.getSubtaskById(4);
        taskManager.getSubtaskById(5);
        taskManager.getSubtaskById(6);


        System.out.println(taskManager.getHistoryManager().getHistory());

        taskManager.removeTaskById(1);
        taskManager.removeEpicById(3);

        System.out.println(taskManager.getHistoryManager().getHistory());
    }
}
