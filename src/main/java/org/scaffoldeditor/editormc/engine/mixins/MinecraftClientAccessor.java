package org.scaffoldeditor.editormc.engine.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;

import net.minecraft.client.MinecraftClient;

@Mixin(MinecraftClient.class)
public interface MinecraftClientAccessor {
	
	@Accessor("currentFPS")
	public static int getFPS() {
		throw new AssertionError();
	}

	@Accessor("authenticationService")
	public YggdrasilAuthenticationService getAuthenticationService();
}
