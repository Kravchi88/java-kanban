package ru.yandex.practicum.tasks;

import ru.yandex.practicum.tasks.specifications.Type;

import java.util.ArrayList;
import java.util.List;


public class Epic extends Task{
    protected final Type TYPE = Type.EPIC;
    protected List<Integer> listOfSubtasks = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
    }


    public Epic(int id, String name, String description) {
        super(name, description);
        this.id = id;
    }

    public void setListOfSubtasks(List<Integer> listOfSubtasks) {
        this.listOfSubtasks = listOfSubtasks;
    }

    public List<Integer> getListOfSubtasks() {
        return listOfSubtasks;
    }

    @Override
    public Type getTYPE() {
        return TYPE;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "TYPE=" + TYPE +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}
