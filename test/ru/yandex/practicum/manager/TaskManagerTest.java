package ru.yandex.practicum.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;
import ru.yandex.practicum.tasks.TaskStatus;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskManagerTest {
    private TaskManager taskManager;

    @BeforeEach
    void beforeEach() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    void createTask_shouldSaveTaskToMemory() {
        Task expectedTask = new Task();
        expectedTask.setId(1);

        Task actualTask = new Task();
        taskManager.createTask(actualTask);

        assertEquals(expectedTask, actualTask, "The tasks are different");
        assertEquals(expectedTask, taskManager.getTaskById(1), "The task was putted wrong");
    }

    @Test
    void createEpic_shouldSaveEpicToMemory() {
        Epic expectedEpic = new Epic();
        expectedEpic.setId(1);

        Epic actualEpic = new Epic();
        taskManager.createEpic(actualEpic);

        assertEquals(expectedEpic, actualEpic, "The epics are different");
        assertEquals(expectedEpic, taskManager.getEpicById(1), "The epic was putted wrong");
    }

    @Test
    void createSubtask_shouldSaveSubtaskToMemoryAndUpdateEpic() {
        Epic epic = setParametersToEpic("Do the cleaning", "-", new ArrayList<>());
        taskManager.createEpic(epic);

        Subtask expectedSubtask = new Subtask();
        expectedSubtask.setId(2);

        Subtask actualSubtask = new Subtask();
        actualSubtask.setEpicId(1);
        actualSubtask.setStatus(TaskStatus.NEW);
        taskManager.createSubtask(actualSubtask);

        assertEquals(expectedSubtask, actualSubtask, "The subtasks are different");
        assertEquals(expectedSubtask, taskManager.getSubtaskById(2), "The subtask was putted wrong");

        assertEquals(epic.getId(), actualSubtask.getEpicId(), "Wrong epic id");
        assertEquals(epic.getSubtaskIds().get(0), actualSubtask.getId(), "Subtask wasn't added to list correctly");
        assertEquals(TaskStatus.NEW, epic.getStatus(), "Epic status is wrong");
        // В данном случае у эпика статус NEW потому что все сабтаски NEW
    }

    @Test
    void getTaskById_shouldReturnTask() {
        Task task = setParametersToTask("Feed the cat", "-", TaskStatus.NEW);
        taskManager.createTask(task);

        assertEquals(task, taskManager.getTaskById(1), "The tasks are different");
    }

    @Test
    void getEpicById_shouldReturnEpic() {
        Epic epic = setParametersToEpic("Do the cleaning", "-", new ArrayList<>());
        taskManager.createEpic(epic);

        assertEquals(epic, taskManager.getEpicById(1), "The epics are different");
    }

    @Test
    void getSubtaskById_shouldReturnSubtask() {
        Epic epic = setParametersToEpic("Do the cleaning", "-", new ArrayList<>());
        taskManager.createEpic(epic);

        Subtask subtask = setParametersToSubtask("Vacuuming", "-", 1, TaskStatus.NEW);
        taskManager.createSubtask(subtask);

        assertEquals(subtask, taskManager.getSubtaskById(2), "The subtasks are different");
    }

    @Test
    void getTasks_shouldReturnTaskList() {
        Task task = setParametersToTask("Feed the cat", "-", TaskStatus.NEW);
        taskManager.createTask(task);

        List<Task> expectedList = new ArrayList<>();
        expectedList.add(task);

        List<Task> actualList = taskManager.getTasks();

        assertEquals(expectedList, actualList, "The lists are different");
        assertEquals(1, actualList.size(), "The size is wrong");
    }

    @Test
    void getEpics_shouldReturnEpicList() {
        Epic epic_1 = setParametersToEpic("Do the cleaning", "-", new ArrayList<>());
        taskManager.createEpic(epic_1);
        Epic epic_2 = setParametersToEpic("Do the homework", "-", new ArrayList<>());
        taskManager.createEpic(epic_2);

        List<Epic> expectedList = new ArrayList<>();
        expectedList.add(epic_1);
        expectedList.add(epic_2);

        List<Epic> actualList = taskManager.getEpics();

        assertEquals(expectedList, actualList, "The lists are different");
        assertEquals(2, actualList.size(), "The size is wrong");
    }

    @Test
    void getSubtasks_shouldReturnSubtaskList() {
        Epic epic = setParametersToEpic("Do the cleaning", "-", new ArrayList<>());
        taskManager.createEpic(epic);

        Subtask subtask1 = setParametersToSubtask("Vacuuming", "-", 1, TaskStatus.NEW);
        taskManager.createSubtask(subtask1);

        Subtask subtask2 = setParametersToSubtask("Wash the floors", "-", 1, TaskStatus.NEW);
        taskManager.createSubtask(subtask2);

        List<Subtask> expectedList = new ArrayList<>();
        expectedList.add(subtask1);
        expectedList.add(subtask2);

        List<Subtask> actualList = taskManager.getSubtasks();

        assertEquals(expectedList, actualList, "The lists are different");
        assertEquals(2, actualList.size(), "The size is wrong");
    }

    @Test
    void removeTaskById_shouldReturnTaskListWithoutThisTask() {
        Task task = setParametersToTask("Feed the cat", "-", TaskStatus.NEW);
        taskManager.createTask(task);

        taskManager.removeTaskById(1);

        assertEquals(0, taskManager.getTasks().size(), "The size of tasksList is wrong");
    }

    @Test
    void removeEpicById_shouldRemoveEpicAndChildrenSubtasks() {
        Epic epic = setParametersToEpic("Do the cleaning", "-", new ArrayList<>());
        taskManager.createEpic(epic);

        Subtask subtask1 = setParametersToSubtask("Vacuuming", "-", 1, TaskStatus.NEW);
        taskManager.createSubtask(subtask1);

        Subtask subtask2 = setParametersToSubtask("Wash the floors", "-", 1, TaskStatus.NEW);
        taskManager.createSubtask(subtask2);

        taskManager.removeEpicById(1);

        assertEquals(0, taskManager.getEpics().size(), "The size of epicsList is wrong");
        assertEquals(0, taskManager.getSubtasks().size(), "The size of subtasksList is wrong");
    }

    @Test
    void removeSubtaskById_shouldRemoveSubtaskAndUpdateEpicStatusIfDemanded() {
        Epic epic = setParametersToEpic("Do the cleaning", "-", new ArrayList<>());
        taskManager.createEpic(epic);

        Subtask subtask1 = setParametersToSubtask("Vacuuming", "-", 1, TaskStatus.NEW);
        taskManager.createSubtask(subtask1);

        Subtask subtask2 = setParametersToSubtask("Wash the floors", "-", 1, TaskStatus.NEW);
        taskManager.createSubtask(subtask2);

        taskManager.removeSubtaskById(3);

        assertEquals(1, taskManager.getSubtasks().size(), "The size of subtasksList is wrong");
        assertFalse(epic.getSubtaskIds().contains(3), "The subtask id wasn't removed from list");
        assertEquals(TaskStatus.NEW, epic.getStatus(), "The epic status is wrong");
    }

    @Test
    void removeTasks_shouldReturnEmptyList() {
        Task task = setParametersToTask("Feed the cat", "-", TaskStatus.NEW);
        taskManager.createTask(task);

        taskManager.removeTasks();

        assertTrue(taskManager.getTasks().isEmpty(), "The task list is not empty");
    }

    @Test
    void removeEpics_shouldReturnEmptyEpicListAndSubtaskList() {
        Epic epic = setParametersToEpic("Do the cleaning", "-", new ArrayList<>());
        taskManager.createEpic(epic);

        Subtask subtask1 = setParametersToSubtask("Vacuuming", "-", 1, TaskStatus.NEW);
        taskManager.createSubtask(subtask1);

        Subtask subtask2 = setParametersToSubtask("Wash the floors", "-", 1, TaskStatus.NEW);
        taskManager.createSubtask(subtask2);

        taskManager.removeEpics();

        assertTrue(taskManager.getEpics().isEmpty(), "The epic list is not empty");
        assertTrue(taskManager.getSubtasks().isEmpty(), "The subtask list is not empty");
    }

    @Test
    void removeSubtasks_shouldReturnEmptySubtaskListAndSetStatusNEWToAllEpics() {
        Epic epic = setParametersToEpic("Do the cleaning", "-", new ArrayList<>());
        taskManager.createEpic(epic);

        Subtask subtask1 = setParametersToSubtask("Vacuuming", "-", 1, TaskStatus.NEW);
        taskManager.createSubtask(subtask1);

        Subtask subtask2 = setParametersToSubtask("Wash the floors", "-", 1, TaskStatus.NEW);
        taskManager.createSubtask(subtask2);

        taskManager.removeSubtasks();

        assertTrue(taskManager.getSubtasks().isEmpty(), "The subtask list is not empty");
        assertSame(epic.getStatus(), TaskStatus.NEW, "The epic status is wrong");
    }

    @Test
    void updateTask_shouldReturnTaskWithNewParameters() {
        Task task = setParametersToTask("Feed the cat", "-", TaskStatus.NEW);
        taskManager.createTask(task);

        assertEquals("Feed the cat", taskManager.getTaskById(1).getName()); // Проверка на старое значение

        Task newTask = new Task();
        newTask.setId(1);
        newTask.setName("Feed the dog");

        taskManager.updateTask(newTask);

        assertEquals("Feed the dog", taskManager.getTaskById(1).getName(), "The task was not updated");
    }

    @Test
    void updateEpic_shouldReturnEpicWithNewParametersAndKeepItsSubtaskList() {
        Epic epic = setParametersToEpic("Do the cleaning", "-", new ArrayList<>());
        taskManager.createEpic(epic);

        Subtask subtask1 = setParametersToSubtask("Vacuuming", "-", 1, TaskStatus.NEW);
        taskManager.createSubtask(subtask1);

        Subtask subtask2 = setParametersToSubtask("Wash the floors", "-", 1, TaskStatus.NEW);
        taskManager.createSubtask(subtask2);

        Epic newEpic = new Epic();
        newEpic.setId(1);
        newEpic.setName("Clean the room");
        taskManager.updateEpic(newEpic);

        List<Integer> expectedListOfSubtaskIds = List.of(2, 3);

        assertEquals("Clean the room", taskManager.getEpicById(1).getName(), "The epic was not updated");
        assertEquals(expectedListOfSubtaskIds, taskManager.getEpicById(1).getSubtaskIds(), "The list of ids is different");
    }

    @Test
    void updateSubtask_shouldUpdateEpicStatusToDoneIfAllEpicSubtasksIsDone() {
        Epic epic = setParametersToEpic("Do the cleaning", "-", new ArrayList<>());
        taskManager.createEpic(epic);

        Subtask subtask1 = setParametersToSubtask("Vacuuming", "-", 1, TaskStatus.NEW);
        taskManager.createSubtask(subtask1);

        Subtask subtask2 = setParametersToSubtask("Wash the floors", "-", 1, TaskStatus.NEW);
        taskManager.createSubtask(subtask2);

        assertEquals(TaskStatus.NEW, epic.getStatus()); // Проверка на старое значение

        Subtask newSubtask1 = new Subtask();
        Subtask newSubtask2 = new Subtask();

        newSubtask1.setId(2);
        newSubtask2.setId(3);
        newSubtask1.setStatus(TaskStatus.IN_PROGRESS);

        taskManager.updateSubtask(newSubtask1);

        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getSubtaskById(2).getStatus(), "The subtask was not updated");
        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getEpicById(1).getStatus(), "The epic status is wrong");

        newSubtask1.setStatus(TaskStatus.DONE);
        newSubtask2.setStatus(TaskStatus.DONE);

        taskManager.updateSubtask(newSubtask1);
        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getEpicById(1).getStatus(), "The epic status is wrong");
        taskManager.updateSubtask(newSubtask2);

        assertEquals(TaskStatus.DONE, taskManager.getSubtaskById(3).getStatus(), "The subtask was not updated");
        assertEquals(TaskStatus.DONE, taskManager.getEpicById(1).getStatus(), "The epic status is wrong");
    }

    @Test
    void getSubtasksByEpicId_shouldGetCorrectSubtaskList() {
        Epic epic = setParametersToEpic("Do the cleaning", "-", new ArrayList<>());
        taskManager.createEpic(epic);

        Subtask subtask1 = setParametersToSubtask("Vacuuming", "-", 1, TaskStatus.NEW);
        taskManager.createSubtask(subtask1);

        Subtask subtask2 = setParametersToSubtask("Wash the floors", "-", 1, TaskStatus.NEW);
        taskManager.createSubtask(subtask2);

        List<Subtask> expectedSubtaskList = List.of(subtask1, subtask2);
        List<Subtask> actualSubtaskList = taskManager.getSubtasksByEpicId(1);

        assertEquals(expectedSubtaskList, actualSubtaskList, "The lists are different");
    }

    Task setParametersToTask(String name, String description, TaskStatus status) {
        Task task = new Task();
        task.setName(name);
        task.setDescription(description);
        task.setStatus(status);
        return task;
    }

    Epic setParametersToEpic(String name, String description, List<Integer> subtaskIds) {
        Epic epic = new Epic();
        epic.setName(name);
        epic.setDescription(description);
        epic.setSubtaskIds(subtaskIds);
        return epic;
    }

    Subtask setParametersToSubtask(String name, String description, int epicId, TaskStatus status) {
        Subtask subtask = new Subtask();
        subtask.setName(name);
        subtask.setDescription(description);
        subtask.setEpicId(epicId);
        subtask.setStatus(status);
        return subtask;
    }
}