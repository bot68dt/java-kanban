package com.yandex.test.service;

import com.yandex.taskmanager.constant.Status;
import com.yandex.taskmanager.model.Epic;
import com.yandex.taskmanager.model.SubTask;
import com.yandex.taskmanager.model.Task;
import com.yandex.taskmanager.service.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    static InMemoryTaskManager taskManager = new InMemoryTaskManager();
    static Epic task1 = new Epic("Test addNewTask", "Test addNewTask description");
    static Task task2 = new Task("Test addNewTask", "Test addNewTask description", Status.NEW);
    static SubTask task3 = new SubTask("Test addNewTask", "Test addNewTask description", Status.NEW);

    @BeforeAll
    static void beforeAll() {
        taskManager.addEpic(task1);
        taskManager.addTask(task2);
        taskManager.addSubTask(task1.getId(), task3);
    }

    @Test
    void addGetTasks() {
        assertEquals(taskManager.getTaskById(task2.getId()), task2, "Задачи не совпадают.");
        assertEquals(taskManager.getEpicById(task1.getId()), task1, "Задачи не совпадают.");
        assertEquals(taskManager.getSubTaskById(task3.getId()), task3, "Задачи не совпадают.");
    }

    @Test
    void differentId() {
        Task task4 = new Task("Test addNewTask", "Test addNewTask description", Status.NEW);
        assertEquals(task4, task2,"Not the same tasks");
        assertNotEquals(task4.getId(), task2.getId(), "Same tasks");
        assertNotEquals(task4.getId(), taskManager.getTaskById(task2.getId()).getId(), "Same tasks");
        SubTask task5 = new SubTask("Test addNewTask", "Test addNewTask description", Status.NEW);
        assertEquals(task5, task3,"Not the same tasks");
        assertNotEquals(task5.getId(), task3.getId(), "Same tasks");
        assertNotEquals(task5.getId(), taskManager.getSubTaskById(task3.getId()).getId(), "Same tasks");
        Epic task6 = new Epic("Test addNewTask", "Test addNewTask description");
        assertEquals(task6, task1,"Not the same tasks");
        assertNotEquals(task6.getId(), task1.getId(), "Same tasks");
        assertNotEquals(task6.getId(), taskManager.getEpicById(task1.getId()).getId(), "Same tasks");
    }

    @Test
    void allIdsAreUnique() {
        assertEquals(taskManager.getCount() - 1, (taskManager.getEpics().size() + taskManager.getTasks().size() + taskManager.getSubTasks().size()), "Not all id's are unique");
    }

    @Test
    void TasksDontChangeDuringAdd() {

        Task task7 = new Task("Test addNewTask", "Test addNewTask description", Status.NEW);
        SubTask task8 = new SubTask("Test addNewTask", "Test addNewTask description", Status.NEW);
        Epic task9 = new Epic("Test addNewTask", "Test addNewTask description");
        taskManager.addEpic(task9);
        assertEquals(task9.getName(),taskManager.getEpicById(task9.getId()).getName(),"Names aren't equal");
        assertEquals(task9.getDescription(),taskManager.getEpicById(task9.getId()).getDescription(),"Descriptions aren't equal");
        assertEquals(task9.getStatus(),taskManager.getEpicById(task9.getId()).getStatus(),"Statuses aren't equal");
        assertEquals(task9.getId(),taskManager.getEpicById(task9.getId()).getId(),"IDs aren't equal");
        assertEquals(task9.getType(),taskManager.getEpicById(task9.getId()).getType(),"Types aren't equal");
        taskManager.addTask(task7);
        assertEquals(task7.getName(),taskManager.getTaskById(task7.getId()).getName(),"Names aren't equal");
        assertEquals(task7.getDescription(),taskManager.getTaskById(task7.getId()).getDescription(),"Descriptions aren't equal");
        assertEquals(task7.getStatus(),taskManager.getTaskById(task7.getId()).getStatus(),"Statuses aren't equal");
        assertEquals(task7.getId(),taskManager.getTaskById(task7.getId()).getId(),"IDs aren't equal");
        assertEquals(task7.getType(),taskManager.getTaskById(task7.getId()).getType(),"Types aren't equal");
        taskManager.addSubTask(task9.getId(), task8);
        assertEquals(task8.getName(),taskManager.getSubTaskById(task8.getId()).getName(),"Names aren't equal");
        assertEquals(task8.getDescription(),taskManager.getSubTaskById(task8.getId()).getDescription(),"Descriptions aren't equal");
        assertEquals(task8.getStatus(),taskManager.getSubTaskById(task8.getId()).getStatus(),"Statuses aren't equal");
        assertEquals(task8.getId(),taskManager.getSubTaskById(task8.getId()).getId(),"IDs aren't equal");
        assertEquals(task8.getType(),taskManager.getSubTaskById(task8.getId()).getType(),"Types aren't equal");
    }

}