package com.yandex.test.model;

import com.yandex.taskmanager.constant.Status;
import com.yandex.taskmanager.model.SubTask;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubTaskTest {

    @Test
    void testEquals() {
        SubTask task1 = new SubTask("Test addNewTask", "Test addNewTask description", Status.NEW);
        SubTask task2 = new SubTask("Test addNewTask", "Test addNewTask description", Status.NEW);
        assertEquals(task1.getId(), task2.getId(), "Задачи не совпадают.");
        assertEquals(task1, task2, "Задачи не совпадают.");
    }
}