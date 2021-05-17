package org.scaffoldeditor.editormc.controls;

import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

/**
 * A camera that can be driven freely
 * @author Igrium
 */
public interface CameraController {
	
	/**
	 * Set the forward/backward movement speed.
	 * @param value A value between -1 and 1 where 1 is the maximum movement speed.
	 */
	public void setFrontBack(double value);
	
	/**
	 * Set the left/right movement speed.
	 * @param value A value between -1 and 1 where 1 is the maximum movement speed.
	 */
	public void setLeftRight(double value);
	
	/**
	 * Set the up/down movement speed.
	 * @param value A value between -1 and 1 where 1 is the maximum movement speed.
	 */
	public void setUpDown(double value);

	/**
	 * Set this camera's max speed.
	 * @param speed Maximum speed in blocks per second.
	 */
	public void setMaxSpeed(double speed);
	
	/**
	 * Get this camera's max speed.
	 * @return Maximum speed in blocks per second.
	 */
	public double getMaxSpeed();
	
	public Vec3d getPosition();
	
	public void setPosition(Vec3d pos);
	
	public default void setPosition(double x, double y, double z) {
		setPosition(new Vec3d(x,y,z));
	}
	
	public void setRot(float yaw, float pitch);
	
	public Vec2f getRot();
	
	public default void addRot(double yaw, double pitch) {
		Vec2f rot = getRot();
		setRot((float) (rot.x + yaw), (float) (rot.y + pitch));
	}
	
	/**
	 * Set this camera's field of view.
	 * @param fov New FOV in degrees.
	 */
	public void setFOV(double fov);
	
	
	/**
	 * Get this camera's field of view.
	 * @return FOV in degrees.
	 */
	public double getFOV();
}
