package org.scaffoldeditor.editormc.util;

import java.util.Optional;

import org.joml.Vector3dc;
import org.scaffoldeditor.editormc.ScaffoldEditor;
import org.scaffoldeditor.editormc.render.MCRenderEntityManager;
import org.scaffoldeditor.editormc.render.RenderEntityRaycastUtil;
import org.scaffoldeditor.editormc.render.RenderEntityRaycastUtil.RenderEntityHitResult;
import org.scaffoldeditor.scaffold.entity.Entity;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.render.RenderEntityManager;

import com.ibm.icu.util.StringTrieBuilder.Option;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
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
	@Deprecated
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
	 * @return Hit result.
	 */
	public static HitResult raycastPixel(float x, float y, float width, float height) {
		MinecraftClient client = MinecraftClient.getInstance();

		// Vector3d dir = Raycast.getRaycastDirection((double) x / (double) width, (double) y / (double) height);

		// return raycastInDirection(client.getCameraEntity(), client.getTickDelta(), new Vec3d(dir.x(), dir.y(), dir.z()),
		// 		distance, collide);

		Vec3d start = client.gameRenderer.getCamera().getPos();
		Vec3d end = raycastViewport(client.gameRenderer.getCamera(), x, y, width, height);

		// DEBUG
		// {
		// 	ScaffoldEditorMod.getInstance().getLineRenderDispatcher().lines.add(new LineRenderer(start, end));
		// }

		return raycastWorld(client.getCameraEntity(), start, end, false);
	}

	/**
	 * Raycast a specific pixel on the screen and return the scaffold entity found
	 * under it.
	 * 
	 * @param x      Pixel X.
	 * @param y      Pixel Y.
	 * @param width  Width of the viewport.
	 * @param height Height of the viewport.
	 * @return The entity found.
	 */
	public static Optional<Entity> raycastPixelScaffold(float x, float y, float width, float height) {
		MinecraftClient client = MinecraftClient.getInstance();

		Vec3d start = client.gameRenderer.getCamera().getPos();
		Vec3d end = raycastViewport(client.gameRenderer.getCamera(), x, y, width, height);

		return raycastScaffoldEntities(start, end);
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
	public static Vec3d raycastViewport(Camera camera, float x, float y, float width, float height) {
		Vector4f screenspace = new Vector4f(2 * x / width - 1, 2 * y / height - 1, -1, 1);

		Matrix4f cameraProjection = getCameraProjection().copy();
		cameraProjection.invert();

		screenspace.transform(cameraProjection);
		screenspace.multiply(-10000); // Bandaid on ray that's broken for some reason.
		screenspace.rotate(camera.getRotation());
		screenspace.add((float) camera.getPos().x, (float) camera.getPos().y, (float) camera.getPos().z, 0);

        return new Vec3d(screenspace.getX(), screenspace.getY(), screenspace.getZ());
    }

	/**
	 * Perform a raycast into the world and return the first Scaffold entity hit.
	 * 
	 * @param start Raycast start point.
	 * @param end   Raycast end point.
	 * @return The Scaffold entity hit.
	 */
	public static Optional<Entity> raycastScaffoldEntities(Vec3d start, Vec3d end) {
		MCRenderEntityManager manager = (MCRenderEntityManager) RenderEntityManager.getInstance();
		MinecraftClient client = MinecraftClient.getInstance();
		Level level = ScaffoldEditor.getInstance().getLevel();

		RenderEntityHitResult entHit = RenderEntityRaycastUtil.raycast(manager, start, end, null);
		BlockHitResult blockHit = raycastWorld(client.getCameraEntity(), start, end, false);

		boolean useBlock;

		if (entHit == null) {
			useBlock = true;
		} else if (blockHit == null) {
			useBlock = false;
		} else {
			if (start.distanceTo(entHit.getPos()) <= start.distanceTo(blockHit.getPos())) {
				useBlock = false;
			} else {
				useBlock = true;
			}
		}

		if (useBlock) {
			if (blockHit == null) return Optional.empty();
			
			BlockPos pos = blockHit.getBlockPos();
			Entity owner = (Entity) level.getBlockWorld().getBlockOwner(pos.getX(), pos.getY(), pos.getZ());
			return Optional.ofNullable(owner);
		} else {
			if (entHit == null) return Optional.empty();
			return Optional.ofNullable(manager.getOwner(entHit.getRenderEntity()));
		}
		
	}

	/**
	 * Perform a raycast into the world and return the first Scaffold entity hit.
	 * Proxy that uses Joml vectors instead of MC vectors.
	 * 
	 * @param start Raycast start point.
	 * @param end   Raycast end point.
	 * @return The Scaffold entity hit.
	 */
	public static Optional<Entity> raycastScaffoldEntities(Vector3dc start, Vector3dc end) {
		return raycastScaffoldEntities(new Vec3d(start.x(), start.y(), start.z()), new Vec3d(end.x(), end.y(), end.z()));
	}

	private static BlockHitResult raycastWorld(net.minecraft.entity.Entity entity, Vec3d startPos, Vec3d endPos, boolean includeFluids) {
		return entity.world.raycast(new RaycastContext(startPos, endPos, RaycastContext.ShapeType.OUTLINE,
				includeFluids ? RaycastContext.FluidHandling.ANY : RaycastContext.FluidHandling.NONE, entity));
	}
}
