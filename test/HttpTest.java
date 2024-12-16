import com.sun.net.httpserver.HttpServer;
import com.yandex.taskmanager.Handlers.*;
import com.yandex.taskmanager.constant.Status;
import com.yandex.taskmanager.interfaces.HistoryManager;
import com.yandex.taskmanager.interfaces.TaskManager;
import com.yandex.taskmanager.model.Epic;
import com.yandex.taskmanager.model.SubTask;
import com.yandex.taskmanager.model.Task;
import com.yandex.taskmanager.service.Managers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.google.gson.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HttpTest {

    static TaskManager taskManager = Managers.getFileManager();
    static HistoryManager historyManager = Managers.getDefaultHistory();
    static HttpServer httpServer;
    static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    static HttpClient client = HttpClient.newHttpClient();

    static {
        try {
            httpServer = HttpServer.create();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public HttpTest() {
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
    }

    @BeforeAll
    static void start() throws IOException {
        HttpTest httpTest = new HttpTest();
        httpServer.bind(new InetSocketAddress(8080), 0);
        httpServer.createContext("/tasks", new TasksHttpHandler(taskManager, historyManager));
        httpServer.createContext("/subtasks", new SubsHttpHandler(taskManager, historyManager));
        httpServer.createContext("/epics", new EpicsHttpHandler(taskManager, historyManager));
        httpServer.createContext("/history", new HistoryHttpHandler(taskManager, historyManager));
        httpServer.createContext("/prioritized", new TimeSortHttpHandler(taskManager, historyManager));
        httpServer.createContext("/logger", new LoggerHttpHandler(taskManager, historyManager));
        httpServer.start();
    }


    @Test
    void testTasks() {
        try {
            HttpRequest request1 = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/tasks")).GET().build();
            HttpRequest request2 = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/tasks/1626573412")).GET().build();
            HttpRequest request3 = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/tasks/16265734122")).GET().build();
            HttpRequest request4 = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/tasks/1626573414")).POST(HttpRequest.BodyPublishers.ofString(gson.toJson("{\"name\": \"Поспать\", \"description\": \"Полежать на кровати\", \"status\": \"NEW\", \"duration\": \"1440\", \"time\": \"06.12.24 13:15\"}"))).build();
            HttpRequest request5 = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/tasks/")).POST(HttpRequest.BodyPublishers.ofString(gson.toJson("{\"name\": \"Поспать\", \"description\": \"Полежать на кровати\", \"status\": \"NEW\", \"duration\": \"1440\", \"time\": \"09.12.24 13:15\"}"))).build();
            HttpRequest request6 = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/tasks/1626573414")).DELETE().build();
            HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> response4 = client.send(request4, HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> response5 = client.send(request5, HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> response6 = client.send(request6, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response1.statusCode());
            assertTrue(response2.statusCode() == 200);
            assertTrue(response3.statusCode() == 404);
            assertTrue(response4.statusCode() == 201);
            assertTrue(response5.statusCode() == 201);
            assertTrue(response6.statusCode() == 200);
        } catch (IOException | InterruptedException e) {
        }
    }

    @Test
    void testSubs() {
        try {
            HttpRequest request1 = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/subtasks")).GET().build();
            HttpRequest request2 = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/subtasks/1626573417")).GET().build();
            HttpRequest request3 = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/tasks/1626573412298")).GET().build();
            HttpRequest request4 = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/subtasks/-772151814")).POST(HttpRequest.BodyPublishers.ofString(gson.toJson("{\"epicid\":\"1924674558\", \"name\": \"Поспать\", \"description\": \"Полежать на кровати\", \"status\": \"NEW\", \"duration\": \"1440\", \"time\": \"17.12.24 13:15\"}"))).build();
            HttpRequest request5 = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/subtasks")).POST(HttpRequest.BodyPublishers.ofString(gson.toJson("{\"epicid\":\"1924674558\", \"name\": \"Поспать\", \"description\": \"Полежать на кровати\", \"status\": \"NEW\", \"duration\": \"1440\", \"time\": \"15.12.24 13:15\"}"))).build();
            HttpRequest request6 = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/subtasks/-772151814")).DELETE().build();
            HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> response4 = client.send(request4, HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> response5 = client.send(request5, HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> response6 = client.send(request6, HttpResponse.BodyHandlers.ofString());
            assertTrue(response1.statusCode() == 200);
            assertTrue(response2.statusCode() == 200);
            assertTrue(response3.statusCode() == 404);
            assertTrue(response4.statusCode() == 201);
            assertTrue(response5.statusCode() == 201);
            assertTrue(response6.statusCode() == 200);
        } catch (IOException | InterruptedException e) {
        }
    }

    @Test
    void testEpics() {
        try {
            HttpRequest request1 = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/epics")).GET().build();
            HttpRequest request2 = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/epics/1924674558")).GET().build();
            HttpRequest request3 = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/tasks/1626573412298")).GET().build();
            HttpRequest request7 = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/epics/1924674558/subtasks")).GET().build();
            HttpRequest request4 = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/epics/1924674558")).POST(HttpRequest.BodyPublishers.ofString(gson.toJson("{\"name\": \"Поспать\", \"description\": \"Полежать на кровати\"}"))).build();
            HttpRequest request5 = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/epics")).POST(HttpRequest.BodyPublishers.ofString(gson.toJson("{\"name\": \"Поспать\", \"description\": \"Полежать на кровати\"}"))).build();
            HttpRequest request6 = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/epics/1924674558")).DELETE().build();
            HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> response4 = client.send(request4, HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> response5 = client.send(request5, HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> response6 = client.send(request6, HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> response7 = client.send(request7, HttpResponse.BodyHandlers.ofString());
            assertTrue(response1.statusCode() == 200);
            assertTrue(response2.statusCode() == 200);
            assertTrue(response3.statusCode() == 404);
            assertTrue(response4.statusCode() == 201);
            assertTrue(response5.statusCode() == 201);
            assertTrue(response6.statusCode() == 200);
            assertTrue(response7.statusCode() == 200);
        } catch (IOException | InterruptedException e) {
        }
    }

    @Test
    void testTimeSort() {
        try {
            HttpRequest request1 = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/prioritized")).GET().build();
            HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
            assertTrue(response1.statusCode() == 200);
        } catch (IOException | InterruptedException e) {
        }
    }

    @Test
    void testLogger() {
        try {
            HttpRequest request1 = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/logger")).GET().build();
            HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
            assertTrue(response1.statusCode() == 200);
        } catch (IOException | InterruptedException e) {
        }
    }

    @Test
    void testHistory() {
        try {
            HttpRequest request1 = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/history")).GET().build();
            HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
            assertTrue(response1.statusCode() == 200);
        } catch (IOException | InterruptedException e) {
        }
    }

    @AfterAll
    static void stop() {
        httpServer.stop(1);
    }
}
