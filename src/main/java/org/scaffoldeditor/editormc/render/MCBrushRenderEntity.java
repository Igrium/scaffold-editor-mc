package org.scaffoldeditor.editormc.render;

import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.scaffoldeditor.scaffold.render.BrushRenderEntity;

import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;

public class MCBrushRenderEntity extends MCRenderEntity implements BrushRenderEntity {

    private Vector3dc startPos = new Vector3d();
    private Vector3dc endPos = new Vector3d();
    private String texture = "";

    public MCBrushRenderEntity(MCRenderEntityManager manager) {
        super(manager);
    }

    @Override
    public Vector3dc getStartPos() {
        return startPos;
    }

    @Override
    public synchronized void setStartPos(Vector3dc startPos) {
        this.startPos = startPos;
    }

    @Override
    public Vector3dc getEndPos() {
        return endPos;
    }

    @Override
    public synchronized void setEndPos(Vector3dc endPos) {
        this.endPos = endPos;
    }

    @Override
    public String getTexture() {
        return texture;
    }

    @Override
    public synchronized void setTexture(String texture) {
        this.texture = texture;
    }

    @Override
    public Box getBoundingBox() {
        return new Box(startPos.x(), startPos.y(), startPos.z(), endPos.x(), endPos.y(), endPos.z());
    }

    @Override
    public synchronized void render(float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
        Vector3d size = new Vector3d(endPos);
        size.sub(startPos);

        matrices.push();
        matrices.translate(startPos.x(), startPos.y(), startPos.z());

        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();

        modelPartData.addChild("body", ModelPartBuilder.create().uv(32, 32).cuboid(0, 0, 0,
                (float) size.x() * 16,
                (float) size.y() * 16,
                (float) size.z() * 16), ModelTransform.NONE);
        ModelPart part = TexturedModelData.of(modelData, 16, 16).createModel();

        VertexConsumer vertices = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucentCull(new Identifier(texture)));
        part.render(matrices, vertices, 255, OverlayTexture.DEFAULT_UV);

        matrices.pop();
    }
    
}
