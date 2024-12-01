package com.yandex.taskmanager.model;

import com.yandex.taskmanager.constant.Status;
import com.yandex.taskmanager.constant.Types;

public class SubTask extends Task {
    private int epicHash;

    public SubTask(String name, String description, Status status, int duration, String startTime) {
        super(name, description, status, duration, startTime);
        type = Types.SUBEPIC;
    }

    public int getEpicHash() {
        return epicHash;
    }

    public int setEpicHash(int hash) {
        return epicHash = hash;
    }

    @Override
    public String toString() {
        return "SubTask{" + "epicHash=" + epicHash + "} " + super.toString();
    }

    @Override
    public String toStringForFile() {
        return super.toStringForFile() + "," + epicHash;
    }
}
