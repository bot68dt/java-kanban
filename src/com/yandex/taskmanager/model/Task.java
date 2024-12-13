package com.yandex.taskmanager.model;

import com.yandex.taskmanager.constant.Status;
import com.yandex.taskmanager.constant.Types;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {
    private final String name;
    private final String description;
    private Duration duration;
    private LocalDateTime startTime;
    private Status status;
    private int id;
    protected Types type;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");

    public Task(String name, String description, Status status, int duration, String startTime) {
        this.name = name;
        this.description = description;
        this.status = status;
        id = hashCode();
        type = Types.SIMPLE;
        this.duration = Duration.ofMinutes(duration);
        this.startTime = LocalDateTime.parse(startTime, formatter);
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime time) {
        this.startTime = time;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Types getType() {
        return type;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Task task = (Task) obj;
        return Objects.equals(name, task.name) && Objects.equals(description, task.description) && Objects.equals(status, task.status);
    }

    @Override
    public int hashCode() {
        int hash = 2;
        if (name != null) {
            hash += name.hashCode();
        }
        hash = hash * 31;
        if (description != null) {
            hash += description.hashCode();
        }
        return hash;
    }

    @Override
    public String toString() {
        return "Task{" + "name='" + name + '\'' + ", description='" + description + '\'' + ", status=" + status + ", type=" + type + ", duration=" + duration.toMinutes() + ", time=" + startTime.format(formatter) + '}' + '\n';
    }

    public String toStringForFile() {
        return id + "," + type + "," + name + "," + status + "," + description + "," + duration.toMinutes() + "," + startTime.format(formatter);
    }
}
