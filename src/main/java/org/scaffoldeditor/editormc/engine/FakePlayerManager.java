package org.scaffoldeditor.editormc.engine;

import net.minecraft.server.integrated.IntegratedPlayerManager;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.WorldSaveHandler;

public class FakePlayerManager extends IntegratedPlayerManager {

	public FakePlayerManager(IntegratedServer server, DynamicRegistryManager.Immutable registryManager, WorldSaveHandler saveHandler) {
		super(server, registryManager, saveHandler);
	}

	@Override
	protected void savePlayerData(ServerPlayerEntity player) {
		return;
	}

}
