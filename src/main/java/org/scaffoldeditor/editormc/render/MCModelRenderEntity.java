package org.scaffoldeditor.editormc.render;

import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.scaffoldeditor.scaffold.render.ModelRenderEntity;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;

public class MCModelRenderEntity extends MCRenderEntity implements ModelRenderEntity {
    private Vector3dc position = new Vector3d();
    private Quaterniondc rotation = new Quaterniond();
    private String model = null;

    protected final MinecraftClient client = MinecraftClient.getInstance();

    public MCModelRenderEntity(MCRenderEntityManager manager) {
        super(manager);
    }

    @Override
    public Vector3dc getPosition() {
        return position;
    }

    @Override
    public synchronized void setPosition(Vector3dc pos) {
        position = pos;
    }

    @Override
    public Quaterniondc getRotation() {
        return rotation;
    }

    @Override
    public synchronized void setRotation(Quaterniondc rotation) {
        this.rotation = rotation;
    }

    @Override
    public String getModel() {
        return model;
    }

    @Override
    public synchronized void setModel(String model) {
        this.model = model;
    }

    @Override
    public synchronized void render(float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
        if (model == null) return;
        matrices.push();
        matrices.translate(position.x(), position.y(), position.z());

        BlockRenderManager blockRenderManager = this.client.getBlockRenderManager();
        BakedModelManager bakedModelManager = blockRenderManager.getModels().getModelManager();
        
        blockRenderManager.getModelRenderer().render(matrices.peek(),
                vertexConsumers.getBuffer(TexturedRenderLayers.getEntitySolid()), null,
                bakedModelManager.getModel(new ModelIdentifier(model)), 1, 1, 1, 1, OverlayTexture.DEFAULT_UV);
                
        matrices.pop();
    }
    
}
