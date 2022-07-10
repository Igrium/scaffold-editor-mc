package org.scaffoldeditor.editormc.engine.world;

import java.util.List;
import java.util.function.Predicate;

import org.scaffoldeditor.editormc.engine.mixins.WorldAccessor;

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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.entity.EntityLookup;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.GameEvent.Emitter;
import net.minecraft.world.tick.QueryableTickScheduler;

/**
 * Derived from the Create mod.
 */
public class WrappedWorld extends World {
    
    protected WrappedWorld(World base) {
        super((MutableWorldProperties) base.getLevelProperties(), base.getRegistryKey(), base.getDimensionEntry(), base.getProfilerSupplier(), base.isClient, base.isDebugWorld(), 0, 64);
        this.base = base;
    }

    protected final World base;
    
    // public WrappedWorld(World base) {
    // 	super(properties, registryKey, dimensionEntry, profiler, isClient, debugWorld, tickOrder, ambientDarkness);
    // 	this.world = base;
    // }

    
    public World getWorld() {
        return base;
    }
    
    @Override
    public LightingProvider getLightingProvider() {
        return base.getLightingProvider();
    }
    
    @Override
    public BlockState getBlockState(BlockPos pos) {
        return base.getBlockState(pos);
    }
    
    @Override
    public boolean testBlockState(BlockPos pos, Predicate<BlockState> state) {
        return base.testBlockState(pos, state);
    }
    
    @Override
    public boolean setBlockState(BlockPos pos, BlockState state, int flags, int maxUpdateDepth) {
        return base.setBlockState(pos, state, flags, maxUpdateDepth);
    }
    
    @Override
    public BlockEntity getBlockEntity(BlockPos pos) {
        return base.getBlockEntity(pos);
    }
    
    @Override
    public int getLightLevel(BlockPos pos) {
        return 15;
    }

    @Override
    public ChunkManager getChunkManager() {
        return base.getChunkManager();
    }

    @Override
    public void syncWorldEvent(PlayerEntity player, int eventId, BlockPos pos, int data) {
        base.syncWorldEvent(player, eventId, pos, data);
    }

    @Override
    public void emitGameEvent(Entity entity, GameEvent event, BlockPos pos) {
        base.emitGameEvent(entity, event, pos);
    }

    @Override
    public DynamicRegistryManager getRegistryManager() {
        return base.getRegistryManager();
    }

    @Override
    public List<? extends PlayerEntity> getPlayers() {
        return base.getPlayers();
    }
    
    @Override
    public boolean spawnEntity(Entity entity) {
        if (entity == null) return false;
        entity.world = base;
        return base.spawnEntity(entity);
    }
    

    @Override
    public float getBrightness(Direction direction, boolean shaded) {
        return base.getBrightness(direction, shaded);
    }

    @Override
    public void updateListeners(BlockPos pos, BlockState oldState, BlockState newState, int flags) {
        base.updateListeners(pos, oldState, newState, flags);
    }
    

    @Override
    public void playSound(PlayerEntity player, double x, double y, double z, SoundEvent sound, SoundCategory category,
            float volume, float pitch) {
        base.playSound(player, x, y, z, sound, category, volume, pitch);
    }

    @Override
    public void playSoundFromEntity(PlayerEntity player, Entity entity, SoundEvent sound, SoundCategory category,
            float volume, float pitch) {
        base.playSoundFromEntity(player, entity, sound, category, volume, pitch);
    }

    @Override
    public String asString() {
        return base.asString();
    }

    @Override
    public Entity getEntityById(int id) {
        return base.getEntityById(id);
    }

    @Override
    public MapState getMapState(String id) {
        return base.getMapState(id);
    }

    @Override
    public void putMapState(String id, MapState state) {
        base.putMapState(id, state);
    }

    @Override
    public int getNextMapId() {
        return base.getNextMapId();
    }

    @Override
    public void setBlockBreakingInfo(int entityId, BlockPos pos, int progress) {
        base.setBlockBreakingInfo(entityId, pos, progress);
    }

    @Override
    public Scoreboard getScoreboard() {
        return base.getScoreboard();
    }

    @Override
    public RecipeManager getRecipeManager() {
        return base.getRecipeManager();
    }
    
    @Override
    public void markDirty(BlockPos pos) {
        base.markDirty(pos);
    }
    
    @Override
    public boolean isChunkLoaded(BlockPos pos) {
        return true;
    }
    
    @Override
    public void updateComparators(BlockPos pos, Block block) {
        base.updateComparators(pos, block);
    }

    @Override
    protected EntityLookup<Entity> getEntityLookup() {
        return ((WorldAccessor) base).getEntityLookup();
    }

    @Override
    public QueryableTickScheduler<Block> getBlockTickScheduler() {
        return base.getBlockTickScheduler();
    }

    @Override
    public QueryableTickScheduler<Fluid> getFluidTickScheduler() {
        return base.getFluidTickScheduler();
    }

    @Override
    public void emitGameEvent(GameEvent var1, Vec3d var2, Emitter var3) {
        base.emitGameEvent(var1, var2, var3);
    }

    @Override
    public RegistryEntry<Biome> getGeneratorStoredBiome(int var1, int var2, int var3) {
        return base.getGeneratorStoredBiome(var1, var2, var3);
    }

    @Override
    public void playSound(PlayerEntity var1, double var2, double var4, double var6, SoundEvent var8, SoundCategory var9,
            float var10, float var11, long var12) {
        base.playSound(var1, var2, var4, var6, var8, var9, var10, var11, var12);
    }

    @Override
    public void playSoundFromEntity(PlayerEntity var1, Entity var2, SoundEvent var3, SoundCategory var4, float var5,
            float var6, long var7) {
        playSoundFromEntity(var1, var2, var3, var4, var5, var6, var7);
    }
}
