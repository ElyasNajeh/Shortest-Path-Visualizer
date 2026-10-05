package application;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class GraphView extends Region {

    private static final Color EDGE_COLOR = Color.web("#CBD5E1", 0.42);
    private static final Color NODE_COLOR = Color.web("#64748B", 0.78);
    private static final Color DISTANCE_COLOR = Color.web("#2563EB");
    private static final Color TIME_COLOR = Color.web("#F59E0B");
    private static final Color SOURCE_COLOR = Color.web("#16A34A");
    private static final Color DESTINATION_COLOR = Color.web("#DC2626");

    private final Canvas canvas = new Canvas();
    private LinkedList[] graph;
    private Vertices vertices;
    private int[] distancePath = new int[0];
    private int[] timePath = new int[0];
    private int source = -1;
    private int destination = -1;

    public GraphView() {
        getChildren().add(canvas);
        setMinSize(420, 320);
        setPrefSize(900, 650);

        widthProperty().addListener((observable, oldValue, newValue) -> draw());
        heightProperty().addListener((observable, oldValue, newValue) -> draw());
    }

    public void setGraph(LinkedList[] graph, Vertices vertices) {
        this.graph = graph;
        this.vertices = vertices;
        distancePath = new int[0];
        timePath = new int[0];
        source = -1;
        destination = -1;
        draw();
    }

    public void setSelection(int source, int destination) {
        this.source = source;
        this.destination = destination;
        draw();
    }

    public void setPaths(int[] distancePath, int[] timePath) {
        this.distancePath = distancePath == null ? new int[0] : distancePath.clone();
        this.timePath = timePath == null ? new int[0] : timePath.clone();
        draw();
    }

    @Override
    protected void layoutChildren() {
        canvas.setWidth(getWidth());
        canvas.setHeight(getHeight());
        draw();
    }

    private void draw() {
        double width = getWidth();
        double height = getHeight();
        if (width <= 0 || height <= 0)
            return;

        canvas.setWidth(width);
        canvas.setHeight(height);
        GraphicsContext graphics = canvas.getGraphicsContext2D();
        graphics.setFill(Color.web("#F8FAFC"));
        graphics.fillRect(0, 0, width, height);

        if (graph == null || graph.length == 0 || vertices == null) {
            drawEmptyState(graphics, width, height);
            return;
        }

        double[][] points = createLayout(width, height, graph.length);
        drawEdges(graphics, points);
        drawNodes(graphics, points);
        drawRoute(graphics, points, timePath, TIME_COLOR);
        drawRoute(graphics, points, distancePath, DISTANCE_COLOR);
        drawEndpoint(graphics, points, source, SOURCE_COLOR, "SOURCE");
        drawEndpoint(graphics, points, destination, DESTINATION_COLOR, "DESTINATION");
    }

    private double[][] createLayout(double width, double height, int count) {
        double[][] points = new double[count][2];
        double centerX = width / 2;
        double centerY = height / 2;
        double radius = Math.max(20, Math.min(width, height) / 2 - 46);

        if (count == 1) {
            points[0][0] = centerX;
            points[0][1] = centerY;
            return points;
        }

        if (count <= 24) {
            for (int i = 0; i < count; i++) {
                double angle = -Math.PI / 2 + 2 * Math.PI * i / count;
                points[i][0] = centerX + radius * Math.cos(angle);
                points[i][1] = centerY + radius * Math.sin(angle);
            }
            return points;
        }

        double goldenAngle = Math.PI * (3 - Math.sqrt(5));
        for (int i = 0; i < count; i++) {
            double radialPosition = radius * Math.sqrt((i + 0.5) / count);
            double angle = i * goldenAngle;
            points[i][0] = centerX + radialPosition * Math.cos(angle);
            points[i][1] = centerY + radialPosition * Math.sin(angle);
        }
        return points;
    }

    private void drawEdges(GraphicsContext graphics, double[][] points) {
        graphics.setStroke(EDGE_COLOR);
        graphics.setLineWidth(graph.length > 1000 ? 0.45 : 0.8);

        for (int from = 0; from < graph.length; from++) {
            Node current = graph[from].getFront();
            while (current != null) {
                Edge edge = (Edge) current.getElement();
                int to = edge.getTo();
                if (to >= 0 && to < points.length) {
                    graphics.strokeLine(points[from][0], points[from][1], points[to][0], points[to][1]);
                    if (graph.length <= 80)
                        drawArrowHead(graphics, points[from], points[to], 5);
                }
                current = current.getNext();
            }
        }
    }

    private void drawNodes(GraphicsContext graphics, double[][] points) {
        double radius = graph.length > 1000 ? 1.15 : graph.length > 100 ? 1.8 : 3.2;
        graphics.setFill(NODE_COLOR);
        for (double[] point : points)
            graphics.fillOval(point[0] - radius, point[1] - radius, radius * 2, radius * 2);
    }

    private void drawRoute(GraphicsContext graphics, double[][] points, int[] path, Color color) {
        if (path.length == 0)
            return;

        graphics.setStroke(Color.WHITE);
        graphics.setLineWidth(7);
        strokePath(graphics, points, path);
        graphics.setStroke(color);
        graphics.setLineWidth(4);
        strokePath(graphics, points, path);
        graphics.setFill(color);
        for (int i = 1; i < path.length; i++) {
            int from = path[i - 1];
            int to = path[i];
            if (from >= 0 && from < points.length && to >= 0 && to < points.length)
                drawArrowHead(graphics, points[from], points[to], 8);
        }

        for (int node : path) {
            if (node >= 0 && node < points.length)
                graphics.fillOval(points[node][0] - 3.5, points[node][1] - 3.5, 7, 7);
        }

        int labelStep = Math.max(1, (int) Math.ceil(path.length / 10.0));
        for (int i = 1; i < path.length - 1; i += labelStep)
            drawLabel(graphics, points[path[i]][0], points[path[i]][1], vertices.getName(path[i]), color);
    }

    private void strokePath(GraphicsContext graphics, double[][] points, int[] path) {
        for (int i = 1; i < path.length; i++) {
            int from = path[i - 1];
            int to = path[i];
            if (from >= 0 && from < points.length && to >= 0 && to < points.length)
                graphics.strokeLine(points[from][0], points[from][1], points[to][0], points[to][1]);
        }
    }

    private void drawArrowHead(GraphicsContext graphics, double[] from, double[] to, double size) {
        double dx = to[0] - from[0];
        double dy = to[1] - from[1];
        double length = Math.hypot(dx, dy);
        if (length < size * 2)
            return;

        double unitX = dx / length;
        double unitY = dy / length;
        double tipX = to[0] - unitX * 7;
        double tipY = to[1] - unitY * 7;
        double baseX = tipX - unitX * size;
        double baseY = tipY - unitY * size;
        double sideX = -unitY * size * 0.55;
        double sideY = unitX * size * 0.55;

        Color fill = (Color) graphics.getStroke();
        graphics.setFill(fill);
        graphics.fillPolygon(
                new double[] { tipX, baseX + sideX, baseX - sideX },
                new double[] { tipY, baseY + sideY, baseY - sideY },
                3);
    }

    private void drawEndpoint(GraphicsContext graphics, double[][] points, int node, Color color, String role) {
        if (node < 0 || node >= points.length)
            return;

        double x = points[node][0];
        double y = points[node][1];
        graphics.setFill(Color.WHITE);
        graphics.fillOval(x - 9, y - 9, 18, 18);
        graphics.setFill(color);
        graphics.fillOval(x - 6.5, y - 6.5, 13, 13);
        drawLabel(graphics, x, y, role + " · " + vertices.getName(node), color);
    }

    private void drawLabel(GraphicsContext graphics, double x, double y, String text, Color color) {
        graphics.setFont(Font.font("System", FontWeight.SEMI_BOLD, 11));
        double labelWidth = Math.max(36, text.length() * 6.4 + 14);
        double labelX = Math.min(Math.max(6, x + 8), Math.max(6, getWidth() - labelWidth - 6));
        double labelY = Math.min(Math.max(20, y - 9), Math.max(20, getHeight() - 8));

        graphics.setFill(Color.web("#FFFFFF", 0.94));
        graphics.fillRoundRect(labelX, labelY - 15, labelWidth, 20, 8, 8);
        graphics.setStroke(Color.web("#CBD5E1"));
        graphics.setLineWidth(0.7);
        graphics.strokeRoundRect(labelX, labelY - 15, labelWidth, 20, 8, 8);
        graphics.setFill(color);
        graphics.fillText(text, labelX + 7, labelY);
    }

    private void drawEmptyState(GraphicsContext graphics, double width, double height) {
        graphics.setFill(Color.web("#334155"));
        graphics.setFont(Font.font("System", FontWeight.SEMI_BOLD, 18));
        graphics.fillText("Load graph data to begin", width / 2 - 105, height / 2 - 4);
        graphics.setFill(Color.web("#64748B"));
        graphics.setFont(Font.font("System", 13));
        graphics.fillText("The graph and calculated routes will appear here.", width / 2 - 145, height / 2 + 22);
    }
}
