package com.yandex.taskmanager.service;

import com.yandex.taskmanager.constant.Status;
import com.yandex.taskmanager.constant.Types;
import com.yandex.taskmanager.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TaskManager {

    private final Map<Integer, Task> tasks;
    private final Map<Integer, Epic> epics;
    private final Map<Integer, SubTask> subtasks;
    private int count = 1;



    public TaskManager()
    {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
    }

    public void addTask(Task task)
    {
        tasks.put((task.getId()+count),task);
        ++count;
    }

    public void addEpic(Epic epic)
    {
        epics.put(epic.getId()+count,epic);
        ++count;
    }

    public void addSubTask(int epicId, SubTask subTask)
    {
        if (!epics.isEmpty()) {
            if(epics.containsKey(epicId))
            {
                subTask.setEpicHash(epicId);
                subtasks.put(subTask.getId()+count, subTask);
                Epic object = epics.get(epicId);
                object.addSubTasks((subTask.getId()+count));
                ++count;
                int progress=0;
                int done =0;
                int new1 = 0;
                for (int i : object.getSubTasks()) {
                    SubTask sub = subtasks.get(i);
                    switch (sub.getStatus()) {
                        case NEW -> new1++;
                        case IN_PROGRESS -> progress++;
                        case DONE -> done++;
                    }
                }
                if (progress > 0 || !(done == object.getSubTasks().size()))
                    object.setStatus(Status.IN_PROGRESS);
                else
                    object.setStatus(Status.DONE);
                if (new1 == object.getSubTasks().size())
                    object.setStatus(Status.NEW);
            }
        }
    }

    public ArrayList<Object> getAllTasks()
    {
        ArrayList<Object> object = new ArrayList<>();
        object.add(epics);
        object.add(subtasks);
        object.add(tasks);
        return object;
    }

    public ArrayList<Object> getTasksByType(Types type) {
        ArrayList<Object> buffer = new ArrayList<>();
        switch (type)
        {
            case SIMPLE -> buffer.add(tasks);
            case SUBEPIC -> buffer.add(subtasks);
            case EPIC -> buffer.add(epics);
        }
        return buffer;
    }

    public void delTasksByType(Types type) {
        switch (type)
        {
            case SIMPLE -> tasks.clear();
            case SUBEPIC ->
            {
                subtasks.clear();
                for (Epic epic :epics.values())
                {
                    epic.delAllSubTasks();
                    epic.setStatus(Status.DONE);
                }
            }
            case EPIC ->
            {
                subtasks.clear();
                epics.clear();
            }
        }
    }

    public ArrayList<Object> getTasksById(int id) {
        ArrayList<Object> buffer = new ArrayList<>();
        if (tasks.containsKey(id))
            buffer.add(tasks.get(id));
        if (subtasks.containsKey(id))
            buffer.add(subtasks.get(id));
        if (epics.containsKey(id))
            buffer.add(epics.get(id));
        return buffer;
    }

    public void updateTask(int id, Task task) {
        if (tasks.containsKey(id))
            tasks.put(id, task);
    }

    public void updateEpic(int id, Epic task) {
        if (epics.containsKey(id))
        {
            ArrayList<Integer> subs = epics.get(id).getSubTasks();
            Status status = epics.get(id).getStatus();
            task.setStatus(status);
            task.setSubTasks(subs);
            epics.put(id, task);
        }
    }

    public void updateSubEpic(int id, SubTask task) {
        if (subtasks.containsKey(id))
        {
            int epicHash = subtasks.get(id).getEpicHash();
            task.setEpicHash(epicHash);
            subtasks.put(id, task);
            Epic object = epics.get(epicHash);
            repeatedExpression(object);
        }
    }

    public void delTaskById(int id) {
        if (tasks.containsKey(id))
            tasks.remove(id);
    }

    public void delEpicById(int id) {
        if (epics.containsKey(id))
        {
            ArrayList<Integer> subs = epics.get(id).getSubTasks();
            epics.remove(id);
            for(int i : subs)
                if (subtasks.containsKey(i))
                    subtasks.remove(i);
        }
    }

    public void delSubEpicById(int id) {
        if (subtasks.containsKey(id))
        {
            int epicHash = subtasks.get(id).getEpicHash();
            subtasks.remove(id);
            epics.get(epicHash).delSubTask(id);
            Epic object = epics.get(epicHash);
            if(!object.getSubTasks().isEmpty())
                repeatedExpression(object);
            else
                object.setStatus(Status.DONE);
        }
    }

    public HashMap<Integer, SubTask> getSubsByEpicId(int id)
    {
        HashMap<Integer, SubTask> buffer = new HashMap<>();
        for (Map.Entry<Integer, SubTask> obj : subtasks.entrySet())
            if (obj.getValue().getEpicHash() == id)
                buffer.put(obj.getKey(),obj.getValue());
        return buffer;
    }

    public void repeatedExpression(Epic object)
    {
        int progress = 0;
        int done = 0;
        int new1 = 0;
        for (int i : object.getSubTasks()) {
            SubTask sub = subtasks.get(i);
            switch (sub.getStatus()) {
                case NEW -> new1++;
                case IN_PROGRESS -> progress++;
                case DONE -> done++;
            }
        }
        if (progress > 0 || !(done == object.getSubTasks().size()))
            object.setStatus(Status.IN_PROGRESS);
        else
            object.setStatus(Status.DONE);
        if (new1 == object.getSubTasks().size())
            object.setStatus(Status.NEW);
    }

    public int getCount()
    {return count;}
}