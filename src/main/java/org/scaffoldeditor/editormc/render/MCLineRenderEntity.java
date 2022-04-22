package org.scaffoldeditor.editormc.render;

import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector4f;
import org.joml.Vector4fc;
import org.scaffoldeditor.editormc.engine.RenderUtils;
import org.scaffoldeditor.scaffold.render.LineRenderEntity;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

public class MCLineRenderEntity extends MCRenderEntity implements LineRenderEntity {

    private Vector3dc startPos = new Vector3d();
    private Vector3dc endPos = new Vector3d();
    private Vector4fc color = new Vector4f(1, 1, 1, 1);

    public MCLineRenderEntity(MCRenderEntityManager manager) {
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
    public Vector4fc getColor() {
        return color;
    }

    @Override
    public synchronized void setColor(Vector4fc color) {
        this.color = color;
    }

    @Override
    public synchronized void render(float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
        VertexConsumer consumer = vertexConsumers.getBuffer(RenderLayer.getLines());
        RenderUtils.renderLine(matrices, consumer, new Vec3d(startPos.x(), startPos.y(), startPos.z()),
                new Vec3d(endPos.x(), endPos.y(), endPos.z()), color.x(), color.y(), color.z(), color.w());
    }
    
}
