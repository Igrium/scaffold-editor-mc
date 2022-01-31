package org.scaffoldeditor.editormc.ui;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.scaffoldeditor.editormc.EditorOperationManager;
import org.scaffoldeditor.editormc.ScaffoldEditor;
import org.scaffoldeditor.editormc.controls.ViewportControls;
import org.scaffoldeditor.editormc.sub_editors.LevelPropertiesEditor;
import org.scaffoldeditor.editormc.tools.Toolbar;
import org.scaffoldeditor.editormc.ui.controllers.ViewportHeader;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.entity.Entity;
import org.scaffoldeditor.scaffold.operation.AddGroupOperation;
import org.scaffoldeditor.scaffold.operation.DeleteEntityOperation;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;


public class MainFXMLController {
	
	public boolean isShiftPressed;
	
	@FXML
	private BorderPane mainPanel;
	
	@FXML
	private BorderPane toolPropertiesPane;
	
	@FXML
	private Label toolPropertiesLabel;
	
	@FXML
	private Menu openRecentMenu;
	
	@FXML
	private Label fpsIndicator;
	
	@FXML
	private Label coordIndicator;
	
	@FXML
	private HBox topBox;
	
	@FXML	
	private ViewportHeader viewportHeaderController;
	
	@FXML
	private VBox outlinerBox;
	
	@FXML
	private ImageView viewport;
	@FXML
	private Pane viewportPane;
	
	private Toolbar toolbar;
	
	@FXML
	public void initialize() {

	}
	
