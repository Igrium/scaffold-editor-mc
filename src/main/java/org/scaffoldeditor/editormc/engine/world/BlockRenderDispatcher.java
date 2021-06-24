package org.scaffoldeditor.editormc.engine.world;

import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.scaffoldeditor.nbt.block.BlockCollection;

import com.mojang.blaze3d.systems.RenderSystem;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3d;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;

public class BlockRenderDispatcher {
	protected MinecraftClient client;
	
	public static class BlockCollectionRenderer {
		private BlockCollection blockCollection;
		private Vec3d pos;
		private float rot;
		
		public BlockCollectionRenderer(BlockCollection blocks) {
			this.blockCollection = blocks;
		}

		public BlockCollection getBlockCollection() {
			return blockCollection;
		}

		public void setBlockCollection(BlockCollection blockCollection) {
			this.blockCollection = blockCollection;
		}

		public Vec3d getPos() {
			return pos;
		}

		public void setPos(Vec3d pos) {
			this.pos = pos;
		}

		public float getRot() {
			return rot;
		}

		public void setRot(float rot) {
			this.rot = rot;
		}
	}
	
	public final Set<BlockCollectionRenderer> blockCollections = new HashSet<>();
	
	public BlockRenderDispatcher(MinecraftClient client) {
		this.client = client;
	}
	
	public void register() {
		WorldRenderEvents.AFTER_ENTITIES.register(context -> {
			render(context.matrixStack(), context.consumers(), context.camera().getPos());
		});
	}
	
	public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumers, Vec3d cameraPos) {
		if (!RenderSystem.isOnRenderThread()) {
			throw new IllegalStateException("Rendering can only happen on the render thread!");
		}
		matrixStack.push();
		matrixStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
		for (BlockCollectionRenderer blocks : blockCollections) {
			Vec3d pos = blocks.getPos();
			renderBlocks(blocks.getBlockCollection(), pos.x, pos.y, pos.z, blocks.getRot(), matrixStack, vertexConsumers);
		}
		matrixStack.pop();
	}
	
	public void renderBlocks(BlockCollection blocks, double x, double y, double z, float yaw, MatrixStack matrixStack, VertexConsumerProvider vertexConsumers) {
		if (!RenderSystem.isOnRenderThread()) {
			throw new IllegalStateException("Rendering can only happen on the render thread!");
		}
		LogManager.getLogger().debug("Rendering block collection at " + new Vector3d(x, y, z));
		matrixStack.push();
		matrixStack.translate(x, y, z);
		matrixStack.multiply(new Quaternion(0, yaw, 0, true));
		
		
		VertexConsumer consumer = vertexConsumers.getBuffer(TexturedRenderLayers.getEntityTranslucentCull());
		WorldRenderUtils.buildBlockMesh(blocks, matrixStack, consumer);
		
		matrixStack.pop();
	}
}
