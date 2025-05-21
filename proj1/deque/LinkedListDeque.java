package deque;

import org.junit.Test;

public class LinkedListDeque<T> {
    //创建节点
    private class Node{
        //声明节点
        private T item;//节点的内容
        private LinkedListDeque.Node next;//指向下一个
        private LinkedListDeque.Node prev;//指向上一个
        //初始化
        public Node(T item){
            this.item = item;
            this.next = null;
            this.prev = null;
        }
    }
    //声明
    private Node sentinel_head;
    private Node sentinel_tail;
    private int size;
    //创建一个新linked list deque
    public LinkedListDeque() {
        sentinel_head = new Node(null);//初始化sentinel变量
        sentinel_tail = new Node(null);
        sentinel_head.next = sentinel_tail;
        sentinel_tail.prev = sentinel_head;
        size = 0;
    }

    public int size(){
        return size;
    }

    public boolean isEmpty(){
        return size == 0;
    }
    //添加头节点
    public void addFirst(T item) {
        Node newNode = new Node(item);
        newNode.next = sentinel_head.next;
        newNode.prev = sentinel_head;
        sentinel_head.next.prev = newNode;
        sentinel_head.next = newNode;
        size++;
    }
    //添加末尾节点
    public void addLast(T item) {
        Node newNode = new Node(item);
        newNode.prev = sentinel_tail.prev;
        sentinel_tail.prev.next = newNode;
        newNode.next = sentinel_tail;
        sentinel_tail.prev = newNode;
        size++;
    }
    //打印
    public void printDeque(){
        Node curr = sentinel_head.next;
        while(curr.item != null ){
            System.out.print(curr.item + " ");
            curr = curr.next;
        }
        System.out.println();//光标换行
    }
    //删除头节点
    public T removeFirst(){
        if(isEmpty()){
            return null;
        }
        Node first = sentinel_head.next;
        sentinel_head.next = first.next;
        first.next.prev = sentinel_head;
        size--;
        return (T) first.item;
    }
    //删除尾节点
    public T removeLast(){
        if(isEmpty()){
            return null;
        }
        Node last = sentinel_tail.prev;
        last.prev.next = sentinel_tail;
        sentinel_tail.prev = last.prev;
        size--;
        return (T) last.item;
    }
    //获得特定节点，用迭代
    public T get(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        Node curr = sentinel_head.next;
        for (int i = 0; i < index; i++) {
            curr = curr.next;
        }
        return (T) curr.item;
    }
    public T getRecursive(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        return (T)getRecursive_helper(sentinel_head.next,index);
    }
    public T getRecursive_helper(Node curr, int index) {
        if (index == 0) {
            return (T) curr.item;
        }
        curr = curr.next;
        return getRecursive_helper(curr,index - 1);
    }
    @Test
    public void testAddFirstAndRemoveFirst() {
        LinkedListDeque<Integer> aa = new LinkedListDeque<>();
        aa.addFirst(3);
        aa.addFirst(2);
        aa.addFirst(1);
        aa.printDeque();
        aa.addLast(4);
        System.out.println("After addLast:");
        aa.addLast(5);
        aa.addLast(6);
        System.out.println(aa.size());
        aa.printDeque();

        aa.removeFirst();
        System.out.println(aa.get(2));
        aa.removeFirst();
        aa.removeLast();
        System.out.println("After removeLast:");
        aa.removeLast();

        System.out.println(aa.size());
        aa.printDeque();
    }
}

