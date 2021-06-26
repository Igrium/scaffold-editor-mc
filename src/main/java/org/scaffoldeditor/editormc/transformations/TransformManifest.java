package org.scaffoldeditor.editormc.transformations;

import org.scaffoldeditor.editormc.ui.ScaffoldUI;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public final class TransformManifest {
	private TransformManifest() {};
	
	public static ViewportTransformation getTransform(KeyEvent e) {
		if (e.getCode() == KeyCode.G) {
			return new ViewportTranslation(ScaffoldUI.getInstance());
		}
		
		return null;
	}
}
