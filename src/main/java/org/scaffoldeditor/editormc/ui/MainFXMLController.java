package org.scaffoldeditor.editormc.ui;

import org.scaffoldeditor.editormc.controls.ViewportControls;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import javafx.stage.Stage;


public class MainFXMLController {
	
	public boolean isShiftPressed;
	
	@FXML
	private Pane viewport_pane;
	
	@FXML
	public void initialize() {
		addPressAndHoldHandler(viewport_pane, ViewportControls.HOLD_TIME, e -> handleViewportMousePressed(e));
		viewport_pane.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> handleViewportMouseReleased(e));
		viewport_pane.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
			if (e.getCode().equals(KeyCode.SHIFT)) {
				isShiftPressed = true;
			}
		});
		viewport_pane.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
			if (e.getCode().equals(KeyCode.SHIFT)) {
				isShiftPressed = false;
			}
		});		
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
	private void save() {
		ScaffoldUI.getInstance().getEditor().save();
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
	private void select(MouseEvent e) {
		int x = (int) e.getX();
		int y = (int) e.getY();

		ScaffoldUI.getInstance().getViewport().select(x, y, isShiftPressed);
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
		} else if (e.getButton() == MouseButton.PRIMARY) {
			select(e);
		}
	}
	
	private void handleViewportMouseReleased(MouseEvent e) {
		if (e.getButton() == MouseButton.SECONDARY) {
			ScaffoldUI.getInstance().viewportControls.setEnableControls(false);
		}
	}
	
}
	