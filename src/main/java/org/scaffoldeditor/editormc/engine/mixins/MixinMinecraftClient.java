package org.scaffoldeditor.editormc.engine.mixins;

import java.net.SocketAddress;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import org.scaffoldeditor.editormc.engine.CustomClientMethods;
import org.scaffoldeditor.editormc.engine.EditorServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;

import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.WorldGenerationProgressTracker;
import net.minecraft.client.gui.screen.LevelLoadingScreen;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkState;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.QueueingWorldGenerationProgressListener;
import net.minecraft.server.SaveLoader;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.ApiServices;
import net.minecraft.util.UserCache;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.world.level.storage.LevelStorage;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient implements CustomClientMethods {
    
    @Shadow
    public ClientWorld world;
    
    // private static final String START_SERVER_METHOD =
    //         "startIntegratedServer(Ljava/lang/String;Lnet/minecraft/util/registry/DynamicRegistryManager$Impl;Ljava/util/function/Function;Lcom/mojang/datafixers/util/Function4;ZLnet/minecraft/client/MinecraftClient$WorldLoadAction;)V";
    
    // private static DynamicRegistryManager.Immutable registryTrackerHolder;
    
    @Shadow
    private AtomicReference<WorldGenerationProgressTracker> worldGenProgressTracker;

    @Shadow
    private Queue<Runnable> renderTaskQueue;
    
    @Shadow
    private IntegratedServer server;

    @Shadow
    private YggdrasilAuthenticationService authenticationService;

    @Shadow
    boolean integratedServerRunning;
    
    @Shadow
    abstract void render(boolean tick);

    @Shadow
    private Supplier<CrashReport> crashReportSupplier;

    @Shadow
    private ClientConnection integratedServerConnection;


    public void startScaffoldServer(LevelStorage.Session session, ResourcePackManager datapackManager, SaveLoader saveLoader) {
        MinecraftClient client = (MinecraftClient) (Object) this;


        client.disconnect();
        worldGenProgressTracker.set(null);

        try {
            ApiServices apiServices = ApiServices.create(authenticationService, client.runDirectory);
            SkullBlockEntity.setServices(apiServices, client);
            UserCache.setUseRemote(false);
            server = MinecraftServer.startServer(thread -> new EditorServer(thread, client, session, datapackManager, saveLoader, apiServices, spawnChunkRadius -> {
                WorldGenerationProgressTracker tracker = new WorldGenerationProgressTracker(spawnChunkRadius);
                worldGenProgressTracker.set(tracker);
                return QueueingWorldGenerationProgressListener.create(tracker, this.renderTaskQueue::add);
            }));
            integratedServerRunning = true;
        } catch (Throwable t) {
            CrashReport report = CrashReport.create(t, "Starting Scaffold server");
            // CrashReportSection section = report.addElement("Starting Scaffold server");

            throw new CrashException(report);
        }

        while (this.worldGenProgressTracker.get() == null) {
            Thread.yield();
        }
        LevelLoadingScreen loadingScreen = new LevelLoadingScreen(worldGenProgressTracker.get());
        client.setScreen(loadingScreen);
        client.getProfiler().push("waitForServer");

        while (!server.isLoading()) {
            loadingScreen.tick();
            render(false);
            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {}

            if (crashReportSupplier == null) continue;
            MinecraftClient.printCrashReport(crashReportSupplier.get());
            return;
        }

        client.getProfiler().pop();
        SocketAddress address = server.getNetworkIo().bindLocal();
        ClientConnection connection = ClientConnection.connectLocal(address);
        connection.setPacketListener(new ClientLoginNetworkHandler(connection, client, null, status -> {}));
        connection.send(new HandshakeC2SPacket(address.toString(), 0, NetworkState.LOGIN));
        connection.send(new LoginHelloC2SPacket(client.getSession().getUsername(), client.getProfileKeys().getPublicKeyData()));
        integratedServerConnection = connection;
    }


//	@Inject(at = @At("HEAD"), method = "tick", cancellable = true)
//	public void tick(CallbackInfo info) {
//		if (this.world instanceof FakeClientWorld) {
//			info.cancel();
//		}
//	}
    
    // @Inject(method = START_SERVER_METHOD, at = @At(value = "HEAD"), locals = LocalCapture.CAPTURE_FAILHARD)
    // @SuppressWarnings("rawtypes")
    // private void captureRegistryTracker(String arg0, DynamicRegistryManager.Immutable registryTracker, Function arg2, Function4 arg3, boolean arg4, MinecraftClient.WorldLoadAction arg5, CallbackInfo ci) {
    // 	MixinMinecraftClient.registryTrackerHolder = registryTracker;
    // }
    
    // @Redirect(method = START_SERVER_METHOD,
    //         at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/LevelStorage;createSession(Ljava/lang/String;)Lnet/minecraft/world/level/storage/LevelStorage$Session;"))
    // private LevelStorage.Session replaceSession(LevelStorage levelStorage, String worldName) throws IOException {

    //     if (worldName.length() == 0) {
    //         return new FakeSession(levelStorage, MixinMinecraftClient.registryTrackerHolder);
    //     } else {
    //         return levelStorage.createSession(worldName);
    //     }
    // }


    
    // static enum WorldLoadAction {
    //       NONE,
    //       CREATE,
    //       BACKUP;
    //    }
    
    // @SuppressWarnings("rawtypes")
    // @Inject(method = START_SERVER_METHOD, at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;startServer(Ljava/util/function/Function;)Lnet/minecraft/server/MinecraftServer;"),
    // 		locals = LocalCapture.CAPTURE_FAILHARD)
    // private void spawnServer(String worldName, DynamicRegistryManager.Impl registryTracker, Function function,
    // 		Function4 function4, boolean safeMode, MinecraftClient.WorldLoadAction worldLoadAction, CallbackInfo ci,
    // 		LevelStorage.Session session2, MinecraftClient.IntegratedResourceManager integratedResourceManager2,
    // 		SaveProperties saveProperties, YggdrasilAuthenticationService yggdrasilAuthenticationService,
    // 		MinecraftSessionService minecraftSessionService, GameProfileRepository gameProfileRepository,
    // 		UserCache userCache) {
        
    // 	// Re-write server spawning code so we can spawn the editor server if we need to.
    // 	WorldGenerationProgressListenerFactory listener = (i) -> {
    // 		 WorldGenerationProgressTracker worldGenerationProgressTracker = new WorldGenerationProgressTracker(i + 0);
    //          worldGenerationProgressTracker.start();
    //          this.worldGenProgressTracker.set(worldGenerationProgressTracker);
    //          Queue<Runnable> queue = this.renderTaskQueue;
    //          queue.getClass();
             
    //          return QueueingWorldGenerationProgressListener.create(worldGenerationProgressTracker, queue::add);
    // 	};
    // 	this.server = MinecraftServer.startServer((serverThread) -> {
    // 		if (worldName.length() == 0) {
    // 			return new EditorServer(serverThread, MinecraftClient.getInstance(),
    // 					registryTracker, session2, integratedResourceManager2.getResourcePackManager(), integratedResourceManager2.getServerResourceManager(),
    // 					saveProperties, minecraftSessionService, gameProfileRepository, userCache, listener);
    // 		} else {
    // 			return new IntegratedServer(serverThread, MinecraftClient.getInstance(),
    // 					registryTracker, session2, integratedResourceManager2.getResourcePackManager(), integratedResourceManager2.getServerResourceManager(),
    // 					saveProperties, minecraftSessionService, gameProfileRepository, userCache, listener);
    // 		}
    // 	});
        
    // }
    
    // @Redirect(method = START_SERVER_METHOD, at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;startServer(Ljava/util/function/Function;)Lnet/minecraft/server/MinecraftServer;"))
    // private MinecraftServer removeServer(Function<Thread, IntegratedServer> serverFactory) {
    // 	return MinecraftClient.getInstance().getServer();
    // }

}
