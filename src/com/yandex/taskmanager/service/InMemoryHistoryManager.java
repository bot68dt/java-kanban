package com.yandex.taskmanager.service;

import java.time.LocalDateTime;
import java.util.*;

import com.yandex.taskmanager.interfaces.HistoryManager;
import com.yandex.taskmanager.model.Task;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, DoublyLinkedList.Node> tasks;
    private final DoublyLinkedList<Task> doublyLinkedList = new DoublyLinkedList<>();


    public InMemoryHistoryManager() {
        tasks = new HashMap<>();
    }

    @Override
    public void add(Task task) {
        DoublyLinkedList.Node node;
        if (task != null && task.getId() != 0) {
            node = doublyLinkedList.linkLast(task);
            if (node != null) tasks.put(task.getId(), node);
        }
    }

    @Override
    public List<Task> getHistory() {
        return List.copyOf(doublyLinkedList.getTasks());
    }

    @Override
    public void remove(int id) {
        DoublyLinkedList.Node n = tasks.remove(id);
        doublyLinkedList.removeNode(n);
    }

    public Map<Integer, DoublyLinkedList.Node> getMapHistory() {
        return Map.copyOf(tasks);
    }

    public static class DoublyLinkedList<T extends Task> {
        private Node<T> head = null;
        private Node<T> tail = null;
        int size = 0;

        private Node moveNodeToTail(Node<T> newNode, Node<T> oldTail) {
            Node node;
            tail = newNode;
            if (oldTail == null) head = newNode;
            else oldTail.next = newNode;
            node = newNode;
            return node;
        }

        private void addNewNodeInHead(Node<T> n) {
            if (n.prev == null && size != 1) {
                final Node<T> newHead = head.next;
                newHead.prev = null;
                this.head = newHead;
            }
        }

        private void addNewNodeInBetween(Node<T> n) {
            if (n.prev != null && n.next != null) {
                final Node oldPrev = n.prev;
                final Node oldNext = n.next;
                oldPrev.next = oldNext;
                oldNext.prev = oldPrev;
            }
        }

        private Node linkLast(T task) {
            boolean noElem = true;
            Node node = null;
            final Node<T> newNode = new Node<>(tail, task, null);
            final Node<T> oldTail = tail;
            if (size != 0 && task != null) {
                Node n = head;
                for (int i = 0; i < size; i++) {
                    if (n.data.equals(task) && n.next != null) {
                        addNewNodeInHead(n);
                        addNewNodeInBetween(n);
                        node = moveNodeToTail(newNode, oldTail);
                        noElem = false;
                    } else if (task.equals(n.data) && n.next == null) noElem = false;
                    n = n.next;
                }
            }
            if (noElem) {
                node = moveNodeToTail(newNode, oldTail);
                size++;
            }
            return node;
        }

        private T getLast() {
            final Node<T> curTail = tail;
            if (curTail == null) throw new NoSuchElementException();
            return tail.data;
        }

        private List<Task> getTasks() {
            List a = new ArrayList<Task>();
            Node b = head;
            for (int i = 0; i < size; i++) {
                a.add(b.data);
                b = b.next;
            }
            return a;
        }

        private void removeNode(Node n) {
            if (n != null && size > 1) {
                if (n.prev == null && n.next != null) {
                    head = n.next;
                } else if (n.next == null && n.prev != null) {
                    tail = n.prev;
                } else if (n.next != null && n.prev != null) {
                    n.prev.next = n.next;
                    n.next.prev = n.prev;
                }
                size--;
            }
            if (n != null && size == 1 && n.next == null && n.prev == null) {
                head = null;
                tail = null;
                size = 0;
            }
        }

        private static class Node<T extends Task> {
            private final T data;
            private Node<T> next;
            private Node<T> prev;

            private Node(Node<T> prev, T data, Node<T> next) {
                this.data = data;
                this.next = next;
                this.prev = prev;
            }

            @Override
            public String toString() {
                return "Task{" + "name='" + this.data.getName() + '\'' + ", description='" + this.data.getDescription() + '\'' + ", status=" + this.data.getStatus() + ", type=" + this.data.getType() + '}' + '\n';
            }
        }
    }
}

