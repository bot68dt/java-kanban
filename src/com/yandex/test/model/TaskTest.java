package com.yandex.test.model;

import com.yandex.taskmanager.constant.Status;
import com.yandex.taskmanager.model.Task;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskTest {

    @Test
    void testEquals() {
        Task task1 = new Task("Test addNewTask", "Test addNewTask description", Status.NEW);
        Task task2 = new Task("Test addNewTask", "Test addNewTask description", Status.NEW);
        assertEquals(task1.getId(), task2.getId(), "Задачи не совпадают.");
        assertEquals(task1, task2, "Задачи не совпадают.");
    }
}