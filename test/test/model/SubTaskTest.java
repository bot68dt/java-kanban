package test.model;

import com.yandex.taskmanager.constant.Status;
import com.yandex.taskmanager.model.SubTask;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubTaskTest {

    @Test
    void testEquals() {
        SubTask task1 = new SubTask("Test addNewTask", "Test addNewTask description", Status.NEW, 1440, "01.11.24 12:15");
        SubTask task2 = new SubTask("Test addNewTask", "Test addNewTask description", Status.NEW, 1440, "03.11.24 12:15");
        assertEquals(task1.getId(), task2.getId(), "Задачи не совпадают.");
        assertEquals(task1, task2, "Задачи не совпадают.");
    }
}