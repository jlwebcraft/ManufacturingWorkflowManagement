package DSA;

public class PriorityQueue {

    private static class Node {
        int data;
        int priority;

        Node(int data, int priority) {
            this.data = data;
            this.priority = priority;
        }
    }

    private Node[] heap;
    private int size;

    public PriorityQueue(int capacity) {
        heap = new Node[capacity];
        size = 0;
    }

    public void enqueue(int data, int priority) {
        if (size == heap.length) {
            System.out.println("Priority queue overflow");
            return;
        }

        heap[size] = new Node(data, priority);
        heapifyUp(size);
        size++;
    }

    public int dequeue() {
        if (isEmpty()) {
            System.out.println("Priority queue underflow");
            return -1;
        }

        int value = heap[0].data;
        heap[0] = heap[size - 1];
        heap[size - 1] = null;
        size--;
        heapifyDown(0);
        return value;
    }

    public int peek() {
        if (isEmpty()) {
            System.out.println("Priority queue is empty");
            return -1;
        }
        return heap[0].data;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public void display() {
        if (isEmpty()) {
            System.out.println("Priority queue is empty");
            return;
        }

        for (int i = 0; i < size; i++) {
            System.out.println("Data: " + heap[i].data + ", Priority: " + heap[i].priority);
        }
    }

    private void heapifyUp(int index) {
        while (index > 0) {
            int parent = (index - 1) / 2;
            if (heap[parent].priority <= heap[index].priority) {
                break;
            }
            swap(parent, index);
            index = parent;
        }
    }

    private void heapifyDown(int index) {
        while (true) {
            int left = 2 * index + 1;
            int right = 2 * index + 2;
            int smallest = index;

            if (left < size && heap[left].priority < heap[smallest].priority) {
                smallest = left;
            }

            if (right < size && heap[right].priority < heap[smallest].priority) {
                smallest = right;
            }

            if (smallest == index) {
                break;
            }

            swap(index, smallest);
            index = smallest;
        }
    }

    private void swap(int first, int second) {
        Node temp = heap[first];
        heap[first] = heap[second];
        heap[second] = temp;
    }
}