package deque;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {
    private class Node {
        private T item;
        private Node next;
        private Node prev;

        Node(T item) {
            this.item = item;
            this.next = null;
            this.prev = null;
        }
    }

    private Node sentinelHead;
    private Node sentinelTail;
    private int size;

    public LinkedListDeque() {
        sentinelHead = new Node(null);
        sentinelTail = new Node(null);
        sentinelHead.next = sentinelTail;
        sentinelTail.prev = sentinelHead;
        size = 0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void addFirst(T item) {
        Node newNode = new Node(item);
        newNode.next = sentinelHead.next;
        newNode.prev = sentinelHead;
        sentinelHead.next.prev = newNode;
        sentinelHead.next = newNode;
        size++;
    }

    @Override
    public void addLast(T item) {
        Node newNode = new Node(item);
        newNode.prev = sentinelTail.prev;
        sentinelTail.prev.next = newNode;
        newNode.next = sentinelTail;
        sentinelTail.prev = newNode;
        size++;
    }

    @Override
    public void printDeque() {
        Node curr = sentinelHead.next;
        while (curr != sentinelTail) {
            System.out.print(curr.item + " ");
            curr = curr.next;
        }
        System.out.println();
    }

    @Override
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        Node first = sentinelHead.next;
        sentinelHead.next = first.next;
        first.next.prev = sentinelHead;
        size--;
        return first.item;
    }

    @Override
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        Node last = sentinelTail.prev;
        last.prev.next = sentinelTail;
        sentinelTail.prev = last.prev;
        size--;
        return last.item;
    }

    @Override
    public T get(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        Node curr = sentinelHead.next;
        for (int i = 0; i < index; i++) {
            curr = curr.next;
        }
        return curr.item;
    }

    public T getRecursive(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        return getRecursiveHelper(sentinelHead.next, index);
    }

    private T getRecursiveHelper(Node curr, int index) {
        if (index == 0) {
            return curr.item;
        }
        return getRecursiveHelper(curr.next, index - 1);
    }


    public Iterator<T> iterator() {
        return new DequeIterator();
    }

    private class DequeIterator implements Iterator<T> {
        private Node curr;

        DequeIterator() {
            curr = sentinelHead.next;
        }

        @Override
        public boolean hasNext() {
            return curr != sentinelTail;
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            T value = curr.item;
            curr = curr.next;
            return value;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || !(o instanceof Deque)) {
            return false;
        }
        Deque<?> other = (Deque<?>) o;
        if (other.size() == this.size()) {
            for (int i = 0; i < this.size(); i++) {
                Object thisItem = this.get(i);
                Object otherItem = other.get(i);
                if (thisItem == null && otherItem != null) {
                    return false;
                }
                if (thisItem != null && !thisItem.equals(otherItem)) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }
}



