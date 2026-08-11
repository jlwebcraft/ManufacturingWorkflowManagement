package DSA;

public class Stack {

    private int[] data;
    private int top;

    public Stack(int size) {
        data = new int[size];
        top = -1;
    }

    public void push(int value) {
        if (isFull()) {
            System.out.println("Stack overflow");
            return;
        }
        data[++top] = value;
    }

    public int pop() {
        if (isEmpty()) {
            System.out.println("Stack underflow");
            return -1;
        }
        return data[top--];
    }

    public int peek() {
        if (isEmpty()) {
            System.out.println("Stack is empty");
            return -1;
        }
        return data[top];
    }

    public boolean isEmpty() {
        return top == -1;
    }

    public boolean isFull() {
        return top == data.length - 1;
    }

    public void display() {
        if (isEmpty()) {
            System.out.println("Stack is empty");
            return;
        }

        for (int i = top; i >= 0; i--) {
            System.out.println(data[i]);
        }
    }
}