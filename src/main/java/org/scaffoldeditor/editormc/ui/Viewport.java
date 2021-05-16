package org.scaffoldeditor.editormc.ui;

import java.nio.ByteBuffer;


import javafx.scene.image.ImageView;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import javafx.scene.image.WritablePixelFormat;

public class Viewport {
	protected ImageView imageView;
	
	private final WritablePixelFormat<ByteBuffer> PIXEL_FORMAT = PixelFormat.getByteBgraInstance();
	
	public Viewport(ImageView imageView) {
		this.imageView = imageView;
	}
	
	/**
	 * Update the editor viewport. Must be called from the FX thread.
	 * @param buffer ByteBuffer to draw viewport from.
	 * @param x Width of the image in pixels.
	 * @param y Height of the image in pixels.
	 */
	public void updateViewport(ByteBuffer buffer, int x, int y) {
		WritableImage image = new WritableImage(x, y);
		if (x > 0 && y > 0) {
			image.getPixelWriter().setPixels(0, 0, x, y, PIXEL_FORMAT, buffer, 0);
			imageView.setImage(image);
		}
	}
	
}
