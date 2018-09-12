package nl.knokko.utils.physics;

import java.nio.ByteBuffer;

import nl.knokko.utils.Maths;

import org.lwjgl.util.vector.Vector3f;

public class Rotation {
	
	public static final float MAX_RADIAN = (float) (2 * Math.PI);
	public static final float MAX_DEGREES = 360;
	
	private float radX;
	private float radY;
	private float radZ;
	
	private float degX;
	private float degY;
	private float degZ;

	public Rotation(float startX, float startY, float startZ, boolean areRadians) {
		setRotation(startX, startY, startZ, areRadians);
	}
	
	public Rotation(float startX, float startY, float startZ){
		this(startX, startY, startZ, false);
	}
	
	public Rotation(){}
	
	private void setCorrect(){
		radX = correctRadian(radX);
		radY = correctRadian(radY);
		radZ = correctRadian(radZ);
		degX = correctDegrees(degX);
		degY = correctDegrees(degY);
		degZ = correctDegrees(degZ);
	}
	
	private float correctRadian(float angle){
		while(angle < 0)
			angle += MAX_RADIAN;
		while(angle > MAX_RADIAN)
			angle -= MAX_RADIAN;
		return angle;
	}
	
	private float correctDegrees(float angle){
		while(angle < 0)
			angle += MAX_DEGREES;
		while(angle > MAX_DEGREES)
			angle -= MAX_DEGREES;
		return angle;
	}
	
	public void setRotation(float x, float y, float z, boolean areRadians){
		if(areRadians){
			radX = x;
			radY = y;
			radZ = z;
			degX = toDegrees(radX);
			degY = toDegrees(radY);
			degZ = toDegrees(radZ);
		}
		else {
			degX = x;
			degY = y;
			degZ = z;
			radX = toRadians(degX);
			radY = toRadians(degY);
			radZ = toRadians(degZ);
		}
		setCorrect();
	}
	
	public void increaseRotation(float x, float y, float z, boolean radians){
		if(radians)
			setRotation(radX + x, radY + y, radZ + z, true);
		else
			setRotation(degX + x, degY + y, degZ + z, false);
	}
	
	public static float toDegrees(float radianAngle){
		return (float) Math.toDegrees(radianAngle);
	}
	
	public static float toRadians(float degreeAngle){
		return (float) Math.toRadians(degreeAngle);
	}
	
	public float getRadianX(){
		return radX;
	}
	
	public float getRadianY(){
		return radY;
	}
	
	public float getRadianZ(){
		return radZ;
	}
	
	public float getDegreeX(){
		return degX;
	}
	
	public float getDegreeY(){
		return degY;
	}
	
	public float getDegreeZ(){
		return degZ;
	}
	
	public float getRadianPitch(){
		return getRadianX();
	}
	
	public float getRadianYaw(){
		return getRadianY();
	}
	
	public float getRadianRoll(){
		return getRadianZ();
	}
	
	public float getDegreePitch(){
		return getDegreeX();
	}
	
	public float getDegreeYaw(){
		return getDegreeY();
	}
	
	public float getDegreeRoll(){
		return getDegreeZ();
	}
	
	public Vector3f getRotationVector(){
		return Maths.getRotationVector(this);
	}
	
	public Vector3f toVector(){
		return new Vector3f(radX, radY, radZ);
	}
	
	@Override
	public Rotation clone(){
		return new Rotation(degX, degY, degZ, false);
	}
	
	@Override
	public String toString(){
		return "Rotation:(" + degX + ", " + degY + ", " + degZ + ")";
	}
	
	@Override
	public boolean equals(Object other){
		if(other instanceof Rotation){
			Rotation r = (Rotation) other;
			return r.degX == degX && r.degY == degY && r.degZ == degZ;
		}
		return false;
	}
	
	public void storeData(ByteBuffer buffer){
		buffer.putFloat(degX);
		buffer.putFloat(degY);
		buffer.putFloat(degZ);
	}
}
