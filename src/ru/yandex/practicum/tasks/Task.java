package ru.yandex.practicum.tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    private int id;
    private String name;
    private String description;
    private TaskStatus status;
    private Duration duration;
    private LocalDateTime startTime;

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

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        if (duration != null && startTime != null) {
            return startTime.plusMinutes(duration.toMinutes());
        } else {
            return null;
        }
    }

    public TaskType getType() {
        return TaskType.TASK;
    }

    public String toCsvLine() {
        return String.join(",", new String[]{
                String.valueOf(this.getId()),
                String.valueOf(this.getType()),
                this.getName(),
                String.valueOf(this.getStatus()),
                this.getDescription(),
                this.getDuration() != null ? String.valueOf(this.getDuration().toMinutes()) : "null",
                this.getStartTime() != null ? this.getStartTime().toString() : "null"
        });
    }

    public Task fromCsvLine(String[] taskData) {
        this.setId(Integer.parseInt(taskData[0]));
        this.setName(taskData[2]);
        this.setStatus(TaskStatus.valueOf(taskData[3]));
        this.setDescription(taskData[4]);
        this.setDuration("null".equals(taskData[5]) ? null : Duration.ofMinutes(Long.parseLong(taskData[5])));
        this.setStartTime("null".equals(taskData[6]) ? null : LocalDateTime.parse(taskData[6]));

        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && status == task.status
                && Objects.equals(duration, task.duration) && Objects.equals(startTime, task.startTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, status, duration, startTime);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + getId() + '\'' +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() + '\'' +
                ", duration=" + getDuration() + '\'' +
                ", startTime=" + getStartTime() +
                '}';
    }
}
