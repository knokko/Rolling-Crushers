package nl.knokko.utils;

import static java.lang.Math.*;
import nl.knokko.entity.category.ICamera;
import nl.knokko.utils.physics.Position;
import nl.knokko.utils.physics.Rotation;

import org.lwjgl.util.vector.*;

public class Maths {
	
	public static final Matrix4f createTransformationMatrix(Vector2f translation, Vector2f scale){
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		Matrix4f.translate(translation, matrix, matrix);
		Matrix4f.scale(new Vector3f(scale.x, scale.y, 1f), matrix, matrix);
		return matrix;
	}

	public static final Matrix4f createTransformationMatrix(Position position, Rotation rotation, float size){
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		Matrix4f.translate(position.toVector(), matrix, matrix);
		Matrix4f.rotate(rotation.getRadianZ(), new Vector3f(0, 0, 1), matrix, matrix);
		Matrix4f.rotate(rotation.getRadianY(), new Vector3f(0, 1, 0), matrix, matrix);
		Matrix4f.rotate(rotation.getRadianX(), new Vector3f(1, 0, 0), matrix, matrix);
		Matrix4f.scale(new Vector3f(size, size, size), matrix, matrix);
		return matrix;
	}
	
	public static final Matrix4f createInvertedTransformationMatrix(Position position, Rotation rotation, float size){
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		Matrix4f.translate(position.toVector(), matrix, matrix);
		Matrix4f.rotate(rotation.getRadianZ(), new Vector3f(0, 0, 1), matrix, matrix);
		Matrix4f.rotate(-rotation.getRadianY(), new Vector3f(0, 1, 0), matrix, matrix);
		Matrix4f.rotate(rotation.getRadianX(), new Vector3f(1, 0, 0), matrix, matrix);
		Matrix4f.scale(new Vector3f(size, size, size), matrix, matrix);
		return matrix;
	}
	
	/*
	public static final Matrix4f createTransformationMatrix(Entity entity){
		return createTransformationMatrix(Vector3f.add(entity.getPosition(), entity.getBaseModelPosition(), null), entity.getRotationX(), entity.getRotationY(), entity.getRotationZ(), entity.getSize());
	}
	
	public static final Matrix4f createTransformationMatrix(SubModel model){
		return createTransformationMatrix(model.getRelativePosition(), model.rotationX, model.rotationY, model.rotationZ, 1);
	}
	*/
	
	public static final Matrix4f createViewMatrix(ICamera camera){
		Matrix4f view = new Matrix4f();
		view.setIdentity();
		Matrix4f.rotate(camera.getRotation().getRadianPitch(), new Vector3f(1, 0, 0), view, view);
		Matrix4f.rotate(camera.getRotation().getRadianYaw(), new Vector3f(0, 1, 0), view, view);
		Matrix4f.rotate(camera.getRotation().getRadianRoll(), new Vector3f(0, 0, 1), view, view);
		Position invert = camera.getPosition().invert();
		Matrix4f.translate(invert.toVector(), view, view);
		return view;
	}
	
	public static final Vector3f getRotationVector(Rotation rotation){
		float r = rotation.getRadianRoll();
		Matrix4f mat = createTransformationMatrix(new Position(), new Rotation(rotation.getDegreePitch(), rotation.getDegreeYaw(), 0, false), 1);
		Vector3f vector = new Vector3f(mat.m20, mat.m21, -mat.m22);
		return new Vector3f((float)(vector.x * cos(r) + vector.y * sin(r)), (float)(vector.y * cos(r) - vector.x * sin(r)), vector.z);
	}
	
	public static final Vector3f getRotationVector(float rotX, float rotY, float rotZ){
		float r = (float) toRadians(rotZ);
		Matrix4f mat = createTransformationMatrix(new Position(), new Rotation(rotX, rotY, 0, false), 1);
		Vector3f vector = new Vector3f(mat.m20, mat.m21, -mat.m22);
		return new Vector3f((float)(vector.x * cos(r) + vector.y * sin(r)), (float)(vector.y * cos(r) - vector.x * sin(r)), vector.z);
	}
	
	public static final double getDistance(Vector3f v1, Vector3f v2){
		return Math.sqrt((v1.x - v2.x) * (v1.x - v2.x) + (v1.y - v2.y) * (v1.y - v2.y) + (v1.z - v2.z) * (v1.z - v2.z));
	}
	
	public static final double getYaw(Vector3f vector){
		Vector3f vec = new Vector3f(vector);
		vec.y = 0;
		if(vec.length() == 0)
			return 0;
		vec.normalise();
		double angle = toDegrees(-acos(-vec.z));
		if(vec.x < 0)
			angle = -angle;
		if(angle < 0)
			angle += 360;
		if(angle > 360)
			angle -= 360;
		return angle;
	}

	public static final double getPitch(Vector3f normalized) {
		return toDegrees(-asin(normalized.y));
	}
	
	public static final float max(float... floats){
		float max = floats[0];
		for(float f : floats)
			if(f > max)
				max = f;
		return max;
	}
	
	public static final float min(float... floats){
		float min = floats[0];
		for(float f : floats)
			if(f < min)
				min = f;
		return min;
	}
	
	public static Vector3f pointAt(float angle, Vector3f center, Vector3f normal, float radius) {
		angle = (float) toRadians(angle);
        float xv = (float) Math.cos(angle);
        float yv = (float) Math.sin(angle);

        Vector3f v = findV(normal);
        Vector3f w = Vector3f.cross(v, normal, null);

        // Return center + r * (V * cos(a) + W * sin(a))
        Vector3f r1 = (Vector3f) v.scale(radius*xv);
        Vector3f r2 = (Vector3f) w.scale(radius*yv);

        return new Vector3f(center.x + r1.x + r2.x,
                         center.y + r1.y + r2.y,
                         center.z + r1.z + r2.z);
    }
	
	/*
	public static Vector3f getFrictionDirection(Vector3f motion, Vector3f normal){
		Vector3f fric = motion.normalise(null);
		fric.negate();
		float pitchN = (float) getPitch(normal);
		float pitchD = -90;
		float pitchA = pitchD - pitchN;
		float pitch = (float) (getPitch(fric) - pitchA);
	}
	*/
	
	public static Vector3f pointAt(Vector3f angle, Vector3f center, Vector3f normal, float radius){
		angle.normalise();
		float xv = angle.x;
		float yv = angle.z;
		Vector3f v = findV(normal);
        Vector3f w = Vector3f.cross(v, normal, null);

        // Return center + r * (V * cos(a) + W * sin(a))
        Vector3f r1 = (Vector3f) v.scale(radius*xv);
        Vector3f r2 = (Vector3f) w.scale(radius*yv);

        return new Vector3f(center.x + r1.x + r2.x,
                         center.y + r1.y + r2.y,
                         center.z + r1.z + r2.z);
	}

    private static Vector3f findV(Vector3f normal) {
        Vector3f vp = new Vector3f(0f, 0f, 0f);
        if (normal.x != 0 || normal.y != 0) {
            vp = new Vector3f(0f, 0f, 1f);
        } else if (normal.x != 0 || normal.z != 0) {
            vp = new Vector3f(0f, 1f, 0f);
        } else if (normal.y != 0 || normal.z != 0) {
            vp = new Vector3f(1f, 0f, 0f);
        } else {
            return null; // will cause an exception later.
        }

        Vector3f cp = Vector3f.cross(normal, vp, null);
        return cp.normalise(null);
    }
}
