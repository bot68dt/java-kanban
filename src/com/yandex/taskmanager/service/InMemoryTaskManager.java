package com.yandex.taskmanager.service;

import com.yandex.taskmanager.constant.Status;
import com.yandex.taskmanager.constant.Types;
import com.yandex.taskmanager.interfaces.HistoryManager;
import com.yandex.taskmanager.interfaces.TaskManager;
import com.yandex.taskmanager.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {

    private final Map<Integer, Task> tasks;
    private final Map<Integer, Epic> epics;
    private final Map<Integer, SubTask> subtasks;
    private int count = 1;



    public InMemoryTaskManager()
    {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
    }

    @Override
    public void addTask(Task task)
    {
        if(!(task.getName()==null || task.getName().trim().isEmpty() || task.getStatus()==null)) {
            task.setId(task.getId() + count);
            tasks.put((task.getId()), task);
            ++count;
        }
    }

    @Override
    public void addEpic(Epic epic)
    {
        if(!(epic.getName()==null || epic.getName().trim().isEmpty())) {
            epic.setId(epic.getId() + count);
            epics.put(epic.getId(), epic);
            ++count;
        }
    }

    @Override
    public void addSubTask(int epicId, SubTask subTask)
    {
        if (!epics.isEmpty() && !(subTask.getName()==null || subTask.getName().trim().isEmpty() || subTask.getStatus()==null)) {
            if(epics.containsKey(epicId))
            {
                subTask.setId(subTask.getId()+count);
                Epic object = repeatedExpression2(subTask, epicId, subTask.getId());
                object.addSubTasks(subTask.getId());
                ++count;
                repeatedExpression(object);
            }
        }
    }

    @Override
    public List<Task> getTasks() {
        if(!tasks.isEmpty())
            return new ArrayList<>(tasks.values());
        else
            return null;
    }

    @Override
    public List<Epic> getEpics() {
        if (!epics.isEmpty())
            return new ArrayList<>(epics.values());
        else
            return null;
    }

    @Override
    public List<SubTask> getSubTasks() {
        if (!subtasks.isEmpty())
            return new ArrayList<>(subtasks.values());
        else
            return null;
    }

    @Override
    public void getAll()
    {
        getEpics();
        getSubTasks();
        getTasks();
    }

    @Override
    public Map<Integer, Task> getTasksWithId() {
        if(!tasks.isEmpty())
            return tasks;
        else
            return null;
    }
    @Override
    public Map<Integer, Epic> getEpicsWithId() {
        if(!epics.isEmpty())
            return epics;
        else
            return null;
    }

    @Override
    public Map<Integer, SubTask> getSubTasksWithId() {
        if(!subtasks.isEmpty())
            return subtasks;
        else
            return null;
    }

    @Override
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

    @Override
    public Task getTaskById(int id) {
        return tasks.getOrDefault(id, null);
    }

    @Override
    public SubTask getSubTaskById(int id) {
        return subtasks.getOrDefault(id, null);
    }

    @Override
    public Epic getEpicById(int id) {
        return epics.getOrDefault(id, null);
    }

    @Override
    public void updateTask(int id, Task task) {
        if (tasks.containsKey(id) && !(task.getName()==null || task.getName().trim().isEmpty() || task.getStatus()==null))
            task.setId(id);
            tasks.put(id, task);
    }

    @Override
    public void updateEpic(int id, Epic task) {
        if (epics.containsKey(id) && !(task.getName()==null || task.getName().trim().isEmpty()))
        {
            ArrayList<Integer> subs = epics.get(id).getSubTasks();
            Status status = epics.get(id).getStatus();
            task.setStatus(status);
            task.setSubTasks(subs);
            task.setId(id);
            epics.put(id, task);
        }
    }

    @Override
    public void updateSubEpic(int id, SubTask task) {
        if (subtasks.containsKey(id)&& !(task.getName()==null || task.getName().trim().isEmpty() || task.getStatus()==null))
        {
            int epicHash = subtasks.get(id).getEpicHash();
            task.setId(id);
            Epic object = repeatedExpression2(task, epicHash, id);
            repeatedExpression(object);
        }
    }

    @Override
    public void delTaskById(int id) {
        tasks.remove(id);
    }

    @Override
    public void delEpicById(int id) {
        if (epics.containsKey(id))
        {
            for(int i : epics.get(id).getSubTasks())
                subtasks.remove(i);
            epics.remove(id);
        }
    }

    @Override
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

    @Override
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

    @Override
    public int getCount()
    {return count;}

}