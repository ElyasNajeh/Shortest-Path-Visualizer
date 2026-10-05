package application;

import java.io.File;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Design {

    private final FileHandler fileHandler = new FileHandler();
    private final GraphView graphView = new GraphView();

    private Stage stage;
    private ComboBox<String> sourceBox;
    private ComboBox<String> destinationBox;
    private TextArea outputArea;
    private ToggleGroup methodGroup;
    private RadioButton distanceButton;
    private RadioButton timeButton;
    private RadioButton bothButton;
    private Button runButton;
    private Label fileLabel;
    private Label graphStatsLabel;
    private Label statusLabel;

    public void interfaceUI(Stage stage) {
        this.stage = stage;

        BorderPane root = new BorderPane();
        root.getStyleClass().add("app-root");
        root.setTop(createHeader());
        root.setLeft(createControls());
        root.setCenter(createGraphPanel());
        root.setRight(createResultPanel());
        root.setBottom(createStatusBar());

        Scene scene = new Scene(root, 1400, 820);
        scene.getStylesheets().add(
                getClass().getResource("/application/style.css").toExternalForm());

        stage.setScene(scene);
        stage.setTitle("Shortest Path Visualizer");
        stage.setMinWidth(1120);
        stage.setMinHeight(700);
        stage.setMaximized(true);
        stage.show();
    }

    private HBox createHeader() {
        Label title = new Label("Shortest Path Visualizer");
        title.getStyleClass().add("app-title");

        Label subtitle = new Label("Explore distance and travel-time routes with Dijkstra's algorithm");
        subtitle.getStyleClass().add("app-subtitle");

        VBox titles = new VBox(2, title, subtitle);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label algorithmTag = new Label("DIJKSTRA  •  DIRECTED GRAPH");
        algorithmTag.getStyleClass().add("algorithm-tag");

        HBox header = new HBox(18, titles, spacer, algorithmTag);
        header.setAlignment(Pos.CENTER_LEFT);
        header.getStyleClass().add("header");
        return header;
    }

    private VBox createControls() {
        Label sectionTitle = new Label("Route setup");
        sectionTitle.getStyleClass().add("section-title");

        Label fileCaption = createFieldLabel("GRAPH DATA");
        Button loadButton = new Button("Open graph file");
        loadButton.getStyleClass().add("secondary-button");
        loadButton.setMaxWidth(Double.MAX_VALUE);
        loadButton.setOnAction(event -> {
            if (fileHandler.loadFromFile(stage))
                displayLoadedGraph();
        });

        fileLabel = new Label("No file selected");
        fileLabel.getStyleClass().add("file-name");
        fileLabel.setWrapText(true);

        sourceBox = createVertexBox("Choose source");
        destinationBox = createVertexBox("Choose destination");

        Button swapButton = new Button("Swap source and destination");
        swapButton.getStyleClass().add("text-button");
        swapButton.setMaxWidth(Double.MAX_VALUE);
        swapButton.setOnAction(event -> {
            String source = selectedText(sourceBox);
            sourceBox.setValue(selectedText(destinationBox));
            destinationBox.setValue(source);
            updateSelectionMarkers();
        });

        methodGroup = new ToggleGroup();
        distanceButton = createMethodButton("Shortest distance", Dijkstra.DISTANCE);
        timeButton = createMethodButton("Least travel time", Dijkstra.TIME);
        bothButton = createMethodButton("Compare both routes", 3);
        distanceButton.setSelected(true);

        VBox methods = new VBox(10, distanceButton, timeButton, bothButton);
        methods.getStyleClass().add("method-list");

        runButton = new Button("Calculate shortest path");
        runButton.getStyleClass().add("primary-button");
        runButton.setMaxWidth(Double.MAX_VALUE);
        runButton.setDisable(true);
        runButton.setOnAction(event -> runCalculation());

        VBox controls = new VBox(
                12,
                sectionTitle,
                fileCaption,
                loadButton,
                fileLabel,
                new Separator(),
                createFieldLabel("SOURCE VERTEX"),
                sourceBox,
                createFieldLabel("DESTINATION VERTEX"),
                destinationBox,
                swapButton,
                new Separator(),
                createFieldLabel("OPTIMIZE FOR"),
                methods,
                runButton);
        controls.getStyleClass().addAll("side-panel", "left-panel");
        controls.setPrefWidth(292);
        controls.setMinWidth(270);
        return controls;
    }

    private VBox createGraphPanel() {
        Label title = new Label("Graph visualization");
        title.getStyleClass().add("panel-title");

        graphStatsLabel = new Label("Waiting for graph data");
        graphStatsLabel.getStyleClass().add("panel-meta");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox panelHeading = new HBox(12, title, spacer, graphStatsLabel);
        panelHeading.setAlignment(Pos.CENTER_LEFT);

        HBox legend = new HBox(
                18,
                createLegendItem("#2563EB", "Distance route"),
                createLegendItem("#F59E0B", "Time route"),
                createLegendItem("#16A34A", "Source"),
                createLegendItem("#DC2626", "Destination"));
        legend.setAlignment(Pos.CENTER_LEFT);
        legend.getStyleClass().add("legend");

        VBox graphCard = new VBox(graphView);
        VBox.setVgrow(graphView, Priority.ALWAYS);
        graphCard.getStyleClass().add("graph-card");

        VBox panel = new VBox(10, panelHeading, legend, graphCard);
        VBox.setVgrow(graphCard, Priority.ALWAYS);
        panel.getStyleClass().add("center-panel");
        return panel;
    }

    private VBox createResultPanel() {
        Label title = new Label("Route details");
        title.getStyleClass().add("section-title");

        Label hint = new Label("Calculated costs and vertex sequences appear below.");
        hint.getStyleClass().add("panel-meta");
        hint.setWrapText(true);

        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setWrapText(true);
        outputArea.setPromptText("Load a graph, choose two vertices, and calculate a route.");
        VBox.setVgrow(outputArea, Priority.ALWAYS);

        VBox panel = new VBox(12, title, hint, outputArea);
        panel.getStyleClass().addAll("side-panel", "right-panel");
        panel.setPrefWidth(340);
        panel.setMinWidth(310);
        return panel;
    }

    private HBox createStatusBar() {
        statusLabel = new Label("Ready — open a graph data file to begin.");
        statusLabel.getStyleClass().add("status-text");

        HBox statusBar = new HBox(statusLabel);
        statusBar.setAlignment(Pos.CENTER_LEFT);
        statusBar.getStyleClass().add("status-bar");
        return statusBar;
    }

    private ComboBox<String> createVertexBox(String prompt) {
        ComboBox<String> box = new ComboBox<>();
        box.setEditable(true);
        box.setPromptText(prompt);
        box.setMaxWidth(Double.MAX_VALUE);
        box.setVisibleRowCount(10);
        box.setOnAction(event -> updateSelectionMarkers());
        return box;
    }

    private RadioButton createMethodButton(String label, int method) {
        RadioButton button = new RadioButton(label);
        button.setToggleGroup(methodGroup);
        button.setUserData(method);
        return button;
    }

    private Label createFieldLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("field-label");
        return label;
    }

    private HBox createLegendItem(String color, String text) {
        Region dot = new Region();
        dot.getStyleClass().add("legend-dot");
        dot.setStyle("-fx-background-color: " + color + ";");
        Label label = new Label(text);
        label.getStyleClass().add("legend-label");
        HBox item = new HBox(6, dot, label);
        item.setAlignment(Pos.CENTER_LEFT);
        return item;
    }

    private void displayLoadedGraph() {
        String[] names = fileHandler.getVertices().getNames();
        sourceBox.setItems(FXCollections.observableArrayList(names));
        destinationBox.setItems(FXCollections.observableArrayList(names));
        sourceBox.setValue(fileHandler.getSourceNode());
        destinationBox.setValue(fileHandler.getDestinationNode());

        if (fileHandler.getMethod() == Dijkstra.DISTANCE)
            distanceButton.setSelected(true);
        else if (fileHandler.getMethod() == Dijkstra.TIME)
            timeButton.setSelected(true);
        else
            bothButton.setSelected(true);

        File file = fileHandler.getSelectedFile();
        fileLabel.setText(file.getName());
        graphStatsLabel.setText(fileHandler.getVertices().size() + " vertices  •  "
                + fileHandler.getEdgeCount() + " directed edges");
        graphView.setGraph(fileHandler.getGraph(), fileHandler.getVertices());
        updateSelectionMarkers();
        outputArea.clear();
        runButton.setDisable(false);
        setStatus("Loaded " + file.getName() + " successfully.", false);
    }

    void loadGraph(File file) throws Exception {
        fileHandler.load(file);
        displayLoadedGraph();
    }

    void runCalculation() {
        if (fileHandler.getGraph() == null) {
            setStatus("Open a graph data file before calculating a route.", true);
            return;
        }

        String sourceName = selectedText(sourceBox);
        String destinationName = selectedText(destinationBox);
        if (sourceName.isBlank() || destinationName.isBlank()) {
            setStatus("Choose both a source and destination vertex.", true);
            return;
        }

        int source = fileHandler.getVertexIndex(sourceName);
        int destination = fileHandler.getVertexIndex(destinationName);
        if (source == -1 || destination == -1) {
            setStatus("The source or destination is not present in this graph.", true);
            return;
        }

        int method = (int) methodGroup.getSelectedToggle().getUserData();
        Dijkstra dijkstra = new Dijkstra(fileHandler.getGraph(), fileHandler.getVertices());
        int[] distancePath = new int[0];
        int[] timePath = new int[0];
        String result;

        if (method == 3) {
            String distanceResult = dijkstra.run(source, destination, Dijkstra.DISTANCE);
            distancePath = dijkstra.getLastPath();
            String timeResult = dijkstra.run(source, destination, Dijkstra.TIME);
            timePath = dijkstra.getLastPath();
            result = distanceResult + "\n\n────────────────────────────\n\n" + timeResult;
        } else if (method == Dijkstra.DISTANCE) {
            result = dijkstra.run(source, destination, method);
            distancePath = dijkstra.getLastPath();
        } else {
            result = dijkstra.run(source, destination, method);
            timePath = dijkstra.getLastPath();
        }

        outputArea.setText(result);
        outputArea.positionCaret(0);
        graphView.setSelection(source, destination);
        graphView.setPaths(distancePath, timePath);

        boolean routeFound = distancePath.length > 0 || timePath.length > 0;
        setStatus(routeFound ? "Route calculation complete." : "No route connects the selected vertices.",
                !routeFound);
    }

    private String selectedText(ComboBox<String> box) {
        String text = box.getEditor().getText();
        return text == null ? "" : text.trim();
    }

    private void updateSelectionMarkers() {
        if (sourceBox == null || destinationBox == null || fileHandler.getVertices() == null)
            return;

        int source = fileHandler.getVertexIndex(selectedText(sourceBox));
        int destination = fileHandler.getVertexIndex(selectedText(destinationBox));
        graphView.setSelection(source, destination);
    }

    private void setStatus(String message, boolean error) {
        statusLabel.setText(message);
        statusLabel.getStyleClass().removeAll("status-success", "status-error");
        statusLabel.getStyleClass().add(error ? "status-error" : "status-success");
    }
}
