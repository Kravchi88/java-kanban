package ru.yandex.practicum.manager;

import ru.yandex.practicum.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager<T extends Task> implements HistoryManager<T> {
    private final Map<Integer, Node<T>> history = new HashMap<>();
    private Node<T> head;
    private Node<T> tail;

    @Override
    public void addToHistory(T instance) {
        if (history.containsKey(instance.getId())) {
            removeFromHistory(instance.getId());
        }
        linkLast(instance);
    }

    @Override
    public void removeFromHistory(int id) {
        removeNode(history.get(id));
    }

    @Override
    public List<T> getHistory() {
        return getTasks();
    }

    private void linkLast(T instance) {
        Node<T> oldTail = tail;
        Node<T> newNode = new Node<>(oldTail, null, instance);
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.next = newNode;
        }
        history.put(instance.getId(), newNode);
    }

    private List<T> getTasks() {
        List<T> list = new ArrayList<>();
        Node<T> node = head;
        while (node != null) {
            list.add(node.instance);
            node = node.next;
        }
        return list;
    }

    private void removeNode(Node<T> node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    class Node<E> {
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