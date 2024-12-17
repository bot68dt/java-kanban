package com.yandex;

import com.sun.net.httpserver.HttpServer;
import com.yandex.taskmanager.Handlers.*;
import com.yandex.taskmanager.constant.Status;
import com.yandex.taskmanager.interfaces.HistoryManager;
import com.yandex.taskmanager.interfaces.TaskManager;
import com.yandex.taskmanager.model.Epic;
import com.yandex.taskmanager.model.SubTask;
import com.yandex.taskmanager.model.Task;
import com.yandex.taskmanager.service.Managers;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.logging.Level;

public class HttpTaskServer {
    private static final int PORT = 8080;
    static TaskManager taskManager = Managers.getFileManager();
    static HistoryManager historyManager = Managers.getDefaultHistory();

    public static void main(String[] args) throws IOException {

        taskManager.addTask(new Task("Поспать", "Полежать на кровати", Status.IN_PROGRESS, 1440, "01.11.24 12:15"));
        taskManager.addTask(new Task("Покушать", null, Status.NEW, 1440, "01.11.24 12:10"));
        taskManager.addTask(new Task("Поспать", "Полежать на кровати", Status.NEW, 1440, "03.11.24 13:15"));
        taskManager.addTask(new Task("    ", null, Status.NEW, 1440, "01.11.24 12:15"));
        taskManager.addEpic(new Epic("Пройти теорию 4-ого спринта", "Уложиться в неделю"));
        taskManager.addEpic(new Epic("Выполнить финальное задание 4-ого спринта", "Сделать без подсказок из задания"));
        taskManager.addSubTask(1924674558, new SubTask("Поспать", "Полежать на кровати", Status.IN_PROGRESS, 1440, "04.11.24 14:15"));
        taskManager.addSubTask(1924674558, new SubTask("Выполнить программу спринта", "Читать внимательно", Status.DONE, 1440, "05.11.24 15:15"));
        taskManager.addSubTask(1802671086, new SubTask("Хорошо выспаться", "Минимум - 7 часов", Status.IN_PROGRESS, 1440, "06.11.24 16:15"));
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

        HttpServer httpServer = HttpServer.create();

        httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TasksHttpHandler(taskManager, historyManager));
        httpServer.createContext("/subtasks", new SubsHttpHandler(taskManager, historyManager));
        httpServer.createContext("/epics", new EpicsHttpHandler(taskManager, historyManager));
        httpServer.createContext("/history", new HistoryHttpHandler(taskManager, historyManager));
        httpServer.createContext("/prioritized", new TimeSortHttpHandler(taskManager, historyManager));
        httpServer.createContext("/logger", new LoggerHttpHandler(taskManager, historyManager));

        httpServer.start();

        taskManager.getLogger().log(Level.SEVERE, "HTTP-сервер запущен на " + PORT + " порту!");
        //httpServer.stop(1);
    }
}
