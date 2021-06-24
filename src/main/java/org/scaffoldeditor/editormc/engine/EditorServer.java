package org.scaffoldeditor.editormc.engine;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.scaffoldeditor.editormc.engine.mixins.MinecraftServerAccessor;
import org.scaffoldeditor.editormc.engine.world.EditorServerWorld;

import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;

import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ServerResourceManager;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.WorldGenerationProgressListenerFactory;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.UserCache;
import net.minecraft.util.registry.DynamicRegistryManager.Impl;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.gen.Spawner;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.storage.LevelStorage.Session;

public class EditorServer extends IntegratedServer {
	
	private static final Logger LOGGER = LogManager.getLogger();
	private EditorServerWorld world;
	
	@Override
	public boolean save(boolean suppressLogs, boolean bl, boolean bl2) {
		return true;
	}
	
	@Override
	protected void createWorlds(WorldGenerationProgressListener worldGenerationProgressListener) {
		ServerWorldProperties serverWorldProperties = this.saveProperties.getMainWorldProperties();
//		GeneratorOptions generatorOptions = this.saveProperties.getGeneratorOptions();
		DimensionType dimensionType = this.registryManager.get(Registry.DIMENSION_TYPE_KEY).getOrThrow(DimensionType.OVERWORLD_REGISTRY_KEY);
		
		GeneratorOptions generatorOptions = this.saveProperties.getGeneratorOptions();
		SimpleRegistry<DimensionOptions> dimensionOptionRegistry = generatorOptions.getDimensions();
		DimensionOptions dimensionOptions = (DimensionOptions) dimensionOptionRegistry.get(DimensionOptions.OVERWORLD);

		ChunkGenerator chunkGen = dimensionOptions.getChunkGenerator();
		
		MinecraftServerAccessor accessor = (MinecraftServerAccessor) this;
		world = new EditorServerWorld(this, accessor.getWorkerExecutor(), this.session, serverWorldProperties, World.OVERWORLD,
				dimensionType, worldGenerationProgressListener, chunkGen, false, BiomeAccess.hashSeed(1), new ArrayList<Spawner>(), true);
		accessor.getWorldMap().put(World.OVERWORLD, world);
		
		if (!serverWorldProperties.isInitialized()) {
			
		}
		
		this.getPlayerManager().setMainWorld(world);
		
	}

	public EditorServerWorld getEditorWorld() {
		return this.world;
	}

	public EditorServer(Thread serverThread, MinecraftClient client, Impl registryManager, Session session,
			ResourcePackManager resourcePackManager, ServerResourceManager serverResourceManager,
			SaveProperties saveProperties, MinecraftSessionService minecraftSessionService,
			GameProfileRepository gameProfileRepository, UserCache userCache,
			WorldGenerationProgressListenerFactory worldGenerationProgressListenerFactory) {
		super(serverThread, client, registryManager, session, resourcePackManager, serverResourceManager, saveProperties,
				minecraftSessionService, gameProfileRepository, userCache, worldGenerationProgressListenerFactory);
		ScaffoldEditorMod.getInstance().isInEditor = true;
		this.setPlayerManager(new FakePlayerManager(this, this.registryManager, this.saveHandler));
		LOGGER.info("Loaded editor server.");
	}
	
	@Override
	public void shutdown() {
		ScaffoldEditorMod mod = ScaffoldEditorMod.getInstance();
		if (mod.isInEditor) {
			mod.editor.forceExit();
		}
		
		MinecraftServerAccessor accessor = (MinecraftServerAccessor) this;
		
		LOGGER.info("Stopping editor server");
		if (this.getNetworkIo() != null) this.getNetworkIo().stop();
		if (this.getPlayerManager() != null) this.getPlayerManager().disconnectAllPlayers();
		if (this.getSnooper().isActive()) this.getSnooper().cancel();
		accessor.getServerResourceManager().close();
		
		try {
			this.session.close();
			FakeSession fakeSession = (FakeSession) this.session;
			if (fakeSession != null) {
				fakeSession.deleteWorldFolder();
			}
		} catch (IOException e) {
			LOGGER.error("Failed to unlock level:", this.session.getDirectoryName(), e);
		}
		
		ScaffoldEditorMod.getInstance().isInEditor = false;
	}
	
	public void teleportPlayers(double x, double y, double z) {
		for (ServerPlayerEntity player : getPlayerManager().getPlayerList()) {
			player.teleport(x, y, z);
		}
	}
}
