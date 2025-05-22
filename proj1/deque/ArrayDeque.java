package deque;

import java.util.Iterator;
import java.util.NoSuchElementException;
//使用循环队列，避免了在执行addfirst等操作时，需将原来数组整体移动，而是采用滚动模式crud
public class ArrayDeque<T> {
    private T[] items;
    private int head;//头指针
    private int tail;//尾指针
    private int size;
    private int capacity;//容量

    //初始化
    public ArrayDeque() {
        capacity = 8;
        items = (T[]) new Object[capacity];
        head = 0;
        tail = -1;//当添加第一个指针时，tail向后移动到0
        size = 0;
    }

    //addFirst
    public void addFirst(T x) {
        if (size == capacity) {
            resize(size * 2);
        }
        head = (head - 1 + capacity) % capacity;//头指针向前滚动
        items[head] = x;
        size++;
    }

    //addLast
    public void addLast(T x) {
        if (size == capacity) {
            resize(size * 2);
        }
        tail = (tail + 1 + capacity) % capacity;//尾指针向后滚动
        items[tail] = x;
        size++;
    }

    public void resize(int x) {
        T[] a = (T[]) new Object[x];
        System.arraycopy(items, 0, a, 0, size);
        //重置状态
        items = a;
        head = 0;
        tail = size - 1;
    }

    //isEmpty
    public boolean isEmpty() {
        return size == 0;
    }

    //size()
    public int size() {
        return size;
    }

    //printDeque
    public void printDeque() {
        for (int i = 0; i < size; i++) {
            System.out.print(items[i] + " ");
        }
    }

    //removeFirst
    public T removeFirst() {
        if ((size < capacity / 4) && (size > 16)) {
            resize(size * 2);
        }
        T x = items[head];
        items[head] = null;
        size = size - 1;
        head = (head + 1 + capacity) % capacity;
        return x;
    }

    //removeLast
    public T removeLast() {
        if ((size < capacity / 4) && (size > 16)) {
            resize(size * 2);
        }
        T x = items[tail];
        items[tail] = null;
        size = size - 1;
        tail = (tail - 1 + capacity) % capacity;
        return x;
    }

    //get()
    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        return items[head + index];
    }

    //iterator
    public Iterator iterator() {
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
            T value = items[head];
            index = (index + 1) % capacity;
            return value;
        }
    }

    //equals
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArrayDeque that = (ArrayDeque) o;
        if (size != that.size || head != that.head || tail != that.tail || capacity != that.capacity) {
            return false;
        } else {
            for (int i = 0; i < size; i++) {
                if (items[i] != that.items[i]) {
                    return false;
                }
            }
            return true;
        }
    }

}