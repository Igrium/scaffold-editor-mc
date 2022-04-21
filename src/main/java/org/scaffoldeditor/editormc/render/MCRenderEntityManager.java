package org.scaffoldeditor.editormc.render;

import java.util.HashSet;
import java.util.Set;

import org.scaffoldeditor.scaffold.render.BillboardRenderEntity;
import org.scaffoldeditor.scaffold.render.BlockRenderEntity;
import org.scaffoldeditor.scaffold.render.BrushRenderEntity;
import org.scaffoldeditor.scaffold.render.EntityRenderEntity;
import org.scaffoldeditor.scaffold.render.LineRenderEntity;
import org.scaffoldeditor.scaffold.render.ModelRenderEntity;
import org.scaffoldeditor.scaffold.render.RenderEntityManager;

import net.minecraft.world.World;

public class MCRenderEntityManager extends RenderEntityManager<MCRenderEntity> {

    private Set<MCRenderEntity> pool = new HashSet<>();
    private final World world;

    public MCRenderEntityManager(World world) {
        this.world = world;
    }

    public World getWorld() {
        return world;
    }

    @Override
    public boolean remove(MCRenderEntity rEnt) {
        return pool.remove(rEnt);
    }

    @Override
    public BillboardRenderEntity createBillboard() {
        return null;
    }

    @Override
    public BlockRenderEntity createBlock() {
        return null;
    }

    @Override
    public BrushRenderEntity createBrush() {
        return null;
    }

    @Override
    public LineRenderEntity createLine() {
        return null;
    }

    @Override
    public MCEntityRenderEntity createMC() {
        return new MCEntityRenderEntity(this);
    }

    @Override
    public ModelRenderEntity createModel() {
        return null;
    }

    @Override
    public Set<MCRenderEntity> getPool() {
        return pool;
    }

}
