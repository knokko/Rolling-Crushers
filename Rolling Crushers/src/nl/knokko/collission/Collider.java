package nl.knokko.collission;

import org.lwjgl.util.vector.Vector3f;

import nl.knokko.entity.category.ICollidingEntity;
import nl.knokko.utils.physics.Position;

public abstract class Collider {
	
	protected Position position;
	protected ICollidingEntity owner;

	public Collider(Position position, ICollidingEntity entity) {
		this.position = position;
		owner = entity;
	}
	
	public abstract float getMinX();
	
	public abstract float getMinY();
	
	public abstract float getMinZ();
	
	public abstract float getMaxX();
	
	public abstract float getMaxY();
	
	public abstract float getMaxZ();
	
	/**
	 * Checks for intersection with another collider.
	 * @param other The collider to check for intersection with.
	 * @return The normal vector where the other collider hit this collider, or null if no intersection occured.
	 */
	public abstract Vector3f findIntersect(Collider other);
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return True if the location (x,y,z) is inside this collider.
	 */
	public abstract boolean isInside(float x, float y, float z);
	
	public boolean couldIntersect(Collider other){
		return other.getMinX() <= getMaxX() && getMinX() <= other.getMaxX() && other.getMinY() <= getMaxY() && getMinY() <= other.getMaxY() && other.getMinZ() <= getMaxZ() && getMinZ() <= other.getMaxZ();
	}
	
	public ICollidingEntity getOwner(){
		return owner;
	}
	
	public Collider expandedBox(float x, float y, float z){
		float minX = getMinX() - position.getX();
		float minY = getMinY() - position.getY();
		float minZ = getMinZ() - position.getZ();
		float maxX = getMaxX() - position.getX();
		float maxY = getMaxY() - position.getY();
		float maxZ = getMaxZ() - position.getZ();
		if(x > 0)
			maxX += x;
		else
			minX += x;
		if(y > 0)
			maxY += y;
		else
			minY += y;
		if(z > 0)
			maxZ += z;
		else
			minZ += z;
		return new ColliderBox(position, owner, minX, minY, minZ, maxX, maxY, maxZ);
	}
}
