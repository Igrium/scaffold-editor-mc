package org.scaffoldeditor.editormc.transformations;

import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.scaffoldeditor.editormc.scaffold_interface.MathConverter;
import org.scaffoldeditor.editormc.ui.Viewport;
import org.scaffoldeditor.editormc.util.RaycastUtils;

import net.minecraft.client.render.Camera;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;

/**
 * Interprets 2d mouse movements as a 3d transformation.
 * 
 * @author Igrium
 */
public class Translation {
    public static record Lock(boolean x, boolean y, boolean z) {
        public static Lock fromString(String str) {
            str = str.toLowerCase();
            return new Lock(str.contains("x"), str.contains("y"), str.contains("z"));
        }
    };

    public final Viewport viewport;
    public final Camera camera;
    private boolean castMode = false;
    private Lock lock = new Lock(true, true, true);
    
    private final Vector3dc startPos;
    private Vector3d translationPlaneVector = new Vector3d();

    /**
     * Begin a translation.
     * @param viewport Viewport to use.
     * @param camera Camera to cast rays from.
     * @param startPos Initial position of the transform.
     */
    public Translation(Viewport viewport, Camera camera, Vector3dc startPos) {
        this.viewport = viewport;
        this.startPos = startPos;
        this.camera = camera;
    }

    /**
     * Get the three dimensional transform for a mouse position.
     * @param mouseX Mouse X.
     * @param mouseY Mouse Y.
     * @return World-space transform.
     */
    public Vector3d getTranslation(int mouseX, int mouseY) {
        calcTranslationPlane(camera, lock, translationPlaneVector);
        
        Vec3d cameraPos = camera.getPos();
        Vec3d raycastEnd = RaycastUtils.raycastViewport(camera, mouseX, mouseY, viewport.getWidth(), viewport.getHeight());
        Vector3d end = new Vector3d(raycastEnd.getX(), raycastEnd.getY(), raycastEnd.getZ());

        Vector3d endPoint = null;
        boolean localCastMode = castMode;

        if (localCastMode) {
            BlockHitResult result = RaycastUtils.raycastWorld(cameraPos, raycastEnd, false);
            if (result.getType() == HitResult.Type.MISS) {
                localCastMode = false;
            } else {
                endPoint = new Vector3d(result.getPos().x, result.getPos().y, result.getPos().z);
            }
        } 
        
        if (!localCastMode) {
            try {
                endPoint = RaycastUtils.intersectRayPlane(
                    new Vector3d(cameraPos.getX(), cameraPos.getY(), cameraPos.getZ()), end, startPos,
                    translationPlaneVector, new Vector3d());
            } catch (ArithmeticException e) {
                endPoint = new Vector3d();
            }

        }
        
        if (!lock.x()) {
            endPoint.x = startPos.x();
        }
        if (!lock.y()) {
            endPoint.y = startPos.y();
        }
        if (!lock.z()) {
            endPoint.z = startPos.z();
        }

        return endPoint;
    }

    public Lock getLock() {
        return lock;
    }

    public void setLock(String lock) {
        setLock(Lock.fromString(lock));
    }

    public void setLock(Lock lock) {
        this.lock = lock;
        translationPlaneVector = calcTranslationPlane(camera, lock, translationPlaneVector);
    }

    public Vector3dc getStartPos() {
        return startPos;
    }

    public void setCastMode(boolean castMode) {
        this.castMode = castMode;
    }

    public boolean isCastMode() {
        return castMode;
    }

    /**
     * Calculate the normal vector of the translation plane.
     */
    private Vector3d calcTranslationPlane(Camera camera, Lock lock, Vector3d dest) {
        if (!lock.x() && !lock.y() && !lock.z()) {
            throw new IllegalArgumentException("Translation plane cannot be generated with all axes locked.");
        }

        Vector3d normal = dest;

        normal.set(0, 0, 1);
        normal.rotate(MathConverter.convertQuaternion(camera.getRotation(), new Quaterniond()));

        normal.div(normal.length());

        if (lock.x() && lock.y() && lock.z()) {

        } else if (lock.x() && lock.y()) {
            normal.set(0, 0, 1);
        } else if (lock.y() && lock.z()) {
            normal.set(1, 0, 0);
        } else if (lock.x() && lock.z()) {
            normal.set(0, 1, 0);
        } else if (lock.x()) {
            normal.x = 0;
        } else if (lock.y()) {
            normal.y = 0;
        } else if (lock.z()) {
            normal.z = 0;
        }

        normal.div(normal.length());
        
        return dest;
    }
}
