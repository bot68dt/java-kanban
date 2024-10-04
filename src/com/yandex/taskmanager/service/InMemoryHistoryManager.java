package com.yandex.taskmanager.service;

import com.yandex.taskmanager.interfaces.HistoryManager;
import com.yandex.taskmanager.model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final static int CAPACITY = 10;
    List<Task> Tasks;

    public InMemoryHistoryManager(){
        Tasks = new ArrayList<>(CAPACITY);
    }
    
    @Override
    public void add(Task task) {
        if(task!=null) {
            if (Tasks.size() == CAPACITY)
                Tasks.removeFirst();
            Tasks.add(task);
        }
    }

    @Override
    public List<Task> getHistory() {
        {return Tasks;}
    }
}
