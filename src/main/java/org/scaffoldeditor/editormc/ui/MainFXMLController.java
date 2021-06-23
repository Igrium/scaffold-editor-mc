package org.scaffoldeditor.editormc.ui;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.scaffoldeditor.editormc.ScaffoldEditor;
import org.scaffoldeditor.editormc.controls.ViewportControls;
import org.scaffoldeditor.editormc.tools.Toolbar;
import org.scaffoldeditor.editormc.ui.controllers.ViewportHeader;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.operation.DeleteEntityOperation;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import javafx.stage.Stage;


public class MainFXMLController {
	
	public boolean isShiftPressed;
	
	@FXML
	private Pane viewport_pane;
	
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
	
	private Toolbar toolbar;
	
	@FXML
	public void initialize() {

	}
	
	public void init() {
		ScaffoldUI ui = ScaffoldUI.getInstance();
		addPressAndHoldHandler(viewport_pane, ViewportControls.HOLD_TIME, e -> handleViewportMousePressed(e));
		viewport_pane.addEventHandler(MouseEvent.MOUSE_PRESSED, ui.viewport::handleMousePressed);
		viewport_pane.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> handleViewportMouseReleased(e));
		viewport_pane.addEventHandler(MouseEvent.MOUSE_CLICKED, ui.viewport::handleMouseClicked);
		viewport_pane.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
			ScaffoldUI.getInstance().viewport.handleKeyReleased(e);
			if (e.getCode().equals(KeyCode.SHIFT)) {
				isShiftPressed = true;
			}
		});
		viewport_pane.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
			ScaffoldUI.getInstance().viewport.handleKeyPressed(e);
			if (e.getCode().equals(KeyCode.SHIFT)) {
				isShiftPressed = false;
			}
		});
		viewport_pane.addEventHandler(KeyEvent.KEY_TYPED, ui.viewport::handleKeyTyped);
		
		viewport_pane.addEventHandler(MouseEvent.MOUSE_MOVED, e -> {
			ScaffoldUI.getInstance().viewport.handleMouseMoved((int) e.getX(), (int) e.getY());
		});
		
		viewport_pane.addEventHandler(MouseEvent.MOUSE_DRAGGED, ui.viewport::handleMouseDragged);
	}
	
	@FXML
	private void showSettings() {
		SettingsWindow window = new SettingsWindow((Stage) viewport_pane.getScene().getWindow());
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
	private void save() {
		ScaffoldUI.getInstance().getEditor().save();
	}
	
	@FXML
	private void compile() {
		ScaffoldUI.getInstance().openCompiler();
	}
	
	@FXML
	private void undo() {
		ScaffoldUI.getInstance().getEditor().getLevel().getOperationManager().undo();
	}
	
	@FXML
	private void redo() {
		ScaffoldUI.getInstance().getEditor().getLevel().getOperationManager().redo();
	}
	
	@FXML
	private void recompileWorld() {
		ScaffoldEditor.getInstance().getProject().getLevelService().execute(() -> {
			ScaffoldEditor.getInstance().getLevel().compileBlockWorld(false);
		});
	}
	
	@FXML
	private void delete() {
		Set<Entity> entities = ScaffoldEditor.getInstance().getSelectedEntities();
		getLevel().getOperationManager().execute(new DeleteEntityOperation(getLevel(), entities));
	}
	
	@FXML
	private void copy() {
		ScaffoldEditor.getInstance().copySelection();
	}
	
	@FXML
	private void cut() {
		ScaffoldEditor.getInstance().cutSelection();
	}
	
	@FXML
	private void paste() {
		ScaffoldEditor.getInstance().paste();
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
}
	