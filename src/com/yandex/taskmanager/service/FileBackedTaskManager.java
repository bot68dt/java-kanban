package com.yandex.taskmanager.service;

import com.yandex.taskmanager.constant.Status;
import com.yandex.taskmanager.constant.Types;
import com.yandex.taskmanager.exceptions.ManagerSaveException;
import com.yandex.taskmanager.exceptions.TimeCrossingException;
import com.yandex.taskmanager.interfaces.TaskManager;
import com.yandex.taskmanager.model.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;
import java.util.stream.Collectors;


public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {

    private final Map<Integer, Task> tasks;
    private final Map<Integer, Epic> epics;
    private final Map<Integer, SubTask> subtasks;
    private final Set<Task> divideByTime;
    private int count = 1;
    private static final String HOME = System.getProperty("user.home");
    static Logger logger;
    static FileHandler fh;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");
    private final Comparator<Task> comparator = Comparator.comparing(Task::getStartTime);


    public FileBackedTaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        divideByTime = new TreeSet<>(comparator);
        setLogger();
    }

    public FileBackedTaskManager(Path file1, Path file2) {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        divideByTime = new TreeSet<>(comparator);
        setLogger();

        List<String> list = readWordsFromFile(file1.toString());
        for (String str : list) {
            String[] split = str.split("\\,");
            switch (split[1]) {
                case "SIMPLE" -> tasks.put(Integer.parseInt(split[0]), readTask(split));
                case "SUBEPIC" -> subtasks.put(Integer.parseInt(split[0]), readSub(split));
                case "EPIC" -> epics.put(Integer.parseInt(split[0]), readEpic(split));
            }
        }

        try (FileReader in = new FileReader(file2.toString(), StandardCharsets.UTF_8); BufferedReader br = new BufferedReader(in)) {
            while (br.ready()) {
                String line = br.readLine();
                String[] split1 = line.split("\\,");
                switch (split1[1]) {
                    case "SIMPLE" -> divideByTime.add(readTask(split1));
                    case "SUBEPIC" -> divideByTime.add(readSub(split1));
                    case "EPIC" -> divideByTime.add(readEpic(split1));
                }
            }
        } catch (IOException e) {
            e.initCause(new ManagerSaveException("Критическая ошибка при чтении файла задач"));
            logger.log(Level.SEVERE, "Перехвачено исключение: ", e);
            logger.log(Level.SEVERE, "Причина: ", e.getCause());
            fh.close();
        }
    }

    private SubTask readSub(String[] split) {
        SubTask subTask = new SubTask(split[2], split[4], Status.valueOf(split[3]), Integer.valueOf(split[5]), split[6]);
        subTask.setEpicHash(Integer.parseInt(split[7]));
        subTask.setId(Integer.parseInt(split[0]));
        return subTask;
    }

    private Task readTask(String[] split1) {
        Task task = new Task(split1[2], split1[4], Status.valueOf(split1[3]), Integer.valueOf(split1[5]), split1[6]);
        task.setId(Integer.parseInt(split1[0]));
        return task;
    }

    private Epic readEpic(String[] split) {
        Epic epic = new Epic(split[2], split[4]);
        epic.setStatus(Status.valueOf(split[3]));
        ArrayList<Integer> list1 = new ArrayList<>();
        String[] subt;
        for (int i = 7; i < split.length; i++) {
            subt = split[i].split("\\, ");
            list1.addAll(new ArrayList<>(Arrays.stream(subt).filter((String sub) -> !sub.equals("[]")).map((String sub) -> Integer.parseInt(sub.replaceAll("^\\[|\\]$", "").trim())).collect(Collectors.toList())));
        }
        epic.setSubTasks(list1);
        epic.setDuration(Duration.ofMinutes(Integer.valueOf(split[5])));
        epic.setStartTime(LocalDateTime.parse(split[6], formatter));
        epic.setId(Integer.parseInt(split[0]));
        return epic;
    }

    public Set<Task> getTimeSort() {
        if (!divideByTime.isEmpty()) {
            saveSortedTree(divideByTime);
            return divideByTime;
        } else return new TreeSet<>();
    }

    @Override
    public Map<Integer, Task> getTasksWithId() {
        if (!tasks.isEmpty()) return tasks;
        else return new HashMap<>();
    }

    @Override
    public Map<Integer, Epic> getEpicsWithId() {
        if (!epics.isEmpty()) return epics;
        else return new HashMap<>();
    }

    @Override
    public Map<Integer, SubTask> getSubTasksWithId() {
        if (!subtasks.isEmpty()) return subtasks;
        else return new HashMap<>();
    }

    @Override
    public void delTasksByType(Types type) {
        switch (type) {
            case SIMPLE -> tasks.clear();
            case SUBEPIC -> {
                subtasks.clear();
                for (Epic epic : epics.values()) {
                    epic.delAllSubTasks();
                    epic.setStatus(Status.NEW);
                }
            }
            case EPIC -> {
                subtasks.clear();
                epics.clear();
            }
        }
    }

    @Override
    public void addTask(Task task) {
        if (!(task.getName() == null || task.getName().trim().isEmpty() || task.getStatus() == null || task.getType() == Types.EPIC || task.getType() == Types.SUBEPIC)) {
            try {
                addTaskBody(task);
                tasks.put((task.getId()), task);
            } catch (TimeCrossingException e) {
                logger.log(Level.SEVERE, "Перехвачено исключение: ", e);
                fh.close();
            }
        }
    }

    private void addDividedByTime(Task task) {
        if (!task.getStartTime().isEqual(LocalDateTime.parse("01.01.01 00:00", formatter))) divideByTime.add(task);
    }

    private void addTaskBody(Task task) throws TimeCrossingException {
        task.setId(task.getId() + count);
        save(task);
        addDividedByTime(task);
        ++count;
        Set d = Set.copyOf(divideByTime).stream().filter((Task tsk) -> ((!task.equals(tsk)) && task.getStartTime().isBefore(tsk.getEndTime()) && (task.getEndTime().isAfter(tsk.getStartTime())))).collect(Collectors.toSet());
        if (!d.isEmpty()) throw new TimeCrossingException("Пересечение времени с " + d + " у задачи " + task);
    }

    @Override
    public void addEpic(Epic epic) {
        if (!(epic.getName() == null || epic.getName().trim().isEmpty() || epic.getType() == Types.SIMPLE || epic.getType() == Types.SUBEPIC)) {
            try {
                addTaskBody(epic);
                epics.put(epic.getId(), epic);
            } catch (TimeCrossingException e) {
                logger.log(Level.SEVERE, "Перехвачено исключение: ", e);
                fh.close();
            }
        }
    }

    public void addSubTask(int epicId, SubTask subTask) {
        try {
            if (!epics.isEmpty() && !(subTask.getName() == null || subTask.getName().trim().isEmpty() || subTask.getStatus() == null) || subTask.getType() == Types.SIMPLE || subTask.getType() == Types.EPIC) {
                if (epics.containsKey(epicId)) {
                    subTask.setId(subTask.getId() + count);
                    Epic object = changeSubsInEpic(subTask, epicId, subTask.getId());
                    object.addSubTasks(subTask.getId());
                    ++count;
                    changeEpicStatus(object);
                    changeEpicTime(object);
                    save(object);
                    save(subTask);
                    addDividedByTime(object);
                    addDividedByTime(subTask);
                    for (Task tsk : divideByTime) {
                        if ((!subTask.equals(tsk)) && subTask.getStartTime().isBefore(tsk.getEndTime()) && (subTask.getEndTime().isAfter(tsk.getStartTime())) && subTask.getEpicHash() != tsk.getId())
                            throw new TimeCrossingException("Пересечение времени с задачей " + tsk + " у задачи " + subTask);
                    }
                }
            }
        } catch (TimeCrossingException e) {
            logger.log(Level.SEVERE, "Перехвачено исключение: ", e);
            fh.close();
        }
    }

    @Override
    public void updateTask(int id, Task task) {
        if (tasks.containsKey(id) && !(task.getName() == null || task.getName().trim().isEmpty() || task.getStatus() == null || task.getType() == Types.EPIC || task.getType() == Types.SUBEPIC))
            task.setId(id);
        tasks.put(id, task);
        save(task);
        updateTime(id, task, tasks.get(id));
    }

    private void updateTime(int id, Task task, Task task2) {
        if (!task.getStartTime().isEqual(LocalDateTime.parse("01.01.01 00:00", formatter))) {
            divideByTime.remove(task2);
            divideByTime.add(task);
        }
    }

    @Override
    public void updateEpic(int id, Epic task) {
        if (epics.containsKey(id) && !(task.getName() == null || task.getName().trim().isEmpty() || task.getType() == Types.SIMPLE || task.getType() == Types.SUBEPIC)) {
            ArrayList<Integer> subs = epics.get(id).getSubTasks();
            Status status = epics.get(id).getStatus();
            Duration duration = epics.get(id).getDuration();
            LocalDateTime time = epics.get(id).getStartTime();
            task.setStatus(status);
            task.setSubTasks(subs);
            task.setId(id);
            task.setDuration(duration);
            task.setStartTime(time);
            epics.put(id, task);
            save(task);
            updateTime(id, task, epics.get(id));
        }
    }

    @Override
    public void updateSubEpic(int id, SubTask task) {
        if (subtasks.containsKey(id) && !(task.getName() == null || task.getName().trim().isEmpty() || task.getStatus() == null || task.getType() == Types.SIMPLE || task.getType() == Types.EPIC)) {
            int epicHash = subtasks.get(id).getEpicHash();
            task.setId(id);
            Epic object1 = epics.get(subtasks.get(id).getEpicHash());
            Epic object = changeSubsInEpic(task, epicHash, id);
            subtasks.put(id, task);
            changeEpicStatus(object);
            changeEpicTime(object);
            save(object);
            save(task);
            updateTime(id, task, subtasks.get(id));
            updateTime(id, object, object1);
        }
    }

    @Override
    public List<Task> getTasks() {
        if (!tasks.isEmpty()) return new ArrayList<>(tasks.values());
        else return new ArrayList<>();
    }

    @Override
    public List<Epic> getEpics() {
        if (!epics.isEmpty()) return new ArrayList<>(epics.values());
        else return new ArrayList<>();
    }

    @Override
    public List<SubTask> getSubTasks() {
        if (!subtasks.isEmpty()) return new ArrayList<>(subtasks.values());
        else return new ArrayList<>();
    }

    @Override
    public int delTaskById(int id) {
        Task a = tasks.remove(id);
        if (a != null) return id;
        else return 0;
    }

    @Override
    public int delEpicById(int id) {
        if (epics.containsKey(id)) {
            for (int i : epics.get(id).getSubTasks())
                subtasks.remove(i);
            epics.remove(id);
            return id;
        } else return 0;
    }

    @Override
    public int delSubEpicById(int id) {
        if (subtasks.containsKey(id)) {
            int epicHash = subtasks.get(id).getEpicHash();
            subtasks.remove(id);
            epics.get(epicHash).delSubTask(id);
            Epic object = epics.get(epicHash);
            if (!object.getSubTasks().isEmpty()) {
                changeEpicStatus(object);
                changeEpicTime(object);
                updateTime(id, object, epics.get(id));
            } else {
                object.setStatus(Status.DONE);
                changeEpicTime(object);
                updateTime(id, object, epics.get(id));
            }
            return id;
        }
        return 0;
    }

    @Override
    public ArrayList<SubTask> getSubsByEpicId(int id) {
        if (epics.containsKey(id)) {
            ArrayList<SubTask> buffer = new ArrayList<>();
            for (SubTask obj : subtasks.values())
                if (obj.getEpicHash() == id) buffer.add(obj);
            return buffer;
        } else return new ArrayList<>();
    }

    @Override
    public Task getTaskById(int id) {
        return tasks.getOrDefault(id, new Task(null, null, null, 0, "01.01.01 00:00"));
    }

    @Override
    public SubTask getSubTaskById(int id) {
        return Map.copyOf(subtasks).getOrDefault(id, new SubTask(null, null, null, 0, "01.01.01 00:00"));
    }

    @Override
    public Epic getEpicById(int id) {
        return epics.getOrDefault(id, new Epic(null, null));
    }


    @Override
    public void changeEpicStatus(Epic object) {
        int progress = 0;
        int done = 0;
        int new1 = 0;
        for (int i : object.getSubTasks()) {
            SubTask sub = subtasks.get(i);
            switch (sub.getStatus()) {
                case NEW -> new1++;
                case IN_PROGRESS -> progress++;
                case DONE -> done++;
            }
        }
        if (progress > 0 || !(done == object.getSubTasks().size())) object.setStatus(Status.IN_PROGRESS);
        else object.setStatus(Status.DONE);
        if (new1 == object.getSubTasks().size()) object.setStatus(Status.NEW);
        save(object);
    }

    public void changeEpicTime(Epic object) {
        Duration duration = Duration.ZERO;
        LocalDateTime time = LocalDateTime.parse("01.01.01 00:00", formatter);
        for (int i : object.getSubTasks()) {
            duration = duration.plus(subtasks.get(i).getDuration());
            LocalDateTime startTime = subtasks.get(i).getStartTime();
            if (time.isEqual(LocalDateTime.parse("01.01.01 00:00", formatter)) || time.isAfter(startTime))
                time = startTime;
        }
        object.setDuration(duration);
        object.setStartTime(time);
    }

    @Override
    public Epic changeSubsInEpic(SubTask task, int epicHash, int id) {
        task.setEpicHash(epicHash);
        subtasks.put(id, task);
        return epics.get(epicHash);
    }

    private void save(Task task) {
        try {
            Path testFile = Paths.get(HOME, "Saves", "testFile.CSV");
            List<String> list = List.of(task.toStringForFile());
            if (!Files.exists(testFile)) {
                Files.createDirectory(Paths.get(HOME, "Saves"));
                testFile = Files.createFile(testFile);
                List<String> list1 = List.of("id,type,name,status,description,epic,duration,time");
                writeListToFile(list1, testFile.toString(), true);
            }
            List<String> list2 = readWordsFromFile(testFile.toString());
            String flag = null;
            for (String str : list2)
                if (task.toStringForFile().substring(0, 10).contains(str.substring(0, 10))) flag = str;
            if (flag != null) {
                list2.remove(flag);
                list2.add(task.toStringForFile());
                writeListToFile(list2, testFile.toString(), false);
            } else writeListToFile(list, testFile.toString(), true);
        } catch (IOException e) {
            e.initCause(new ManagerSaveException("Критическая ошибка при работе с файлом задач"));
            logger.log(Level.SEVERE, "Перехвачено исключение: ", e);
            logger.log(Level.SEVERE, "Причина: ", e.getCause());
            fh.close();
        }
    }

    private void saveSortedTree(Set<Task> divideByTime) {
        try {
            Path testFile = Paths.get(HOME, "Saves", "testFileSortedByTime.CSV");
            if (!Files.exists(testFile)) {
                testFile = Files.createFile(testFile);
                List<String> list1 = List.of("id,type,name,status,description,epic,duration,time");
                writeListToFile(list1, testFile.toString(), true);
            }
            writeSetToFile(divideByTime, testFile.toString(), false);
        } catch (IOException e) {
            e.initCause(new ManagerSaveException("Критическая ошибка при работе с файлом задач"));
            logger.log(Level.SEVERE, "Перехвачено исключение: ", e);
            logger.log(Level.SEVERE, "Причина: ", e.getCause());
            fh.close();
        }
    }

    private static void writeSetToFile(Set<Task> divideByTime, String filename, boolean append) {
        try (FileWriter out = new FileWriter(filename, StandardCharsets.UTF_8, append)) {
            for (Task str : divideByTime)
                out.write(str.toStringForFile() + "\n");
        } catch (IOException e) {
            e.initCause(new ManagerSaveException("Критическая ошибка при записи файла задач"));
            logger.log(Level.SEVERE, "Перехвачено исключение: ", e);
            logger.log(Level.SEVERE, "Причина: ", e.getCause());
            fh.close();
        }
    }

    private static void writeListToFile(List<String> list, String filename, boolean append) {
        try (FileWriter out = new FileWriter(filename, StandardCharsets.UTF_8, append)) {
            for (String str : list)
                out.write(str + "\n");
        } catch (IOException e) {
            e.initCause(new ManagerSaveException("Критическая ошибка при записи файла задач"));
            logger.log(Level.SEVERE, "Перехвачено исключение: ", e);
            logger.log(Level.SEVERE, "Причина: ", e.getCause());
            fh.close();
        }
    }

    private static List<String> readWordsFromFile(String filename) {
        List<String> a = new ArrayList<>();
        try (FileReader in = new FileReader((filename), StandardCharsets.UTF_8); BufferedReader br = new BufferedReader(in)) {
            while (br.ready()) {
                String line = br.readLine();
                a.add(line);
            }
        } catch (IOException e) {
            e.initCause(new ManagerSaveException("Критическая ошибка при чтении файла задач"));
            logger.log(Level.SEVERE, "Перехвачено исключение: ", e);
            logger.log(Level.SEVERE, "Причина: ", e.getCause());
            fh.close();
        }
        return a;
    }

    private static void setLogger() {
        logger = Logger.getLogger(FileBackedTaskManager.class.getName());
        try {
            String pattern = "yyyyMMddhhmmss";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            String date = simpleDateFormat.format(new Date());
            fh = new FileHandler("MyLogFile" + ".log", false);
            logger.addHandler(fh);
            fh.setFormatter(new SimpleFormatter());
            logger.info("Log message");
        } catch (SecurityException | IOException e) {
            logger.log(Level.SEVERE, "Произошла ошибка при работе с FileHandler.", e);
        }
    }

    public static List<String> loggerPrint() {
        Path testFile = Paths.get("MyLogFile.log");
        List<String> list = readWordsFromFile(testFile.toString());
        return list;
    }
}
