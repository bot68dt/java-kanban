package com.yandex.taskmanager.service;

import com.yandex.taskmanager.interfaces.HistoryManager;
import com.yandex.taskmanager.interfaces.TaskManager;

public class Managers {
    private static TaskManager taskManager;
    private static HistoryManager historyManager;

    public static TaskManager getDefault()
    {
        return taskManager = new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory()
    {
        return historyManager = new InMemoryHistoryManager();
    }
}