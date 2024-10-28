package com.yandex;

import com.yandex.taskmanager.constant.Status;
import com.yandex.taskmanager.constant.Types;
import com.yandex.taskmanager.interfaces.HistoryManager;
import com.yandex.taskmanager.model.Epic;
import com.yandex.taskmanager.model.SubTask;
import com.yandex.taskmanager.model.Task;
import com.yandex.taskmanager.service.InMemoryHistoryManager;
import com.yandex.taskmanager.service.Managers;
import com.yandex.taskmanager.interfaces.TaskManager;

public class Main {
    public static void main(String[] args) {

        TaskManager taskManager = Managers.getDefault();
        HistoryManager historyManager = Managers.getDefaultHistory();

        taskManager.addTask(new Task("Поспать", "Полежать на кровати", Status.IN_PROGRESS));
        taskManager.addTask(new Task("Покушать", null, Status.NEW));
        taskManager.addTask(new Task("Поспать", "Полежать на кровати", Status.NEW));
        taskManager.addTask(new Task("    ", null, Status.NEW));
        taskManager.addEpic(new Epic("Пройти теорию 4-ого спринта", "Уложиться в неделю"));
        taskManager.addEpic(new Epic("Выполнить финальное задание 4-ого спринта", "Сделать без подсказок из задания"));
        taskManager.addSubTask(1924674558, new SubTask("Поспать", "Полежать на кровати", Status.IN_PROGRESS));
        taskManager.addSubTask(1924674558, new SubTask("Выполнить программу спринта", "Читать внимательно", Status.DONE));
        taskManager.addSubTask(1802671086, new SubTask("Хорошо выспаться", "Минимум - 7 часов", Status.IN_PROGRESS));
        System.out.println(taskManager.getEpicsWithId());
        System.out.println(taskManager.getSubTasksWithId());
        System.out.println(taskManager.getTasksWithId());
        taskManager.updateTask(1626573414, new Task("Поспать", "Полежать на кровати", Status.DONE));
        taskManager.updateEpic(1924674558, new Epic("Пройти теорию 4-ого спринта", "Раньше недели"));
        taskManager.updateSubEpic(1626573417, new SubTask("Поспать", "Полежать на кровати", Status.DONE));

        System.out.println(taskManager.getEpicsWithId());
        System.out.println(taskManager.getSubTasksWithId());
        System.out.println(taskManager.getTasksWithId());
        System.out.println();
        System.out.println(taskManager.getSubsByEpicId(1924674558));
        taskManager.delSubEpicById(1509758710);
        System.out.println(taskManager.getCount());

        historyManager.add(taskManager.getTaskById(-866394938));
        historyManager.add(taskManager.getTaskById(1626573414));
        historyManager.add(taskManager.getTaskById(1626573414));
        historyManager.add(taskManager.getTaskById(1626573414));
        historyManager.add(taskManager.getTaskById(1626573414));
        historyManager.add(taskManager.getTaskById(1626573414));
        historyManager.add(taskManager.getTaskById(1626573414));
        historyManager.add(taskManager.getTaskById(1626573414));
        historyManager.add(taskManager.getTaskById(1626573414));
        historyManager.add(taskManager.getTaskById(1626573414));
        historyManager.add(taskManager.getSubTaskById(1626573417));
        historyManager.add(taskManager.getEpicById(1924674558));

        printAllTasks(taskManager, historyManager);

        historyManager.remove(taskManager.delTaskById(1626573414));
        historyManager.remove(taskManager.delEpicById(1924674558));
        printAllTasks(taskManager, historyManager);
    }

    private static void printAllTasks(TaskManager manager, HistoryManager historyManager) {
        System.out.println("Задачи:");
        for (Task task : manager.getTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : manager.getEpics()) {
            System.out.println(epic);
            if (manager.getSubsByEpicId(epic.getId()) != null)
                for (Task task : manager.getSubsByEpicId(epic.getId())) {
                    System.out.println("--> " + task);
                }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getSubTasks()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : historyManager.getHistory()) {
            System.out.println(task);
        }
    }
}
