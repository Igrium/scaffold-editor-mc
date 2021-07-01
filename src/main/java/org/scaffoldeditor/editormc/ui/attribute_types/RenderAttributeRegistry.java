package org.scaffoldeditor.editormc.ui.attribute_types;

import java.util.HashMap;
import java.util.Map;

import org.scaffoldeditor.editormc.ui.EntityEditor;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.level.entity.attribute.AssetAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.Attribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.BlockAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.BlockTextureAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.BooleanAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.ContainerAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.DoubleAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.EntityAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.EnumAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.FloatAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.IntAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.ListAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.LongAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.NBTAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.StringAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.VectorAttribute;

import javafx.scene.Node;

public final class RenderAttributeRegistry {
	private RenderAttributeRegistry() {}
	
	public static final Map<String, IRenderAttributeType> registry = new HashMap<>();
	
	public static Node createSetter(String attributeName, Attribute<?> defaultValue, Entity entity) {
		if (defaultValue == null) {
			return EntityEditor.DEFAULT_ATTRIBUTE_TYPE.createSetter(attributeName, null, entity);
		}
		
		if (registry.containsKey(defaultValue.registryName)) {
			return registry.get(defaultValue.registryName).createSetter(attributeName, defaultValue, entity);
		} else {
			return EntityEditor.DEFAULT_ATTRIBUTE_TYPE.createSetter(attributeName, defaultValue, entity);
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
		registry.put(ListAttribute.REGISTRY_NAME, new ListAttributeType());
		registry.put(AssetAttribute.REGISTRY_NAME, new AssetAttributeType());
		registry.put(EnumAttribute.REGISTRY_NAME, new EnumAttributeType());
		registry.put(ContainerAttribute.REGISTRY_NAME, new ContainerAttributeType());
		registry.put(BlockAttribute.REGISTRY_NAME, new BlockAttributeType());
		registry.put(EntityAttribute.REGISTRY_NAME, new EntityAttributeType());
	}
}
