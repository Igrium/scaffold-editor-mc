package org.scaffoldeditor.editormc.engine;

import net.minecraft.server.integrated.IntegratedPlayerManager;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.registry.DynamicRegistryManager.Impl;
import net.minecraft.world.WorldSaveHandler;

public class FakePlayerManager extends IntegratedPlayerManager {

	public FakePlayerManager(IntegratedServer server, Impl registryManager, WorldSaveHandler saveHandler) {
		super(server, registryManager, saveHandler);
	}

	@Override
	protected void savePlayerData(ServerPlayerEntity player) {
		return;
	}

}
