/*package ru.yandex.practicum;

import ru.yandex.practicum.manager.FileBackedTaskManager;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;
import ru.yandex.practicum.tasks.TaskStatus;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Set;

public class Main {
    public static void main(String[] args) {

        File file = new File("resources/data.csv");
        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(file);


        Task task = new Task();
        task.setName("task");
        task.setStatus(TaskStatus.NEW);
        task.setDescription("Base task description");
        task.setDuration(Duration.ofMinutes(30));
        task.setStartTime(LocalDateTime.of(2024, 9, 1, 10, 0));
        manager.createTask(task);

        Epic epic = new Epic();
        epic.setName("epic");
        epic.setDescription("Base epic description");
        manager.createEpic(epic);

        Subtask subtask1 = new Subtask();
        subtask1.setName("subtask1");
        subtask1.setDescription("Base subtask description");
        subtask1.setStatus(TaskStatus.IN_PROGRESS);
        subtask1.setEpicId(2);
        subtask1.setDuration(Duration.ofMinutes(15));
        subtask1.setStartTime(LocalDateTime.of(2024, 8, 1, 12, 30));
        manager.createSubtask(subtask1);

        Subtask subtask2 = new Subtask();
        subtask2.setName("subtask2");
        subtask2.setDescription("Base subtask description");
        subtask2.setStatus(TaskStatus.IN_PROGRESS);
        subtask2.setEpicId(2);
        subtask2.setDuration(Duration.ofMinutes(45));
        subtask2.setStartTime(LocalDateTime.of(2024, 7, 1, 14, 0));
        manager.createSubtask(subtask2);

        Subtask subtask3 = new Subtask();
        subtask3.setName("subtask3");
        subtask3.setDescription("Base subtask description");
        subtask3.setStatus(TaskStatus.IN_PROGRESS);
        subtask3.setEpicId(2);
        subtask3.setDuration(Duration.ofMinutes(5));
        subtask3.setStartTime(LocalDateTime.of(2024, 9, 1, 10, 15));
        manager.createSubtask(subtask3);

        Set<Task> set = manager.getPrioritizedTasks();

        for (Task t : set) {
            System.out.println(t);
        }
    }
}*/
