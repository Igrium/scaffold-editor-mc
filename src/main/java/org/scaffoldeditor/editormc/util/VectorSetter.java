package org.scaffoldeditor.editormc.util;

import org.scaffoldeditor.nbt.math.Vector3f;

import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class VectorSetter extends HBox {
	TextField x = new TextField();
	TextField y = new TextField();
	TextField z = new TextField();
	
	public VectorSetter(Vector3f initial) {
		setVector(initial);
		
		x.setPrefWidth(40);
		y.setPrefWidth(40);
		z.setPrefWidth(40);
		
		getChildren().add(x);
		getChildren().add(y);
		getChildren().add(z);
		
		setSpacing(5);
	}
	
	public VectorSetter() {
		this(new Vector3f(0, 0, 0));
	}
	
	public Vector3f getVector() throws NumberFormatException {
		float x = Float.parseFloat(this.x.getText());
		float y = Float.parseFloat(this.y.getText());
		float z = Float.parseFloat(this.z.getText());
		return new Vector3f(x, y, z);
	}
	
	public void setVector(Vector3f vector) {
		x.setText(Float.toString(vector.x));
		y.setText(Float.toString(vector.y));
		z.setText(Float.toString(vector.z));
	}
	
	public void onUpdate(Runnable callback) {
		x.textProperty().addListener((prop, val, old) -> {
			callback.run();
		});
		
		y.textProperty().addListener((prop, val, old) -> {
			callback.run();
		});
		
		z.textProperty().addListener((prop, val, old) -> {
			callback.run();
		});
	}
}
