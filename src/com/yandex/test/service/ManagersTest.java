package com.yandex.test.service;

import com.yandex.taskmanager.service.Managers;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ManagersTest {
    static Managers managers = new Managers();

    @Test
    void getDefault() {
        assertNotNull(Managers.getDefault(), "InMemoryTaskManager is not initialized");
    }

    @Test
    void getDefaultHistory() {
        assertNotNull(Managers.getDefaultHistory(), "InMemoryHistoryManager is not initialized");
    }
}
