package org.scaffoldeditor.editormc.engine.mixins;

import java.util.Map;
import java.util.concurrent.Executor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

@Mixin(MinecraftServer.class)
public interface MinecraftServerAccessor {
	@Accessor("workerExecutor")
	public Executor getWorkerExecutor();
	
	@Accessor("worlds")
	public Map<RegistryKey<World>, ServerWorld> getWorldMap();
	
	// @Accessor("serverResourceManager")
	// public DataPackContents getServerResourceManager();
	
}