package DSA;

public class Vector {

    private int[] data;
    private int size;

    public Vector() {
        data = new int[5];
        size = 0;
    }

    public void add(int value) {
        ensureCapacity();
        data[size++] = value;
    }

    public void insertAt(int index, int value) {
        if (index < 0 || index > size) {
            System.out.println("Invalid index");
            return;
        }

        ensureCapacity();
        for (int i = size; i > index; i--) {
            data[i] = data[i - 1];
        }
        data[index] = value;
        size++;
    }

    public int get(int index) {
        if (index < 0 || index >= size) {
            System.out.println("Invalid index");
            return -1;
        }
        return data[index];
    }

    public void deleteAt(int index) {
        if (index < 0 || index >= size) {
            System.out.println("Invalid index");
            return;
        }

        for (int i = index; i < size - 1; i++) {
            data[i] = data[i + 1];
        }
        size--;
    }

    public boolean search(int value) {
        for (int i = 0; i < size; i++) {
            if (data[i] == value) {
                return true;
            }
        }
        return false;
    }

    public int size() {
        return size;
    }

    public void display() {
        if (size == 0) {
            System.out.println("Vector is empty");
            return;
        }

        for (int i = 0; i < size; i++) {
            System.out.print(data[i] + " ");
        }
        System.out.println();
    }

    private void ensureCapacity() {
        if (size < data.length) {
            return;
        }

        int[] expanded = new int[data.length * 2];
        for (int i = 0; i < data.length; i++) {
            expanded[i] = data[i];
        }
        data = expanded;
    }
}