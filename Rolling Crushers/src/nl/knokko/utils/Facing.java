package nl.knokko.utils;

import nl.knokko.utils.physics.Rotation;

import org.lwjgl.util.vector.Vector3f;

public enum Facing {
	
	UP(0, 1, 0, -90, 0),
	DOWN(0, -1, 0, 90, 0),
	NORTH(0, 0, -1, 0, 0),
	EAST(1, 0, 0, 0, 90),
	SOUTH(0, 0, 1, 0, 180),
	WEST(-1, 0, 0, 0, 270);
	
	public final float rotationX;
	public final float rotationY;
	
	public final float normalX;
	public final float normalY;
	public final float normalZ;
	
	public static Facing fromRotation(Rotation rotation){
		return fromRotation(rotation.getDegreePitch(), rotation.getDegreeYaw(), rotation.getDegreeRoll());
	}
	
	public static Facing fromRotation(float pitch, float yaw, float roll){
		if(pitch <= -45)
			return UP;
		if(pitch >= 45)
			return DOWN;
		if(yaw <= 45)
			return NORTH;
		if(yaw >= 45 && yaw <= 135)
			return EAST;
		if(yaw >= 135 && yaw <= 225)
			return SOUTH;
		if(yaw >= 225 && yaw <= 315)
			return WEST;
		if(yaw >= 315)
			return NORTH;
		throw new IllegalArgumentException("Can't get right Facing for pitch " + pitch + " and yaw " + yaw);
	}
	
	private Facing(float x, float y, float z, float pitch, float yaw){
		normalX = x;
		normalY = y;
		normalZ = z;
		rotationX = pitch;
		rotationY = yaw;
	}
	
	public Vector3f getVector(){
		return new Vector3f(normalX, normalY, normalZ);
	}
	
	public Rotation getRotation(){
		return new Rotation(rotationX, rotationY, 0);
	}
}
