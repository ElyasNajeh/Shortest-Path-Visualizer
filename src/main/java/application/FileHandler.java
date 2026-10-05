package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Window;

public class FileHandler {

    private Vertices vertices;
    private LinkedList[] graph;
    private String sourceNode;
    private String destinationNode;
    private int method;
    private int edgeCount;
    private File selectedFile;

    public LinkedList[] getGraph() {
        return graph;
    }

    public Vertices getVertices() {
        return vertices;
    }

    public int getVertexIndex(String name) {
        return vertices == null ? -1 : vertices.getIndex(name);
    }

    public String getSourceNode() {
        return sourceNode;
    }

    public String getDestinationNode() {
        return destinationNode;
    }

    public int getMethod() {
        return method;
    }

    public int getEdgeCount() {
        return edgeCount;
    }

    public File getSelectedFile() {
        return selectedFile;
    }

    public boolean loadFromFile(Window owner) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Open graph data");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Graph text files", "*.txt", "*.graph", "*.dat"));

        File file = chooser.showOpenDialog(owner);
        if (file == null)
            return false;

        try {
            load(file);
            return true;
        } catch (IOException | IllegalArgumentException exception) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(owner);
            alert.setTitle("Could not load graph");
            alert.setHeaderText("The selected file is not valid graph data.");
            alert.setContentText(exception.getMessage());
            alert.showAndWait();
            return false;
        }
    }

    public void load(File file) throws IOException {
        if (file == null || !file.isFile())
            throw new IOException("Choose an existing graph data file.");

        Vertices loadedVertices = new Vertices();
        Header header;

        try (BufferedReader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
            LineCursor cursor = new LineCursor(reader);
            String headerLine = cursor.nextDataLine();
            if (headerLine == null)
                throw new IllegalArgumentException("The graph file is empty.");

            header = parseHeader(headerLine, cursor.getLineNumber());
            loadedVertices.insert(header.source());
            loadedVertices.insert(header.destination());

            String line;
            while ((line = cursor.nextDataLine()) != null) {
                EdgeData edge = parseEdge(line, cursor.getLineNumber());
                loadedVertices.insert(edge.from());
                loadedVertices.insert(edge.to());
            }
        }

        LinkedList[] loadedGraph = new LinkedList[loadedVertices.size()];
        for (int i = 0; i < loadedGraph.length; i++)
            loadedGraph[i] = new LinkedList();

        int loadedEdgeCount = 0;
        try (BufferedReader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
            LineCursor cursor = new LineCursor(reader);
            cursor.nextDataLine();

            String line;
            while ((line = cursor.nextDataLine()) != null) {
                EdgeData edge = parseEdge(line, cursor.getLineNumber());
                int from = loadedVertices.getIndex(edge.from());
                int to = loadedVertices.getIndex(edge.to());
                loadedGraph[from].addLast(new Edge(to, edge.distance(), edge.time()));
                loadedEdgeCount++;
            }
        }

        vertices = loadedVertices;
        graph = loadedGraph;
        sourceNode = header.source();
        destinationNode = header.destination();
        method = header.method();
        edgeCount = loadedEdgeCount;
        selectedFile = file.getAbsoluteFile();
    }

    private Header parseHeader(String line, int lineNumber) {
        String[] values = line.split("\\s+");
        if (values.length != 3)
            throw new IllegalArgumentException("Line " + lineNumber
                    + ": expected source, destination, and mode.");

        int parsedMethod;
        try {
            parsedMethod = Integer.parseInt(values[2]);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Line " + lineNumber + ": mode must be 1, 2, or 3.");
        }

        if (parsedMethod < 1 || parsedMethod > 3)
            throw new IllegalArgumentException("Line " + lineNumber + ": mode must be 1, 2, or 3.");

        return new Header(values[0], values[1], parsedMethod);
    }

    private EdgeData parseEdge(String line, int lineNumber) {
        String[] values = line.split("\\s+");
        if (values.length != 4)
            throw new IllegalArgumentException("Line " + lineNumber
                    + ": expected from, to, distance, and time.");

        double distance;
        double time;
        try {
            distance = Double.parseDouble(values[2]);
            time = Double.parseDouble(values[3]);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Line " + lineNumber
                    + ": distance and time must be numbers.");
        }

        if (!Double.isFinite(distance) || !Double.isFinite(time) || distance < 0 || time < 0)
            throw new IllegalArgumentException("Line " + lineNumber
                    + ": distance and time must be finite, non-negative values.");

        return new EdgeData(values[0], values[1], distance, time);
    }

    private record Header(String source, String destination, int method) {
    }

    private record EdgeData(String from, String to, double distance, double time) {
    }

    private static class LineCursor {
        private final BufferedReader reader;
        private int lineNumber;

        LineCursor(BufferedReader reader) {
            this.reader = reader;
        }

        String nextDataLine() throws IOException {
            String line;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                int comment = line.indexOf('#');
                if (comment >= 0)
                    line = line.substring(0, comment);
                line = line.trim();
                if (!line.isEmpty())
                    return line;
            }
            return null;
        }

        int getLineNumber() {
            return lineNumber;
        }
    }
}
