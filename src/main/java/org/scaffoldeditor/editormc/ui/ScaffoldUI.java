package org.scaffoldeditor.editormc.ui;

import java.util.concurrent.CountDownLatch;

import org.scaffoldeditor.editormc.ScaffoldEditor;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * The main JavaFX application for Scaffold's UI.
 * @author Igrium
 */
public class ScaffoldUI extends Application {
	
	private static final CountDownLatch latch = new CountDownLatch(1);
	private static ScaffoldUI instance;
	
	private ScaffoldEditor editor;
	protected Stage stage;
	protected Scene mainScene;
	protected Viewport viewport;
	
	public ScaffoldUI() {
		instance = this;
		latch.countDown();
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		this.stage = primaryStage;
		
		Parent root = FXMLLoader.load(getClass().getResource("/assets/scaffold/ui/scaffold.fxml"));
		mainScene = new Scene(root, 1280, 800);
		
		stage.setTitle("Scaffold Editor");
		stage.setScene(mainScene);
		
		viewport = new Viewport((ImageView) mainScene.lookup("#viewport"), (Pane) mainScene.lookup("#viewport_pane"));
		
		stage.show();
	}
	
	public ScaffoldEditor getEditor() {
		return editor;
	}
	
	public Viewport getViewport() {
		return viewport;
	}
	
	// INITIALIZATION
	
	/**
	 * Wait for the ui to finish initializing.
	 * @return Initialized UI
	 */
	public static ScaffoldUI waitForinit() {
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return instance;
	}
	
}
