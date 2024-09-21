package com.yandex.taskmanager.model;

import com.yandex.taskmanager.constant.Status;
import com.yandex.taskmanager.constant.Types;

import java.util.Objects;

public class Task
{
    private final String name;
    private final String description;
    private Status status;
    private final int id;
    protected Types type;

    public Task(String name, String description, Status status)
    {
        this.name = name;
        this.description = description;
        this.status = status;
        id = hashCode();
        type = Types.SIMPLE;
    }

    public Status getStatus()
    {
        return status;
    }

    public void setStatus(Status status)
    {
        this.status = status;
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public int getId()
    {
        return id;
    }

    public Types getType()
    {
        return type;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Task task = (Task) obj;
        return Objects.equals(name, task.name) &&
                Objects.equals(description, task.description) &&
                Objects.equals(status, task.status);
    }

    @Override
    public int hashCode()
    {
        int hash = 2;
        if (name != null)
        {
            hash += name.hashCode();
        }
        hash = hash * 31;
        if (description != null)
        {
            hash += description.hashCode();
        }
        return hash;
    }
    @Override
    public String toString()
    {
        return "Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", type=" + type +
                '}'+ '\n';
    }
}
