package bstmap;


import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>,V> implements Map61B<K, V> {
    private Node root;
    private int size;

    //定义节点
    private class Node {
        private K key;
        private V value;
        private Node left , right;

        public Node(K key, V value){
            this.key = key;
            this.value = value;
        }
    }

    public  BSTMap() {
        root = null;
        size = 0;
    }

    //清除，即将根节点设置为0，直接断开连接，将size设为0
    @Override
    public void clear(){
        root = null;
        size = 0;
    }
    //如果key存在，返回true
    @Override
    public boolean containsKey(K key){
        if(key == null){
            throw new IllegalArgumentException("key is null");
        }
        return get(key) != null;
    }

    //返回一个private方法去查找key
    @Override
    public V get(K key){
        return get(root, key);
    }
    //实际的get方法
    private V get(Node x, K key){
        //key为空，报错
        if(key == null){
            throw new IllegalArgumentException("key is null");
        }
        //节点为null,到头了返回无
        if(x == null){
            return null;
        }
        //比较key和x.key
        int cmp = key.compareTo(x.key);
        if(cmp == 0){
            return x.value;
        } else if(cmp < 0){
            return get(x.left, key);
        } else {
            return get(x.right, key);
        }
    }

    public int size(){
        return size;
    }

    //放置节点
    @Override
    public void put(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("key is null");
        }
        if (value == null) {
            throw new IllegalArgumentException("value is null");
        }
         root = put(root, key, value);
    }

    private Node put(Node x, K key, V value) {
        //当达到叶节点时，添加节点
        if (x == null) {
            size++;
            return new Node(key, value);
        }
        int cmp = key.compareTo(x.key);
        if (cmp == 0) {
            x.value = value;
        } else if (cmp < 0) {
            x.left = put(x.left, key, value);
        } else {
            x.right = put(x.right, key, value);
        }
        return x;
    }
    @Override
    public Set<K> keySet(){
        throw new UnsupportedOperationException();
    }
    @Override
    public V remove(K key){
        throw new  UnsupportedOperationException();
    }
    @Override
    public V remove(K key, V value){
        throw new  UnsupportedOperationException();
    }

    public void printInOrder(){
         printInOrder(root);
    }

    private void printInOrder(Node x){
        if(x == null){
            return;
        }
        printInOrder(x.left);
        System.out.print(x.key + "=>" + x.value);
        printInOrder(x.right);
    }

    @Override
    public Iterator<K> iterator(){
        throw new UnsupportedOperationException();
    }

}
