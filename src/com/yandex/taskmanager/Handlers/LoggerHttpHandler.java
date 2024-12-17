package com.yandex.taskmanager.Handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.yandex.taskmanager.interfaces.HistoryManager;
import com.yandex.taskmanager.interfaces.TaskManager;
import com.yandex.taskmanager.service.FileBackedTaskManager;

import java.io.IOException;
import java.net.URI;

public class LoggerHttpHandler extends BaseHttpHandler implements HttpHandler {
    TaskManager taskManager;
    HistoryManager historyManager;

    public LoggerHttpHandler(TaskManager taskManager, HistoryManager historyManager) {
        this.taskManager = taskManager;
        this.historyManager = historyManager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        System.out.println("Началась обработка /logger запроса от клиента.");
        String method = httpExchange.getRequestMethod();
        URI requestURI = httpExchange.getRequestURI();
        String path = requestURI.getPath();
        String[] splitStrings = path.split("/");
        switch (method) {
            case "GET": {
                if (splitStrings.length == 2) {
                    TasksHttpHandler.sendText(httpExchange, FileBackedTaskManager.loggerPrint().toString());
                }
                return;
            }
            default:
                TasksHttpHandler.sendHasError(httpExchange);
        }
    }
}

