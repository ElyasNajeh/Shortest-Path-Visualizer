package application;

public class Node {

    private Object element;
    private Node next;

    Node(Object element, Node next) {
        this.element = element;
        this.next = next;
    }

    Node(Object element) {
        this(element, null);
    }

    public Object getElement() {
        return element;
    }

    public void setElement(Object element) {
        this.element = element;
    }

    public Node getNext() {
        return next;
    }

    public void setNext(Node next) {
        this.next = next;
    }
}
