package org.scaffoldeditor.editormc.engine.world;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.light.LightingProvider;

/**
 * Derived from the create mod.
 */
public class PlacementSimulationWorld extends WrappedWorld {
	
	public Map<BlockPos, BlockState> blocksAdded;
	public Map<BlockPos, BlockEntity> entsAdded;
	
	public Set<ChunkSectionPos> spannedChunks;
	public LightingProvider lighter;
	
	public PlacementSimulationWorld(World base) {
		super(base);
	}
	
	@Override
	public LightingProvider getLightingProvider() {
		return lighter;
	}
	
	public void addBlockEntities(Collection<BlockEntity> blockEntities) {
		blockEntities.forEach(ent -> entsAdded.put(ent.getPos(), ent));
	}
	
	public void setBlockEntities(Collection<BlockEntity> blockEntities) {
		entsAdded.clear();
		addBlockEntities(blockEntities);
	}
	
	public void clear() {
		blocksAdded.clear();
		entsAdded.clear();
	}
	
	@Override
	public boolean setBlockState(BlockPos pos, BlockState state, int flags) {
		blocksAdded.put(pos, state);
		return true;
	}
	
	@Override
	public BlockEntity getBlockEntity(BlockPos pos) {
		return entsAdded.get(pos);
	}
	
	@Override
	public boolean testBlockState(BlockPos pos, Predicate<BlockState> state) {
		return state.test(getBlockState(pos));
	}
	
	@Override
	public boolean isRegionLoaded(BlockPos min, BlockPos max) {
		return true;
	}
	
	@Override
	public BlockState getBlockState(BlockPos pos) {
		BlockState state = blocksAdded.get(pos);
		return state != null ? state : Blocks.AIR.getDefaultState();
	}
}
