package ru.itis.start;

import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ru.itis.utils.FileLoader;
import ru.itis.utils.PropertiesLoader;
import ru.itis.view.ViewManager;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

public class RarriateStart extends Application {
    private Stage mainStage;

    @Override
    public void start(Stage primaryStage) {
        try {
            initWindow(primaryStage);
            ViewManager viewManager = new ViewManager(mainStage);
            viewManager.setMainMenuScene();
            mainStage.setFullScreenExitHint("");
            mainStage.show();
        } catch (Exception e){
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public static void showError(Exception e) {
        Alert error = new Alert(Alert.AlertType.ERROR);
        error.setTitle("Error");
        error.setHeaderText(e.getMessage());

        VBox pane = new VBox();
        Label stackTrace = new Label("Stack Trace:");
        TextArea content = new TextArea();

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);

        content.setText(sw.toString());

        pane.getChildren().addAll(stackTrace, content);
        error.getDialogPane().setContent(pane);

        Optional<ButtonType> result = error.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            System.exit(-1);
        }
    }



    private void initWindow(Stage stage) {
        //Init window
        PropertiesLoader propertiesLoader = PropertiesLoader.getInstance();

        mainStage = stage;
        mainStage.setMinWidth(800);
        mainStage.setMinHeight(800);

        if(Boolean.parseBoolean(propertiesLoader.getProperty("FULLSCREEN"))){
            mainStage.setFullScreen(true);
        }
        mainStage.setTitle("Rarriate!");
        mainStage.getIcons().add(FileLoader.getIcon());
    }

    public static void main(String[] args) {
        launch(args);
    }

}
