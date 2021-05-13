package org.scaffoldeditor.editormc.engine;

import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;

import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ServerResourceManager;
import net.minecraft.server.WorldGenerationProgressListenerFactory;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.UserCache;
import net.minecraft.util.registry.DynamicRegistryManager.Impl;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.level.storage.LevelStorage.Session;

public class EditorServer extends IntegratedServer {
	
	@Override
	public boolean save(boolean suppressLogs, boolean bl, boolean bl2) {
		return true;
	}

	public EditorServer(Thread serverThread, MinecraftClient client, Impl registryManager, Session session,
			ResourcePackManager resourcePackManager, ServerResourceManager serverResourceManager,
			SaveProperties saveProperties, MinecraftSessionService minecraftSessionService,
			GameProfileRepository gameProfileRepository, UserCache userCache,
			WorldGenerationProgressListenerFactory worldGenerationProgressListenerFactory) {
		super(serverThread, client, registryManager, session, resourcePackManager, serverResourceManager, saveProperties,
				minecraftSessionService, gameProfileRepository, userCache, worldGenerationProgressListenerFactory);
		System.out.println("loaded editor server");
	}
	
	@Override
	public void shutdown() {
		System.out.println("Shutting down editor server.");
		super.shutdown();
	}

}
