package com.yandex.taskmanager.interfaces;

import com.yandex.taskmanager.constant.Types;
import com.yandex.taskmanager.model.Epic;
import com.yandex.taskmanager.model.SubTask;
import com.yandex.taskmanager.model.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public interface TaskManager {
    void addTask(Task task);

    void addEpic(Epic epic);

    void addSubTask(int epicId, SubTask subTask);

    List<Task> getTasks();

    List<Epic> getEpics();

    List<SubTask> getSubTasks();

    void getAll();

    Map<Integer, Task> getTasksWithId();

    Map<Integer, Epic> getEpicsWithId();

    Map<Integer, SubTask> getSubTasksWithId();

    void delTasksByType(Types type);

    Task getTaskById(int id);

    SubTask getSubTaskById(int id);

    Epic getEpicById(int id);

    void updateTask(int id, Task task);

    void updateEpic(int id, Epic task);

    void updateSubEpic(int id, SubTask task);

    int delTaskById(int id);

    int delEpicById(int id);

    int delSubEpicById(int id);

    ArrayList<SubTask> getSubsByEpicId(int id);

    int getCount();

    Set<Task> getTimeSort();

    Logger getLogger();
}
