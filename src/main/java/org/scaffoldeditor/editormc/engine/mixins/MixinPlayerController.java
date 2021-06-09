package org.scaffoldeditor.editormc.engine.mixins;

import org.scaffoldeditor.editormc.engine.EditorCameraEntity;
import org.scaffoldeditor.editormc.engine.EditorServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.stat.StatHandler;

@Mixin(ClientPlayerInteractionManager.class)
public class MixinPlayerController {
	
	@Shadow
	private MinecraftClient client;
	
	@Shadow
	private ClientPlayNetworkHandler networkHandler;

	@Inject(method = "createPlayer(Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/stat/StatHandler;Lnet/minecraft/client/recipebook/ClientRecipeBook;ZZ)Lnet/minecraft/client/network/ClientPlayerEntity;", at = @At("HEAD"), cancellable = true)
	private void createCamera(ClientWorld worldIn,StatHandler statisticsManager, ClientRecipeBook recipeBookClient,boolean lastIsHoldingSneakKey,
			boolean lastSprinting,CallbackInfoReturnable<ClientPlayerEntity> ci) {
		if (client.getServer() instanceof EditorServer) {
			ci.setReturnValue(new EditorCameraEntity(client, worldIn, networkHandler, statisticsManager, recipeBookClient, lastIsHoldingSneakKey, lastSprinting));
		}
	}
}
