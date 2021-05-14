package org.scaffoldeditor.editormc.engine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resource.DataPackSettings;
import net.minecraft.resource.FileResourcePackProvider;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProvider;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.resource.ServerResourceManager;
import net.minecraft.resource.VanillaDataPackProvider;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.util.ProgressListener;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.WorldSaveHandler;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.level.storage.LevelSummary;
import net.minecraft.world.level.storage.LevelStorage.Session;

public class FakeSession extends Session {
	
	private static String GEN_SETTINGS_JSON = "{\"bonus_chest\":false,\"dimensions\":{\"minecraft:overworld\":{\"type\":\"minecraft:overworld\",\"generator\":{\"settings\":{\"lakes\":false,\"features\":true,\"biome\":\"minecraft:plains\",\"structures\":{\"structures\":{}},\"layers\":[{\"height\":1,\"block\":\"minecraft:air\"}]},\"type\":\"minecraft:flat\"}},\"minecraft:the_nether\":{\"type\":\"minecraft:the_nether\",\"generator\":{\"biome_source\":{\"preset\":\"minecraft:nether\",\"seed\":-959868788587540434,\"type\":\"minecraft:multi_noise\"},\"seed\":-959868788587540434,\"settings\":\"minecraft:nether\",\"type\":\"minecraft:noise\"}},\"minecraft:the_end\":{\"type\":\"minecraft:the_end\",\"generator\":{\"biome_source\":{\"seed\":-959868788587540434,\"type\":\"minecraft:the_end\"},\"seed\":-959868788587540434,\"settings\":\"minecraft:end\",\"type\":\"minecraft:noise\"}}},\"seed\":-959868788587540434,\"generate_features\":false}";
	
	
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
	
	public SaveProperties generateLevelProperties(DynamicOps<Tag> dynamicOps, DataPackSettings dataPackSettings) throws IOException, InterruptedException, ExecutionException {
		// Create a whole bunch of fake level data because we're not actually reading a level file.
				LevelInfo info = new LevelInfo("", GameMode.SPECTATOR,
						false, Difficulty.NORMAL, true, new GameRules(), dataPackSettings);

				DynamicRegistryManager.Impl dynRegMan = DynamicRegistryManager.create();
//				Registry<DimensionType> dimensions = dynRegMan.getDimensionTypes();
//				Registry<ChunkGeneratorSettings> chunkGenSettings = BuiltinRegistries.CHUNK_GENERATOR_SETTINGS;
////				GeneratorOptions genOptions = GeneratorOptions.getDefaultOptions(dimensions, null, chunkGenSettings);
//				
//				FlatChunkGeneratorLayer[] layers = new FlatChunkGeneratorLayer[] { new FlatChunkGeneratorLayer(5, Registry.BLOCK.get(new Identifier("minecraft:stone")))};
//				StructuresConfig structConfig = new StructuresConfig(false);
//				Supplier<Biome> biome = () -> {return BuiltinRegistries.BIOME.get(new Identifier("minecraft:plains"));};
//				FlatChunkGenerator generator = new FlatChunkGenerator(
//						new FlatChunkGeneratorConfig(BuiltinRegistries.BIOME, structConfig, Arrays.asList(layers), false, false, Optional.ofNullable(biome)));
//				
//				SimpleRegistry<DimensionOptions> dimOptionsReg = DimensionType.createDefaultDimensionOptions(dimensions, BuiltinRegistries.BIOME, chunkGenSettings, 1083719637794L);
//				DimensionOptions overworldOptions = new DimensionOptions(() -> { return dimensions.get(DimensionType.OVERWORLD_ID); }, generator);
//				dimOptionsReg.add(DimensionOptions.OVERWORLD, overworldOptions, Lifecycle.stable());
				
//				GeneratorOptions genOptions = new GeneratorOptions(1083719637794L, false, false, dimOptionsReg);
				Properties genProperties = new Properties();
//				genProperties.put("level-seed", "1083719637794");
//				genProperties.put("generate-structures", "false");
				genProperties.put("level-type", "flat");
				genProperties.put("generator-settings", GEN_SETTINGS_JSON);
				
				System.out.println(genProperties);
				
				Path dataPackTempDir = Files.createTempDirectory("mcworld-");
				
				ResourcePackManager resourcePackManager = new ResourcePackManager(new ResourcePackProvider[]{new VanillaDataPackProvider(), new FileResourcePackProvider(dataPackTempDir.toFile(), ResourcePackSource.PACK_SOURCE_WORLD)});
				
				MinecraftServer.loadDataPacks(resourcePackManager, dataPackSettings, false);
				CompletableFuture<ServerResourceManager> completableFuture = ServerResourceManager.reload(resourcePackManager.createResourcePacks(), CommandManager.RegistrationEnvironment.INTEGRATED, 2, Util.getMainWorkerExecutor(), MinecraftClient.getInstance());
		        MinecraftClient.getInstance().runTasks(completableFuture::isDone);
		        ServerResourceManager serverResourceManager = (ServerResourceManager)completableFuture.get();
				
				RegistryOps<JsonElement> registryOps = RegistryOps.of(JsonOps.INSTANCE, serverResourceManager.getResourceManager(), dynRegMan);
				
				System.out.println(GEN_SETTINGS_JSON);
				JsonParser jsonParser = new JsonParser();
				JsonElement jsonElement = jsonParser.parse(GEN_SETTINGS_JSON);
				DataResult<GeneratorOptions> dataResult = GeneratorOptions.CODEC.parse(registryOps, jsonElement);
				
				resourcePackManager.close();
				
				
				GeneratorOptions genOps;
				Optional<GeneratorOptions> result = dataResult.result();
				if (result.isPresent()) {
					genOps = result.get();
				} else {
					genOps = GeneratorOptions.getDefaultOptions(dynRegMan.getDimensionTypes(), BuiltinRegistries.BIOME, BuiltinRegistries.CHUNK_GENERATOR_SETTINGS);
				}
				
				return new LevelProperties(info, genOps, Lifecycle.stable());
	}
	
	@Override
	public SaveProperties readLevelProperties(DynamicOps<Tag> dynamicOps, DataPackSettings dataPackSettings) {
		try {
			return generateLevelProperties(dynamicOps, dataPackSettings);
		} catch (IOException | InterruptedException | ExecutionException e) {
			throw new RuntimeException("Igrium fucked something up in the fake world generator code", e);
		}
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
