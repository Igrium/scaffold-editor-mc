package org.scaffoldeditor.editormc.engine.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

/**
 * Renders an arbitrary block model as an entity in the world.
 * @author Igrium
 */
public class ModelEntity extends Entity {
	
	public static final Identifier IDENTIFIER = new Identifier("scaffold", "modelentitiy");
	
	public static final EntityType<ModelEntity> TYPE = Registry.register(Registry.ENTITY_TYPE, IDENTIFIER, FabricEntityTypeBuilder
			.create(SpawnGroup.MISC, ModelEntity::new).dimensions(EntityDimensions.fixed(1, 1)).build());
	
	private static final TrackedData<String> MODEL = DataTracker.registerData(ModelEntity.class, TrackedDataHandlerRegistry.STRING);

	public ModelEntity(EntityType<?> type, World world) {
		super(type, world);
	}
	
	@Override
	protected void initDataTracker() {
		this.dataTracker.startTracking(MODEL, "");
	}

	@Override
	protected void readCustomDataFromNbt(NbtCompound nbt) {
		if (nbt.contains("model")) setModel(new ModelIdentifier(nbt.getString("model")));		
	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound nbt) {
		nbt.putString("model", getModel().toString());
	}

	@Override
	public Packet<?> createSpawnPacket() {
		return new EntitySpawnS2CPacket(this);
	}

	public ModelIdentifier getModel() {
		return new ModelIdentifier(this.dataTracker.get(MODEL));
	}

	public void setModel(ModelIdentifier modelIdentifier) {
		String modelString = modelIdentifier.toString();
		if (!modelString.equals(dataTracker.get(MODEL))) {
			this.dataTracker.set(MODEL, modelIdentifier.toString());
		}
	}

}
