package ru.yandex.practicum.tasks;

public class Subtask extends Task {
    private int epicId;

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    @Override
    public String toCsvLine() {
        return String.join(",", new String[]{String.valueOf(this.getId()),
                String.valueOf(this.getType()), this.getName(),
                String.valueOf(this.getStatus()), this.getDescription(), String.valueOf(this.getEpicId())});
    }

    @Override
    public Task fromCsvLine(String[] taskData) {
        super.fromCsvLine(taskData);
        this.setEpicId(Integer.parseInt(taskData[5]));
        return this;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                ", id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                '}';
    }
}
