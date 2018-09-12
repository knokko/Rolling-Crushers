package nl.knokko.utils.physics;

import java.nio.ByteBuffer;

import org.lwjgl.util.vector.Vector3f;

public class Position {
	
	private float x;
	private float y;
	private float z;

	public Position() {}
	
	public Position(float spawnX, float spawnY, float spawnZ){
		x = spawnX;
		y = spawnY;
		z = spawnZ;
	}
	
	@Override
	public Position clone(){
		return new Position(x, y, z);
	}
	
	@Override
	public boolean equals(Object other){
		if(other instanceof Position){
			Position p = (Position) other;
			return p.x == x && p.y == y && p.z == z;
		}
		return false;
	}
	
	@Override
	public String toString(){
		return "Position:(" + x + ", " + y + ", " + z + ")";
	}
	
	public Position invert(){
		return new Position(-x, -y, -z);
	}
	
	public float getX(){
		return x;
	}
	
	public float getY(){
		return y;
	}
	
	public float getZ(){
		return z;
	}
	
	public void teleport(float newX, float newY, float newZ){
		x = newX;
		y = newY;
		z = newZ;
	}
	
	public void move(float deltaX, float deltaY, float deltaZ){
		x += deltaX;
		y += deltaY;
		z += deltaZ;
	}
	
	public float getDistance(Position target){
		return getDistance(target.x, target.y, target.z);
	}
	
	public float getDistance(float targetX, float targetY, float targetZ){
		float disX = targetX - x;
		float disY = targetY - y;
		float disZ = targetZ - z;
		return (float) Math.sqrt(disX * disX + disY * disY + disZ * disZ);
	}
	
	/**
	 * 
	 * @param other The other position
	 * @return other minus this
	 */
	public Vector3f getDifference(Position other){
		return new Vector3f(other.x - x, other.y - y, other.z - z);
	}
	
	public Vector3f getDifference(Vector3f other){
		return getDifference(other.x, other.y, other.z);
	}
	
	public Vector3f getDifference(float x, float y, float z){
		return new Vector3f(x - this.x, y - this.y, z - this.z);
	}
	
	public Vector3f getMin(Position other){
		return new Vector3f(Math.min(x, other.x), Math.min(y, other.y), Math.min(z, other.z));
	}
	
	public Vector3f getMax(Position other){
		return new Vector3f(Math.max(x, other.x), Math.max(y, other.y), Math.max(z, other.z));
	}
	
	public Vector3f toVector(){
		return new Vector3f(x, y, z);
	}
	
	public void storeData(ByteBuffer buffer){
		buffer.putFloat(x);
		buffer.putFloat(y);
		buffer.putFloat(z);
	}
}
