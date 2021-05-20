package org.scaffoldeditor.editormc.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

// Derived from (well, basically copied from) https://fabricmc.net/wiki/tutorial:pixel_raycast
public class RaycastUtils {
	
	/**
	 * Raycast a specific spot on the screen.
	 * @param x Percentage accross the screen (0-1).
	 * @param y Percentage down the screen (0-1)
	 * @return Hit result.
	 */
	public static HitResult raycastPixel(double x, double y) {
		if (x < 0 || x > 1 || y < 0 || y > 1) {
			throw new IllegalArgumentException("X and Y values must be between 0 and 1!");
		}
		
		MinecraftClient client = MinecraftClient.getInstance();
		int width = 1;
		int height = 1;
		Vec3d cameraDirection = client.cameraEntity.getRotationVec(client.getTickDelta());
		double fov = client.options.fov;
		double angleSize = fov / 1;

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
		
		return raycastInDirection(client.getCameraEntity(), client.getTickDelta(), direction, 100);
	}

	private static Vec3d map(float anglePerIncriment, Vec3d center, Vector3f horizontalRotationAxis,
			Vector3f verticalRotationAxis, double x, double y, int width, int height) {
		float horizontalRotation = (float) (x - width / 2d) * anglePerIncriment;
		float verticalRotation = (float) (y - height / 2d) * anglePerIncriment;

		final Vector3f temp = new Vector3f(center);
		temp.rotate(verticalRotationAxis.getDegreesQuaternion(verticalRotation));
		temp.rotate(horizontalRotationAxis.getDegreesQuaternion(horizontalRotation));
		return new Vec3d(temp);

	}

	public static HitResult raycastInDirection(Entity entity, float tickDelta, Vec3d direction,
			double distance) {
		if (entity == null || entity.getEntityWorld() == null) {
			return null;
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
