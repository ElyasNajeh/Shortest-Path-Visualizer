package application;

public class LinkedList {

    private Node front;
    private Node back;
    private int size;

    public Node getFront() {
        return front;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void addLast(Object element) {
        Node newNode = new Node(element);

        if (size == 0) {
            front = newNode;
            back = newNode;
        } else {
            back.setNext(newNode);
            back = newNode;
        }

        size++;
    }

    public Object get(int index) {
        if (index < 0 || index >= size)
            return null;

        Node current = front;
        for (int i = 0; i < index; i++)
            current = current.getNext();

        return current.getElement();
    }

    public void clear() {
        front = null;
        back = null;
        size = 0;
    }
}
