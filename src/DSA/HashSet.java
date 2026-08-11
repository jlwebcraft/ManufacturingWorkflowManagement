package DSA;

public class HashSet {

    private static class Node {
        int value;
        Node next;

        Node(int value) {
            this.value = value;
            this.next = null;
        }
    }

    private Node[] table;

    public HashSet(int capacity) {
        table = new Node[capacity];
    }

    public void add(int value) {
        if (contains(value)) {
            return;
        }

        int index = hash(value);
        Node node = new Node(value);
        node.next = table[index];
        table[index] = node;
    }

    public boolean contains(int value) {
        int index = hash(value);
        Node temp = table[index];

        while (temp != null) {
            if (temp.value == value) {
                return true;
            }
            temp = temp.next;
        }

        return false;
    }

    public void remove(int value) {
        int index = hash(value);
        Node temp = table[index];

        if (temp == null) {
            System.out.println("Value not found");
            return;
        }

        if (temp.value == value) {
            table[index] = temp.next;
            temp.next = null;
            return;
        }

        while (temp.next != null && temp.next.value != value) {
            temp = temp.next;
        }

        if (temp.next == null) {
            System.out.println("Value not found");
            return;
        }

        Node deleted = temp.next;
        temp.next = deleted.next;
        deleted.next = null;
    }

    public void display() {
        for (int i = 0; i < table.length; i++) {
            System.out.print(i + " : ");
            Node temp = table[i];
            while (temp != null) {
                System.out.print(temp.value + " ");
                temp = temp.next;
            }
            System.out.println();
        }
    }

    private int hash(int value) {
        int index = value % table.length;
        if (index < 0) {
            index = index + table.length;
        }
        return index;
    }
}