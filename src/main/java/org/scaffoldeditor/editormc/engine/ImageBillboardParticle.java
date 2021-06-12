package org.scaffoldeditor.editormc.engine;

import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.world.ClientWorld;

public class ImageBillboardParticle extends SpriteBillboardParticle {

	protected ImageBillboardParticle(ClientWorld clientWorld, double x, double y, double z, Sprite sprite) {
		super(clientWorld, x, y, z);
		this.setSprite(sprite);
		this.gravityStrength = 0;
		this.maxAge = 80;
		this.collidesWithWorld = false;
	}

	@Override
	public ParticleTextureSheet getType() {
		return ParticleTextureSheet.CUSTOM;
	}

}
