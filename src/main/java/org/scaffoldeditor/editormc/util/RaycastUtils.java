package org.scaffoldeditor.editormc.util;

import org.joml.Vector3d;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vector4f;
import net.minecraft.world.RaycastContext;

// Derived from (well, basically copied from) https://fabricmc.net/wiki/tutorial:pixel_raycast
public class RaycastUtils {
	
	private static Matrix4f cameraProjection;
	
	public static Matrix4f getCameraProjection() {
		return cameraProjection;
	}

	public static void register() {
		WorldRenderEvents.AFTER_SETUP.register(context -> {
			cameraProjection = context.projectionMatrix().copy();
		});
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
	public static HitResult raycastPixel(int x, int y, int width, int height, boolean collide) {
		return raycastPixel(x, y, width, height);
	}
		
	/**
	 * Raycast a specific pixel on the screen.
	 * 
	 * @param x        Pixel X.
	 * @param y        Pixel Y.
	 * @param width    Width of the viewport.
	 * @param height   Height of the viewport.
	 * @param distance Maximum distance of the raycast.
	 * @param collide  Whether to collide with stuff.
	 * @return Hit result.
	 */
	public static HitResult raycastPixel(float x, float y, float width, float height) {
		MinecraftClient client = MinecraftClient.getInstance();

		// Vector3d dir = Raycast.getRaycastDirection((double) x / (double) width, (double) y / (double) height);

		// return raycastInDirection(client.getCameraEntity(), client.getTickDelta(), new Vec3d(dir.x(), dir.y(), dir.z()),
		// 		distance, collide);

		Vec3d start = client.gameRenderer.getCamera().getPos();
		Vector3d end1 = raycastViewport(client.gameRenderer.getCamera(), x, y, width, height);
		Vec3d end = new Vec3d(end1.x, end1.y, end1.z);

		// DEBUG
		// {
		// 	ScaffoldEditorMod.getInstance().getLineRenderDispatcher().lines.add(new LineRenderer(start, end));
		// }

		return raycast(client.getCameraEntity(), start, end, false);
	}

	/**
	 * Using the given camera, determine the world location of a specific point in
	 * screen space.
	 * 
	 * @param camera The camera to use.
	 * @param x      X coordinate on-screen.
	 * @param y      Y coordinate on-screen.
	 * @param width  Width of the viewport.
	 * @param height Height of the viewport.
	 * @return A point in 3D space that falls under the 2D screenspace point.
	 */
	public static Vector3d raycastViewport(Camera camera, float x, float y, float width, float height) {
		Vector4f screenspace = new Vector4f(2 * x / width - 1, 2 * y / height - 1, -1, 1);

		Matrix4f cameraProjection = getCameraProjection().copy();
		cameraProjection.invert();

		screenspace.transform(cameraProjection);
		screenspace.multiply(-10000); // Bandaid on ray that's broken for some reason.
		screenspace.rotate(camera.getRotation());
		screenspace.add((float) camera.getPos().x, (float) camera.getPos().y, (float) camera.getPos().z, 0);

        return new Vector3d(screenspace.getX(), screenspace.getY(), screenspace.getZ());
    }

	private static HitResult raycast(Entity entity, Vec3d startPos, Vec3d endPos, boolean includeFluids) {
		return entity.world.raycast(new RaycastContext(startPos, endPos, RaycastContext.ShapeType.OUTLINE,
				includeFluids ? RaycastContext.FluidHandling.ANY : RaycastContext.FluidHandling.NONE, entity));
	}
}
