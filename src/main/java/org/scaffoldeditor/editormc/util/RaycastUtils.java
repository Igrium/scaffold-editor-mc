package org.scaffoldeditor.editormc.util;

import java.util.Optional;

import org.joml.Intersectiond;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.scaffoldeditor.editormc.ScaffoldEditor;
import org.scaffoldeditor.editormc.engine.RenderUtils;
import org.scaffoldeditor.editormc.render.MCRenderEntityManager;
import org.scaffoldeditor.editormc.render.RenderEntityRaycastUtil;
import org.scaffoldeditor.editormc.render.RenderEntityRaycastUtil.RenderEntityHitResult;
import org.scaffoldeditor.scaffold.entity.Entity;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.render.RenderEntityManager;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vector4f;
import net.minecraft.world.RaycastContext;

public class RaycastUtils {

    /**
     * Raycast a specific pixel on the screen.
     * @param x Pixel X.
     * @param y Pixel Y.
     * @param width Width of the viewport.
     * @param height Height of the viewport.
     * @param distance Maximum distance of the raycast.
     * @param collide Whether to collide with stuff.
     * @return Hit result.
     */
    @Deprecated
    public static HitResult raycastPixel(int x, int y, int width, int height, boolean collide) {
        return raycastPixel(x, y, width, height);
    }
        
    /**
     * Raycast a specific pixel on the screen.
     * 
     * @param x        Pixel X.
     * @param y        Pixel Y.
     * @param width    Width of the viewport.
     * @param height   Height of the viewport.
     * @return Hit result.
     */
    public static HitResult raycastPixel(float x, float y, float width, float height) {
        MinecraftClient client = MinecraftClient.getInstance();

        Vec3d start = client.gameRenderer.getCamera().getPos();
        Vec3d end = raycastViewport(client.gameRenderer.getCamera(), x, y, width, height);

        // DEBUG
        // {
        // 	ScaffoldEditorMod.getInstance().getLineRenderDispatcher().lines.add(new LineRenderer(start, end));
        // }

        return raycastWorld(client.getCameraEntity(), start, end, false);
    }

    /**
     * Raycast a specific pixel on the screen and return the scaffold entity found
     * under it.
     * 
     * @param x      Pixel X.
     * @param y      Pixel Y.
     * @param width  Width of the viewport.
     * @param height Height of the viewport.
     * @return The entity found.
     */
    public static Optional<Entity> raycastPixelSelection(float x, float y, float width, float height) {
        MinecraftClient client = MinecraftClient.getInstance();

        Vec3d start = client.gameRenderer.getCamera().getPos();
        Vec3d end = raycastViewport(client.gameRenderer.getCamera(), x, y, width, height);

        return raycastScaffoldEntities(start, end);
    }

    /**
     * Using the given camera, determine the world location of a specific point in
     * screen space.
     * 
     * @param camera The camera to use.
     * @param x      X coordinate on-screen.
     * @param y      Y coordinate on-screen.
     * @param width  Width of the viewport.
     * @param height Height of the viewport.
     * @return A point in 3D space that falls under the 2D screenspace point.
     */
    public static Vec3d raycastViewport(Camera camera, float x, float y, float width, float height) {
        Vector4f screenspace = new Vector4f(2 * x / width - 1, 2 * y / height - 1, -1, 1);

        Matrix4f cameraProjection = RenderUtils.getCameraProjection().copy();
        cameraProjection.invert();

        screenspace.transform(cameraProjection);
        screenspace.multiply(-10000); // Bandaid on ray that's broken for some reason.
        screenspace.rotate(camera.getRotation());
        screenspace.add((float) camera.getPos().x, (float) camera.getPos().y, (float) camera.getPos().z, 0);

        return new Vec3d(screenspace.getX(), screenspace.getY(), screenspace.getZ());
    }

