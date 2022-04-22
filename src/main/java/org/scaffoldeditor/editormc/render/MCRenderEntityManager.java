package org.scaffoldeditor.editormc.render;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.scaffoldeditor.scaffold.entity.Entity;
import org.scaffoldeditor.scaffold.render.BlockRenderEntity;
import org.scaffoldeditor.scaffold.render.BrushRenderEntity;
import org.scaffoldeditor.scaffold.render.LineRenderEntity;
import org.scaffoldeditor.scaffold.render.ModelRenderEntity;
import org.scaffoldeditor.scaffold.render.RenderEntity;
import org.scaffoldeditor.scaffold.render.RenderEntityManager;

public class MCRenderEntityManager extends RenderEntityManager<MCRenderEntity> {

    /**
     * The keyset is the pool. The values are the owners.
     */
    private Map<MCRenderEntity, Entity> pool = new HashMap<>();

    public MCRenderEntityManager() {
    }

    @Override
    public synchronized boolean remove(MCRenderEntity rEnt) {
        return (pool.remove(rEnt) != null);
    }

    @Override
    public synchronized MCBillboardRenderEntity createBillboard() {
        MCBillboardRenderEntity ent = new MCBillboardRenderEntity(this);
        pool.put(ent, null);
        return ent;
    }

    @Override
    public synchronized BlockRenderEntity createBlock() {
        MCBlockRenderEntity ent = new MCBlockRenderEntity(this);
        pool.put(ent, null);
        return ent;
    }

    @Override
    public synchronized BrushRenderEntity createBrush() {
        MCBrushRenderEntity ent = new MCBrushRenderEntity(this);
        pool.put(ent, null);
        return ent;
    }

    @Override
    public synchronized LineRenderEntity createLine() {
        MCLineRenderEntity ent = new MCLineRenderEntity(this);
        pool.put(ent, null);
        return ent;
    }

    @Override
    public synchronized MCEntityRenderEntity createMC() {
        MCEntityRenderEntity ent = new MCEntityRenderEntity(this);
        pool.put(ent, null);
        return ent;
    }

    @Override
    public synchronized ModelRenderEntity createModel() {
        MCModelRenderEntity ent = new MCModelRenderEntity(this);
        pool.put(ent, null);
        return ent;
    }

    @Override
    public Set<MCRenderEntity> getPool() {
        return pool.keySet();
    }

    @Override
    public synchronized void assignOwner(RenderEntity rEntity, Entity owner) throws IllegalArgumentException {
        if (!pool.containsKey(rEntity)) {
            throw new IllegalArgumentException("Render entity "+rEntity.toString()+" does not belong to this entity manager!");
        }
        pool.put((MCRenderEntity) rEntity, owner);
    }

    /**
     * Get the owner of this render entity.
     * @param rEntity The render entity.
     * @return The owner, or <code>null</code> if it doesn't have one.
     */
    public Entity getOwner(RenderEntity rEntity) {
        return pool.get(rEntity);
    }

    /**
     * Find the render entity responsible for spawning a given Minecraft entity, if any.
     * @param entity Minecraft entity to check.
     * @return Owning render entity, or <code>null</code> if there is none.
     */
    public MCRenderEntity findOwner(net.minecraft.entity.Entity entity) {
        for (MCRenderEntity rEnt : pool.keySet()) {
            if (rEnt.ownsEntity(entity)) return rEnt;
        }
        return null;
    }

    public Entity findScaffoldOwner(net.minecraft.entity.Entity entity) {
        MCRenderEntity rEnt = findOwner(entity);
        if (rEnt == null) return null;
        return getOwner(rEnt);
    }

}
