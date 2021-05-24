package org.scaffoldeditor.editormc.ui;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.jetbrains.annotations.Nullable;
import org.scaffoldeditor.editormc.ScaffoldEditor;
import org.scaffoldeditor.editormc.controls.ViewportControls;
import org.scaffoldeditor.editormc.tools.SelectTool;
import org.scaffoldeditor.editormc.tools.Toolbar;
import org.scaffoldeditor.editormc.ui.controllers.FXMLCompileController;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.Entity;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * The main JavaFX application for Scaffold's UI.
 * @author Igrium
 */
public class ScaffoldUI extends Application {
	
	private static final CountDownLatch latch = new CountDownLatch(1);
	private static ScaffoldUI instance;
	
	private ScaffoldEditor editor;
	private MainFXMLController controller;
	protected Stage stage;
	protected Scene mainScene;
	protected Viewport viewport;
	protected ViewportControls viewportControls = new ViewportControls();
	protected Toolbar toolbar = new Toolbar();
	
	private boolean isExiting = false;
	
	
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
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/assets/scaffold/ui/scaffold.fxml"));
			root = loader.load();
			controller = loader.getController();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		mainScene = new Scene(root, 1280, 800);
		
		stage.setTitle("Scaffold Editor");
		stage.setScene(mainScene);
		
		viewport = new Viewport((ImageView) mainScene.lookup("#viewport"), (Pane) mainScene.lookup("#viewport_pane"));
		
		try {
			toolbar.addTool(new SelectTool());
			controller.getMainPanel().setTop(toolbar.root);
		} catch (Exception e) {
			e.printStackTrace();
		}

		
		System.setProperty("java.awt.headless", "false");
		stage.show();
		viewportControls.init(this);
		
		
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			
			@Override
			public void handle(WindowEvent event) {
				if (isExiting) {
					isExiting = false;
				} else {
					event.consume();
					editor.exit();
				}
			}
		});
		
		if (getEditor().getProject() == null) {
			openSplashScreen();
		}
	}
	
	public void openSplashScreen() {
		
		Parent root;
		try {
			root = FXMLLoader.load(getClass().getResource("/assets/scaffold/ui/splash_screen.fxml"));
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		Scene scene = new Scene(root, 600, 400);
		Stage stage = new Stage();
		stage.setTitle("Scaffold Editor");
		stage.setScene(scene);
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.initOwner(this.stage);
		
		stage.show();
	}
	
	/**
	 * Launch the open level screen.
	 */
	public void openLevel() {
		if (editor.getProject() == null) {
			return;
		}
		
		FileChooser chooser = new FileChooser();
		chooser.setInitialDirectory(editor.getProject().getProjectFolder().resolve("maps").toFile());
		chooser.getExtensionFilters().add(new ExtensionFilter("Scaffold Level Files", "*.mclevel"));
			
		File level = chooser.showOpenDialog(stage);
		if (level != null) {
			editor.openLevelFile(level);
		}
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
	
	public void setFPSIndicator(int value) {
		Label fps = (Label) mainScene.lookup("#fps_indicator");
		fps.setText("fps: "+value);
	}
	
	public Stage getStage() {
		return stage;
	}
	
	public void openCompiler() {
		try {
			FXMLCompileController.open(stage);
		} catch (IOException e) {
			e.printStackTrace();
		}
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
			isExiting = true;
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

	public void updateEntityList() {
		Platform.runLater(() -> {
			GridPane entityList = (GridPane) mainScene.lookup("#entityList");
			entityList.getChildren().clear();
			Level level = ScaffoldUI.getInstance().getEditor().getLevel();
			List<String> entityStack = level.getEntityStack();
			for (int i = 0; i < entityStack.size(); i++) {
				Entity entity = level.getEntity(entityStack.get(i));
				generateEntityListing(entityList, i, entity, false, false);
			}
		});
	}
	
	private void generateEntityListing(GridPane entityList, int index, Entity entity, boolean enableUp, boolean enableDown) {
		Label label = new Label(entity.getName());
		label.setStyle("entity-label");
		
		label.setOnMouseClicked(e -> {
			if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
				openEntityEditor(entity);
			}
		});
		
		MenuItem edit = new MenuItem("Edit Entity");
		edit.setOnAction(e -> {
			openEntityEditor(entity);
		});
		
		ContextMenu contextMenu = new ContextMenu(edit);
		label.setContextMenu(contextMenu);
		
		Button upButton = new Button("↑");
		upButton.setDisable(!enableUp);
		
		Button downButton = new Button("↓");
		downButton.setDisable(!enableDown);
		
		entityList.addRow(index, label, upButton, downButton);
	}
	
	public void openEntityEditor(Entity entity) {
		EntityEditor editor = new EntityEditor(stage, entity);
		editor.show();
	}
}
