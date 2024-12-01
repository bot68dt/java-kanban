import com.yandex.taskmanager.constant.Status;
import com.yandex.taskmanager.exceptions.TimeCrossingException;
import com.yandex.taskmanager.interfaces.HistoryManager;
import com.yandex.taskmanager.interfaces.TaskManager;
import com.yandex.taskmanager.model.Task;
import com.yandex.taskmanager.service.Managers;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class mainTestFromFile {

    TaskManager taskManager = Managers.loadFileManager("testFile.CSV", "testFileSortedByTime.CSV");
    HistoryManager historyManager = Managers.getDefaultHistory();
    Task task1 = new Task("Test addNewTask", "Test addNewTask description", Status.NEW, 1440, "01.11.24 12:15");
    Task task2 = new Task("Test addNewTask", "Test addNewTask description", Status.DONE, 1440, "03.11.24 12:15");

    @Test
    void testException() {
        TimeCrossingException e = assertThrows(TimeCrossingException.class, () -> {
            Task task3 = new Task("Test addNewTask", "Test addNewTask description", Status.IN_PROGRESS, 1440, "01.11.24 14:15");
            throw new TimeCrossingException("an exception");
        }, "exception");
        assertTrue(e.getMessage().contains("an exception"));
    }

    @Test
    void historyManagerSavesPreviousVersions() {
        taskManager.addTask(task1);
        historyManager.add(taskManager.getTaskById(task1.getId()));
        taskManager.updateTask(task1.getId(), task2);
        historyManager.add(taskManager.getTaskById(task2.getId()));
        assertEquals(historyManager.getHistory().get(0), task1, "Tasks aren't equal");
        assertEquals(historyManager.getHistory().get(1), task2, "Tasks aren't equal");
    }

    @Test
    void historyManagerUsesRemove() {
        taskManager.addTask(task1);
        historyManager.add(taskManager.getTaskById(task1.getId()));
        historyManager.remove(task1.getId());
        assertTrue(historyManager.getHistory().isEmpty(), "Node wasn't deleted");
    }
}
