package org.scaffoldeditor.editormc.engine.mixins;

import org.scaffoldeditor.editormc.engine.ViewportExporter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.GameRenderer;

@Environment(EnvType.CLIENT)
@Mixin(GameRenderer.class)
public class MixinGameRenderer {
	
	@Inject(method = "render()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gl/Framebuffer;beginWrite(Z)V", shift = At.Shift.AFTER))
	private void afterRender(CallbackInfo ci) {
		ViewportExporter.export();
	}
}
