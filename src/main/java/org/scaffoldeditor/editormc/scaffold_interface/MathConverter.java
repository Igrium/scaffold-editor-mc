package org.scaffoldeditor.editormc.scaffold_interface;

import java.nio.FloatBuffer;

import org.joml.Matrix4d;
import org.joml.Matrix4fc;
import org.joml.Quaterniond;
import org.joml.Quaternionf;
import org.scaffoldeditor.editormc.engine.mixins.Matrix4fAccessor;

import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;

public class MathConverter {

    public static Matrix4d convertMatrix(Matrix4f source) {
        Matrix4fAccessor mat = (Matrix4fAccessor)(Object) source;
        return new Matrix4d(
            mat.getA00(), mat.getA01(), mat.getA02(), mat.getA03(),
            mat.getA10(), mat.getA11(), mat.getA12(), mat.getA13(),
            mat.getA20(), mat.getA21(), mat.getA22(), mat.getA23(),
            mat.getA30(), mat.getA31(), mat.getA32(), mat.getA33()
        );
    }

    public static Matrix4f convertMatrix(Matrix4fc source, Matrix4f dest) {
        FloatBuffer buf = FloatBuffer.allocate(16);
        source.get(buf);
        buf.rewind();
        dest.readColumnMajor(buf);
        return dest;
    }

    public static Quaternionf convertQuaternion(Quaternion source, Quaternionf dest) {
        dest.w = source.getW();
        dest.x = source.getX();
        dest.y = source.getY();
        dest.z = source.getZ();
        return dest;
    }

    public static Quaterniond convertQuaternion(Quaternion source, Quaterniond dest) {
        dest.w = source.getW();
        dest.x = source.getX();
        dest.y = source.getY();
        dest.z = source.getZ();
        return dest;
    }
}
