package org.scaffoldeditor.editormc.engine;

import org.apache.logging.log4j.LogManager;

import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.FileResourcePackProvider;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.VanillaDataPackProvider;
import net.minecraft.server.SaveLoader;
import net.minecraft.server.integrated.IntegratedServerLoader;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.level.storage.LevelStorage;

public class ScaffoldServerLoader {
    final MinecraftClient client;
    private LevelStorage storage;
    private IntegratedServerLoader base;

    public ScaffoldServerLoader(MinecraftClient client, LevelStorage storage) {
        this.client = client;
        this.storage = storage;
        base = client.createIntegratedServerLoader();
    }

    public ScaffoldServerLoader(MinecraftClient client) {
        this(client, client.getLevelStorage());
    }

    public LevelStorage getStorage() {
        return storage;
    }

    public void start() {
        try {
            DynamicRegistryManager.Immutable dynRegMan = DynamicRegistryManager.createAndLoad().toImmutable();
            LevelStorage.Session session = new FakeSession(client.getLevelStorage(), dynRegMan);

            ResourcePackManager datapackManager = createDataPackManager(session);
            

            ((CustomClientMethods) client).startScaffoldServer(session, datapackManager, createSaveLoader(session));
        } catch (Exception e) {
            LogManager.getLogger().error("Unable to load Scaffold server.", e);
            return;
        }
    }

    public SaveLoader createSaveLoader(LevelStorage.Session session) {
        try {
            return base.createSaveLoader(session, true);
        } catch (Exception e) {
            throw new RuntimeException("Error creating save loader", e);
        }
    }

    private static ResourcePackManager createDataPackManager(LevelStorage.Session session) {
        return new ResourcePackManager(ResourceType.SERVER_DATA, new VanillaDataPackProvider(),
                new FileResourcePackProvider(session.getDirectory(WorldSavePath.DATAPACKS).toFile(),
                        ResourcePackSource.PACK_SOURCE_WORLD));
    }
}
