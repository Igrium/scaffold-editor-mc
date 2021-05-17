package org.scaffoldeditor.editormc.engine;

import org.scaffoldeditor.editormc.controls.CameraController;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.stat.StatHandler;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;


public class EditorCameraEntity extends ClientPlayerEntity implements CameraController {
	
	private float frontBack = 0;
	private float leftRight = 0;
	private float upDown = 0;
	private double maxSpeed = 1;
	
	public EditorCameraEntity(MinecraftClient client, ClientWorld world, ClientPlayNetworkHandler networkHandler,
			StatHandler stats, ClientRecipeBook recipeBook, boolean lastSneaking, boolean lastSprinting) {
		super(client, world, networkHandler, stats, recipeBook, lastSneaking, lastSprinting);
	}

	@Override
	public boolean isOnFire() {
		return false;
	}
	
	@Override
	public boolean collides() {
		return false;
	}
	
	@Override
	public boolean isSpectator() {
		return true;
	}
	
	@Override
	public void tickMovement() {
		this.input.movementForward = frontBack;
		this.input.movementSideways = -leftRight;
		this.input.jumping = upDown > 0;
		this.input.sneaking = upDown < 0;
		super.tickMovement();
	}
	
	@Override
	public boolean shouldRender(double cameraX, double cameraY, double cameraZ) {
		return false;
	}
	
	@Override
	public void setFrontBack(double value) {
		frontBack = (float) value;
	}

	@Override
	public void setLeftRight(double value) {
		leftRight = (float) value;
	}
	
	@Override
	public void setUpDown(double value) {
		upDown = (float) value;
	}

	@Override
	public void setMaxSpeed(double speed) {
		this.movementMultiplier = new Vec3d(speed, speed, speed);
		maxSpeed = speed;
	}

	@Override
	public double getMaxSpeed() {
		return maxSpeed;
	}
	
	@Override
	public Vec3d getPosition() {
		return this.getPos();
	}

	@Override
	public void setPosition(Vec3d pos) {
		this.setPos(pos.x, pos.y, pos.z);
	}
	
	@Override
	public void setRot(float yaw, float pitch) {
		this.setRotation(yaw, pitch);
	}
	
	@Override
	public void addRot(double yaw, double pitch) {
		this.changeLookDirection(yaw, pitch);
	}

	@Override
	public Vec2f getRot() {
		return this.getRotationClient();
	}

	@Override
	public void setFOV(double fov) {
	}

	@Override
	public double getFOV() {
		return 0;
	}
}
