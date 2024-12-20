import com.yandex.taskmanager.constant.Status;
import com.yandex.taskmanager.interfaces.HistoryManager;
import com.yandex.taskmanager.interfaces.TaskManager;
import com.yandex.taskmanager.model.Task;
import com.yandex.taskmanager.service.Managers;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MainTest {

    TaskManager taskManager = Managers.getFileManager();
    HistoryManager historyManager = Managers.getDefaultHistory();
    Task task1 = new Task("Test addNewTask", "Test addNewTask description", Status.NEW, 1440, "01.11.24 12:15");
    Task task2 = new Task("Test addNewTask", "Test addNewTask description", Status.DONE, 1440, "03.11.24 12:15");

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
        assertEquals(historyManager.getHistory().isEmpty(), true, "Node wasn't deleted");
    }

    @Test
    void taskFileExists() {
        final String HOME = System.getProperty("user.home");
        assertTrue(Files.exists(Paths.get(HOME, "Saves", "testFile.CSV")));
    }
}
