package bearmaps;

import java.util.HashSet;
import java.util.HashMap;
import java.util.ArrayList;


public class ArrayHeapMinPQ<T> implements ExtrinsicMinPQ<T> {

    private ArrayList<T> values;
    private ArrayList<Double> priorities;
    private HashSet<T> checks;
    private HashMap<T, Integer> positions;
    private int size;


    public ArrayHeapMinPQ() {
        values = new ArrayList<T>();
        values.add(null);
        priorities = new ArrayList<Double>();
        priorities.add(null);
        positions = new HashMap<>();
        checks = new HashSet<>();
        size = 0;
    }

    private int parent(int child) {
        int parent = child / 2;
        return parent;
    }

    private int leftChild(int parent) {
        return parent * 2;
    }

    private int rightChild(int parent) {
        return parent * 2 + 1;
    }

    private void swim(int current) {
        if (current == 1) {
            return;
        }
        if (priorities.get(parent(current)) < priorities.get(current)) {
            return;
        }
        swap(current);
        swim(parent(current));
    }

    private void swap(int current) {
        Double temp = priorities.get(parent(current));
        T temp1 = values.get(parent(current));
        int temp2 = parent(current);
        positions.replace(values.get(parent(current)), current);
        positions.replace(values.get(current), temp2);
        priorities.set(parent(current), priorities.get(current));
        values.set(parent(current), values.get(current));
        priorities.set(current, temp);
        values.set(current, temp1);
    }

    private void sink(int current) {
        if (leftChild(current) > size && rightChild(current) > size) {
            return;
        } else if (rightChild(current) > size) {
            if (priorities.get(current) > leftChild(current)) {
                swap(leftChild(current));
            }
        } else if (priorities.get(current) <= priorities.get(leftChild(current))
                && priorities.get(current) <= priorities.get(rightChild(current))) {
            return;
        } else if (priorities.get(leftChild(current)) > priorities.get(rightChild(current))) {
            swap(rightChild(current));
            sink(rightChild(current));
        } else {
            swap(leftChild(current));
            sink(leftChild(current));
        }
    }

    public boolean contains(T item) {
        return checks.contains(item);
    }

    public void add(T item, double priority) {
        if (contains(item)) {
            throw new IllegalArgumentException();
        }
        size = size + 1;
        checks.add(item);
        values.add(size, item);
        priorities.add(size, priority);
        positions.put(item, size);
        swim(size);
    }

    public T getSmallest() {
        return values.get(1);
    }

    public T removeSmallest() {
        T top = values.get(1);
        values.set(1, null);
        double bPriority = priorities.remove(size);
        priorities.set(1, bPriority);
        T bottom = values.remove(size);
        values.set(1, bottom);
        size = size - 1;
        checks.remove(top);
        sink(1);
        return top;
    }

    public int size() {
        return size;
    }

    public void changePriority(T item, double priority) {
        int temp = positions.get(item);
        double temp2 = priorities.get(temp);
        priorities.set(temp, priority);
        if (priority > temp2) {
            sink(temp);
        } else if (priority < temp2) {
            swim(temp);
        } else {
            return;
        }
    }

//    public String toString() {
//        return values.toString();
//    }
}
