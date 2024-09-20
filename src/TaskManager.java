import TaskTypes.*;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private final HashMap<Types, ArrayList<Object>> contacts;
    private final static Task TASK = new Task(null, null, null);
    private final static SubTask SUBTASK = new SubTask(null, null, null);
    private final static Epic EPIC = new Epic(null, null);

    public TaskManager() {
        contacts = new HashMap<>();
    }

    public void addTask(Object obj) {
        if (obj.getClass() == TASK.getClass()) {
            ArrayList<Object> Tasks = contacts.get(Types.SIMPLE);
            repeatedExpression1(Tasks, obj, Types.SIMPLE);
        }
        if (obj.getClass() == SUBTASK.getClass()) {
            System.out.println("Нельзя создать подзадачу без эпика");
        }
        if (obj.getClass() == EPIC.getClass()) {
            System.out.println("Для создания эпика есть команда addEpic");
        }
    }

    public void addSubTask(int epicId, Object obj)
    {
        ArrayList<Object> Tasks1 = contacts.get(Types.SUBEPIC);
        ArrayList<Object> Tasks = contacts.get(Types.EPIC);
        Object epic = null;
        ArrayList<Integer> subtasks;
        if (obj.getClass() == SUBTASK.getClass())
        {
            ((SubTask) obj).setEpicHash(epicId);
            for (Object object :Tasks)
            {
                if(((Epic) object).getId() == epicId)
                {
                    ((Epic) object).setSubTasks(((SubTask) obj).getId());
                    epic=object;
                    repeatedExpression1(Tasks1, obj, Types.SUBEPIC);
                    break;
                }
            }
        }
        if (((Epic)epic).getSubTasks().isEmpty())
            ((Epic)epic).getSubTasks().add(epicId);
        subtasks = ((Epic)epic).getSubTasks();
        repeatedExpression2(subtasks, Tasks1, epic);
    }

    public void addEpic(Object obj, ArrayList<Object> objs)
    {
        if (obj.getClass() == EPIC.getClass()) {
            ArrayList<Object> Tasks = contacts.get(Types.EPIC);
            repeatedExpression1(Tasks,obj, Types.EPIC);
        }

        for (Object object : objs) {
            if (object.getClass() == SUBTASK.getClass())
            {
                ((SubTask) object).setEpicHash(obj.hashCode());
                ((Epic) obj).addSubTasks(((SubTask) object).getId());
                ArrayList<Object> Tasks = contacts.get(Types.SUBEPIC);
                repeatedExpression1(Tasks,object,Types.SUBEPIC);
            }
        }
    }

    public HashMap<Types, ArrayList<Object>> getAllTasks() {
        return contacts;
    }

    public ArrayList<Object> getTasksByType(Types type) {
        return contacts.get(type);
    }

    public ArrayList<Object> delTasksByType(Types type) {
        return contacts.put(type, null);
    }

    public ArrayList<Object> getTasksById(int id) {
        ArrayList<Object> buffer = new ArrayList<>();
        for (ArrayList<Object> obj : contacts.values())
            for (Object object : obj) {
                int objId = ((Task) object).getId();
                if (objId == id)
                    buffer.add(object);
            }
        return buffer;
    }

    public void updateTask(int id, Types type, Object task) {
        int objId;
        int j = -1;
        int epicHash=0;
        int epicId;
        int m = -1;
        ArrayList<Integer> subs;

        switch (type) {
            case SIMPLE -> {
                ArrayList<Object> Tasks = contacts.get(Types.SIMPLE);
                for (int i = 0; i < Tasks.size(); i++) {
                    for (Object tsk : Tasks) {
                        objId = ((Task) tsk).getId();
                        if (objId == id && !(task.equals(tsk))){
                            repeatedExpression3(Tasks, tsk, task, Types.SIMPLE);
                            break;
                        }
                    }
                }
            }
            case SUBEPIC -> {
                ArrayList<Object> Tasks = contacts.get(Types.SUBEPIC);
                ArrayList<Object> Tasks1 = contacts.get(Types.EPIC);
                ArrayList<Integer> subtasks = null;
                for (int i = 0; i < Tasks.size(); i++)
                {
                    for (Object tsk : Tasks) {
                        objId = ((SubTask) tsk).getId();
                        if (objId == id && !(task.equals(tsk)))
                        {
                            epicHash = ((SubTask) tsk).getEpicHash();
                            ((SubTask) task).setEpicHash(epicHash);
                            repeatedExpression3(Tasks, tsk, task, Types.SUBEPIC);
                            break;
                        }
                    }
                   for (int n = 0; n < Tasks1.size(); n++) {
                        for (Object tsk1 : Tasks1)
                        {
                            epicId = ((Epic) tsk1).getId();
                            if (epicId == epicHash) {
                                subtasks = ((Epic) tsk1).getSubTasks();
                                for (int k : subtasks)
                                {
                                    m++;
                                    if (k == id) {
                                        int o = ((SubTask) task).getId();
                                        ((Epic) tsk1).delSubTasks(m);
                                        ((Epic) tsk1).setSubTasks(m,o);
                                        break;
                                    }
                                }
                            }
                        }
                        m=-1;
                   }
                }
                for (Object tsk1 : Tasks1)
                {
                    assert subtasks != null;
                    repeatedExpression2(subtasks, Tasks, tsk1);
                }

            }
            case EPIC ->
            {
                Status status;
                ArrayList<Object> Tasks = contacts.get(Types.EPIC);
                for (int i = 0; i < Tasks.size(); i++)
                {
                    for (Object tsk : Tasks) {
                        j++;
                        objId = ((Epic) tsk).getId();
                        if (objId == id) {
                            subs = ((Epic) tsk).getSubTasks();
                            status = ((Epic) tsk).getStatus();
                            Tasks.remove(j);
                            ((Epic) task).resetSubTasks(subs);
                            ((Epic) task).setStatus(status);
                            Tasks.add(j, task);
                            contacts.put(Types.EPIC, Tasks);
                            break;
                        }
                    }
                    j=-1;
                }
                ArrayList<Object> Tasks1 = contacts.get(Types.SUBEPIC);
                objId = ((Epic) task).getId();
                for (Object tsk : Tasks1)
                {
                    epicHash = ((SubTask) tsk).getEpicHash();
                    if(epicHash == id)
                        ((SubTask) tsk).setEpicHash(objId);
                }
            }
        }
    }

    public void delTaskById(int id, Types type) {
        int objId;
        int j = -1;
        int epicHash=0;
        int epicId;
        int m = -1;

        switch (type) {
            case SIMPLE ->
            {
                ArrayList<Object> Tasks = contacts.get(Types.SIMPLE);
                for (int i = 0; i < Tasks.size(); i++)
                {
                    for (Object tsk : Tasks)
                    {
                        j++;
                        objId = ((Task) tsk).getId();
                        if (objId == id) {
                            Tasks.remove(j);
                            contacts.put(Types.SIMPLE, Tasks);
                            break;
                        }
                    }

                    j = -1;
                }
            }
            case SUBEPIC -> {
                ArrayList<Object> Tasks = contacts.get(Types.SUBEPIC);
                ArrayList<Object> Tasks1 = contacts.get(Types.EPIC);
                ArrayList<Integer> subtasks = new ArrayList<>();
                for (int i = 0; i < Tasks.size(); i++)
                {
                    for (Object tsk : Tasks)
                    {
                        j++;
                        objId = ((SubTask) tsk).getId();
                        if (objId == id)
                        {
                            epicHash = ((SubTask) tsk).getEpicHash();
                            Tasks.remove(j);
                            contacts.put(Types.SUBEPIC, Tasks);
                            break;
                        }

                    }
                    j = -1;
                    for (int n = 0; n < Tasks1.size(); n++) {
                        for (Object tsk1 : Tasks1)
                        {
                            epicId = ((Epic) tsk1).getId();
                            if (epicId == epicHash)
                            {
                                subtasks = ((Epic) tsk1).getSubTasks();
                                for (int k : subtasks)
                                {
                                    m++;
                                    if (k == id)
                                    {
                                        ((Epic) tsk1).delSubTasks(m);
                                        break;
                                    }
                                }
                            }
                        }
                            m = -1;
                    }
                }
                for (Object tsk1 : Tasks1)
                {
                    repeatedExpression2(subtasks,Tasks,tsk1);
                }
            }
            case EPIC ->
            {
                ArrayList<Object> Tasks = contacts.get(Types.EPIC);
                ArrayList<Integer> subtasks = new ArrayList<>();
                for (int i = 0; i < Tasks.size(); i++)
                {
                    for (Object tsk : Tasks) {
                        j++;
                        objId = ((Epic) tsk).getId();
                        if (objId == id)
                        {
                            subtasks = ((Epic) tsk).getSubTasks();
                            Tasks.remove(j);
                            contacts.put(Types.EPIC, Tasks);
                            break;
                        }
                    }
                    j = -1;
                }

                ArrayList<Object> Tasks1 = contacts.get(Types.SUBEPIC);
                for (int i = 0; i < subtasks.size(); i++)
                {
                    for (Object tsk : Tasks1)
                    {
                        j++;
                        epicHash = ((SubTask) tsk).getEpicHash();
                        if(epicHash == id)
                        {
                            Tasks1.remove(j);
                            contacts.put(Types.SUBEPIC, Tasks1);
                            break;
                        }
                    }
                    j = -1;
                }
            }
        }
    }

    public ArrayList<Object> getSubsByEpicId(int id)
    {
        ArrayList<Object> buffer = new ArrayList<>();
        ArrayList<Object> Tasks = contacts.get(Types.SUBEPIC);
        for (Object obj : Tasks)
        {
                int objId = ((SubTask) obj).getEpicHash();
                if (objId == id)
                    buffer.add(obj);
            }
        return buffer;
    }

    public void repeatedExpression1(ArrayList<Object> task, Object object, Types type)
    {
        if (task == null)
            task = new ArrayList<>();
        task.add(object);
        contacts.put(type, task);
    }

    public void repeatedExpression2(ArrayList<Integer> subtasks, ArrayList<Object> tasks, Object object)
    {
        int progress=0;
        int done =0;
        for (int i : subtasks)
        {
            for (Object tsk : tasks)
            {
                if (((SubTask) tsk).getId() == i && ((SubTask) tsk).getStatus() == Status.IN_PROGRESS)
                    progress++;
                if (((SubTask) tsk).getId() == i && ((SubTask) tsk).getStatus() == Status.DONE)
                    done++;
                if ((((Epic) object).getId() == ((SubTask)tsk).getEpicHash() && progress > 0) || ((((Epic) object).getId() == ((SubTask)tsk).getEpicHash() && done > 0) && done < subtasks.size()))
                    ((Epic) object).setStatus(Status.IN_PROGRESS);
                if (((Epic) object).getId() == ((SubTask)tsk).getEpicHash() && done == subtasks.size())
                    ((Epic) object).setStatus(Status.DONE);
            }
        }
        if (((Epic)object).getSubTasks().isEmpty())
            ((Epic)object).setStatus(Status.DONE);
    }

    public void repeatedExpression3(ArrayList<Object> task, Object object1, Object object2, Types type)
    {
        task.remove(object1);
        task.add(object2);
        contacts.put(type, task);
    }
}