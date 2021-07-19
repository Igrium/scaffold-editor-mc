package org.scaffoldeditor.editormc.util;

import org.scaffoldeditor.editormc.ui.ExceptionDialog;

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
		Alert alert = new Alert(AlertType.ERROR);
		alert.setHeaderText(title);
		alert.setContentText(message);
		alert.show();
	}
	
	public static void showError(String title, Throwable exception) {
		ExceptionDialog dialog = new ExceptionDialog();
		dialog.setException(exception);
		dialog.setHeaderText(title);
		dialog.showAndPrint();
	}
}
