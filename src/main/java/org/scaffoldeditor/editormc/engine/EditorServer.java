package org.scaffoldeditor.editormc.engine;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.scaffoldeditor.editormc.engine.mixins.MinecraftServerAccessor;
import org.scaffoldeditor.editormc.engine.world.EditorServerWorld;

import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.server.SaveLoader;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.WorldGenerationProgressListenerFactory;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ApiServices;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.gen.GeneratorOptions;
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

        GeneratorOptions generatorOptions = this.saveProperties.getGeneratorOptions();
        Registry<DimensionOptions> dimensionOptionRegistry = generatorOptions.getDimensions();
        DimensionOptions dimensionOptions = dimensionOptionRegistry.get(DimensionOptions.OVERWORLD);
        
        MinecraftServerAccessor accessor = (MinecraftServerAccessor) this;

        world = new EditorServerWorld(this, accessor.getWorkerExecutor(), session, serverWorldProperties,
                World.OVERWORLD, dimensionOptions, worldGenerationProgressListener, false, BiomeAccess.hashSeed(1),
                new ArrayList<>(), false);
        accessor.getWorldMap().put(World.OVERWORLD, world);
        
        if (!serverWorldProperties.isInitialized()) {
            
        }
        
        this.getPlayerManager().setMainWorld(world);
        
    }

    public EditorServerWorld getEditorWorld() {
        return this.world;
    }

    public EditorServer(Thread serverThread, MinecraftClient client, Session session,
            ResourcePackManager dataPackManager, SaveLoader saveLoader, ApiServices apiServices,
            WorldGenerationProgressListenerFactory worldGenerationProgressListenerFactory) {
        super(serverThread, client, session, dataPackManager, saveLoader, apiServices,
                worldGenerationProgressListenerFactory);
        ScaffoldEditorMod.getInstance().setInEditor(true);
        this.setPlayerManager(new FakePlayerManager(this, getRegistryManager(), saveHandler));
        LOGGER.info("Loaded editor server.");
    }
    
    @Override
    public void shutdown() {
        super.shutdown();
        ScaffoldEditorMod mod = ScaffoldEditorMod.getInstance();
        if (mod.isInEditor()) {
            mod.editor.forceExit();
        }
        
        // MinecraftServerAccessor accessor = (MinecraftServerAccessor) this;
        
        // LOGGER.info("Stopping editor server");
        // if (this.getNetworkIo() != null) this.getNetworkIo().stop();
        // if (this.getPlayerManager() != null) this.getPlayerManager().disconnectAllPlayers();
        // if (this.getSnooper().isActive()) this.getSnooper().cancel();
        // accessor.getServerResourceManager().close();
        
        // try {
        //     this.session.close();

        // } catch (IOException e) {
        //     LOGGER.error("Failed to unlock level:", this.session.getDirectoryName(), e);
        // }
        
        ScaffoldEditorMod.getInstance().setInEditor(false);
    }
    
    public void teleportPlayers(double x, double y, double z) {
        for (ServerPlayerEntity player : getPlayerManager().getPlayerList()) {
            player.teleport(x, y, z);
        }
    }
}
