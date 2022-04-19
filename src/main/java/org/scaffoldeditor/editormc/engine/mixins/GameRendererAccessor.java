package org.scaffoldeditor.editormc.engine.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;

@Mixin(GameRenderer.class)
public interface GameRendererAccessor {
    @Invoker("getFov")
    public double calcFov(Camera camera, float tickDelta, boolean changingFov);
}
