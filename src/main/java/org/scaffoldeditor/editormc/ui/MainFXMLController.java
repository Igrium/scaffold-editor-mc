package org.scaffoldeditor.editormc.ui;

import org.scaffoldeditor.editormc.controls.ViewportControls;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public class MainFXMLController {
	
	@FXML
	private Pane viewport_pane;
	
	@FXML
	public void initialize() {
		addPressAndHoldHandler(viewport_pane, ViewportControls.HOLD_TIME, e -> handleViewportMousePressed(e));
		viewport_pane.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> handleViewportMouseReleased(e));
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
	}
	
}
	