package test.service;

import com.yandex.taskmanager.service.Managers;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

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

    @Test
    void getFileManager() {
        assertNotNull(Managers.getFileManager(), "InMemoryHistoryManager is not initialized");
    }

    @Test
    void loadFileManager() {
        assertNotNull(Managers.loadFileManager("testFile.CSV"), "InMemoryHistoryManager is not initialized");
    }
}
