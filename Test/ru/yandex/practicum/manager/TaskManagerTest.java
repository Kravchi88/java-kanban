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
    TaskManager taskManager;
    Task task_1; // id = 1
    Epic epic_1; // id = 2
    Subtask subtask_1_1; // id = 3
    Subtask subtask_1_2; // id = 4
    Epic epic_2; // id = 5
    Subtask subtask_2_1; // id = 6

    @BeforeEach
    void beforeEach() {
        // Для каждого теста нужен новый менеджер, поэтому создаётся напрямую, а не через getDefault()
        taskManager = new InMemoryTaskManager();

        // Без конструкторов конечно очень больно:((
        task_1 = new Task();
        task_1.setName("Feed the cat");
        task_1.setDescription("-");
        task_1.setStatus(TaskStatus.NEW);
        taskManager.createTask(task_1);

        epic_1 = new Epic();
        epic_1.setName("Do the cleaning");
        epic_1.setDescription("-");
        epic_1.setSubtaskIds(new ArrayList<>());
        taskManager.createEpic(epic_1);

        subtask_1_1 = new Subtask();
        subtask_1_1.setName("Vacuuming");
        subtask_1_1.setDescription("-");
        subtask_1_1.setStatus(TaskStatus.NEW);
        subtask_1_1.setEpicId(2);
        taskManager.createSubtask(subtask_1_1);

        subtask_1_2 = new Subtask();
        subtask_1_2.setName("Wash the floors");
        subtask_1_2.setDescription("-");
        subtask_1_2.setStatus(TaskStatus.NEW);
        subtask_1_2.setEpicId(2);
        taskManager.createSubtask(subtask_1_2);

        epic_2 = new Epic();
        epic_2.setName("Do the homework");
        epic_2.setDescription("-");
        epic_2.setSubtaskIds(new ArrayList<>());
        taskManager.createEpic(epic_2);

        subtask_2_1 = new Subtask();
        subtask_2_1.setName("Write an essay");
        subtask_2_1.setDescription("-");
        subtask_2_1.setStatus(TaskStatus.NEW);
        subtask_2_1.setEpicId(5);
        taskManager.createSubtask(subtask_2_1);
    }

    @Test
    void shouldCreateTaskAndPutItToCollection() {
        Task expectedTask = new Task();
        expectedTask.setId(7);

        Task actualTask = new Task();
        taskManager.createTask(actualTask);

        assertEquals(expectedTask, actualTask, "The tasks are different");
        assertEquals(expectedTask, taskManager.getTaskById(7), "The task was putted wrong");
    }

    @Test
    void shouldCreateEpicAndPutItToCollection() {
        Epic expectedEpic = new Epic();
        expectedEpic.setId(7);

        Epic actualEpic = new Epic();
        taskManager.createEpic(actualEpic);

        assertEquals(expectedEpic, actualEpic, "The epics are different");
        assertEquals(expectedEpic, taskManager.getEpicById(7), "The epic was putted wrong");
    }

    @Test
    void shouldCreateSubtaskAndUpdateEpic() {
        Subtask expectedSubtask = new Subtask();
        expectedSubtask.setId(7);

        Subtask actualSubtask = new Subtask();
        actualSubtask.setEpicId(5);
        actualSubtask.setStatus(TaskStatus.NEW);
        taskManager.createSubtask(actualSubtask);

        assertEquals(expectedSubtask, actualSubtask, "The subtasks are different");
        assertEquals(expectedSubtask, taskManager.getSubtaskById(7), "The subtask was putted wrong");

        assertEquals(epic_2.getId(), actualSubtask.getEpicId(), "Wrong epic id");
        assertEquals(epic_2.getSubtaskIds().get(1), actualSubtask.getId(), "Subtask wasn't added to list correctly");
        assertEquals(TaskStatus.NEW, epic_2.getStatus(), "Epic status is wrong");
        // В данном случае у эпика статус NEW потому что все сабтаски NEW
    }

    @Test
    void shouldGetCorrectTaskById() {
        assertEquals(task_1, taskManager.getTaskById(1), "The tasks are different");
    }

    @Test
    void shouldGetCorrectEpicById() {
        assertEquals(epic_1, taskManager.getEpicById(2), "The epics are different");
    }

    @Test
    void shouldGetCorrectSubtaskById() {
        assertEquals(subtask_1_1, taskManager.getSubtaskById(3), "The subtasks are different");
    }

    @Test
    void shouldGetCorrectTaskList() {
        List<Task> expectedList = new ArrayList<>();
        expectedList.add(task_1);

        List<Task> actualList = taskManager.getTasks();

        assertEquals(expectedList, actualList, "The lists are different");
        assertEquals(1, actualList.size(), "The size is wrong");
    }

    @Test
    void shouldGetCorrectEpicList() {
        List<Epic> expectedList = new ArrayList<>();
        expectedList.add(epic_1);
        expectedList.add(epic_2);

        List<Epic> actualList = taskManager.getEpics();

        assertEquals(expectedList, actualList, "The lists are different");
        assertEquals(2, actualList.size(), "The size is wrong");
    }

    @Test
    void shouldGetCorrectSubtaskList() {
        List<Subtask> expectedList = new ArrayList<>();
        expectedList.add(subtask_1_1);
        expectedList.add(subtask_1_2);
        expectedList.add(subtask_2_1);

        List<Subtask> actualList = taskManager.getSubtasks();

        assertEquals(expectedList, actualList, "The lists are different");
        assertEquals(3, actualList.size(), "The size is wrong");
    }

    @Test
    void shouldRemoveTaskByIdAndReturnEmptyTaskList() {
        taskManager.removeTaskById(1);

        assertEquals(0, taskManager.getTasks().size(), "The size of tasksList is wrong");
    }

    @Test
    void shouldRemoveEpicByIdAndRemoveItsSubtasks() {
        taskManager.removeEpicById(2);

        assertEquals(1, taskManager.getEpics().size(), "The size of epicsList is wrong");
        assertEquals(1, taskManager.getSubtasks().size(), "The size of subtasksList is wrong");
    }

    @Test
    void shouldRemoveSubtaskByIdAndUpdateEpicStatusIfDemanded() {
        taskManager.removeSubtaskById(3);

        assertEquals(2, taskManager.getSubtasks().size(), "The size of subtasksList is wrong");
        assertFalse(epic_1.getSubtaskIds().contains(3), "The subtask id wasn't removed from list");
        assertEquals(TaskStatus.NEW, epic_1.getStatus(), "The epic status is wrong");
    }

    @Test
    void shouldRemoveAllTasksAndReturnEmptyList() {
        taskManager.removeTasks();

        assertTrue(taskManager.getTasks().isEmpty(), "The task list is not empty");
    }

    @Test
    void shouldRemoveAllEpicsAndReturnEmptyList() {
        taskManager.removeEpics();

        assertTrue(taskManager.getEpics().isEmpty(), "The epic list is not empty");
        assertTrue(taskManager.getSubtasks().isEmpty(), "The subtask list is not empty");
    }

    @Test
    void shouldRemoveAllSubtasksAndReturnEmptyList() {
        taskManager.removeSubtasks();

        assertTrue(taskManager.getSubtasks().isEmpty(), "The subtask list is not empty");
        assertSame(epic_1.getStatus(), TaskStatus.NEW, "The epic status is wrong");
    }

    @Test
    void shouldUpdateTaskName() {
        assertEquals("Feed the cat", taskManager.getTaskById(1).getName()); // Проверка на старое значение

        Task newTask = new Task();
        newTask.setId(1);
        newTask.setName("Feed the dog");

        taskManager.updateTask(newTask);

        assertEquals("Feed the dog", taskManager.getTaskById(1).getName(), "The task was not updated");
    }

    @Test
    void shouldUpdateEpicNameAndKeepItsSubtaskList() {
        Epic newEpic = new Epic();
        newEpic.setId(2);
        newEpic.setName("Clean the room");

        taskManager.updateEpic(newEpic);
        List<Integer> expectedListOfSubtaskIds = List.of(3, 4);

        assertEquals("Clean the room", taskManager.getEpicById(2).getName(), "The epic was not updated");
        assertEquals(expectedListOfSubtaskIds, taskManager.getEpicById(2).getSubtaskIds(), "The list of ids is different");
    }

    @Test
    void shouldUpdateSubtaskAndEpicStatus() {
        assertEquals(TaskStatus.NEW, epic_1.getStatus()); // Проверка на старое значение

        Subtask newSubtask1 = new Subtask();
        Subtask newSubtask2 = new Subtask();

        newSubtask1.setId(3);
        newSubtask2.setId(4);
        newSubtask1.setStatus(TaskStatus.IN_PROGRESS);

        taskManager.updateSubtask(newSubtask1);

        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getSubtaskById(3).getStatus(), "The subtask was not updated");
        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getEpicById(2).getStatus(), "The epic status is wrong");

        newSubtask1.setStatus(TaskStatus.DONE);
        newSubtask2.setStatus(TaskStatus.DONE);

        taskManager.updateSubtask(newSubtask1);
        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getEpicById(2).getStatus(), "The epic status is wrong");
        taskManager.updateSubtask(newSubtask2);

        assertEquals(TaskStatus.DONE, taskManager.getSubtaskById(4).getStatus(), "The subtask was not updated");
        assertEquals(TaskStatus.DONE, taskManager.getEpicById(2).getStatus(), "The epic status is wrong");
    }

    @Test
    void shouldGetCorrectSubtaskListByEpicId() {
        List<Subtask> expectedSubtaskList = List.of(subtask_1_1, subtask_1_2);
        List<Subtask> actualSubtaskList = taskManager.getSubtasksByEpicId(2);

        assertEquals(expectedSubtaskList, actualSubtaskList, "The lists are different");
    }
}