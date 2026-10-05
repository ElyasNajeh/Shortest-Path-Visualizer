package application;

public class Vertices {

    private final MyArrayList<String> list;

    public Vertices() {
        list = new MyArrayList<>();
    }

    public void insert(String name) {
        if (name != null && !name.isBlank())
            list.insertSorted(name.trim());
    }

    public int getIndex(String name) {
        if (name == null)
            return -1;
        return list.binarySearch(name.trim());
    }

    public int size() {
        return list.getSize();
    }

    public String getName(int index) {
        return list.get(index);
    }

    public String[] getNames() {
        String[] names = new String[size()];
        for (int i = 0; i < names.length; i++)
            names[i] = getName(i);
        return names;
    }
}
