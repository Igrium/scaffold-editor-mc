package org.scaffoldeditor.editormc.util;

import com.mojang.blaze3d.systems.RenderSystem;

import org.joml.Matrix4d;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.scaffoldeditor.editormc.scaffold_interface.MathConverter;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;

public class Raycast {
    private static MinecraftClient client = MinecraftClient.getInstance();

    // https://stackoverflow.com/questions/51375630/screenpointtoray-whats-behind-the-function
    /**
     * Based on the MC camera's current transform, get a direction vector based on a
     * position on the screen.
     * 
     * @param x Input position X. Must be between 0 and 1.
     * @param y Input position Y. Must be between 0 and 1.
     * @return A vector which, if applied to a raycast, will hit the screenspace
     *         location specified by the input coordinates.
     */
    public static Vector3d getRaycastDirection(double x, double y) {
        Camera camera = client.gameRenderer.getCamera();

        double fov = client.options.fov;
        Matrix4f viewbox = Matrix4f.viewboxMatrix(fov,
                (float) client.getFramebuffer().textureWidth / (float) client.getFramebuffer().textureHeight, .05f,
                client.gameRenderer.method_32796());
        // Matrix4d projection = MathConverter.convertMatrix(client.gameRenderer.getBasicProjectionMatrix(fov));
        Matrix4d projection = MathConverter.convertMatrix(viewbox);
        {
            Matrix4d worldToCamera = new Matrix4d();

            Quaterniond rotation = MathConverter.convertQuaternion(camera.getRotation(), new Quaterniond());
            rotation.invert();

            worldToCamera.rotate(rotation);
            // worldToCamera.translate(-camera.getPos().x, -camera.getPos().y, -camera.getPos().z);

            projection.mul(worldToCamera);
        }

        projection.invert();

        Vector3d direction = projection.transformPosition(new Vector3d(x * 2 - 1, y * 2 - 1, 0)); // Clip plane is between -1 and 1
        // direction.sub(camera.getPos().x, camera.getPos().y, camera.getPos().z);

        return direction.mul(-1);

    }

}
