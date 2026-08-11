package DSA;

public class HashMap {

    private static class Node {
        int key;
        String value;
        Node next;

        Node(int key, String value) {
            this.key = key;
            this.value = value;
            this.next = null;
        }
    }

    private Node[] table;

    public HashMap(int capacity) {
        table = new Node[capacity];
    }

    public void put(int key, String value) {
        int index = hash(key);
        Node temp = table[index];

        while (temp != null) {
            if (temp.key == key) {
                temp.value = value;
                return;
            }
            temp = temp.next;
        }

        Node node = new Node(key, value);
        node.next = table[index];
        table[index] = node;
    }

    public String get(int key) {
        int index = hash(key);
        Node temp = table[index];

        while (temp != null) {
            if (temp.key == key) {
                return temp.value;
            }
            temp = temp.next;
        }

        return null;
    }

    public boolean containsKey(int key) {
        return get(key) != null;
    }

    public void remove(int key) {
        int index = hash(key);
        Node temp = table[index];

        if (temp == null) {
            System.out.println("Key not found");
            return;
        }

        if (temp.key == key) {
            table[index] = temp.next;
            temp.next = null;
            return;
        }

        while (temp.next != null && temp.next.key != key) {
            temp = temp.next;
        }

        if (temp.next == null) {
            System.out.println("Key not found");
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
                System.out.print("[" + temp.key + "=" + temp.value + "] ");
                temp = temp.next;
            }
            System.out.println();
        }
    }

    private int hash(int key) {
        int value = key % table.length;
        if (value < 0) {
            value = value + table.length;
        }
        return value;
    }
}