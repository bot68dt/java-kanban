package com.yandex.taskmanager.interfaces;

import com.yandex.taskmanager.model.Task;

import java.util.List;

public interface HistoryManager {
    void add(Task task);
    List<Task> getHistory();
}
