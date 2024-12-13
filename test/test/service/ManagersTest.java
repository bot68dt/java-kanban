package test.service;

import com.yandex.taskmanager.service.Managers;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ManagersTest {

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
        assertNotNull(Managers.loadFileManager("testFile.CSV", "testFileSortedByTime.CSV"), "InMemoryHistoryManager is not initialized");
    }
}
