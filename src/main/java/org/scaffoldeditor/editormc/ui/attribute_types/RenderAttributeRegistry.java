package org.scaffoldeditor.editormc.ui.attribute_types;

import java.util.HashMap;
import java.util.Map;

import org.scaffoldeditor.editormc.ui.EntityEditor;
import org.scaffoldeditor.scaffold.level.entity.attribute.Attribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.BlockTextureAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.BooleanAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.DoubleAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.FloatAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.IntAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.LongAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.NBTAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.StringAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.VectorAttribute;

import javafx.scene.Node;

public final class RenderAttributeRegistry {
	private RenderAttributeRegistry() {}
	
	public static final Map<String, IRenderAttributeType> registry = new HashMap<>();
	
	public static Node createSetter(String attributeName, Attribute<?> defaultValue) {
		if (registry.containsKey(defaultValue.registryName)) {
			return registry.get(defaultValue.registryName).createSetter(attributeName, defaultValue);
		} else {
			return EntityEditor.DEFAULT_ATTRIBUTE_TYPE.createSetter(attributeName, defaultValue);
		}
	}
	
	public static void initDefaults() {
		registry.put(StringAttribute.REGISTRY_NAME, new StringAttributeType());
		registry.put(BooleanAttribute.REGISTRY_NAME, new BooleanAttributeType());
		registry.put(IntAttribute.REGISTRY_NAME, new IntAttributeType());
		registry.put(LongAttribute.REGISTRY_NAME, new LongAttributeType());
		registry.put(FloatAttribute.REGISTRY_NAME, new FloatAttributeType());
		registry.put(DoubleAttribute.REGISTRY_NAME, new DoubleAttributeType());
		registry.put(VectorAttribute.REGISTRY_NAME, new VectorAttributeType());
		registry.put(NBTAttribute.REGISTRY_NAME, new NBTAttributeType());
		registry.put(BlockTextureAttribute.REGISTRY_NAME, new BlockTextureAttributeType());
	}
}