	public void init() {
		ScaffoldUI ui = ScaffoldUI.getInstance();
		addPressAndHoldHandler(viewportPane, ViewportControls.HOLD_TIME, e -> handleViewportMousePressed(e));
		viewportPane.addEventHandler(MouseEvent.MOUSE_PRESSED, ui.viewport::handleMousePressed);
		viewportPane.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> handleViewportMouseReleased(e));
		viewportPane.addEventHandler(MouseEvent.MOUSE_CLICKED, ui.viewport::handleMouseClicked);
		viewportPane.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
			ScaffoldUI.getInstance().viewport.handleKeyReleased(e);
			if (e.getCode().equals(KeyCode.SHIFT)) {
				isShiftPressed = true;
			}
		});
		mainPanel.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
			ScaffoldUI.getInstance().viewport.handleKeyPressed(e);
			if (e.getCode().equals(KeyCode.SHIFT)) {
				isShiftPressed = false;
			}
		});
		mainPanel.addEventHandler(KeyEvent.KEY_TYPED, ui.viewport::handleKeyTyped);
		
		mainPanel.addEventHandler(MouseEvent.MOUSE_MOVED, e -> {
			ScaffoldUI.getInstance().viewport.handleMouseMoved((int) e.getX(), (int) e.getY());
		});
		
		viewportPane.addEventHandler(MouseEvent.MOUSE_DRAGGED, ui.viewport::handleMouseDragged);
	}
	
	@FXML
	private void showSettings() {
		SettingsWindow window = new SettingsWindow((Stage) viewportPane.getScene().getWindow());
		window.show();
	}
	
	@FXML
	private void openLevel() {
		ScaffoldUI.getInstance().openLevel();
	}
	
	@FXML
	private void newLevel() {
		ScaffoldUI.getInstance().newLevel();
	}
	
	@FXML
	public void save() {
		ScaffoldEditor editor = ScaffoldEditor.getInstance();
		if (editor.getLevelFile() == null) {
			saveAs();
		} else {
			editor.save();
		}
	}
	
	@FXML
	public void saveAs() {
		FileChooser chooser = new FileChooser();
		chooser.setInitialDirectory(
				ScaffoldEditor.getInstance().getProject().getProjectFolder().resolve("maps").toFile());
		chooser.setTitle("Save level file");
		File old = ScaffoldEditor.getInstance().getLevelFile();
		chooser.setInitialFileName(old != null ? old.getName() : "*.mclevel");
		chooser.getExtensionFilters().add(new ExtensionFilter("Scaffold Level Files", "*.mclevel"));
		
		File file = chooser.showSaveDialog(ScaffoldUI.getInstance().stage);
		if (file != null) {
			File level2;
			if (!file.getName().endsWith(".mclevel")) {
				level2 = new File(file.getParentFile(), file.getName()+".mclevel");
			} else {
				level2 = file;
			}
			
			ScaffoldEditor.getInstance().saveAs(level2);
		}
	}
	
	@FXML
	private void compile() {
		if (ScaffoldEditor.getInstance().getLevel().hasUnsavedChanges()) {
			if (!ScaffoldUI.getInstance().showUnsavedDialog()) return;
		}
		ScaffoldUI.getInstance().openCompiler();
	}
	
	@FXML
	private void undo() {
		EditorOperationManager.getInstance().undo();
	}
	
	@FXML
	private void redo() {
		EditorOperationManager.getInstance().redo();
	}
	
	@FXML
	private void recompileWorld() {
		EditorOperationManager.getInstance().compileLevel();
	}
	
	@FXML
	private void delete() {
		Set<Entity> entities = ScaffoldEditor.getInstance().getSelectedEntities();
		EditorOperationManager.getInstance().runOperation(new DeleteEntityOperation(getLevel(), entities));
	}
	
	@FXML
	private void copy() {
		EditorOperationManager.getInstance().copySelection();
	}
	
	@FXML
	private void cut() {
		EditorOperationManager.getInstance().cutSelection();
	}
	
	@FXML
	private void paste() {
		EditorOperationManager.getInstance().paste();
	}
	
	@FXML
	private void newGroup() {
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("New Entity Group");
		dialog.setHeaderText("Enter Group Name");
		Optional<String> name = dialog.showAndWait();
		if (name.isEmpty() || name.get().length() == 0) return;
		
		EditorOperationManager.getInstance().runOperation(new AddGroupOperation(
				ScaffoldUI.getInstance().getOutliner().getSelectedGroup(), name.get(), getLevel()));
	}
	
	@FXML
	public void openLevelProperties() {
		LevelPropertiesEditor.open(ScaffoldUI.getInstance().getStage(), getLevel());
	}
	
	// VIEWPORT
	private void addPressAndHoldHandler(Node node, Duration holdTime, EventHandler<MouseEvent> handler) {

		class Wrapper<T> {
			T content;
		}
		Wrapper<MouseEvent> eventWrapper = new Wrapper<>();

		PauseTransition holdTimer = new PauseTransition(holdTime);
		holdTimer.setOnFinished(event -> handler.handle(eventWrapper.content));

		node.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
			eventWrapper.content = event;
			holdTimer.playFromStart();
		});
		node.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> holdTimer.stop());
	}
	
	private void handleViewportMousePressed(MouseEvent e) {
		if (e.getButton() == MouseButton.SECONDARY) {
			ScaffoldUI.getInstance().viewportControls.setEnableControls(true);
		}
	}
	
	private void handleViewportMouseReleased(MouseEvent e) {
		if (e.getButton() == MouseButton.SECONDARY) {
			ScaffoldUI.getInstance().viewportControls.setEnableControls(false);
		}
		ScaffoldUI.getInstance().viewport.handleMouseReleased(e);
	}
	
	public BorderPane getMainPanel() {
		return mainPanel;
	}
	
	public BorderPane getToolPropertiesPane() {
		return toolPropertiesPane;
	}
	
	public Label getToolPropertiesLabel() {
		return toolPropertiesLabel;
	}
	
	public VBox getOutlinerBox() {
		return outlinerBox;
	}
	
	public Level getLevel() {
		return ScaffoldUI.getInstance().getEditor().getLevel();
	}
	
	@FXML
	protected void openConsole() {
		ScaffoldUI.getInstance().openConsole();
	}
	
	public void setFPS(String value) {
		fpsIndicator.setText("FPS: "+value);
	}
	
	public void setCoords(String value) {
		coordIndicator.setText("Viewport Pos: "+value);
	}
	
	public void reloadRecent(List<String> items) {
		openRecentMenu.getItems().clear();
		for (String string : items) {
			MenuItem item = new MenuItem(string);
			File file = ScaffoldEditor.getInstance().getProject().assetManager().getAbsoluteFile(string);
			item.setOnAction(event -> {
				ScaffoldEditor.getInstance().openLevelFile(file);
			});
			openRecentMenu.getItems().add(item);
		}
	}
	
	public void setToolbar(Toolbar toolbar) {
		this.toolbar = toolbar;
		topBox.getChildren().add(0, toolbar.root);
	}
	
	public Toolbar getToolbar() {
		return toolbar;
	}

	public ViewportHeader getViewportHeader() {
		return viewportHeaderController;
	}
	
	public Pane getViewportPane() {
		return viewportPane;
	}
	
	public ImageView getViewportImage() {
		return viewport;
	}
}
	