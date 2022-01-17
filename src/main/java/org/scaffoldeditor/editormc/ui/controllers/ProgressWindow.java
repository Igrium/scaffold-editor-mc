package org.scaffoldeditor.editormc.ui.controllers;

import java.io.IOException;

import org.scaffoldeditor.editormc.ui.controllers.CompileProgressUI.MessageType;
import org.scaffoldeditor.scaffold.util.ProgressListener;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class ProgressWindow {
    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label progressLabel;

    @FXML
    private TextFlow outputField;
    private Stage stage;

    public class ProgressWindowListener implements ProgressListener {

        @Override
        public void progress(float percent, String stage) {
            Platform.runLater(() -> {
                setProgress(percent);
                setProgressLabel(stage);
            });
        }

        @Override
        public void error(Throwable e, String detailMessage) {
            println(detailMessage+" "+e.getMessage(), MessageType.ERROR);
        }
        
    }

    /**
     * Get a {@link ProgressListener} that applies updates to this 
     * @return
     */
    public ProgressWindowListener getProgressListener() {
        return new ProgressWindowListener();
    }

    /**
     * Print a message to the window's output field.
     * @param message The message.
     * @param messageType The message type.
     */
    public void println(String message, MessageType messageType) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> println(message, messageType));
        }

        Text output = new Text(message);
        if (messageType == MessageType.ERROR) {
            output.getStyleClass().add("output-error");
        } else {
            output.getStyleClass().add("output-text");
        }

        outputField.getChildren().addAll(output, new Text(System.lineSeparator()));
    }

    public void setProgressLabel(String text) {
        progressLabel.setText(text);
    }

    public String getProgressLabel() {
        return progressLabel.getText();
    }

    public void setProgress(double progress) {
        progressBar.setProgress(progress);
    }

    public double getProgress() {
        return progressBar.getProgress();
    }

    public void close() {
        stage.close();
    }

    public static ProgressWindow open(Window parent, String title) {
        FXMLLoader loader = new FXMLLoader(ProgressWindow.class.getResource("/assets/scaffold/ui/progress_window.fxml"));
        Parent root;
        try {
            root = loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Unable to load progress UI!", e);
        }

        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(parent);
        stage.setTitle(title);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        ProgressWindow controller = loader.getController();
        controller.stage = stage;
        return controller;
    }
}
