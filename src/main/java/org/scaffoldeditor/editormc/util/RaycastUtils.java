package org.scaffoldeditor.editormc.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
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
		MinecraftClient client = MinecraftClient.getInstance();
		Vec3d cameraDirection = client.cameraEntity.getRotationVec(client.getTickDelta());
		double fov = client.options.fov;
		double angleSize = fov / height;

		Vector3f verticalRotationAxis = new Vector3f(cameraDirection);
		verticalRotationAxis.cross(Vector3f.POSITIVE_Y);
		if (!verticalRotationAxis.normalize()) {
			// The camera is pointed straight up or down. Need to deal with this
			return new HitResult(cameraDirection) {	
				@Override
				public Type getType() {
					return HitResult.Type.MISS;
				}
			};
		}

		Vector3f horizontalRotationAxis = new Vector3f(cameraDirection);
		horizontalRotationAxis.cross(verticalRotationAxis);

		Vec3d direction = map((float) angleSize, cameraDirection, horizontalRotationAxis, verticalRotationAxis, x, y,
				width, height);
		
		return raycastInDirection(client.getCameraEntity(), client.getTickDelta(), direction, 100, true);
	}

	private static Vec3d map(float anglePerPixel, Vec3d center, Vector3f horizontalRotationAxis,
			Vector3f verticalRotationAxis, int x, int y, int width, int height) {
		float horizontalRotation = (x - width / 2f) * anglePerPixel;
		float verticalRotation = (y - height / 2f) * -anglePerPixel;

		final Vector3f temp = new Vector3f(center);
		temp.rotate(verticalRotationAxis.getDegreesQuaternion(verticalRotation));
		temp.rotate(horizontalRotationAxis.getDegreesQuaternion(horizontalRotation));
		return new Vec3d(temp);

	}

	private static HitResult raycastInDirection(Entity entity, float tickDelta, Vec3d direction,
			double distance, boolean collide) {
		if (entity == null || entity.getEntityWorld() == null) {
			return null;
		}
		
		if (!collide) {
			Vec3d end = entity.getCameraPosVec(tickDelta).add(direction.multiply(distance));
			return BlockHitResult.createMissed(end, Direction.UP, new BlockPos(end));
		}

		HitResult target = raycast(entity, distance, tickDelta, false, direction);
		Vec3d cameraPos = entity.getCameraPosVec(tickDelta);
		double reach = distance * distance;
		if (target != null) {
			reach = target.getPos().squaredDistanceTo(cameraPos);
		}

		Vec3d vec3d = entity.getCameraPosVec(tickDelta);
		Box box = entity.getBoundingBox().stretch(entity.getRotationVec(1f).multiply(distance));
		EntityHitResult entityHitResult = ProjectileUtil.raycast(entity, cameraPos, vec3d, box,
				entityx -> !entityx.isSpectator() && entityx.collides(), reach);

		if (entityHitResult == null) {
			return target;
		}

		Vec3d vec3d2 = entityHitResult.getPos();
		double g = cameraPos.squaredDistanceTo(vec3d2);
		if (g < reach || target == null) {
			target = entityHitResult;
		}

		return target;
	}

	private static HitResult raycast(Entity entity, double maxDistance, float tickDelta, boolean includeFluids,
			Vec3d direction) {
		Vec3d end = entity.getCameraPosVec(tickDelta).add(direction.multiply(maxDistance));
		return entity.world
				.raycast(new RaycastContext(entity.getCameraPosVec(tickDelta), end, RaycastContext.ShapeType.OUTLINE,
						includeFluids ? RaycastContext.FluidHandling.ANY : RaycastContext.FluidHandling.NONE, entity));
	}
}
