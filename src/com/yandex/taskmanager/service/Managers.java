package com.yandex.taskmanager.service;

import com.yandex.taskmanager.interfaces.HistoryManager;
import com.yandex.taskmanager.interfaces.TaskManager;

public class Managers {
    TaskManager taskManager;
    static HistoryManager historyManager;

    public TaskManager getDefault()
    {
        return this.taskManager = new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory()
    {
        return historyManager = new InMemoryHistoryManager();
    }
}