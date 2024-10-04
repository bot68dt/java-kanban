package com.yandex.taskmanager.test;

import com.yandex.taskmanager.constant.Status;
import com.yandex.taskmanager.model.Task;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskTest {

    @Test
    void testEquals() {
        Task task1 = new Task("Test addNewTask", "Test addNewTask description", Status.NEW);
        Task task2 = new Task("Test addNewTask", "Test addNewTask description", Status.NEW);
        assertEquals(task1.getId(), task2.getId(), "Задачи не совпадают.");
        assertTrue(task1.equals(task2),"Задачи не совпадают.");
    }
}