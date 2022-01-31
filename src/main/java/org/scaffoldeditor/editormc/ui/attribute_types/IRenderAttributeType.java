package org.scaffoldeditor.editormc.ui.attribute_types;

import org.scaffoldeditor.scaffold.entity.Entity;
import org.scaffoldeditor.scaffold.entity.attribute.Attribute;

import javafx.scene.Node;

/**
 * Handles the rendering of attribute types in the UI.
 * @author Igrium
 */
public interface IRenderAttributeType {
	public Node createSetter(String name, Attribute<?> defaultValue, Entity entity);	
}

