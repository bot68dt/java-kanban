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
        if(!(task.getName()==null || task.getName().trim().isEmpty() || task.getStatus()==null || task.getType()==Types.EPIC || task.getType()==Types.SUBEPIC)) {
            task.setId(task.getId() + count);
            tasks.put((task.getId()), task);
            ++count;
        }
    }

    @Override
    public void addEpic(Epic epic)
    {
        if(!(epic.getName()==null || epic.getName().trim().isEmpty() || epic.getType()==Types.SIMPLE || epic.getType()==Types.SUBEPIC)) {
            epic.setId(epic.getId() + count);
            epics.put(epic.getId(), epic);
            ++count;
        }
    }

    @Override
    public void addSubTask(int epicId, SubTask subTask)
    {
        if (!epics.isEmpty() && !(subTask.getName()==null || subTask.getName().trim().isEmpty() || subTask.getStatus()==null) || subTask.getType()==Types.SIMPLE || subTask.getType()==Types.EPIC) {
            if(epics.containsKey(epicId))
            {
                subTask.setId(subTask.getId()+count);
                Epic object = changeSubsInEpic(subTask, epicId, subTask.getId());
                object.addSubTasks(subTask.getId());
                ++count;
                changeEpicStatus(object);
            }
        }
    }

    @Override
    public List<Task> getTasks() {
        if(!tasks.isEmpty())
            return new ArrayList<>(tasks.values());
        else
            return new ArrayList<>();
    }

    @Override
    public List<Epic> getEpics() {
        if (!epics.isEmpty())
            return new ArrayList<>(epics.values());
        else
            return new ArrayList<>();
    }

    @Override
    public List<SubTask> getSubTasks() {
        if (!subtasks.isEmpty())
            return new ArrayList<>(subtasks.values());
        else
            return new ArrayList<>();
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
            return new HashMap<>();
    }
    @Override
    public Map<Integer, Epic> getEpicsWithId() {
        if(!epics.isEmpty())
            return epics;
        else
            return new HashMap<>();
    }

    @Override
    public Map<Integer, SubTask> getSubTasksWithId() {
        if(!subtasks.isEmpty())
            return subtasks;
        else
            return new HashMap<>();
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
        return tasks.getOrDefault(id, new Task(null,null,null));
    }

    @Override
    public SubTask getSubTaskById(int id) {
        return subtasks.getOrDefault(id, new SubTask(null, null, null));
    }

    @Override
    public Epic getEpicById(int id) {
        return epics.getOrDefault(id, new Epic(null, null));
    }

    @Override
    public void updateTask(int id, Task task) {
        if (tasks.containsKey(id) && !(task.getName()==null || task.getName().trim().isEmpty() || task.getStatus()==null || task.getType()==Types.EPIC || task.getType()==Types.SUBEPIC))
            task.setId(id);
            tasks.put(id, task);
    }

    @Override
    public void updateEpic(int id, Epic task) {
        if (epics.containsKey(id) && !(task.getName()==null || task.getName().trim().isEmpty()  || task.getType()==Types.SIMPLE || task.getType()==Types.SUBEPIC))
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
        if (subtasks.containsKey(id)&& !(task.getName()==null || task.getName().trim().isEmpty() || task.getStatus()==null || task.getType()==Types.SIMPLE || task.getType()==Types.EPIC))
        {
            int epicHash = subtasks.get(id).getEpicHash();
            task.setId(id);
            Epic object = changeSubsInEpic(task, epicHash, id);
            changeEpicStatus(object);
        }
    }

    @Override
    public int delTaskById(int id) {
        Task a = tasks.remove(id);
        if(a!=null)
            return id;
        else return 0;
    }

    @Override
    public int delEpicById(int id) {
        if (epics.containsKey(id))
        {
            for(int i : epics.get(id).getSubTasks())
                subtasks.remove(i);
            epics.remove(id);
            return id;
        }
        else return 0;
    }

    @Override
    public int delSubEpicById(int id) {
        if (subtasks.containsKey(id))
        {
            int epicHash = subtasks.get(id).getEpicHash();
            subtasks.remove(id);
            epics.get(epicHash).delSubTask(id);
            Epic object = epics.get(epicHash);
            if(!object.getSubTasks().isEmpty())
                changeEpicStatus(object);
            else
                object.setStatus(Status.DONE);
            return id;
        }
        return 0;
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
            return new ArrayList<>();
    }

    public void changeEpicStatus(Epic object)
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

    public Epic changeSubsInEpic(SubTask task, int epicHash, int id)
    {
        task.setEpicHash(epicHash);
        subtasks.put(id, task);
        return epics.get(epicHash);
    }

    @Override
    public int getCount()
    {return count;}

}