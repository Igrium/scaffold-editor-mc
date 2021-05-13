package org.scaffoldeditor.editormc.engine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resource.DataPackSettings;
import net.minecraft.util.ProgressListener;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.WorldSaveHandler;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.level.storage.LevelSummary;
import net.minecraft.world.level.storage.LevelStorage.Session;

public class FakeSession extends Session {
	
	
	public FakeSession(LevelStorage levelStorage) throws IOException {
		levelStorage.super(".scaffold_placeholder");
	}
	
	@Override
	public WorldSaveHandler createSaveHandler() {
		return new FakeWorldSaveHandler(this, null);
	}
	
	@Override
	public boolean needsConversion() {
		return false;
	}
	
	@Override
	public boolean convert(ProgressListener progressListener) {
		return true;
	}
	
	@Override
	public LevelSummary getLevelSummary() {
		return null;
	}
	
	@Override
	public SaveProperties readLevelProperties(DynamicOps<Tag> dynamicOps, DataPackSettings dataPackSettings) {
		// Create a whole bunch of fake level data because we're not actually reading a level file.
		LevelInfo info = new LevelInfo("", GameMode.SPECTATOR,
				false, Difficulty.NORMAL, true, new GameRules(), dataPackSettings);

		
		Registry<DimensionType> dimensions = DynamicRegistryManager.create().getDimensionTypes();
		Registry<ChunkGeneratorSettings> chunkGenSettings = BuiltinRegistries.CHUNK_GENERATOR_SETTINGS;
		
		
		return new LevelProperties(info, GeneratorOptions.getDefaultOptions(dimensions, BuiltinRegistries.BIOME, chunkGenSettings), Lifecycle.stable());
	}
	
	@Override
	public DataPackSettings getDataPackSettings() {
		return new DataPackSettings(Arrays.asList(new String[] {"vanilla"}), new ArrayList<String>());
	}
	
	@Override
	public void backupLevelDataFile(DynamicRegistryManager dynamicRegistryManager, SaveProperties saveProperties,
			CompoundTag compoundTag) {}
	
	@Override
	public void save(String name) throws IOException {
		return;
	}
	
	@Override
	public long createBackup() throws IOException {
		return 0;
	}
	
	private class FakeWorldSaveHandler extends WorldSaveHandler {

		public FakeWorldSaveHandler(Session session, DataFixer dataFixer) {
			super(session, dataFixer);
		}
		
		@Override
		public void savePlayerData(PlayerEntity playerEntity) {}
		
		@Override
		public CompoundTag loadPlayerData(PlayerEntity playerEntity) {
			return null;
		}
		
		@Override
		public String[] getSavedPlayerIds() {
			return new String[] {};
		}
		
	}
}
