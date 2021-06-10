package org.scaffoldeditor.editormc.ui.controllers;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.scaffoldeditor.scaffold.compile.Compiler;
import org.apache.logging.log4j.LogManager;
import org.scaffoldeditor.editormc.ScaffoldEditor;
import org.scaffoldeditor.scaffold.compile.Compiler.CompileEndStatus;
import org.scaffoldeditor.scaffold.compile.Compiler.CompileProgressListener;
import org.scaffoldeditor.scaffold.compile.Compiler.CompileResult;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.attribute.Attribute;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class CompileProgressUI {
	public enum MessageType {
		LOG,
		WARNING,
		ERROR
	}
	
	@FXML
	private ProgressBar progressBar;
	@FXML
	private TextFlow outputField;
	@FXML
	private Button launchButton;
	
	private Thread thread;
	private boolean isFinished = false;
	
	private List<Consumer<CompileResult>> endListeners = new ArrayList<>();
	
	private Stage stage;
	
	public Thread compile(Level level, Path target, Map<String, Attribute<?>> args) {
		thread = new Thread(() -> {
			println("Initializing compile into world folder: "+target.toString(), MessageType.LOG);
			
			Compiler compiler = ScaffoldEditor.getInstance().getProject().getCompiler();
			CompileResult result = compiler.compile(level, target, args, new CompileProgressListener() {
				
				@Override
				public void onError(String message) {
					CompileProgressUI.this.println(message, MessageType.ERROR);
				}
				
				@Override
				public void onCompileProgress(float percent, String description) {
					println(description);
					Platform.runLater(() -> {
						progressBar.setProgress(percent);
					});
				}

				@Override
				public void println(String string) {
					CompileProgressUI.this.println(string, MessageType.LOG);
				}
			});
			
			if (result.endStatus == CompileEndStatus.FINISHED) {
				println("Compile finished!", MessageType.LOG);
				launchButton.setDisable(false);
				
			} else if (result.endStatus == CompileEndStatus.FAILED) {
				println("Compile failed! "+result.errorMessage, MessageType.ERROR);
			}
			Platform.runLater(() -> {
				isFinished = true;
				for (Consumer<CompileResult> c : endListeners) {
					c.accept(result);
				}
			});
			
		}, "Compile Thread");
		
		thread.start();
		return thread;
	}
	
	public void println(String message, MessageType type) {
		LogManager.getLogger().info(message);
		Platform.runLater(() -> {
			Text output = new Text("> "+message);
			switch(type) {
			case ERROR:
				output.getStyleClass().add("output-error");
				break;
			default:
				output.getStyleClass().add("output-text");
				break;
			}
			outputField.getChildren().addAll(output, new Text(System.lineSeparator()));
		});
	}

	public void onFinishedCompile(Consumer<CompileResult> listener) {
		endListeners.add(listener);
	}
	
	@FXML
	public void cancel() {
		ScaffoldEditor.getInstance().getProject().getCompiler().cancel();
		stage.close();
	}
	
	public static CompileProgressUI open(Window parent) throws IOException {
		FXMLLoader loader = new FXMLLoader(CompileProgressUI.class.getResource("/assets/scaffold/ui/compile_progress_screen.fxml"));
		Parent root = loader.load();
		
		Scene scene = new Scene(root, 600, 400);
		Stage stage = new Stage();
		stage.setTitle("Compiler Progress");
		stage.setScene(scene);
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.initOwner(parent);
		CompileProgressUI controller = loader.getController();
		controller.stage = stage;
		
		stage.setOnCloseRequest(e -> {
			if (!controller.isFinished) {
				e.consume();
				controller.cancel();
			}
		});
		
		stage.show();
		
		return controller;
	}
	
}
