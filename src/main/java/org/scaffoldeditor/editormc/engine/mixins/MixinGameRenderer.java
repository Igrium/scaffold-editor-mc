package org.scaffoldeditor.editormc.engine.mixins;

import org.scaffoldeditor.editormc.engine.ViewportExporter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.render.GameRenderer;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {
	
	private static final String TARGET = "Lnet/minecraft/client/render/WorldRenderer;render(Lnet/minecraft/client/util/math/MatrixStack;FJZLnet/minecraft/client/render/Camera;Lnet/minecraft/client/render/GameRenderer;Lnet/minecraft/client/render/LightmapTextureManager;Lnet/minecraft/util/math/Matrix4f;)V";
	
	@Inject(method = "renderWorld", at = @At(value = "INVOKE", target = TARGET, shift = At.Shift.AFTER))
	private void renderWorld(CallbackInfo ci) {
		ViewportExporter.export();
	}
}
