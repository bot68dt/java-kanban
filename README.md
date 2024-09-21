# java-kanban
Repository for homework project.

# Функции com.yandex.taskmanager.service.TaskManager:
В функциях сознательно добавлял поле типа задачи, чтобы облегчить код. Одна переменная убирает
необходимость писать кейс из трёх условий на проверку типа

addTask(Object obj) -добавить простую задачу, аргумент - объект простая задача

addSubTask(int epicId, Object obj) - добавить подзадачу, epicId - Ид эпика, к которому она
относится, obj - задача типа Subtask

addEpic(Object obj, ArrayList<Object> objs) - добавить эпик и список подзадач в нём.
obj - задача типа Epic
objs - список подзадач типа Subtask

getAllTasks() - вывод всех задач в com.yandex.taskmanager.service.TaskManager

getTasksByType(Types type) - вывод задач по их типу: EPIC, SUBEPIC, SIMPLE

delTasksByType(Types type) - удаление всех задач выбранного типа. Считаю данный метод неконструктивным,
но в условии он требуется. Считаю более эффективным и правильным удаление по ИДентификатору

getTasksById(int id) - получить список задач по заданному идентификатору

updateTask(int id, Types type, Object task) - обновление существующей задачи, аргументы - 
id задачи, её тип, новый объект задачи

delTaskById(int id, Types type) - удаление задачи по её id, аргументы - ид задачи и её тип

getSubsByEpicId(int id) - получение списка подзадач по Ид задачи-эпика

Далее идут 3 чисто технических функции-сокращения, которые не понадобятся для тестировщика или фронт-энд
