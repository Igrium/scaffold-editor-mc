package org.scaffoldeditor.editormc.ui;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.jetbrains.annotations.Nullable;
import org.scaffoldeditor.editormc.ScaffoldEditor;

import javafx.application.Application;
import javafx.application.Platform;
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
		Platform.setImplicitExit(false);
		initUI(primaryStage);
	}
	
	protected void initUI(Stage stage) {
		this.stage = stage;
		Parent root;
		try {
			root = FXMLLoader.load(getClass().getResource("/assets/scaffold/ui/scaffold.fxml"));
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		mainScene = new Scene(root, 1280, 800);
		
		stage.setTitle("Scaffold Editor");
		stage.setScene(mainScene);
		
		viewport = new Viewport((ImageView) mainScene.lookup("#viewport"), (Pane) mainScene.lookup("#viewport_pane"));
		
		stage.show();
	}
	
	/**
	 * Get the Scaffold Editor instance this ui is tied to.
	 * @return The editor, or null if the editor is closed.
	 */
	@Nullable
	public ScaffoldEditor getEditor() {
		return editor;
	}
	
	public void setEditor(ScaffoldEditor editor) {
		this.editor = editor;
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
	
	@Nullable
	public static ScaffoldUI getInstance() {
		return instance;
	}
	
	/**
	 * Exit the editor.
	 */
	public void exit() {
		Platform.runLater(() -> {
			editor = null;
			stage.hide();
		});
	}
	
	/**
	 * Open the editor UI. If the UI has been opened already,
	 * this method returns the original instance. Otherwise,
	 * it launches a new instance and holds the thread until
	 * it has initialized.
	 * @return Scaffold UI instance.
	 */
	public static ScaffoldUI open() {
		if (instance != null) {
			Platform.runLater(() -> instance.initUI(instance.stage));
			return instance;
		} else {
			new Thread(() -> {
				Application.launch(ScaffoldUI.class, new String[] {});
			}).start();
			return ScaffoldUI.waitForinit();
		}
	}
	
}
