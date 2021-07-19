package org.scaffoldeditor.editormc.ui;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.logging.log4j.LogManager;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 * Displays an exception object in a readable fashion.
 * @author Igrium
 */
public class ExceptionDialog extends Alert {
	
	private SimpleObjectProperty<Throwable> exceptionProperty = new SimpleObjectProperty<>();
	private TextArea textArea;
	
	public ExceptionDialog() {
		super(AlertType.ERROR);
		
		initUI();
		
		exceptionProperty.addListener(new ChangeListener<Throwable>() {

			@Override
			public void changed(ObservableValue<? extends Throwable> observable, Throwable oldValue,
					Throwable newValue) {
				if (newValue == null) {
					setContentText("null");
					textArea.setText("");
				}
				
				setContentText(newValue.getLocalizedMessage());
				
				StringWriter sw = new StringWriter();
				newValue.printStackTrace(new PrintWriter(sw));
				textArea.setText(sw.toString());
			}
		});
	}
	
	private void initUI() {
		textArea = new TextArea();
		textArea.setEditable(false);
		textArea.setWrapText(true);
		
		Label label = new Label("Stacktrace:");
		
		textArea.setMaxWidth(Double.MAX_VALUE);
		textArea.setMaxHeight(Double.MAX_VALUE);
		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);
		
		GridPane expContent = new GridPane();
		expContent.setMaxWidth(Double.MAX_VALUE);
		expContent.add(label, 0, 0);
		expContent.add(textArea, 0, 1);
		
		getDialogPane().setExpandableContent(expContent);
		
		setTitle("Exception thrown");
		setHeaderText("Exception");
	}
	
	/**
	 * Set the exception being displayed.
	 */
	public void setException(Throwable e) {
		exceptionProperty.set(e);
	}
	
	/**
	 * Get the exception being displayed.
	 */
	public Throwable getException() {
		return exceptionProperty.getValue();
	}
	
	public SimpleObjectProperty<Throwable> getExceptionProperty() {
		return exceptionProperty;
	}
	
	/**
	 * Shows the exception dialog (without waiting for a user response) and prints the exception to console.
	 * @throws IllegalStateException If this method is called on a threadother than the JavaFX Application Thread.
	 */
	public void showAndPrint() throws IllegalStateException {
		Throwable e = getException();
		
		StackTraceElement element = e.getStackTrace()[0];
		LogManager.getLogger(element.getClassName()).error(getHeaderText(), e);
		show();
	}
}
