package nl.knokko.collission;

import org.lwjgl.util.vector.Vector3f;

import nl.knokko.entity.category.ICollidingEntity;
import nl.knokko.utils.Facing;
import nl.knokko.utils.Maths;
import nl.knokko.utils.physics.Position;

public class ColliderBox extends Collider {
	
	float minX;
	float minY;
	float minZ;
	
	float maxX;
	float maxY;
	float maxZ;

	public ColliderBox(Position position, ICollidingEntity entity, float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
		super(position, entity);
		this.minX = minX;
		this.minY = minY;
		this.minZ = minZ;
		this.maxX = maxX;
		this.maxY = maxY;
		this.maxZ = maxZ;
	}
	
	@Override
	public String toString(){
		return "Box Collider:[" + getMinX() + "," + getMinY() + "," + getMinZ() + " - " + getMaxX() + "," + getMaxY() + "," + getMaxZ() + "]";
	}

	@Override
	public float getMinX() {
		return position.getX() + minX;
	}

	@Override
	public float getMinY() {
		return position.getY() + minY;
	}

	@Override
	public float getMinZ() {
		return position.getZ() + minZ;
	}

	@Override
	public float getMaxX() {
		return position.getX() + maxX;
	}

	@Override
	public float getMaxY() {
		return position.getY() + maxY;
	}

	@Override
	public float getMaxZ() {
		return position.getZ() + maxZ;
	}

	@Override
	public Vector3f findIntersect(Collider other) {
		if(other instanceof ColliderBox){
			ColliderBox box = (ColliderBox) other;
			if(getMinX() <= box.getMaxX() && box.getMinX() <= getMaxX() && getMinY() <= box.getMaxY() && box.getMinY() <= getMaxY() && getMinZ() <= box.getMaxZ() && box.getMinZ() <= getMaxZ()){
				float deltaAX = Math.abs(getMaxX() - box.getMinX());
				float deltaAY = Math.abs(getMaxY() - box.getMinY());
				float deltaAZ = Math.abs(getMaxZ() - box.getMinZ());
				float deltaIX = Math.abs(getMinX() - box.getMaxX());
				float deltaIY = Math.abs(getMinY() - box.getMaxY());
				float deltaIZ = Math.abs(getMinZ() - box.getMaxZ());
				float min = Maths.min(deltaAX, deltaAY, deltaAZ, deltaIX, deltaIY, deltaIZ);
				if(min == deltaAX)
					return Facing.EAST.getVector();
				if(min == deltaAY)
					return Facing.UP.getVector();
				if(min == deltaAZ)
					return Facing.SOUTH.getVector();
				if(min == deltaIX)
					return Facing.WEST.getVector();
				if(min == deltaIY)
					return Facing.DOWN.getVector();
				return Facing.NORTH.getVector();
			}
			return null;
		}
		if(other instanceof ColliderSphere){
			ColliderSphere sp = (ColliderSphere) other;
			float x = sp.position.getX();
			if(x > getMaxX())
				x = getMaxX();
			else if(x < getMinX())
				x = getMinX();
			float y = sp.position.getY();
			if(y > getMaxY())
				y = getMaxY();
			else if(y < getMinY())
				y = getMinY();
			float z = sp.position.getZ();
			if(z > getMaxZ())
				z = getMaxZ();
			if(z < getMinZ())
				z = getMinZ();
			if(sp.isInside(x, y, z)){
				float deltaAX = Math.abs(getMaxX() - x);
				float deltaAY = Math.abs(getMaxY() - y);
				float deltaAZ = Math.abs(getMaxZ() - z);
				float deltaIX = Math.abs(getMinX() - x);
				float deltaIY = Math.abs(getMinY() - y);
				float deltaIZ = Math.abs(getMinZ() - z);
				float min = Maths.min(deltaAX, deltaAY, deltaAZ, deltaIX, deltaIY, deltaIZ);
				if(min == deltaAX)
					return Facing.EAST.getVector();
				if(min == deltaAY)
					return Facing.UP.getVector();
				if(min == deltaAZ)
					return Facing.SOUTH.getVector();
				if(min == deltaIX)
					return Facing.WEST.getVector();
				if(min == deltaIY)
					return Facing.DOWN.getVector();
				return Facing.NORTH.getVector();
			}
			return null;
		}
		if(other instanceof ColliderCilinder){
			ColliderCilinder cil = (ColliderCilinder) other;
			switch(cil.axis){
			case X : {
				float y = cil.position.getY();
				if(y > getMaxY())
					y = getMaxY();
				if(y < getMinY())
					y = getMinY();
				float z = cil.position.getZ();
				if(z > getMaxZ())
					z = getMaxZ();
				if(z < getMinZ())
					z = getMinZ();
				if(Math.hypot(y - cil.position.getY(), z - cil.position.getZ()) <= cil.radius){
					return getVector(cil.getMinX(), y, z, cil.getMaxX(), y, z);
				}//TODO unfinished
				return null;
			}
			case Y: {
				float x = cil.position.getX();
				if(x > getMaxX())
					x = getMaxX();
				if(x < getMinX())
					x = getMinX();
				float z = cil.position.getZ();
				if(z > getMaxZ())
					z = getMaxZ();
				if(z < getMinZ())
					z = getMinZ();
				if(Math.hypot(x - cil.position.getX(), z - cil.position.getZ()) <= cil.radius){
					return getVector(x, cil.getMinY(), z, x, cil.getMaxY(), z);
				}
				return null;
			}
			case Z: {
				float x = cil.position.getX();
				if(x > getMaxX())
					x = getMaxX();
				if(x < getMinX())
					x = getMinX();
				float y = cil.position.getY();
				if(y > getMaxY())
					y = getMaxY();
				if(y < getMinY())
					y = getMinY();
				if(Math.hypot(y - cil.position.getY(), x - cil.position.getX()) <= cil.radius){
					return getVector(x, y, cil.getMinZ(), x, y, cil.getMaxZ());
				}
				return null;
			}
		}
		}
		throw new CollissionException(ColliderBox.class, other.getClass());
	}
			
	private Vector3f getVector(float minX, float minY, float minZ, float maxX, float maxY, float maxZ){
		float deltaAX = Math.abs(getMaxX() - minX);
		float deltaAY = Math.abs(getMaxY() - minY);
		float deltaAZ = Math.abs(getMaxZ() - minZ);
		float deltaIX = Math.abs(getMinX() - maxX);
		float deltaIY = Math.abs(getMinY() - maxY);
		float deltaIZ = Math.abs(getMinZ() - maxZ);
		float min = Maths.min(deltaAX, deltaAY, deltaAZ, deltaIX, deltaIY, deltaIZ);
		if(min == deltaAX)
			return Facing.EAST.getVector();
		if(min == deltaAY)
			return Facing.UP.getVector();
		if(min == deltaAZ)
			return Facing.SOUTH.getVector();
		if(min == deltaIX)
			return Facing.WEST.getVector();
		if(min == deltaIY)
			return Facing.DOWN.getVector();
		return Facing.NORTH.getVector();
	}
	
	@Override
	public boolean isInside(float x, float y, float z){
		return x >= getMinX() && x <= getMaxX() && y >= getMinY() && y <= getMaxY() && z >= getMinZ() && z <= getMaxZ();
	}
	
	protected Vector3f getCentre(){
		return new Vector3f(position.getX() + (minX + maxX) / 2, position.getY() + (minY + maxY) / 2, position.getZ() + (minZ + maxZ) / 2);
	}
}
