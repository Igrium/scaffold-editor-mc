package org.scaffoldeditor.editormc.engine.mixins;

import org.scaffoldeditor.editormc.engine.world.EditorServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.world.ServerTickScheduler;
import net.minecraft.server.world.ServerWorld;

@Mixin(ServerTickScheduler.class)
public class MixinServerTickScheduler {
	@Shadow
	private ServerWorld world;
	
	@Inject(method = "tick()V", at = @At("HEAD"), cancellable = true)
	private void tick(CallbackInfo info) {
		if (world instanceof EditorServerWorld) {
			info.cancel();
		}
	}
}
