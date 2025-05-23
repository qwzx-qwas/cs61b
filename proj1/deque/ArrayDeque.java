package deque;

import java.util.Iterator;
import java.util.NoSuchElementException;

// 使用循环队列，避免了在执行 addFirst 等操作时，需将原来数组整体移动，而是采用滚动模式
public class ArrayDeque<T> implements Deque<T> {
    private T[] items;
    private int head;      // 头指针
    private int tail;      // 尾指针
    private int size;
    private int capacity;  // 容量

    public ArrayDeque() {
        capacity = 8;
        items = (T[]) new Object[capacity];
        head = 0;
        tail = -1;
        size = 0;
    }

    @Override
    public void addFirst(T x) {
        if (size == capacity) {
            resize(size * 2);
        }
        head = (head - 1 + capacity) % capacity;
        if (size == 0) {
            tail = head;
        }
        items[head] = x;
        size++;
    }

    @Override
    public void addLast(T x) {
        if (size == capacity) {
            resize(size * 2);
        }
        tail = (tail + 1 + capacity) % capacity;
        items[tail] = x;
        size++;
    }

    private void resize(int newCapacity) {
        T[] a = (T[]) new Object[newCapacity];
        for (int i = 0; i < size; i++) {
            a[i] = items[(head + i) % capacity];
        }
        items = a;
        head = 0;
        tail = size - 1;
        capacity = newCapacity;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        for (int i = 0; i < size; i++) {
            System.out.print(items[(head + i) % capacity] + " ");
        }
        System.out.println();
    }

    @Override
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        if (size < capacity / 4 && capacity > 16) {
            resize(capacity / 2);
        }
        T x = items[head];
        items[head] = null;
        size--;
        head = (head + 1 + capacity) % capacity;
        return x;
    }

    @Override
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        if (size < capacity / 4 && capacity > 16) {
            resize(capacity / 2);
        }
        T x = items[tail];
        items[tail] = null;
        size--;
        tail = (tail - 1 + capacity) % capacity;
        return x;
    }

    @Override
    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        return items[(head + index) % capacity];
    }

    @Override
    public Iterator<T> iterator() {
        return new DequeIterator();
    }

    private class DequeIterator implements Iterator<T> {
        private int index;

        public DequeIterator() {
            index = head;
        }

        @Override
        public boolean hasNext() {
            return index != (tail + 1) % capacity;
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            T value = items[index];
            index = (index + 1) % capacity;
            return value;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Deque)) {
            return false;
        }

        Deque<?> other = (Deque<?>) o;

        if (this.size() != other.size()) {
            return false;
        }

        for (int i = 0; i < size(); i++) {
            T thisItem = this.get(i);
            Object otherItem = other.get(i);
            if (!thisItem.equals(otherItem)) {
                return false;
            }
        }
        return true;
    }

}

