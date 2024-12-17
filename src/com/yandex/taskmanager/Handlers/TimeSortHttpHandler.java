package com.yandex.taskmanager.Handlers;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.yandex.taskmanager.interfaces.HistoryManager;
import com.yandex.taskmanager.interfaces.TaskManager;

import java.io.IOException;
import java.net.URI;

public class TimeSortHttpHandler extends BaseHttpHandler implements HttpHandler {
    TaskManager taskManager;
    HistoryManager historyManager;

    public TimeSortHttpHandler(TaskManager taskManager, HistoryManager historyManager) {
        this.taskManager = taskManager;
        this.historyManager = historyManager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        System.out.println("Началась обработка /prioritized запроса от клиента.");
        String method = httpExchange.getRequestMethod();
        String response;
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        URI requestURI = httpExchange.getRequestURI();
        String path = requestURI.getPath();
        String[] splitStrings = path.split("/");
        switch (method) {
            case "GET": {
                if (splitStrings.length == 2) {
                    response = gson.toJson(taskManager.getTimeSort().toString());
                    TasksHttpHandler.sendText(httpExchange, response);
                }
                return;
            }
            default:
                TasksHttpHandler.sendHasError(httpExchange);
        }
    }
}

