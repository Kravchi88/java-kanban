package ru.yandex.practicum;

import ru.yandex.practicum.manager.Manager;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;
import ru.yandex.practicum.tasks.specifications.Status;

public class Main {

    public static void main(String[] args) {
        Manager manager = new Manager();

        Task task1 = manager.createTask(new Task("New task 1", "Default description"));
        manager.createTask(new Task("New task 2", "Default description"));
        Epic epic1 = (Epic) manager.createTask(new Epic("New epic 1", "Default description"));
        Subtask subtask1 = (Subtask) manager.createTask
                (new Subtask("New subtask 1", "Default description", epic1.getId()));
        Subtask subtask2 = (Subtask) manager.createTask
                (new Subtask("New subtask 2", "Default description", epic1.getId()));
        Epic epic2 = (Epic) manager.createTask(new Epic("New epic 2", "Default description"));
        Subtask subtask3 = (Subtask) manager.createTask
                (new Subtask("New subtask 1", "Default description", epic2.getId()));

        System.out.println(manager.getAllTasks());
        System.out.println();

        task1 = manager.updateTask
                (new Task(task1.getId(), "New name", "New description", Status.IN_PROGRESS));

        manager.updateTask(new Epic(epic1.getId(), "New name", "New description"));

        subtask1 = (Subtask) manager.updateTask
                (new Subtask(subtask1.getId(), "New name", "New description", Status.IN_PROGRESS));

        subtask3 = (Subtask) manager.updateTask
                (new Subtask(subtask3.getId(), "New name", "New description", Status.IN_PROGRESS));


        System.out.println(manager.getAllTasks());
        System.out.println();

        manager.updateTask(new Task(task1.getId(), "New name", "New description", Status.DONE));

        manager.updateTask(new Subtask(subtask1.getId(), "New name", "New description", Status.DONE));

        subtask2 = (Subtask) manager.updateTask
                (new Subtask(subtask2.getId(), "New name", "New description", Status.IN_PROGRESS));

        manager.updateTask(new Subtask(subtask3.getId(), "New name", "New description", Status.DONE));

        System.out.println(manager.getAllTasks());
        System.out.println();

        manager.updateTask(new Subtask(subtask2.getId(), "New name", "New description", Status.DONE));

        System.out.println(manager.getAllTasks());
        System.out.println();

        System.out.println(manager.getSubtasksByEpic(epic1));
        System.out.println();

        manager.removeTaskById(epic1.getId());
        manager.removeTaskById(subtask3.getId());

        System.out.println(manager.getAllTasks());
    }
}
