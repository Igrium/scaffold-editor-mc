package org.scaffoldeditor.editormc.engine;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.scaffoldeditor.editormc.engine.mixins.MinecraftServerAccessor;

import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;

import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ServerResourceManager;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.WorldGenerationProgressListenerFactory;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.UserCache;
import net.minecraft.util.registry.DynamicRegistryManager.Impl;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.gen.Spawner;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.storage.LevelStorage.Session;

public class EditorServer extends IntegratedServer {
	
	private static final Logger LOGGER = LogManager.getLogger();
	
	@Override
	public boolean save(boolean suppressLogs, boolean bl, boolean bl2) {
		return true;
	}
	
	@Override
	protected void createWorlds(WorldGenerationProgressListener worldGenerationProgressListener) {
		ServerWorldProperties serverWorldProperties = this.saveProperties.getMainWorldProperties();
//		GeneratorOptions generatorOptions = this.saveProperties.getGeneratorOptions();
		DimensionType dimensionType = this.registryManager.getDimensionTypes().getOrThrow(DimensionType.OVERWORLD_REGISTRY_KEY);
		NoiseChunkGenerator chunkGen = GeneratorOptions.createOverworldGenerator(this.registryManager.get(Registry.BIOME_KEY),
				this.registryManager.get(Registry.NOISE_SETTINGS_WORLDGEN), getTaskCount());
		
		MinecraftServerAccessor accessor = (MinecraftServerAccessor) this;
		ServerWorld world = new EditorServerWorld(this, accessor.getWorkerExecutor(), this.session, serverWorldProperties, World.OVERWORLD,
				dimensionType, worldGenerationProgressListener, chunkGen, false, BiomeAccess.hashSeed(1), new ArrayList<Spawner>(), true);
		accessor.getWorldMap().put(World.OVERWORLD, world);
		
		if (!serverWorldProperties.isInitialized()) {
			
		}
		
		this.getPlayerManager().setMainWorld(world);
		
	}

	public EditorServer(Thread serverThread, MinecraftClient client, Impl registryManager, Session session,
			ResourcePackManager resourcePackManager, ServerResourceManager serverResourceManager,
			SaveProperties saveProperties, MinecraftSessionService minecraftSessionService,
			GameProfileRepository gameProfileRepository, UserCache userCache,
			WorldGenerationProgressListenerFactory worldGenerationProgressListenerFactory) {
		super(serverThread, client, registryManager, session, resourcePackManager, serverResourceManager, saveProperties,
				minecraftSessionService, gameProfileRepository, userCache, worldGenerationProgressListenerFactory);
		ScaffoldEditor.getInstance().isInEditor = true;
		this.setPlayerManager(new FakePlayerManager(this, this.registryManager, this.saveHandler));
		System.out.println("loaded editor server");
	}
	
	@Override
	public void shutdown() {
		MinecraftServerAccessor accessor = (MinecraftServerAccessor) this;
		
		LOGGER.info("Stopping editor server");
		if (this.getNetworkIo() != null) this.getNetworkIo().stop();
		if (this.getPlayerManager() != null) this.getPlayerManager().disconnectAllPlayers();
		if (this.getSnooper().isActive()) this.getSnooper().cancel();
		accessor.getServerResourceManager().close();
		
		try {
			this.session.close();
		} catch (IOException e) {
			LOGGER.error("Failed to unlock level {}", this.session.getDirectoryName(), e);
		}
		
		ScaffoldEditor.getInstance().isInEditor = false;
	}

}
