package ru.yandex.practicum.manager;

import ru.yandex.practicum.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node<Task>> history = new HashMap<>();
    private Node<Task> head;
    private Node<Task> tail;

    @Override
    public void addToHistory(Task task) {
        if (history.containsKey(task.getId())) {
            removeFromHistory(task.getId());
        }
        linkLast(task);
    }

    @Override
    public void removeFromHistory(int id) {
        removeNode(history.get(id));
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private void linkLast(Task task) {
        Node<Task> oldTail = tail;
        Node<Task> newNode = new Node<>(oldTail, null, task);
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
        Node<Task> node = head;
        while (node != null) {
            list.add(node.instance);
            node = node.next;
        }
        return list;
    }

    private void removeNode(Node<Task> node) {
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

    static class Node<E> {
        Node<E> prev;
        Node<E> next;
        E instance;

        public Node(Node<E> prev, Node<E> next, E instance) {
            this.prev = prev;
            this.next = next;
            this.instance = instance;
        }
    }
}