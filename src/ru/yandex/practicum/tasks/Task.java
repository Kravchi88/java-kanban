package ru.yandex.practicum.tasks;

import java.util.Objects;

public class Task {
    private int id;
    private String name;
    private String description;
    private TaskStatus status;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TaskType getType() {
        return TaskType.TASK;
    }

    public String toCsvLine() {
        return String.join(",", new String[]{String.valueOf(this.getId()), String.valueOf(this.getType()),
                this.getName(), String.valueOf(this.getStatus()), this.getDescription()});
    }

    public Task fromCsvLine(String[] taskData) {
        this.setId(Integer.parseInt(taskData[0]));
        this.setName(taskData[2]);
        this.setStatus(TaskStatus.valueOf(taskData[3]));
        this.setDescription(taskData[4]);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Task task)) {
            return false;
        }
        return getId() == task.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return "Task{" +
                ", id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                '}';
    }
}
