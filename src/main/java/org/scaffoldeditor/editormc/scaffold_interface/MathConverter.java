package org.scaffoldeditor.editormc.scaffold_interface;

import java.nio.FloatBuffer;

import org.joml.Matrix4d;
import org.joml.Matrix4fc;
import org.joml.Quaterniond;
import org.joml.Quaternionf;

import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;

public class MathConverter {

    public static Matrix4d convertMatrix(Matrix4f source) {
        FloatBuffer buf = FloatBuffer.allocate(16);
        source.writeRowMajor(buf);
        buf.rewind();

        return new Matrix4d(
            buf.get(), buf.get(), buf.get(), buf.get(),
            buf.get(), buf.get(), buf.get(), buf.get(),
            buf.get(), buf.get(), buf.get(), buf.get(),
            buf.get(), buf.get(), buf.get(), buf.get()
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
