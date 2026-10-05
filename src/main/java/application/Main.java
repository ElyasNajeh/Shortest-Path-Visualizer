package application;

import java.io.File;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        Design design = new Design();
        design.interfaceUI(stage);

        if (getParameters().getRaw().contains("--smoke-test")) {
            try {
                design.loadGraph(new File("sample-data/graph_input_distance_time_5k_nodes.txt"));
                design.runCalculation();
            } catch (Exception exception) {
                exception.printStackTrace();
                System.exit(1);
            }

            PauseTransition exitDelay = new PauseTransition(Duration.seconds(2));
            exitDelay.setOnFinished(event -> stage.close());
            exitDelay.play();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
