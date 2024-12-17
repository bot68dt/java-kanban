package com.yandex.taskmanager.Handlers;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.yandex.taskmanager.interfaces.HistoryManager;
import com.yandex.taskmanager.interfaces.TaskManager;
import com.yandex.taskmanager.model.Epic;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;

public class EpicsHttpHandler extends BaseHttpHandler implements HttpHandler {
    TaskManager taskManager;
    HistoryManager historyManager;

    public EpicsHttpHandler(TaskManager taskManager, HistoryManager historyManager) {
        this.taskManager = taskManager;
        this.historyManager = historyManager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        System.out.println("Началась обработка /epics запроса от клиента.");
        String method = httpExchange.getRequestMethod();
        String response;
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        URI requestURI = httpExchange.getRequestURI();
        String path = requestURI.getPath();
        String[] splitStrings = path.split("/");
        switch (method) {
            case "POST": {
                if (splitStrings.length == 3) {
                    if (taskManager.getEpicsWithId().containsKey(Integer.parseInt(splitStrings[2]))) {
                        JsonElement jsonElement = JsonParser.parseString(new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
                        JsonObject jsonObject = jsonElement.getAsJsonObject();
                        taskManager.updateEpic(Integer.parseInt(splitStrings[2]), new Epic(jsonObject.get("name").getAsString(), jsonObject.get("description").getAsString()));
                        TasksHttpHandler.sendTextWithNoData(httpExchange);
                    } else TasksHttpHandler.sendNotFound(httpExchange);
                } else {
                    JsonElement jsonElement = JsonParser.parseString(new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    taskManager.addEpic(new Epic(jsonObject.get("name").getAsString(), jsonObject.get("description").getAsString()));
                    TasksHttpHandler.sendTextWithNoData(httpExchange);
                }
                return;
            }
            case "GET": {
                if (splitStrings.length == 3) {
                    if (taskManager.getEpicsWithId().containsKey(Integer.parseInt(splitStrings[2]))) {
                        response = gson.toJson(taskManager.getEpicById(Integer.parseInt(splitStrings[2])).toString());
                        TasksHttpHandler.sendText(httpExchange, response);
                    } else TasksHttpHandler.sendNotFound(httpExchange);
                    return;
                } else if (splitStrings.length == 4 && splitStrings[3].equals("subtasks")) {
                    if (taskManager.getEpicsWithId().containsKey(Integer.parseInt(splitStrings[2]))) {
                        response = gson.toJson(taskManager.getSubsByEpicId(Integer.parseInt(splitStrings[2])).toString());
                        TasksHttpHandler.sendText(httpExchange, response);
                    } else TasksHttpHandler.sendNotFound(httpExchange);
                    return;
                } else {
                    TasksHttpHandler.sendText(httpExchange, taskManager.getEpicsWithId().toString());
                    return;
                }
            }
            case "DELETE": {
                if (splitStrings.length == 3 && taskManager.getEpicsWithId().containsKey(Integer.parseInt(splitStrings[2]))) {
                    TasksHttpHandler.sendText(httpExchange, "Задача №" + Integer.parseInt(splitStrings[2]) + " удалена");
                    taskManager.delEpicById(Integer.parseInt(splitStrings[2]));
                } else TasksHttpHandler.sendNotFound(httpExchange);
                return;
            }
            default:
                TasksHttpHandler.sendHasError(httpExchange);
        }
    }
}

