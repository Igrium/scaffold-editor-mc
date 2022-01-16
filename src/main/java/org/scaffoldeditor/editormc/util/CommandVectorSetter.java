package org.scaffoldeditor.editormc.util;

import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3fc;
import org.scaffoldeditor.scaffold.logic.datapack.arguements.CommandVector.Mode;
import org.scaffoldeditor.scaffold.logic.datapack.arguements.CommandVector3f;

import javafx.scene.control.ChoiceBox;

public class CommandVectorSetter extends VectorSetter {
	ChoiceBox<String> mode = new ChoiceBox<>();
	
	public CommandVectorSetter() {
		mode.getItems().addAll("Global", "Relative", "Local");
		getChildren().add(0, mode);
		mode.getSelectionModel().select(0);
	}
	
	public CommandVector3f getCommandVector() throws NumberFormatException {
		Vector3dc vec = getVector();
		Mode mode;
		String modeStr = this.mode.getSelectionModel().getSelectedItem();
		
		if (modeStr.equals("Global")) mode = Mode.GLOBAL;
		else if (modeStr.equals("Local")) mode = Mode.LOCAL;
		else if (modeStr.equals("Relative")) mode = Mode.RELATIVE;
		else {
			throw new AssertionError("Unknown Mode: "+modeStr);
		}
		
		return new CommandVector3f(vec, mode);
	}
	
	public void setVector(Vector3fc vector) {
		if (vector instanceof CommandVector3f) {
			setCommandVector((CommandVector3f) vector);
		} else {
			super.setVector(new Vector3d(vector));
		}
	}
	
	public void setCommandVector(CommandVector3f val) {
		switch (val.mode) {
		case GLOBAL:
			mode.getSelectionModel().select("Global");
			break;
		case LOCAL:
			mode.getSelectionModel().select("Local");
			break;
		case RELATIVE:
			mode.getSelectionModel().select("Relative");
			break;
		}
		
		super.setVector(new Vector3d(val));
	}
}
