package deque;

import java.util.Iterator;

public interface Deque<T> extends Iterable<T> {
    void addFirst(T element);

    void addLast(T element);

    T removeFirst();

    T removeLast();

    int size();

    T get(int index);

    void printDeque();

    default boolean isEmpty() {
        return size() == 0;
    }

    @Override
    Iterator<T> iterator();
}

