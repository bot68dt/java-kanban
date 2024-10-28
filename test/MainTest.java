import com.yandex.taskmanager.constant.Status;
import com.yandex.taskmanager.interfaces.HistoryManager;
import com.yandex.taskmanager.interfaces.TaskManager;
import com.yandex.taskmanager.model.Task;
import com.yandex.taskmanager.service.Managers;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MainTest {

    TaskManager taskManager = Managers.getDefault();
    HistoryManager historyManager = Managers.getDefaultHistory();
    Task task1 = new Task("Test addNewTask", "Test addNewTask description", Status.NEW);
    Task task2 = new Task("Test addNewTask", "Test addNewTask description", Status.DONE);

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
}
