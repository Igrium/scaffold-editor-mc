package org.scaffoldeditor.editormc.engine.mixins;

import org.scaffoldeditor.editormc.engine.EditorCameraEntity;
import org.scaffoldeditor.editormc.engine.EditorInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetworkHandler {
	
	@Shadow
	private MinecraftClient client;
	
	
	@Inject(method="onGameJoin", at=@At("RETURN"))
	private void onGameJoin(CallbackInfo ci) {
		replaceInputHandler();
	}
	
	@Inject(method="onPlayerRespawn", at=@At("RETURN"))
	private void onPlayerRespawn(CallbackInfo ci) {
		replaceInputHandler();
	}
	
	private void replaceInputHandler() {
		if (client.cameraEntity instanceof EditorCameraEntity) {
			((EditorCameraEntity) client.cameraEntity).input = new EditorInput();
		}
	}
}
