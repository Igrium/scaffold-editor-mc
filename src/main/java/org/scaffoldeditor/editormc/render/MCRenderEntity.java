package org.scaffoldeditor.editormc.render;

import org.jetbrains.annotations.Nullable;
import org.scaffoldeditor.scaffold.render.RenderEntity;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;

public abstract class MCRenderEntity implements RenderEntity {
    MCRenderEntityManager manager;
    private boolean enabled = true;
    private boolean alive = true;

    public MCRenderEntity(MCRenderEntityManager manager) {
        this.manager = manager;
    }

    public abstract void render(float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers);
    
    /**
     * Get the bounding box of this render entity. Used for raycasting.
     * @return Entity bounding box, or <code>null</code> if it has none.
     */
    @Nullable
    public abstract Box getBoundingBox();

    @Override
    public void kill() {
        manager.remove(this);
        alive = false;
    }

    public void disable() {
        enabled = false;
    }

    public void enable() {
        enabled = true;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isAlive() {
        return alive;
    }

    /**
     * Check if this render entity is responsible for spawning this Minecraft entity.
     * @param entity Render entity to spawn.
     * @return Does this render entity own this MC entity?
     */
    public boolean ownsEntity(Entity entity) {
        return false;
    };
}
