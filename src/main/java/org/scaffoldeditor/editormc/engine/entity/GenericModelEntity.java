package org.scaffoldeditor.editormc.engine.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.world.World;

public class GenericModelEntity extends Entity {

	public GenericModelEntity(EntityType<?> type, World world) {
		super(type, world);
		
	}
	
	@Override
	protected boolean canClimb() {
		return false;
	}

	@Override
	protected void initDataTracker() {
	}

	@Override
	protected void readCustomDataFromTag(CompoundTag tag) {
	}

	@Override
	protected void writeCustomDataToTag(CompoundTag tag) {
	}

	@Override
	public Packet<?> createSpawnPacket() {
		return null;
	}
	
}
