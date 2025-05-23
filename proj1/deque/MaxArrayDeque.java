package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private Comparator<T> comparator;//声明变量

    public MaxArrayDeque(Comparator<T> c) {//传入Comparator类型的参数c
        comparator = c;//将c赋值给comparator
    }

    public T max() {
        return max(comparator);
    }

    public T max(Comparator<T> c) {
        if (isEmpty()) {
            return null;
        }
        T maxitem = get(0);
        for (int i = 1; i < size(); i++) {
            T tmp = get(i);
            if (c.compare(tmp, maxitem) > 0) {
                maxitem = tmp;
            }
        }
        return maxitem;
    }

}