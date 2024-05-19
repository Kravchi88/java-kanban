package ru.yandex.practicum.tasks;

import java.util.ArrayList;
import java.util.List;


public class Epic extends Task {
    private List<Integer> subtaskIds = new ArrayList<>();
    private final TaskType type = TaskType.EPIC;

    public void setSubtaskIds(List<Integer> subtaskIds) {
        this.subtaskIds = subtaskIds;
    }

    public List<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    @Override
    public TaskType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Epic{" +
                ", id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                '}';
    }
}
