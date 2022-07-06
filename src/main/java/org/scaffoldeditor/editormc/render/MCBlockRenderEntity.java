package org.scaffoldeditor.editormc.render;

import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.scaffoldeditor.editormc.engine.world.WorldRenderUtils;
import org.scaffoldeditor.nbt.block.BlockCollection;
import org.scaffoldeditor.scaffold.render.BlockRenderEntity;

import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Quaternion;

public class MCBlockRenderEntity extends MCRenderEntity implements BlockRenderEntity {

    public MCBlockRenderEntity(MCRenderEntityManager manager) {
        super(manager);
    }

    private Vector3dc pos = new Vector3d();
    private Quaterniondc rot = new Quaterniond();
    private Quaternion mcRot = new Quaternion(0, 0, 0, 1);

    private BlockCollection blocks;

    @Override
    public Vector3dc getPosition() {
        return pos;
    }

    @Override
    public synchronized void setPosition(Vector3dc pos) {
        this.pos = pos;
        setRotation(new Quaterniond());
    }

    @Override
    public Quaterniondc getRotation() {
        return rot;
    }

    @Override
    public synchronized void setRotation(Quaterniondc rotation) {
        this.rot = rotation;
        mcRot = new Quaternion((float) rot.x(), (float) rot.y(), (float) rot.z(), (float) rot.w());
    }

    @Override
    public BlockCollection getBlocks() {
        return blocks;
    }

    @Override
    public synchronized void setBlocks(BlockCollection blocks) {
        this.blocks = blocks;
    }

    @Override
    public Box getBoundingBox() {
        return null; // TODO: properly calculate bounding box.
    }

    @Override
    public synchronized void render(float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
        matrices.push();
        matrices.translate(pos.x(), pos.y(), pos.z());
        matrices.multiply(mcRot);

        // TODO: Implement mesh caching.
        VertexConsumer consumer = vertexConsumers.getBuffer(TexturedRenderLayers.getEntityTranslucentCull());
        WorldRenderUtils.buildBlockMesh(blocks, matrices, consumer);

        matrices.pop();
    }
    
}
