package com.yandex.taskmanager.service;

import com.yandex.taskmanager.constant.Status;
import com.yandex.taskmanager.constant.Types;
import com.yandex.taskmanager.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        if(!(task.getName()==null || task.getName().trim().isEmpty() || task.getStatus()==null)) {
            tasks.put((task.getId() + count), task);
            ++count;
        }
    }

    public void addEpic(Epic epic)
    {
        if(!(epic.getName()==null || epic.getName().trim().isEmpty())) {
            epics.put(epic.getId() + count, epic);
            ++count;
        }
    }

    public void addSubTask(int epicId, SubTask subTask)
    {
        if (!epics.isEmpty() && !(subTask.getName()==null || subTask.getName().trim().isEmpty() || subTask.getStatus()==null)) {
            if(epics.containsKey(epicId))
            {
                Epic object = repeatedExpression2(subTask, epicId, subTask.getId()+count);
                object.addSubTasks((subTask.getId()+count));
                ++count;
                repeatedExpression(object);
            }
        }
    }

    public List<Task> getTasks() {
        if(!tasks.isEmpty())
            return new ArrayList<>(tasks.values());
        else
            return null;
    }

    public List<Epic> getEpics() {
        if (!epics.isEmpty())
            return new ArrayList<>(epics.values());
        else
            return null;
    }

    public List<SubTask> getSubTasks() {
        if (!subtasks.isEmpty())
            return new ArrayList<>(subtasks.values());
        else
            return null;
    }

    public void getAll()
    {
        getEpics();
        getSubTasks();
        getTasks();
    }

    public Map<Integer, Task> getTasksWithId() {
        if(!tasks.isEmpty())
            return tasks;
        else
            return null;
    }
    public Map<Integer, Epic> getEpicsWithId() {
        if(!epics.isEmpty())
            return epics;
        else
            return null;
    }

    public Map<Integer, SubTask> getSubTasksWithId() {
        if(!subtasks.isEmpty())
            return subtasks;
        else
            return null;
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
                    epic.setStatus(Status.NEW);
                }
            }
            case EPIC ->
            {
                subtasks.clear();
                epics.clear();
            }
        }
    }

    public Task getTaskById(int id) {
        return tasks.getOrDefault(id, null);
    }

    public SubTask getSubTaskById(int id) {
        return subtasks.getOrDefault(id, null);
    }

    public Epic getEpicById(int id) {
        return epics.getOrDefault(id, null);
    }

    public void updateTask(int id, Task task) {
        if (tasks.containsKey(id) && !(task.getName()==null || task.getName().trim().isEmpty() || task.getStatus()==null))
            tasks.put(id, task);
    }

    public void updateEpic(int id, Epic task) {
        if (epics.containsKey(id) && !(task.getName()==null || task.getName().trim().isEmpty()))
        {
            ArrayList<Integer> subs = epics.get(id).getSubTasks();
            Status status = epics.get(id).getStatus();
            task.setStatus(status);
            task.setSubTasks(subs);
            epics.put(id, task);
        }
    }

    public void updateSubEpic(int id, SubTask task) {
        if (subtasks.containsKey(id)&& !(task.getName()==null || task.getName().trim().isEmpty() || task.getStatus()==null))
        {
            int epicHash = subtasks.get(id).getEpicHash();
            Epic object = repeatedExpression2(task, epicHash, id);
            repeatedExpression(object);
        }
    }

    public void delTaskById(int id) {
        tasks.remove(id);
    }

    public void delEpicById(int id) {
        if (epics.containsKey(id))
        {
            for(int i : epics.get(id).getSubTasks())
                subtasks.remove(i);
            epics.remove(id);
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

    public ArrayList<SubTask> getSubsByEpicId(int id)
    {
        if (epics.containsKey(id)) {
            ArrayList<SubTask> buffer = new ArrayList<>();
            for (SubTask obj : subtasks.values())
                if (obj.getEpicHash() == id)
                    buffer.add(obj);
            return buffer;
        }
        else
            return null;
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

    public Epic repeatedExpression2(SubTask task, int epicHash, int id)
    {
        task.setEpicHash(epicHash);
        subtasks.put(id, task);
        return epics.get(epicHash);
    }

    public int getCount()
    {return count;}
}