package org.scaffoldeditor.editormc.render;

import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.scaffoldeditor.scaffold.render.BillboardRenderEntity;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.math.Vector4f;

public class MCBillboardRenderEntity extends MCRenderEntity implements BillboardRenderEntity {

    public MCBillboardRenderEntity(MCRenderEntityManager manager) {
        super(manager);
    }

    private Vector3dc position = new Vector3d();
    private String texture = "";
    private float scale = 1f;

    private Box bbox = calculateBounds();

    @Override
    public Vector3dc getPosition() {
        return position;
    }

    @Override
    public synchronized void setPosition(Vector3dc pos) {
        this.position = pos;
        bbox = calculateBounds();
    }

    @Override
    public String getTexture() {
        return texture;
    }

    @Override
    public synchronized void setTexture(String tex) {
        this.texture = tex;
    }

    @Override
    public float getScale() {
        return scale;
    }

    @Override
    public synchronized void setScale(float scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("Scale must be at least 0.");
        }
        this.scale = scale;
        bbox = calculateBounds();
    }

    private Box calculateBounds() {
        float radius = this.scale / 2f;
        return new Box(position.x() - radius, position.y() - radius, position.z() - radius, position.x() + radius,
                position.y() + radius, position.z() + radius);
    }

    @Override
    public Box getBoundingBox() {
        return bbox;
    }

    @Override
    public synchronized void render(float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
        MinecraftClient client = MinecraftClient.getInstance();
        Camera camera = client.gameRenderer.getCamera();

        matrices.push();
        matrices.translate(position.x(), position.y(), position.z());

        Vec3f rotVector = new Vec3f(-1, -1, 0);
        rotVector.rotate(camera.getRotation());
        Vec3f[] verts = new Vec3f[] { new Vec3f(-1, -1, 0), new Vec3f(-1, 1, 0), new Vec3f(1, 1, 0),
                new Vec3f(1, -1, 0) };

        Matrix4f model = matrices.peek().getModel();

        for (int i = 0; i < 4; i++) {
            Vec3f vert = verts[i];
            vert.rotate(camera.getRotation());
            vert.scale(scale / 2f);

            Vector4f vector4 = new Vector4f(vert.getX(), vert.getY(), vert.getZ(), 1.0F);
            vector4.transform(model);
            vert.set(vector4.getX(), vector4.getY(), vector4.getZ());
        }

        float minU = 0;
        float maxU = 1;
        float minV = 0;
        float maxV = 1;

        VertexConsumer buffer = vertexConsumers
                .getBuffer(RenderLayer.getEntityTranslucent(new Identifier(texture)));
        buffer.vertex(verts[0].getX(), verts[0].getY(), verts[0].getZ(), 1, 1, 1, 1, maxU, maxV,
                OverlayTexture.DEFAULT_UV, 255, rotVector.getX(), rotVector.getY(), rotVector.getZ());
        buffer.vertex(verts[1].getX(), verts[1].getY(), verts[1].getZ(), 1, 1, 1, 1, maxU, minV,
                OverlayTexture.DEFAULT_UV, 255, rotVector.getX(), rotVector.getY(), rotVector.getZ());
        buffer.vertex(verts[2].getX(), verts[2].getY(), verts[2].getZ(), 1, 1, 1, 1, minU, minV,
                OverlayTexture.DEFAULT_UV, 255, rotVector.getX(), rotVector.getY(), rotVector.getZ());
        buffer.vertex(verts[3].getX(), verts[3].getY(), verts[3].getZ(), 1, 1, 1, 1, minU, maxV,
                OverlayTexture.DEFAULT_UV, 255, rotVector.getX(), rotVector.getY(), rotVector.getZ());
        
        matrices.pop();
    }

}
