package com.yandex.taskmanager.test;

import com.yandex.taskmanager.model.Epic;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    @Test
    void testEquals() {
        Epic task1 = new Epic("Test addNewTask", "Test addNewTask description");
        Epic task2 = new Epic("Test addNewTask", "Test addNewTask description");
        assertEquals(task1.getId(), task2.getId(), "Задачи не совпадают.");
        assertTrue(task1.equals(task2),"Задачи не совпадают.");
    }
}