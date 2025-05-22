package deque;

public class ArrayDeque {
    private Object[] array;
    private int head;
    private int tail;
    private int size;

    public ArrayDeque() {
        array = new Object[8];
        head = 0;
        tail = 0;
        size = 0;
    }
    
}
