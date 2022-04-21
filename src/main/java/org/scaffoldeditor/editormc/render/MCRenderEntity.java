package org.scaffoldeditor.editormc.render;

import org.scaffoldeditor.scaffold.render.RenderEntity;

public abstract class MCRenderEntity implements RenderEntity {
    MCRenderEntityManager manager;

    public MCRenderEntity(MCRenderEntityManager manager) {
        this.manager = manager;
    }

    @Override
    public void kill() {
        manager.remove(this);
    }
}
