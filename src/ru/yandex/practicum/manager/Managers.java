package ru.yandex.practicum.manager;

import java.io.File;

public final class Managers {
    private static final File FILE = new File("resources/data.csv");
    private static TaskManager inMemoryTaskManager;
    private static TaskManager fileBackedTaskManager;
    private static HistoryManager historyManager;

    private Managers() {
    }

    public static TaskManager getDefault() {
        if (inMemoryTaskManager == null) {
            inMemoryTaskManager = new InMemoryTaskManager();
        }

        return inMemoryTaskManager;
    }

    public static TaskManager getDefaultSave() {
        if (fileBackedTaskManager == null) {
            fileBackedTaskManager = FileBackedTaskManager.loadFromFile(FILE);
        }

        return fileBackedTaskManager;
    }

    public static HistoryManager getDefaultHistory() {
        if (historyManager == null) {
            historyManager = new InMemoryHistoryManager();
        }

        return historyManager;
    }
}