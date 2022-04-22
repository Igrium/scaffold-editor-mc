package org.scaffoldeditor.editormc.render;

import org.scaffoldeditor.editormc.ScaffoldEditor;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

public final class RenderEntityDispatcher {
    public void register() {
        WorldRenderEvents.AFTER_ENTITIES.register(context -> {
            ScaffoldEditor editor = ScaffoldEditor.getInstance();
            if (editor == null) return;
            
            MCRenderEntityManager manager = editor.getRenderEntityManager();

            MatrixStack matrices = context.matrixStack();
            matrices.push();
            
            Vec3d cameraPos = context.camera().getPos();
            matrices.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

            synchronized(manager) {
                for (MCRenderEntity ent : manager.getPool()) {
                    if (ent.isEnabled()) ent.render(context.tickDelta(), matrices, context.consumers());
                }
            }

            matrices.pop();
        });
    }
}
