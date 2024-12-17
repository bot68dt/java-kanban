package com.yandex.taskmanager.Handlers;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.yandex.taskmanager.constant.Status;
import com.yandex.taskmanager.interfaces.HistoryManager;
import com.yandex.taskmanager.interfaces.TaskManager;
import com.yandex.taskmanager.model.SubTask;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;

public class SubsHttpHandler extends BaseHttpHandler implements HttpHandler {
    TaskManager taskManager;
    HistoryManager historyManager;

    public SubsHttpHandler(TaskManager taskManager, HistoryManager historyManager) {
        this.taskManager = taskManager;
        this.historyManager = historyManager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        System.out.println("Началась обработка /subtasks запроса от клиента.");
        String method = httpExchange.getRequestMethod();
        String response;
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        URI requestURI = httpExchange.getRequestURI();
        String path = requestURI.getPath();
        String[] splitStrings = path.split("/");
        switch (method) {
            case "POST": {
                if (splitStrings.length == 3) {
                    if (taskManager.getSubTasksWithId().containsKey(Integer.parseInt(splitStrings[2]))) {
                        JsonElement jsonElement = JsonParser.parseString(new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
                        JsonObject jsonObject = jsonElement.getAsJsonObject();
                        taskManager.updateSubEpic(Integer.parseInt(splitStrings[2]), new SubTask(jsonObject.get("name").getAsString(), jsonObject.get("description").getAsString(), Status.valueOf(jsonObject.get("status").getAsString()), Integer.parseInt(jsonObject.get("duration").getAsString()), jsonObject.get("time").getAsString()));
                        TasksHttpHandler.sendTextWithNoData(httpExchange);
                    } else TasksHttpHandler.sendNotFound(httpExchange);
                } else {
                    JsonElement jsonElement = JsonParser.parseString(new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    taskManager.addSubTask(Integer.parseInt(jsonObject.get("epicid").getAsString()), new SubTask(jsonObject.get("name").getAsString(), jsonObject.get("description").getAsString(), Status.valueOf(jsonObject.get("status").getAsString()), Integer.parseInt(jsonObject.get("duration").getAsString()), jsonObject.get("time").getAsString()));
                    TasksHttpHandler.sendTextWithNoData(httpExchange);
                }
                return;
            }
            case "GET": {
                if (splitStrings.length == 3) {
                    if (taskManager.getSubTasksWithId().containsKey(Integer.parseInt(splitStrings[2]))) {
                        response = gson.toJson(taskManager.getSubTaskById(Integer.parseInt(splitStrings[2])).toString());
                        TasksHttpHandler.sendText(httpExchange, response);
                    } else TasksHttpHandler.sendNotFound(httpExchange);
                } else {
                    TasksHttpHandler.sendText(httpExchange, taskManager.getSubTasksWithId().toString());
                }
                return;
            }
            case "DELETE": {
                if (splitStrings.length == 3 && taskManager.getSubTasksWithId().containsKey(Integer.parseInt(splitStrings[2]))) {
                    TasksHttpHandler.sendText(httpExchange, "Задача №" + Integer.parseInt(splitStrings[2]) + " удалена");
                    taskManager.delSubEpicById(Integer.parseInt(splitStrings[2]));
                } else TasksHttpHandler.sendNotFound(httpExchange);
                return;
            }
            default:
                TasksHttpHandler.sendHasError(httpExchange);
        }
    }
}
