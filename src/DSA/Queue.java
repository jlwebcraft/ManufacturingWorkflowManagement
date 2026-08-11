package DSA;

public class Queue {

    private int[] data;
    private int front;
    private int rear;
    private int count;

    public Queue(int size) {
        data = new int[size];
        front = 0;
        rear = -1;
        count = 0;
    }

    public void enqueue(int value) {
        if (isFull()) {
            System.out.println("Queue overflow");
            return;
        }
        rear = (rear + 1) % data.length;
        data[rear] = value;
        count++;
    }

    public int dequeue() {
        if (isEmpty()) {
            System.out.println("Queue underflow");
            return -1;
        }
        int value = data[front];
        front = (front + 1) % data.length;
        count--;
        return value;
    }

    public int peek() {
        if (isEmpty()) {
            System.out.println("Queue is empty");
            return -1;
        }
        return data[front];
    }

    public boolean isEmpty() {
        return count == 0;
    }

    public boolean isFull() {
        return count == data.length;
    }

    public int size() {
        return count;
    }

    public void display() {
        if (isEmpty()) {
            System.out.println("Queue is empty");
            return;
        }

        for (int i = 0; i < count; i++) {
            int index = (front + i) % data.length;
            System.out.print(data[index] + " ");
        }
        System.out.println();
    }
}