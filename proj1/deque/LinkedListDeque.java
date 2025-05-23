package deque;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {
    //创建节点
    private class Node {
        //声明节点
        private T item;//节点的内容
        private LinkedListDeque.Node next;//指向下一个
        private LinkedListDeque.Node prev;//指向上一个

        //初始化
        public Node(T item) {
            this.item = item;
            this.next = null;
            this.prev = null;
        }
    }

    //声明
    private Node sentinelHead;
    private Node sentinelTail;
    private int size;

    //创建一个新linked list deque
    public LinkedListDeque() {
        sentinelHead = new Node(null);//初始化sentinel变量
        sentinelTail = new Node(null);
        sentinelHead.next = sentinelTail;
        sentinelTail.prev = sentinelHead;
        size = 0;
    }

    public int size() {
        return size;
    }

    /*public boolean isEmpty() {
        return size == 0;
    }*/

    //添加头节点
    public void addFirst(T item) {
        Node newNode = new Node(item);
        newNode.next = sentinelHead.next;
        newNode.prev = sentinelHead;
        sentinelHead.next.prev = newNode;
        sentinelHead.next = newNode;
        size++;
    }

    //添加末尾节点
    public void addLast(T item) {
        Node newNode = new Node(item);
        newNode.prev = sentinelTail.prev;
        sentinelTail.prev.next = newNode;
        newNode.next = sentinelTail;
        sentinelTail.prev = newNode;
        size++;
    }

    //打印
    public void printDeque() {
        Node curr = sentinelHead.next;
        while (curr.item != null) {
            System.out.print(curr.item + " ");
            curr = curr.next;
        }
        System.out.println();//光标换行
    }

    //删除头节点
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        Node first = sentinelHead.next;
        sentinelHead.next = first.next;
        first.next.prev = sentinelHead;
        size--;
        return (T) first.item;
    }

    //删除尾节点
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        Node last = sentinelTail.prev;
        last.prev.next = sentinelTail;
        sentinelTail.prev = last.prev;
        size--;
        return (T) last.item;
    }

    //获得特定节点，用迭代
    public T get(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        Node curr = sentinelHead.next;
        for (int i = 0; i < index; i++) {
            curr = curr.next;
        }
        return (T) curr.item;
    }

    public T getRecursive(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        return (T) getRecursiveHelper(sentinelHead.next, index);
    }

    private T getRecursiveHelper(Node curr, int index) {
        if (index == 0) {
            return (T) curr.item;
        }
        curr = curr.next;
        return getRecursiveHelper(curr, index - 1);
    }

    public Iterator <T> iterator() {
        return new DequeIterator();
    }

    private class DequeIterator implements Iterator<T> {
        private Node curr;

        public DequeIterator() {
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
        } else if (o == null) {
            return false;
        } else if (o.getClass() == this.getClass()
                && ((LinkedListDeque) o).size == this.size) {
            LinkedListDeque other = (LinkedListDeque) o;
            Node curr = this.sentinelHead.next;
            Node curro = other.sentinelHead.next;
            while (curr != sentinelTail) {
                if (!curr.item.equals(curro.item)) {
                    return false;
                }
                curr = curr.next;
                curro = curro.next;
            }
            return true;
        } else {
            return false;
        }
    }
}


