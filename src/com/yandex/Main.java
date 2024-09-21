package com.yandex;
import com.yandex.taskmanager.constant.Status;
import com.yandex.taskmanager.constant.Types;
import com.yandex.taskmanager.model.Epic;
import com.yandex.taskmanager.model.SubTask;
import com.yandex.taskmanager.model.Task;
import com.yandex.taskmanager.service.TaskManager;

public class Main
{
    public static void main(String[] args)
    {
        TaskManager taskManager = new TaskManager();
        taskManager.addTask(new Task("Поспать","Полежать на кровати", Status.IN_PROGRESS));
        taskManager.addTask(new Task("Покушать","Завтрак", Status.NEW));
        taskManager.addTask(new Task("Поспать","Полежать на кровати", Status.NEW));
        taskManager.addEpic(new Epic("Пройти теорию 4-ого спринта","Уложиться в неделю"));
        taskManager.addEpic(new Epic("Выполнить финальное задание 4-ого спринта","Сделать без подсказок из задания"));
        taskManager.addSubTask(1924674558, new SubTask("Поспать", "Полежать на кровати", Status.IN_PROGRESS));
        taskManager.addSubTask(1924674558, new SubTask("Выполнить программу спринта", "Читать внимательно", Status.DONE));
        taskManager.addSubTask(1802671086, new SubTask("Хорошо выспаться", "Минимум - 7 часов", Status.IN_PROGRESS));
        System.out.println(taskManager.getTasksByType(Types.EPIC));
        System.out.println(taskManager.getTasksByType(Types.SUBEPIC));
        System.out.println(taskManager.getTasksByType(Types.SIMPLE));
        taskManager.updateTask(1626573414, new Task("Поспать","Полежать на кровати", Status.DONE));
        taskManager.updateEpic(1924674558, new Epic("Пройти теорию 4-ого спринта","Раньше недели"));
        taskManager.updateSubEpic(1626573417, new SubTask("Поспать","Полежать на кровати", Status.DONE));
        System.out.println(taskManager.getTasksByType(Types.EPIC));
        System.out.println(taskManager.getTasksByType(Types.SUBEPIC));
        System.out.println(taskManager.getTasksByType(Types.SIMPLE));
        System.out.println();
        System.out.println(taskManager.getSubsByEpicId(1924674558));
        System.out.println(taskManager.getCount());
    }
}
