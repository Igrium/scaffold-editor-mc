package org.scaffoldeditor.editormc.util;

import org.scaffoldeditor.editormc.ui.ExceptionDialog;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public final class UIUtils {
	private UIUtils() {}
	
	/**
	 * Show an error message to the user and hold until it's closed.
	 * @param title Header text.
	 * @param message Body text.
	 */
	public static void showError(String title, String message) {
		if (!Platform.isFxApplicationThread()) {
			Platform.runLater(() -> showError(title, message));
			return;
		}

		Alert alert = new Alert(AlertType.ERROR);
		alert.setHeaderText(title);
		alert.setContentText(message);
		alert.show();
	}
	
	/**
	 * Show an error message to the user and hold until it's closed.
	 * @param title Header text.
	 * @param exception Responsible exception.
	 */
	public static void showError(String title, Throwable exception) {
		if (!Platform.isFxApplicationThread()) {
			Platform.runLater(() -> showError(title, exception));
			return;
		}

		ExceptionDialog dialog = new ExceptionDialog();
		dialog.setException(exception);
		dialog.setHeaderText(title);
		dialog.showAndPrint();
	}
}
