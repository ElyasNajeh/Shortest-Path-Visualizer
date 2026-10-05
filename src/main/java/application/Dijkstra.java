package application;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class Dijkstra {

    public static final int DISTANCE = 1;
    public static final int TIME = 2;

    private final LinkedList[] graph;
    private final int n;
    private final Vertices vertices;

    private double[] dist;
    private int[] parent;
    private boolean[] visited;
    private int[] lastPath = new int[0];
    private double lastCost = Double.POSITIVE_INFINITY;

    public Dijkstra(LinkedList[] graph, Vertices vertices) {
        this.graph = graph;
        this.vertices = vertices;
        this.n = graph == null ? 0 : graph.length;
    }

    public String run(int source, int destination, int mode) {
        lastPath = new int[0];
        lastCost = Double.POSITIVE_INFINITY;

        if (graph == null || n == 0 || vertices == null)
            return "Graph is empty. Please load a graph file first.";
        if (source < 0 || source >= n || destination < 0 || destination >= n)
            return "Source or destination is out of range.";
        if (mode != DISTANCE && mode != TIME)
            return "Unknown optimization mode.";

        dist = new double[n];
        parent = new int[n];
        visited = new boolean[n];

        for (int i = 0; i < n; i++) {
            dist[i] = Double.POSITIVE_INFINITY;
            parent[i] = -1;
        }

        Heap heap = new Heap(n, n);
        dist[source] = 0;
        heap.insert(source, 0);

        while (!heap.isEmpty()) {
            int u = heap.extractMin();
            if (u < 0 || u >= n || visited[u])
                continue;

            visited[u] = true;
            if (u == destination)
                break;

            Node current = graph[u].getFront();
            while (current != null) {
                Edge edge = (Edge) current.getElement();
                int v = edge.getTo();

                if (v >= 0 && v < n && !visited[v]) {
                    double weight = mode == DISTANCE
                            ? edge.getDistance()
                            : edge.getTime();
                    double newCost = dist[u] + weight;

                    if (newCost < dist[v]) {
                        dist[v] = newCost;
                        parent[v] = u;

                        if (heap.contains(v))
                            heap.decreaseKey(v, newCost);
                        else
                            heap.insert(v, newCost);
                    }
                }

                current = current.getNext();
            }
        }

        lastCost = dist[destination];
        if (!Double.isInfinite(lastCost))
            lastPath = createPath(source, destination);

        return buildResult(source, destination, mode);
    }

    public int[] getLastPath() {
        return lastPath.clone();
    }

    public double getLastCost() {
        return lastCost;
    }

    private int[] createPath(int source, int destination) {
        int[] reversed = new int[n];
        int length = 0;
        int current = destination;

        while (current != -1 && length < n) {
            reversed[length++] = current;
            if (current == source)
                break;
            current = parent[current];
        }

        if (length == 0 || reversed[length - 1] != source)
            return new int[0];

        int[] path = new int[length];
        for (int i = 0; i < length; i++)
            path[i] = reversed[length - i - 1];
        return path;
    }

    private String buildResult(int source, int destination, int mode) {
        if (Double.isInfinite(dist[destination])) {
            return "No path found from " + vertices.getName(source)
                    + " to " + vertices.getName(destination) + ".";
        }

        StringBuilder result = new StringBuilder();
        result.append(mode == DISTANCE ? "SHORTEST DISTANCE" : "LEAST TRAVEL TIME")
              .append("\n\n")
              .append("Source        ").append(vertices.getName(source)).append("\n")
              .append("Destination   ").append(vertices.getName(destination)).append("\n")
              .append("Total         ").append(formatCost(dist[destination]))
              .append(mode == DISTANCE ? " km" : " minutes")
              .append("\n")
              .append("Stops         ").append(Math.max(0, lastPath.length - 1))
              .append("\n\nPath\n");

        for (int i = 0; i < lastPath.length; i++) {
            if (i > 0)
                result.append("  →  ");
            result.append(vertices.getName(lastPath[i]));
        }

        return result.toString();
    }

    private String formatCost(double value) {
        DecimalFormat format = new DecimalFormat("0.##", DecimalFormatSymbols.getInstance(Locale.US));
        return format.format(value);
    }
}
