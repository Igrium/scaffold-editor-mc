package org.scaffoldeditor.editormc.engine.viewport;

import java.util.function.Consumer;

import javafx.scene.image.WritableImage;

/**
 * Responsible for exporting a Minecraft viewport into JavaFX
 */
public interface EngineViewport {

    /**
     * Get the image that will be written to when this viewport updates. The amount
     * of time which this image is valid is not guaranteed.
     * 
     * @return Writable image.
     */
    public WritableImage getImage();

    /**
     * Get the width of this viewport
     * @return Width in pixels.
     */
    public int getWidth();

    /**
     * Get the height of this viewport.
     * @return Height in pixels.
     */
    public int getHeight();
    
    /**
     * Set the resolution of this viewport. May not set until the next frame is rendered.
     * @param width Width in pixels.
     * @param height Height in pixels.
     */
    public void setResolution(int width, int height);

    /**
     * Set a consumer to recieve this viewport's image whenever it is updated.
     * @param consumer Image consumer.
     */
    public void setImageConsumer(Consumer<WritableImage> consumer);
}
