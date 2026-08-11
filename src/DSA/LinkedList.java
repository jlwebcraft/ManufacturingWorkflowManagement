package DSA;

public class LinkedList {

    private static class Node {
        int data;
        Node next;

        Node(int data) {
            this.data = data;
            this.next = null;
        }
    }

    private Node head;

    public void addFirst(int data) {
        Node node = new Node(data);
        node.next = head;
        head = node;
    }

    public void addLast(int data) {
        Node node = new Node(data);

        if (head == null) {
            head = node;
            return;
        }

        Node temp = head;
        while (temp.next != null) {
            temp = temp.next;
        }
        temp.next = node;
    }

    public void insertAfter(int existingValue, int newValue) {
        Node temp = head;
        int found = 0;

        while (temp != null) {
            if (temp.data == existingValue) {
                found = 1;
                break;
            }
            temp = temp.next;
        }

        if (found == 0) {
            System.out.println("Value not found");
            return;
        }

        Node node = new Node(newValue);
        node.next = temp.next;
        temp.next = node;
    }

    public void insertBefore(int existingValue, int newValue) {
        if (head == null) {
            System.out.println("List is empty");
            return;
        }

        Node temp = head;
        int found = 0;

        while (temp != null) {
            if (temp.data == existingValue) {
                found = 1;
                break;
            }
            temp = temp.next;
        }

        if (found == 0) {
            System.out.println("Value not found");
            return;
        }

        if (head.data == existingValue) {
            addFirst(newValue);
            return;
        }

        Node previous = head;
        while (previous.next != null && previous.next.data != existingValue) {
            previous = previous.next;
        }

        Node node = new Node(newValue);
        node.next = previous.next;
        previous.next = node;
    }

    public void deleteFirst() {
        if (head == null) {
            System.out.println("List is empty");
            return;
        }

        Node deleted = head;
        head = head.next;
        deleted.next = null;
    }

    public void deleteLast() {
        if (head == null) {
            System.out.println("List is empty");
            return;
        }

        if (head.next == null) {
            head = null;
            return;
        }

        Node temp = head;
        while (temp.next.next != null) {
            temp = temp.next;
        }
        temp.next = null;
    }

    public void deleteValue(int value) {
        if (head == null) {
            System.out.println("List is empty");
            return;
        }

        if (head.data == value) {
            deleteFirst();
            return;
        }

        Node temp = head;
        while (temp.next != null && temp.next.data != value) {
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

    public void deleteAll(int value) {
        while (head != null && head.data == value) {
            deleteFirst();
        }

        Node temp = head;
        while (temp != null && temp.next != null) {
            if (temp.next.data == value) {
                Node deleted = temp.next;
                temp.next = deleted.next;
                deleted.next = null;
            } else {
                temp = temp.next;
            }
        }
    }

    public boolean search(int value) {
        Node temp = head;
        while (temp != null) {
            if (temp.data == value) {
                return true;
            }
            temp = temp.next;
        }
        return false;
    }

    public void display() {
        Node temp = head;

        if (temp == null) {
            System.out.println("List is empty");
            return;
        }

        while (temp != null) {
            System.out.print(temp.data + "-->");
            temp = temp.next;
        }
        System.out.println("null");
    }
}