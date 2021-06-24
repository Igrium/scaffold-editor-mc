package org.scaffoldeditor.editormc.engine.world;

import java.util.List;
import java.util.function.Predicate;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.map.MapState;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.tag.TagManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.TickScheduler;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.entity.EntityLookup;
import net.minecraft.world.event.GameEvent;

/**
 * Derived from the Create mod.
 */
public class WrappedWorld extends World {
	protected final World world;
	
	public WrappedWorld(World base) {
		super((MutableWorldProperties) base.getLevelProperties(), base.getRegistryKey(), base.getDimension(), base::getProfiler, base.isClient, base.isDebugWorld(), 0);
		this.world = base;
	}
	
	public World getWorld() {
		return world;
	}
	
	@Override
	public LightingProvider getLightingProvider() {
		return world.getLightingProvider();
	}
	
	@Override
	public BlockState getBlockState(BlockPos pos) {
		return world.getBlockState(pos);
	}
	
	@Override
	public boolean testBlockState(BlockPos pos, Predicate<BlockState> state) {
		return world.testBlockState(pos, state);
	}
	
	@Override
	public boolean setBlockState(BlockPos pos, BlockState state, int flags, int maxUpdateDepth) {
		return world.setBlockState(pos, state, flags, maxUpdateDepth);
	}
	
	@Override
	public BlockEntity getBlockEntity(BlockPos pos) {
		return world.getBlockEntity(pos);
	}
	
	@Override
	public int getLightLevel(BlockPos pos) {
		return 15;
	}
	

	@Override
	public TickScheduler<Block> getBlockTickScheduler() {
		return world.getBlockTickScheduler();
	}

	@Override
	public TickScheduler<Fluid> getFluidTickScheduler() {
		return world.getFluidTickScheduler();
	}

	@Override
	public ChunkManager getChunkManager() {
		return world.getChunkManager();
	}

	@Override
	public void syncWorldEvent(PlayerEntity player, int eventId, BlockPos pos, int data) {
		world.syncWorldEvent(player, eventId, pos, data);
	}

	@Override
	public void emitGameEvent(Entity entity, GameEvent event, BlockPos pos) {
		world.emitGameEvent(entity, event, pos);
	}

	@Override
	public DynamicRegistryManager getRegistryManager() {
		return world.getRegistryManager();
	}

	@Override
	public List<? extends PlayerEntity> getPlayers() {
		return world.getPlayers();
	}
	
	@Override
	public boolean spawnEntity(Entity entity) {
		if (entity == null) return false;
		entity.world = world;
		return world.spawnEntity(entity);
	}
	
	@Override
	public Biome getGeneratorStoredBiome(int biomeX, int biomeY, int biomeZ) {
		return world.getGeneratorStoredBiome(biomeX, biomeY, biomeZ);
	}

	@Override
	public float getBrightness(Direction direction, boolean shaded) {
		return world.getBrightness(direction, shaded);
	}

	@Override
	public void updateListeners(BlockPos pos, BlockState oldState, BlockState newState, int flags) {
		world.updateListeners(pos, oldState, newState, flags);
	}
	

	@Override
	public void playSound(PlayerEntity player, double x, double y, double z, SoundEvent sound, SoundCategory category,
			float volume, float pitch) {
		world.playSound(player, x, y, z, sound, category, volume, pitch);
	}

	@Override
	public void playSoundFromEntity(PlayerEntity player, Entity entity, SoundEvent sound, SoundCategory category,
			float volume, float pitch) {
		world.playSoundFromEntity(player, entity, sound, category, volume, pitch);
	}

	@Override
	public String asString() {
		return world.asString();
	}

	@Override
	public Entity getEntityById(int id) {
		return world.getEntityById(id);
	}

	@Override
	public MapState getMapState(String id) {
		return world.getMapState(id);
	}

	@Override
	public void putMapState(String id, MapState state) {
		world.putMapState(id, state);
	}

	@Override
	public int getNextMapId() {
		return world.getNextMapId();
	}

	@Override
	public void setBlockBreakingInfo(int entityId, BlockPos pos, int progress) {
		world.setBlockBreakingInfo(entityId, pos, progress);
	}

	@Override
	public Scoreboard getScoreboard() {
		return world.getScoreboard();
	}

	@Override
	public RecipeManager getRecipeManager() {
		return world.getRecipeManager();
	}

	@Override
	public TagManager getTagManager() {
		return world.getTagManager();
	}
	
	@Override
	public Biome getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
		return world.getBiomeForNoiseGen(biomeX, biomeY, biomeZ);
	}
	
	@Override
	public void markDirty(BlockPos pos) {
	}
	
	@Override
	public boolean isChunkLoaded(BlockPos pos) {
		return true;
	}
	
	@Override
	public void updateComparators(BlockPos pos, Block block) {
	}

	@Override
	protected EntityLookup<Entity> getEntityLookup() {
		return null;
	}
}
