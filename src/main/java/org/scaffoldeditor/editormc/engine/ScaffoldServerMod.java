package org.scaffoldeditor.editormc.engine;

import org.scaffoldeditor.editormc.engine.entity.GenericModelEntity;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ScaffoldServerMod implements ModInitializer {
	
//	public static final EntityType<GenericModelEntity> GENERIC_MODEL = Registry.register(
//			Registry.ENTITY_TYPE,
//			new Identifier("scaffold", "generic_model"),
//			FabricEntityTypeBuilder.create(SpawnGroup.MISC, GenericModelEntity::new).dimensions(EntityDimensions.fixed(.9f, .9f)).build()
//	);

	@Override
	public void onInitialize() {
		
	}

}
