package org.scaffoldeditor.editormc.engine.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
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
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class BillboardEntity extends Entity {
	
	public static final Identifier IDENTIFIER = new Identifier("scaffold", "billboardentity");
	
	public static final EntityType<BillboardEntity> TYPE = Registry.register(Registry.ENTITY_TYPE, IDENTIFIER, FabricEntityTypeBuilder
			.create(SpawnGroup.MISC, BillboardEntity::new).dimensions(EntityDimensions.fixed(1, 1)).trackedUpdateRate(1).build());
	
	private static final TrackedData<String> TEXTURE = DataTracker.registerData(BillboardEntity.class, TrackedDataHandlerRegistry.STRING);
	private static final TrackedData<Float> SCALE = DataTracker.registerData(BillboardEntity.class, TrackedDataHandlerRegistry.FLOAT);

	public BillboardEntity(EntityType<?> type, World world) {
		super(type, world);
	}

	@Override
	protected void initDataTracker() {
		this.dataTracker.startTracking(TEXTURE, "");
		this.dataTracker.startTracking(SCALE, 1.0f);
	}

	@Override
	protected void readCustomDataFromNbt(NbtCompound nbt) {
		if (nbt.contains("texture")) setTexture(nbt.getString("texture"));
		if (nbt.contains("scale")) setScale(nbt.getFloat("scale"));
	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound nbt) {
		nbt.putString("texture", getTexture());
		nbt.putFloat("scale", getScale());
	}

	@Override
	public Packet<?> createSpawnPacket() {
		return new EntitySpawnS2CPacket(this);
	}
	
	public void setTexture(String texture) {
		if (!texture.equals(dataTracker.get(TEXTURE))) {
			this.dataTracker.set(TEXTURE, texture);
		}
	}
	
	@Override
	protected Box calculateBoundingBox() {
		float scale = getScale();
		Vec3d min = new Vec3d(-.5 * scale, -.5 * scale, -.5 * scale);
		Vec3d max = new Vec3d(.5 * scale, .5 * scale, .5 * scale);
		
		min = min.add(getPos());
		max = max.add(getPos());
		
		return new Box(min, max);
	}
	
	
	public String getTexture() {
		return this.dataTracker.get(TEXTURE);
	}
	
	public float getScale() {
		return dataTracker.get(SCALE);
	}
	
	public void setScale(float scale) {
		if (scale != getScale()) {
			dataTracker.set(SCALE, scale);
		}
	}
}
