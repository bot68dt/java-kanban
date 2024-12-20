package test.model;

import com.yandex.taskmanager.constant.Status;
import com.yandex.taskmanager.model.Task;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskTest {

    @Test
    void testEquals() {
        Task task1 = new Task("Test addNewTask", "Test addNewTask description", Status.NEW, 1440, "01.11.24 12:15");
        Task task2 = new Task("Test addNewTask", "Test addNewTask description", Status.NEW, 1440, "06.11.24 12:15");
        assertEquals(task1.getId(), task2.getId(), "Задачи не совпадают.");
        assertEquals(task1, task2, "Задачи не совпадают.");
    }
}