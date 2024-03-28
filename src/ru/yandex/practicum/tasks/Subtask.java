package ru.yandex.practicum.tasks;

import ru.yandex.practicum.tasks.specifications.Status;
import ru.yandex.practicum.tasks.specifications.Type;

public class Subtask extends Task{
    protected final Type TYPE = Type.SUBTASK;
    protected int epicId;

    public Subtask(String name, String description, int epicId) {
        super(name, description);
        this.epicId = epicId;
    }

    public Subtask(int id, String name, String description, Status status) {
        super(id, name, description, status);
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public Type getTYPE() {
        return TYPE;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "TYPE=" + TYPE +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}
