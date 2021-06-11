package org.scaffoldeditor.editormc.engine.billboard;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class Billboard {
	private Identifier texture;
	private Vec3d pos;
	private float scale;
	private Vec2f minUV;
	private Vec2f maxUV;
	
	public Billboard(Identifier texture, Vec3d pos) {
		this(texture, pos, 1, new Vec2f(0, 0), new Vec2f(1, 1));
	}
	
	public Billboard(Identifier texture, Vec3d pos, float scale, Vec2f minUV, Vec2f maxUV) {
		this.texture = texture;
		this.pos = pos;
		this.scale = scale;
		this.minUV = minUV;
		this.maxUV = maxUV;
	}
	
	public Identifier getTexture() {
		return texture;
	}
	public void setTexture(Identifier texture) {
		this.texture = texture;
	}
	public Vec3d getPos() {
		return pos;
	}
	public void setPos(Vec3d pos) {
		this.pos = pos;
	}
	public float getScale() {
		return scale;
	}
	public void setScale(float scale) {
		this.scale = scale;
	}
	public Vec2f getMinUV() {
		return minUV;
	}
	public void setMinUV(Vec2f minUV) {
		this.minUV = minUV;
	}
	public Vec2f getMaxUV() {
		return maxUV;
	}
	public void setMaxUV(Vec2f maxUV) {
		this.maxUV = maxUV;
	}

}
