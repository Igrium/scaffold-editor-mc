package org.scaffoldeditor.editormc.engine.mixins;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import org.scaffoldeditor.editormc.engine.EditorServer;
import org.scaffoldeditor.editormc.engine.FakeSession;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.datafixers.util.Function4;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.WorldGenerationProgressTracker;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.QueueingWorldGenerationProgressListener;
import net.minecraft.server.WorldGenerationProgressListenerFactory;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.UserCache;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.level.storage.LevelStorage;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient {

	public ClientWorld world;
	
	private static final String START_SERVER_METHOD =
			"startIntegratedServer(Ljava/lang/String;Lnet/minecraft/util/registry/DynamicRegistryManager$Impl;Ljava/util/function/Function;Lcom/mojang/datafixers/util/Function4;ZLnet/minecraft/client/MinecraftClient$WorldLoadAction;)V";
	
	private static DynamicRegistryManager.Impl impl_holder;
	
	private AtomicReference<WorldGenerationProgressTracker> worldGenProgressTracker;
	private Queue<Runnable> renderTaskQueue;
	
	@Shadow
	private IntegratedServer server;

//	@Inject(at = @At("HEAD"), method = "tick", cancellable = true)
//	public void tick(CallbackInfo info) {
//		if (this.world instanceof FakeClientWorld) {
//			info.cancel();
//		}
//	}
	
	@Inject(method = START_SERVER_METHOD, at = @At(value = "HEAD"), locals = LocalCapture.CAPTURE_FAILHARD)
	@SuppressWarnings("rawtypes")
	private void captureIMPL(String arg0, DynamicRegistryManager.Impl registryTracker, Function arg2, Function4 arg3, boolean arg4, MinecraftClient.WorldLoadAction arg5, CallbackInfo ci) {
		MixinMinecraftClient.impl_holder = registryTracker;
	}
	
	@Redirect(method = START_SERVER_METHOD,
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/LevelStorage;createSession(Ljava/lang/String;)Lnet/minecraft/world/level/storage/LevelStorage$Session;"))
	private LevelStorage.Session replaceSession(LevelStorage levelStorage, String worldName) throws IOException {
		System.out.println("Running mixin!");

		if (worldName.length() == 0) {
			return new FakeSession(levelStorage, MixinMinecraftClient.impl_holder);
		} else {
			return levelStorage.createSession(worldName);
		}
	}
	
	static enum WorldLoadAction {
	      NONE,
	      CREATE,
	      BACKUP;
	   }
	
	@SuppressWarnings("rawtypes")
	@Inject(method = START_SERVER_METHOD, at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;startServer(Ljava/util/function/Function;)Lnet/minecraft/server/MinecraftServer;"),
			locals = LocalCapture.CAPTURE_FAILHARD)
	private void spawnServer(String worldName, DynamicRegistryManager.Impl registryTracker, Function function,
			Function4 function4, boolean safeMode, MinecraftClient.WorldLoadAction worldLoadAction, CallbackInfo ci,
			LevelStorage.Session session2, MinecraftClient.IntegratedResourceManager integratedResourceManager2,
			SaveProperties saveProperties, YggdrasilAuthenticationService yggdrasilAuthenticationService,
			MinecraftSessionService minecraftSessionService, GameProfileRepository gameProfileRepository,
			UserCache userCache) {
		
		// Re-write server spawning code so we can spawn the editor server if we need to.
		WorldGenerationProgressListenerFactory listener = (i) -> {
			 WorldGenerationProgressTracker worldGenerationProgressTracker = new WorldGenerationProgressTracker(i + 0);
             worldGenerationProgressTracker.start();
             this.worldGenProgressTracker.set(worldGenerationProgressTracker);
             Queue<Runnable> queue = this.renderTaskQueue;
             queue.getClass();
             return new QueueingWorldGenerationProgressListener(worldGenerationProgressTracker, queue::add);
		};
		this.server = MinecraftServer.startServer((serverThread) -> {
			if (worldName.length() == 0) {
				return new EditorServer(serverThread, MinecraftClient.getInstance(),
						registryTracker, session2, integratedResourceManager2.getResourcePackManager(), integratedResourceManager2.getServerResourceManager(),
						saveProperties, minecraftSessionService, gameProfileRepository, userCache, listener);
			} else {
				return new IntegratedServer(serverThread, MinecraftClient.getInstance(),
						registryTracker, session2, integratedResourceManager2.getResourcePackManager(), integratedResourceManager2.getServerResourceManager(),
						saveProperties, minecraftSessionService, gameProfileRepository, userCache, listener);
			}
		});
		
	}
	
	@Redirect(method = START_SERVER_METHOD, at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;startServer(Ljava/util/function/Function;)Lnet/minecraft/server/MinecraftServer;"))
	private MinecraftServer removeServer(Function<Thread, IntegratedServer> serverFactory) {
		return MinecraftClient.getInstance().getServer();
	}

//	@Redirect(method = "method_29603(Lnet/minecraft/util/registry/DynamicRegistryManager$Impl;Lnet/minecraft/world/level/storage/LevelStorage$Session;Lnet/minecraft/client/MinecraftClient$IntegratedResourceManager;Lnet/minecraft/world/SaveProperties;Lcom/mojang/authlib/minecraft/MinecraftSessionService;Lcom/mojang/authlib/GameProfileRepository;Lnet/minecraft/util/UserCache;Ljava/lang/Thread;)Lnet/minecraft/server/integrated/IntegratedServer;",
//			at = @At(value = "NEW", target = "net/minecraft/server/integrated/IntegratedServer"))
//	private IntegratedServer replaceIntegratedServer(Thread serverThread, MinecraftClient client,
//			DynamicRegistryManager.Impl registryManager, LevelStorage.Session session,
//			ResourcePackManager resourcePackManager, ServerResourceManager serverResourceManager,
//			SaveProperties saveProperties, MinecraftSessionService minecraftSessionService,
//			GameProfileRepository gameProfileRepository, UserCache userCache,
//			WorldGenerationProgressListenerFactory worldGenerationProgressListenerFactory) {
//		System.out.println("World status: "+ScaffoldEditor.getInstance().isInEditor());
//		if (ScaffoldEditor.getInstance().isInEditor()) {
//			return new EditorServer(serverThread, client, registryManager, session, resourcePackManager,
//					serverResourceManager, saveProperties, minecraftSessionService, gameProfileRepository, userCache,
//					worldGenerationProgressListenerFactory);
//		} else {
//			return new IntegratedServer(serverThread, client, registryManager, session, resourcePackManager,
//					serverResourceManager, saveProperties, minecraftSessionService, gameProfileRepository, userCache,
//					worldGenerationProgressListenerFactory);
//		}
//	}
}
