package org.scaffoldeditor.editormc.util;

import org.joml.Matrix4d;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.scaffoldeditor.editormc.engine.mixins.GameRendererAccessor;
import org.scaffoldeditor.editormc.scaffold_interface.MathConverter;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

// Derived from (well, basically copied from) https://fabricmc.net/wiki/tutorial:pixel_raycast
public class RaycastUtils {
	
	/**
	 * Raycast a specific pixel on the screen.
	 * @param x Pixel X.
	 * @param y Pixel Y
	 * @param width Width of the viewport.
	 * @param height Height of the viewport.
	 * @param distance Maximum distance of the raycast.
	 * @return Hit result.
	 */
	public static HitResult raycastPixel(int x, int y, int width, int height, double distance) {
		return raycastPixel(x, y, width, height, distance, true);
	}

	/**
	 * Raycast a specific pixel on the screen.
	 * @param x Pixel X.
	 * @param y Pixel Y.
	 * @param width Width of the viewport.
	 * @param height Height of the viewport.
	 * @param distance Maximum distance of the raycast.
	 * @param collide Whether to collide with stuff.
	 * @return Hit result.
	 */
	public static HitResult raycastPixel(int x, int y, int width, int height, double distance, boolean collide) {
		return raycastPixel((double) x / (double) width, (double) y / (double) height, distance);
	}
		
	/**
	 * Raycast a specific pixel on the screen.
	 * @param x Pixel x, <code>0 - 1</code>
	 * @param y Pixel y, <code>0 - 1</code>
	 * @param distance Maximum distance of the raycast.
	 * @return Hit result.
	 */
	public static HitResult raycastPixel(double x, double y, double distance) {
		MinecraftClient client = MinecraftClient.getInstance();

		// Vector3d dir = Raycast.getRaycastDirection((double) x / (double) width, (double) y / (double) height);

		// return raycastInDirection(client.getCameraEntity(), client.getTickDelta(), new Vec3d(dir.x(), dir.y(), dir.z()),
		// 		distance, collide);

		Vec3d start = client.gameRenderer.getCamera().getPos();
		Vector3d end1 = raycastViewport(client.gameRenderer.getCamera(), x, y, 100);
		Vec3d end = new Vec3d(end1.x, end1.y, end1.z);

		// // DEBUG
		// {
		// 	ScaffoldEditorMod.getInstance().getLineRenderDispatcher().lines.add(new LineRenderer(start, end));
		// }

		return raycast(client.getCameraEntity(), start, end, false);
	}

	/**
	 * Using the given camera, determine the world location of a specific point in screen space.
	 * @param camera The camera to use.
	 * @param x Screenspace X position, from <code>0 - 1</code>
	 * @param y Screenspace Y position, from <code>0 - 1</code>
	 * @param distance Z distance to use.
	 * @return A point in 3D space that falls under the 2D screenspace point.
	 */
	public static Vector3d raycastViewport(Camera camera, double x, double y, float distance) {
		MinecraftClient client = MinecraftClient.getInstance();
        float aspect = (float) client.getFramebuffer().textureWidth / (float) client.getFramebuffer().textureHeight;
        double fov = ((GameRendererAccessor) client.gameRenderer).calcFov(camera, 0, true);
        fov = Math.toRadians(fov);

        Vector3d cameraPos = new Vector3d(camera.getPos().getX(), camera.getPos().getY(), camera.getPos().getZ());

        Matrix4d projection = new Matrix4d();
        projection.perspective(fov, aspect, .5f, 100);
        
        Quaterniond rotation = MathConverter.convertQuaternion(camera.getRotation(), new Quaterniond());
        rotation.invert();
        Matrix4d worldToCamera = new Matrix4d().rotate(rotation);
        worldToCamera.translate(cameraPos.mul(-1, new Vector3d()));

        projection.mul(worldToCamera);
        projection.invert();

        Vector3d vec = new Vector3d(x * 2 - 1, y * 2 - 1, -1); // Clip plane is between -1 and 1
        projection.transformPosition(vec);
        
        vec.sub(cameraPos);
        vec.mul(-1);
        vec.mul(distance);
        vec.add(cameraPos);
        
        return vec;
    }

	private static HitResult raycast(Entity entity, Vec3d startPos, Vec3d endPos, boolean includeFluids) {
		return entity.world.raycast(new RaycastContext(startPos, endPos, RaycastContext.ShapeType.OUTLINE,
				includeFluids ? RaycastContext.FluidHandling.ANY : RaycastContext.FluidHandling.NONE, entity));
	}
}