    /**
     * Perform a raycast into the world and return the first Scaffold entity hit.
     * 
     * @param start Raycast start point.
     * @param end   Raycast end point.
     * @return The Scaffold entity hit.
     */
    public static Optional<Entity> raycastScaffoldEntities(Vec3d start, Vec3d end) {
        MCRenderEntityManager manager = (MCRenderEntityManager) RenderEntityManager.getInstance();
        MinecraftClient client = MinecraftClient.getInstance();
        Level level = ScaffoldEditor.getInstance().getLevel();

        RenderEntityHitResult entHit = RenderEntityRaycastUtil.raycast(manager, start, end, null);
        BlockHitResult blockHit = raycastWorld(client.getCameraEntity(), start, end, false);

        boolean useBlock;

        if (entHit == null) {
            useBlock = true;
        } else if (blockHit == null) {
            useBlock = false;
        } else {
            if (start.distanceTo(entHit.getPos()) <= start.distanceTo(blockHit.getPos())) {
                useBlock = false;
            } else {
                useBlock = true;
            }
        }

        if (useBlock) {
            if (blockHit == null) return Optional.empty();
            
            BlockPos pos = blockHit.getBlockPos();
            Entity owner = (Entity) level.getBlockWorld().getBlockOwner(pos.getX(), pos.getY(), pos.getZ());
            return Optional.ofNullable(owner);
        } else {
            if (entHit == null) return Optional.empty();
            return Optional.ofNullable(manager.getOwner(entHit.getRenderEntity()));
        }
        
    }

    /**
     * Perform a raycast into the world and return the first Scaffold entity hit.
     * Proxy that uses Joml vectors instead of MC vectors.
     * 
     * @param start Raycast start point.
     * @param end   Raycast end point.
     * @return The Scaffold entity hit.
     */
    public static Optional<Entity> raycastScaffoldEntities(Vector3dc start, Vector3dc end) {
        return raycastScaffoldEntities(new Vec3d(start.x(), start.y(), start.z()), new Vec3d(end.x(), end.y(), end.z()));
    }

    /**
     * Find the intersection between a ray and a plane.
     * @param start Ray start point.
     * @param end A point along the ray.
     * @param point A point on the plane.
     * @param normal The plane's normal.
     * @param dest Put the resulting poing in this vector.
     * @return <code>dest</code>
     * @throws IllegalArgumentException If <code>start</code> and <code>end</code> are equal.
     * @throws ArithmeticException If the ray and the plane do not intersect.
     */
    public static Vector3d intersectRayPlane(Vector3dc start, Vector3dc end, Vector3dc point, Vector3dc normal,
            Vector3d dest) throws IllegalArgumentException, ArithmeticException {
        if (start.equals(end)) {
            throw new IllegalArgumentException("Start must not equal end.");
        }

        // Get ray direction. dest = dir
        dest.set(end);
        dest.sub(start);
        dest.div(dest.length());

        double t = Intersectiond.intersectRayPlane(start, dest, point, normal, .000001);

        if (t < 0) {
            normal = normal.mul(-1, new Vector3d());
            t = Intersectiond.intersectRayPlane(start, dest, point, normal, .000001);
            if (t < 0) {
                throw new ArithmeticException("Ray and plane do not intersect.");
            }
            
        }

        dest.mul(t);
        dest.add(start);

        return dest;
    }

    private static BlockHitResult raycastWorld(net.minecraft.entity.Entity entity, Vec3d startPos, Vec3d endPos, boolean includeFluids) {
        return entity.world.raycast(new RaycastContext(startPos, endPos, RaycastContext.ShapeType.OUTLINE,
                includeFluids ? RaycastContext.FluidHandling.ANY : RaycastContext.FluidHandling.NONE, entity));
    }

    /**
     * Perform a raycast into the currently loaded world.
     * @param startPos Raycast start position.
     * @param endPos Raycast end position.
     * @param includeFluids Whether to include fluids in the raycast.
     * @return The hit result.
     */
    public static BlockHitResult raycastWorld(Vec3d startPos, Vec3d endPos, boolean includeFluids) {
        MinecraftClient client = MinecraftClient.getInstance();
        return raycastWorld(client.getCameraEntity(), startPos, endPos, includeFluids);
    }
}
