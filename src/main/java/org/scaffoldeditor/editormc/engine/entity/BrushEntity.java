package org.scaffoldeditor.editormc.engine.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;

/**
 * Visual representation of a Scaffold brush that is not represented by blocks.
 * 
 * @author Igrium
 */
public class BrushEntity extends Entity {
	
	private static TrackedData<String> TEXTURE = DataTracker.registerData(BrushEntity.class, TrackedDataHandlerRegistry.STRING);
	private static TrackedData<Float> SIZE_X = DataTracker.registerData(BrushEntity.class, TrackedDataHandlerRegistry.FLOAT);
	private static TrackedData<Float> SIZE_Y = DataTracker.registerData(BrushEntity.class, TrackedDataHandlerRegistry.FLOAT);
	private static TrackedData<Float> SIZE_Z = DataTracker.registerData(BrushEntity.class, TrackedDataHandlerRegistry.FLOAT);

	public BrushEntity(EntityType<?> type, World world) {
		super(type, world);
	}

	@Override
	protected void initDataTracker() {
		dataTracker.startTracking(TEXTURE, "");
		dataTracker.startTracking(SIZE_X, 1f);
		dataTracker.startTracking(SIZE_Y, 1f);
		dataTracker.startTracking(SIZE_Z, 1f);
	}
	
	public String getTexture() {
		return dataTracker.get(TEXTURE);
	}
	
	public void setTexture(String newTexture) {
		if (!newTexture.equals(getTexture())) {
			dataTracker.set(TEXTURE, newTexture);
		}
	}
	
	public float getSizeX() {
		return dataTracker.get(SIZE_X);
	}
	
	public void setSizeX(float size) {
		if (size != getSizeX()) dataTracker.set(SIZE_X, size);
	}
	
	public float getSizeY() {
		return dataTracker.get(SIZE_Y);
	}
	
	public void setSizeY(float size) {
		if (size != getSizeY()) dataTracker.set(SIZE_Y, size);
	}
	
	public float getSizeZ() {
		return dataTracker.get(SIZE_Z);
	}
	
	public void setSizeZ(float size) {
		if (size != getSizeZ()) dataTracker.set(SIZE_Z, size);
	}
	
	public Vec3f getSize() {
		return new Vec3f(getSizeX(), getSizeY(), getSizeZ());
	}
	
	public void setSize(Vec3f size) {
		setSizeX(size.getX());
		setSizeY(size.getY());
		setSizeZ(size.getZ());
	}
	
	@Override
	protected Box calculateBoundingBox() {
		return new Box(0, 0, 0, getSizeX(), getSizeY(), getSizeZ());
	}

	@Override
	protected void readCustomDataFromNbt(NbtCompound nbt) {
		if (nbt.contains("texture")) setTexture(nbt.getString("texture"));
	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound nbt) {
		nbt.putString("texture", getTexture());
	}

	@Override
	public Packet<?> createSpawnPacket() {
		return new EntitySpawnS2CPacket(this);
	}

}
