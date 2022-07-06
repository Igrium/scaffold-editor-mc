package org.scaffoldeditor.editormc.engine.world;

import java.util.Random;

import org.joml.Vector3ic;
import org.scaffoldeditor.editormc.scaffold_interface.BlockConverter;
import org.scaffoldeditor.nbt.block.Block;
import org.scaffoldeditor.nbt.block.BlockCollection;

import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;

public class WorldRenderUtils {
	private static PlacementSimulationWorld renderWorld;
	
	
	public static synchronized void buildBlockMesh(BlockCollection blocks, MatrixStack matrixStack, VertexConsumer vertexConsumer) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (renderWorld == null || renderWorld.getWorld() != client.world) {
			renderWorld = new PlacementSimulationWorld(client.world);
		}
		
		BlockRenderManager dispatcher = client.getBlockRenderManager();
		BlockModelRenderer renderer = dispatcher.getModelRenderer();
		Random random = new Random();
		
		for (Vector3ic pos : blocks) {
			Block block = blocks.blockAt(pos);
			if (block == null) continue;
			renderWorld.setBlockState(scaffoldVec2MC(pos), BlockConverter.scaffoldToMinecraft(blocks.blockAt(pos)));
		}
		
		for (Vector3ic pos : blocks) {
			BlockState state = renderWorld.getBlockState(scaffoldVec2MC(pos));
			
			if (state.getRenderType() == BlockRenderType.ENTITYBLOCK_ANIMATED) continue;
			
			BakedModel originalModel = dispatcher.getModel(state);
			matrixStack.push();
			matrixStack.translate(pos.x(), pos.y(), pos.z());
			renderer.render(renderWorld, originalModel, state, scaffoldVec2MC(pos), matrixStack, vertexConsumer, true,
					random, 32, OverlayTexture.DEFAULT_UV);
			matrixStack.pop();
		}
		
		renderWorld.clear();
	}
	
	private static BlockPos scaffoldVec2MC(Vector3ic vec) {
		return new BlockPos(vec.x(), vec.y(), vec.z());
	}
}
