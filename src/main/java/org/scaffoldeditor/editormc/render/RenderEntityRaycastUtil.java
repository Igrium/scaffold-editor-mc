package org.scaffoldeditor.editormc.render;

import java.util.Optional;
import java.util.function.Predicate;

import org.jetbrains.annotations.Nullable;
import org.scaffoldeditor.scaffold.render.RenderEntityManager;

import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class RenderEntityRaycastUtil {
    public static class RenderEntityHitResult extends HitResult {
        private final MCRenderEntity renderEntity;

        public RenderEntityHitResult(MCRenderEntity entity, Vec3d pos) {
            super(pos);
            this.renderEntity = entity;
        }

        public MCRenderEntity getRenderEntity() {
            return renderEntity;
        }
        
    
        @Override
        public Type getType() {
            return Type.ENTITY;
        }
    }

    /**
     * Perform a raycast against Scaffold render entities.
     * @param manager Render entity manager to use.
     * @param start Raycast start position.
     * @param end Raycast end position.
     * @param predicate Only include entities that satisfy this predicate.
     * @return The hit result, or <code>null</code> if nothing was hit.
     */
    public static RenderEntityHitResult raycast(RenderEntityManager<MCRenderEntity> manager,
            Vec3d start, Vec3d end,
            @Nullable Predicate<MCRenderEntity> predicate) {

        Vec3d closestPos = null;
        MCRenderEntity closestEnt = null;
        double minDistance = Double.MAX_VALUE;

        if (predicate == null) {
            predicate = ent -> true;
        }

        for (MCRenderEntity ent : manager.getPool()) {
            if (!predicate.test(ent)) continue;

            Box bbox = ent.getBoundingBox();
            if (bbox == null) continue;
            Optional<Vec3d> result = bbox.raycast(start, end);
            if (!result.isPresent()) {
                continue;
            }

            Vec3d vec = result.get();
            double distance = vec.squaredDistanceTo(start);

            if (closestEnt == null || distance < minDistance) {
                closestPos = vec;
                minDistance = distance;
                closestEnt = ent;
            }
        }

        if (closestEnt == null) {
            return null;
        }
        
        return new RenderEntityHitResult(closestEnt, closestPos);
    }

}
