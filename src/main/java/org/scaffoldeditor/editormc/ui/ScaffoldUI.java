package org.scaffoldeditor.editormc.ui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;
import org.scaffoldeditor.editormc.ScaffoldEditor;
import org.scaffoldeditor.editormc.controls.ViewportControls;
import org.scaffoldeditor.editormc.tools.EntityTool;
import org.scaffoldeditor.editormc.tools.MoveTool;
import org.scaffoldeditor.editormc.tools.ResizeTool;
import org.scaffoldeditor.editormc.tools.SelectTool;
import org.scaffoldeditor.editormc.tools.Toolbar;
import org.scaffoldeditor.editormc.tools.ViewportTool;
import org.scaffoldeditor.editormc.ui.controllers.FXMLCompileController;
import org.scaffoldeditor.editormc.ui.controllers.Outliner;
import org.scaffoldeditor.editormc.ui.controllers.MinecraftConsole;
import org.scaffoldeditor.editormc.ui.controllers.ViewportHeader;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
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
	
	public CountDownLatch playerSpawnLatch = new CountDownLatch(1);
	private ScaffoldEditor editor;
	private MainFXMLController controller;
	protected Stage stage;
	protected Scene mainScene;
	protected Viewport viewport;
	protected ViewportControls viewportControls = new ViewportControls();
	protected Toolbar toolbar;
	protected Outliner outliner;
	protected MinecraftConsole console;
	
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
		try {
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
			
			System.setProperty("java.awt.headless", "false");
			stage.show();
			controller.init();
			
			toolbar = new Toolbar();
			toolbar.addTool(new SelectTool(viewport), "select");
			toolbar.addTool(new MoveTool(viewport, editor), "move");
			toolbar.addTool(new ResizeTool(this), "resize");
			toolbar.addTool(new EntityTool(), "entity");
			toolbar.setTool("select");
			controller.setToolbar(toolbar);
			
			outliner = Outliner.load(this);
			VBox.setVgrow(outliner.getRoot(), Priority.ALWAYS);
			controller.getOutlinerBox().getChildren().add(outliner.getRoot());
			
			controller.getViewportHeader().init(editor);
			
			isExiting = false;
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
			
			LogManager.getLogger().info("Awaiting player spawn...");
			playerSpawnLatch.await();
			viewportControls.init(this);
			
			if (getEditor().getProject() == null) {
				openSplashScreen();
			}
		} catch (Exception e) {
			LogManager.getLogger().error("Unable to initialize scaffold ui!");
			e.printStackTrace();
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
	
	public void newLevel() {
		if (editor.getProject() == null) return;
		FileChooser chooser = new FileChooser();
		chooser.setInitialDirectory(editor.getProject().getProjectFolder().resolve("maps").toFile());
		chooser.getExtensionFilters().add(new ExtensionFilter("Scaffold Level Files", "*.mclevel"));
		chooser.setInitialFileName("*.mclevel");
		
		File level = chooser.showSaveDialog(stage);
		if (level != null) {
			File level2;
			if (!level.getName().endsWith(".mclevel")) {
				level2 = new File(level.getParentFile(), level.getName()+".mclevel");
			} else {
				level2 = level;
			}
			editor.newLevel(level2);
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
		controller.setFPS(Integer.toString(value));
	}
	
	public void setCoordIndicator(String value) {
		controller.setCoords(value);
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
	 * @param editor Editor instance.
	 * @return Scaffold UI instance.
	 */
	public static ScaffoldUI open(ScaffoldEditor editor) {
		if (instance != null) {
			instance.playerSpawnLatch = new CountDownLatch(1);
			Platform.runLater(() -> {
				instance.setEditor(editor);
				instance.initUI(instance.stage);
			});
			return instance;
		} else {
			new Thread(() -> {
				Application.launch(ScaffoldUI.class, new String[] {});
			}).start();
			ScaffoldUI instance = ScaffoldUI.waitForinit();
			instance.setEditor(editor);
			return instance;
		}
	}

	public void updateEntityList() {
		Platform.runLater(() -> {
			Level level = getEditor().getLevel();
			outliner.setEntities(level.getLevelStack());
		});
	}
	
	public void openEntityEditor(Entity entity) {
		if (entity == null) {
			throw new IllegalArgumentException("Entity cannot be null!");
		}
		
		EntityEditor editor = new EntityEditor(stage, entity);
		editor.show();
	}
	
	public void setToolVisual(ViewportTool tool) {
		if (tool != null) {
			controller.getToolPropertiesLabel().setText("Tool Properties: "+tool.getName());
			controller.getToolPropertiesPane().setCenter(tool.getPropertiesPane());
		} else {
			controller.getToolPropertiesLabel().setText("[No tool selected]");
			controller.getToolPropertiesPane().setCenter(null);
		}
	}
	
	public void openConsole() {
		if (console != null) {
			console.getStage().requestFocus();
		} else {
			try {
				console = MinecraftConsole.open(stage);
			} catch (IOException e) {
				LogManager.getLogger().error("Error opening console!", e);
				return;
			}
			
			console.getStage().setOnCloseRequest(event -> {
				this.console = null;
			});
		}
	}
	
	public MainFXMLController getController() {
		return controller;
	}
	
	public ViewportHeader getViewportHeader() {
		return controller.getViewportHeader();
	}
	
	public Outliner getOutliner() {
		return outliner;
	}
	
	public void reloadRecentFiles() {
		if (editor.getCache().has("recentLevels")) {
			List<String> recent = new ArrayList<String>();
			for (Object fileName : getEditor().getCache().getJSONArray("recentLevels")) {
				if (fileName instanceof String) {
					recent.add((String) fileName);
				}
			}
			controller.reloadRecent(recent);
		} else {
			controller.reloadRecent(Collections.emptyList());
		}
	}
}
