package ru.yandex.practicum.manager;

import ru.yandex.practicum.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> history = new HashMap<>();
    private Node head;
    private Node tail;

    @Override
    public void add(Task task) {
        if (history.containsKey(task.getId())) {
            remove(task.getId());
        }
        linkLast(task);
    }

    @Override
    public void remove(int id) {
        if (history.containsKey(id)) {
            removeNode(history.get(id));
            history.remove(id);
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private void linkLast(Task task) {
        Node oldTail = tail;
        Node newNode = new Node(task);
        newNode.prev = oldTail;
        newNode.next = null;
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.next = newNode;
        }
        history.put(task.getId(), newNode);
    }

    private List<Task> getTasks() {
        List<Task> list = new ArrayList<>();
        Node node = head;
        while (node != null) {
            list.add(node.task);
            node = node.next;
        }
        return list;
    }

    private void removeNode(Node node) {
        if (head == tail) {
            head = null;
            tail = null;
            return;
        }

        if (node == head) {
            head = head.next;
            head.prev = null;
            return;
        }

        if (node == tail) {
            tail = tail.prev;
            tail.next = null;
            return;
        }

        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    private static class Node {
        Node prev;
        Node next;
        Task task;

        public Node(Task task) {
            this.task = task;
        }
    }
}