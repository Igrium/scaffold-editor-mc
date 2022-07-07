package org.scaffoldeditor.editormc.engine.viewport;

import java.nio.ByteBuffer;
import java.util.function.Consumer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.system.MemoryUtil;

import com.mojang.blaze3d.systems.RenderSystem;

import javafx.application.Platform;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.util.Window;

/**
 * Viewport onto the primary Minecraft window.
 */
public class PrimaryViewport implements EngineViewport {

    private static PrimaryViewport instance;

    public static PrimaryViewport getInstance() {
        if (instance == null) {
            instance = new PrimaryViewport();
        }
        return instance;
    }

    private WritableImage image;
    private ByteBuffer buffer;
    private final PixelFormat<ByteBuffer> PIXEL_FORMAT = PixelFormat.getByteBgraInstance();

    protected Consumer<WritableImage> imageConsumer;

    private MinecraftClient client = MinecraftClient.getInstance();

    @Override
    public WritableImage getImage() {
        return null;
    }

    @Override
    public int getWidth() {
        return client.getFramebuffer().textureWidth;
    }

    @Override
    public int getHeight() {
        return client.getFramebuffer().textureHeight;
    }

    @Override
    public synchronized void setResolution(int width, int height) {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> setResolution(width, height));
            return;
        }

        if (buffer != null) {
            MemoryUtil.memFree(buffer);
            buffer = null;
        }

        Framebuffer fb = client.getFramebuffer();
        fb.resize(width, height, false);

        Window window = client.getWindow();
        window.setFramebufferWidth(width);
        window.setFramebufferHeight(height);

        client.gameRenderer.onResized(width, height);

        buffer = MemoryUtil.memAlloc(width * height * 4);
        image = new WritableImage(width, height);

        Platform.runLater(() -> {
            if (imageConsumer != null) {
                imageConsumer.accept(image);
            }
        });
    }

    @Override
    public void setImageConsumer(Consumer<WritableImage> imageConsumer) {
        this.imageConsumer = imageConsumer;
    }

    public void writeImage() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);

        if (buffer == null || image == null) {
            setResolution(getWidth(), getHeight());
        }
        
        RenderSystem.bindTexture(client.getFramebuffer().getColorAttachment());
        int x = getWidth(); int y = getHeight();
        synchronized(this) {
            RenderSystem.readPixels(0, 0, x, y, GL12.GL_BGRA, GL11.GL_UNSIGNED_BYTE, buffer);
            buffer.rewind();

            image.getPixelWriter().setPixels(0, 0, x, y, PIXEL_FORMAT, buffer, x * 4);
            buffer.rewind();
        }
    }
    
}
