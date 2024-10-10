package ru.yandex.practicum.exceptions;

public class TaskConflictException extends RuntimeException {
    public TaskConflictException() {
        super("Task conflict detected: The task overlaps with an existing one.");
    }
}