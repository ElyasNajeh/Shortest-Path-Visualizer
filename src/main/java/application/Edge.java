package application;

public class Edge {

    // Destination node index
    private final int to;

    // Distance weight (kilometers)
    private final double distance;

    // Time weight (minutes)
    private final double time;

    public Edge(int to, double distance, double time) {
        this.to = to;
        this.distance = distance;
        this.time = time;
    }

    public int getTo() {
        return to;
    }

    public double getDistance() {
        return distance;
    }

    public double getTime() {
        return time;
    }
}
