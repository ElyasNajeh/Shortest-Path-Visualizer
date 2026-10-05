package application;

public class Heap {

    private final int[] node;
    private final double[] key;
    private final int[] pos;
    private int size;
    private final int capacity;

    public Heap(int capacity, int numberOfNodes) {
        this.capacity = Math.max(0, capacity);
        this.size = 0;
        node = new int[this.capacity];
        key = new double[this.capacity];
        pos = new int[Math.max(0, numberOfNodes)];

        for (int i = 0; i < pos.length; i++)
            pos[i] = -1;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean contains(int v) {
        return v >= 0 && v < pos.length && pos[v] != -1;
    }

    private int parent(int i) {
        return (i - 1) / 2;
    }

    private int left(int i) {
        return 2 * i + 1;
    }

    private int right(int i) {
        return 2 * i + 2;
    }

    private void swap(int i, int j) {
        int tempNode = node[i];
        node[i] = node[j];
        node[j] = tempNode;

        double tempKey = key[i];
        key[i] = key[j];
        key[j] = tempKey;

        pos[node[i]] = i;
        pos[node[j]] = j;
    }

    public void insert(int v, double k) {
        if (size == capacity || v < 0 || v >= pos.length || contains(v))
            return;

        int i = size;
        node[i] = v;
        key[i] = k;
        pos[v] = i;
        size++;

        while (i != 0 && key[parent(i)] > key[i]) {
            swap(i, parent(i));
            i = parent(i);
        }
    }

    public void decreaseKey(int v, double newKey) {
        if (!contains(v))
            return;

        int i = pos[v];
        if (newKey >= key[i])
            return;

        key[i] = newKey;
        while (i != 0 && key[parent(i)] > key[i]) {
            swap(i, parent(i));
            i = parent(i);
        }
    }

    private void minHeapify(int i) {
        int left = left(i);
        int right = right(i);
        int smallest = i;

        if (left < size && key[left] < key[smallest])
            smallest = left;
        if (right < size && key[right] < key[smallest])
            smallest = right;

        if (smallest != i) {
            swap(i, smallest);
            minHeapify(smallest);
        }
    }

    public int extractMin() {
        if (isEmpty())
            return -1;

        int minNode = node[0];
        pos[minNode] = -1;

        if (size > 1) {
            node[0] = node[size - 1];
            key[0] = key[size - 1];
            pos[node[0]] = 0;
        }

        size--;
        if (size > 0)
            minHeapify(0);

        return minNode;
    }

    public double getMinKey() {
        return isEmpty() ? Double.POSITIVE_INFINITY : key[0];
    }

    public double getKeyOf(int v) {
        return contains(v) ? key[pos[v]] : Double.POSITIVE_INFINITY;
    }
}
