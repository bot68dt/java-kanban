import TaskTypes.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Main
{
    public static void main(String[] args)
    {
        TaskManager taskManager = new TaskManager();
        System.out.println("Поехали!");
        taskManager.addTask(new Task("Поспать","Полежать на кровати", Status.NEW));
        taskManager.addTask(new Task("Покушать","Завтрак", Status.NEW));
        taskManager.addTask(new Task("Поспать","Полежать на кровати", Status.NEW));

        taskManager.addEpic(new Epic("Пройти теорию 4-ого спринта","Уложиться в неделю"), new ArrayList<>(Arrays.asList(new SubTask("Поспать", "Полежать на кровати", Status.NEW),
                                                                                                                                            new SubTask("Выполнить программу курса", "Читать внимательно", Status.NEW))));
        taskManager.addEpic(new Epic("Выполнить финальное задание 4-ого спринта","Сделать без подсказок из задания"), new ArrayList<>(Arrays.asList(new SubTask("Хорошо выспаться", "Минимум - 7 часов", Status.NEW))));

        System.out.println(taskManager.getTasksByType(Types.EPIC));
        System.out.println(taskManager.getTasksByType(Types.SUBEPIC));
        System.out.println(taskManager.getTasksByType(Types.SIMPLE));
        taskManager.updateTask(-752483278,Types.SIMPLE, new Task("Поспать","Полежать на кровати", Status.DONE));
        taskManager.updateTask(-90941132,Types.SIMPLE, new Task("Покушать","Завтрак", Status.IN_PROGRESS));
        taskManager.updateTask(-454382135,Types.EPIC, new Epic("Пройти теорию 4-ого спринта","Раньше недели"));
        taskManager.updateTask(-752483278,Types.SUBEPIC, new SubTask("Поспать","Полежать на кровати", Status.DONE));
        taskManager.updateTask(314140945,Types.SUBEPIC, new SubTask("Выполнить программу курса","Читать внимательно", Status.DONE));
        taskManager.updateTask(-869297987,Types.SUBEPIC, new SubTask("Хорошо выспаться","Минимум - 7 часов", Status.IN_PROGRESS));
        taskManager.addSubTask(322416025, new SubTask("Хорошо выспаться","Минимум - 7 часов", Status.IN_PROGRESS));
        System.out.println();
        System.out.println(taskManager.getTasksByType(Types.EPIC));
        System.out.println();
        System.out.println(taskManager.getTasksByType(Types.SUBEPIC));
        System.out.println();
        System.out.println(taskManager.getTasksByType(Types.SIMPLE));
        taskManager.delTaskById(-1610671478,Types.SIMPLE);
        System.out.println(taskManager.getTasksByType(Types.SIMPLE));
        taskManager.delTaskById(1905938963,Types.SUBEPIC);
        System.out.println(taskManager.getTasksByType(Types.SUBEPIC));
        System.out.println(taskManager.getTasksByType(Types.EPIC));
        taskManager.delTaskById(-576385608,Types.EPIC);
        System.out.println(taskManager.getTasksByType(Types.SUBEPIC));
        System.out.println(taskManager.getTasksByType(Types.EPIC));
        taskManager.addSubTask(322416025, new SubTask("Хорошо выспаться","Минимум - 7 часов", Status.IN_PROGRESS));
        System.out.println(taskManager.getTasksByType(Types.SUBEPIC));
        System.out.println(taskManager.getTasksByType(Types.EPIC));
        taskManager.delTaskById(-1158460960,Types.SUBEPIC);
        System.out.println(taskManager.getTasksByType(Types.SUBEPIC));
        System.out.println(taskManager.getTasksByType(Types.EPIC));
        taskManager.delTaskById(1905938963,Types.SUBEPIC);
        System.out.println(taskManager.getTasksByType(Types.SUBEPIC));
        System.out.println(taskManager.getTasksByType(Types.EPIC));
    }
}
