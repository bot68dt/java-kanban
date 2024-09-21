package com.yandex.taskmanager.model;
import com.yandex.taskmanager.constant.Status;
import com.yandex.taskmanager.constant.Types;

import java.util.ArrayList;

public class Epic extends Task
{
    private ArrayList<Integer> subTasks;

    public Epic(String name, String description)
    {
        super(name, description, Status.NEW);
        type= Types.EPIC;
        subTasks = new ArrayList<>();
    }

    public  ArrayList<Integer> getSubTasks()
    {
        return subTasks;
    }
    public  void addSubTasks(int subTasks1)
    {
        subTasks.add(subTasks1);
    }

    public  void delSubTask(Integer i)
    {
        subTasks.remove(i);
    }
    public  void delAllSubTasks()
    {
        subTasks.clear();
    }
    public  void setSubTasks(ArrayList<Integer> subtask)
    {
        subTasks.addAll(subtask);
    }

    public  void setSubTasks(int i, Integer subtask)
    {
        subTasks.add(i, subtask);
    }
    public  void resetSubTasks(ArrayList<Integer> subTasks1)
    {
        subTasks = new ArrayList<>();
        subTasks.addAll(subTasks1);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subTasks=" + subTasks +
                "} " + super.toString();
    }
}
