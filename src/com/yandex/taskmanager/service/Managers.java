package com.yandex.taskmanager.service;

import com.yandex.taskmanager.interfaces.HistoryManager;
import com.yandex.taskmanager.interfaces.TaskManager;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Managers {
    private static TaskManager taskManager;
    private static HistoryManager historyManager;

    public static TaskManager getDefault() {
        return taskManager = new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return historyManager = new InMemoryHistoryManager();
    }

    public static TaskManager getFileManager() {
        return taskManager = new FileBackedTaskManager();
    }

    public static TaskManager loadFileManager(String file1, String file2) {
        final String HOME = System.getProperty("user.home");
        Path testFile1 = Paths.get(HOME, "Saves", file1);
        Path testFile2 = Paths.get(HOME, "Saves", file2);
        return taskManager = new FileBackedTaskManager(testFile1, testFile2);
    }
}