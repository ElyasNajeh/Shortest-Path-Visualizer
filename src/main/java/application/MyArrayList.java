package application;

public class MyArrayList<T extends Comparable<T>> {

    private T[] arrayList;
    private int size;
    private int capacity;

    public MyArrayList() {
        this(10);
    }

    @SuppressWarnings("unchecked")
    public MyArrayList(int capacity) {
        this.capacity = Math.max(1, capacity);
        this.size = 0;
        arrayList = (T[]) new Comparable[this.capacity];
    }

    public int getSize() {
        return size;
    }

    public T get(int index) {
        if (index < 0 || index >= size)
            return null;
        return arrayList[index];
    }

    public int binarySearch(T key) {
        if (key == null)
            return -1;

        int left = 0;
        int right = size - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            int cmp = arrayList[mid].compareTo(key);

            if (cmp == 0)
                return mid;
            else if (cmp < 0)
                left = mid + 1;
            else
                right = mid - 1;
        }
        return -1;
    }

    public void insertSorted(T value) {
        if (value == null || binarySearch(value) != -1)
            return;

        if (size == capacity)
            reSize();

        int i = size - 1;
        while (i >= 0 && arrayList[i].compareTo(value) > 0) {
            arrayList[i + 1] = arrayList[i];
            i--;
        }

        arrayList[i + 1] = value;
        size++;
    }

    public void add(T value) {
        if (value == null)
            return;
        if (size == capacity)
            reSize();
        arrayList[size++] = value;
    }

    @SuppressWarnings("unchecked")
    private void reSize() {
        capacity *= 2;
        T[] newArray = (T[]) new Comparable[capacity];
        for (int i = 0; i < size; i++)
            newArray[i] = arrayList[i];
        arrayList = newArray;
    }
}
