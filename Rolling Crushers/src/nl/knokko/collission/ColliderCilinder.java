package nl.knokko.collission;

import nl.knokko.entity.category.ICollidingEntity;
import nl.knokko.utils.Axis;
import nl.knokko.utils.Facing;
import nl.knokko.utils.physics.Position;

import org.lwjgl.util.vector.Vector3f;

public class ColliderCilinder extends Collider {
	
	float min;
	float max;
	float radius;
	
	Axis axis;

	public ColliderCilinder(Position position, ICollidingEntity entity, Axis axis, float min, float max, float radius) {
		super(position, entity);
		this.min = min;
		this.max = max;
		this.radius = radius;
		this.axis = axis;
	}

	@Override
	public float getMinX() {
		return axis == Axis.X ? position.getX() + min : position.getX() - radius;
	}

	@Override
	public float getMinY() {
		return axis == Axis.Y ? position.getY() + min : position.getY() - radius;
	}

	@Override
	public float getMinZ() {
		return axis == Axis.Z ? position.getZ() + min : position.getZ() - radius;
	}

	@Override
	public float getMaxX() {
		return axis == Axis.X ? position.getX() + max : position.getX() + radius;
	}

	@Override
	public float getMaxY() {
		return axis == Axis.Y ? position.getY() + max : position.getY() + radius;
	}

	@Override
	public float getMaxZ() {
		return axis == Axis.Z ? position.getZ() + max : position.getZ() + radius;
	}

	@Override
	public Vector3f findIntersect(Collider other) {
		if(other instanceof ColliderSphere){
			ColliderSphere sp = (ColliderSphere) other;
			float x = sp.position.getX();
			if(x > getMaxX())
				x = getMaxX();
			if(x < getMinX())
				x = getMinX();
			float y = sp.position.getY();
			if(y > getMaxY())
				y = getMaxY();
			if(y < getMinY())
				y = getMinY();
			float z = sp.position.getZ();
			if(z > getMaxZ())
				z = getMaxZ();
			if(z < getMinZ())
				z = getMinZ();
			if(sp.isInside(x, y, z)){
				switch(axis){
				case X: {
					if(Math.abs(sp.getMinX() - getMaxX()) < 0.05f)
						return Facing.EAST.getVector();
					if(Math.abs(getMinX() - sp.getMaxX()) < 0.05f)
						return Facing.WEST.getVector();
					float edgeDistance = 0;//use shorter radius when the sphere has almost passed this cilinder
					if(sp.position.getX() > getMaxX())
						edgeDistance = sp.position.getX() - getMaxX();
					else if(sp.position.getX() < getMinX())
						edgeDistance = getMinX() - sp.position.getX();
					double radius = this.radius;
					if(edgeDistance != 0)
						radius = Math.sqrt(radius * radius - edgeDistance * edgeDistance);
					double distance = Math.hypot(sp.position.getY() - position.getY(), sp.position.getZ() - position.getZ());
					if(distance <= (float) radius + sp.radius)
						return (Vector3f) new Vector3f(0, sp.position.getY() - position.getY(), sp.position.getZ() - position.getZ()).normalise();
					return null;
				}
				case Y: {
					if(Math.abs(getMinY() - sp.getMaxY()) < 0.05f)
						return Facing.DOWN.getVector();
					if(Math.abs(sp.getMinY() - getMaxY()) < 0.05f)
						return Facing.UP.getVector();
					float edgeDistance = 0;//use shorter radius when the sphere has almost passed this cilinder
					if(sp.position.getY() > getMaxY())
						edgeDistance = sp.position.getY() - getMaxY();
					else if(sp.position.getY() < getMinY())
						edgeDistance = getMinY() - sp.position.getY();
					double radius = this.radius;
					if(edgeDistance != 0)
						radius = Math.sqrt(radius * radius - edgeDistance * edgeDistance);
					double distance = Math.hypot(sp.position.getX() - position.getX(), sp.position.getZ() - position.getZ());
					if(distance <= radius + sp.radius)
						return (Vector3f) new Vector3f(x - position.getX(), 0, z - position.getZ()).normalise();
					return null;
				}
				case Z:{
					if(Math.abs(getMinZ() - sp.getMaxZ()) < 0.05f)
						return Facing.NORTH.getVector();
					if(Math.abs(sp.getMinZ() - getMaxZ()) < 0.05f)
						return Facing.SOUTH.getVector();
					float edgeDistance = 0;//use shorter radius when the sphere has almost passed this cilinder
					if(sp.position.getZ() > getMaxZ())
						edgeDistance = sp.position.getZ() - getMaxZ();
					else if(sp.position.getZ() < getMinZ())
						edgeDistance = getMinZ() - sp.position.getZ();
					double radius = this.radius;
					if(edgeDistance != 0)
						radius = Math.sqrt(radius * radius - edgeDistance * edgeDistance);
					double distance = Math.hypot(sp.position.getY() - position.getY(), sp.position.getX() - position.getX());
					if(distance <= radius + sp.radius)
						return (Vector3f) new Vector3f(x - position.getX(), y - position.getY(), 0).normalise();
					return null;
				}
				default:
					throw new RuntimeException("Unknown axis: " + axis);
				}
			}
			return null;
		}
		if(other instanceof ColliderBox){
			ColliderBox box = (ColliderBox) other;
			switch(axis){
			case X : {
				float y = position.getY();
				if(y > box.getMaxY())
					y = box.getMaxY();
				if(y < box.getMinY())
					y = box.getMinY();
				float z = position.getZ();
				if(z > box.getMaxZ())
					z = box.getMaxZ();
				if(z < box.getMinZ())
					z = box.getMinZ();
				if(Math.hypot(y - position.getY(), z - position.getZ()) <= radius){
					return (Vector3f) new Vector3f(0, y - position.getY(), z - position.getZ()).normalise();
				}
				return null;
			}
			case Y: {
				float x = position.getX();
				if(x > box.getMaxX())
					x = box.getMaxX();
				if(x < box.getMinX())
					x = box.getMinX();
				float z = position.getZ();
				if(z > box.getMaxZ())
					z = box.getMaxZ();
				if(z < box.getMinZ())
					z = box.getMinZ();
				if(Math.hypot(x - position.getX(), z - position.getZ()) <= radius){
					return (Vector3f) new Vector3f(x - position.getX(), 0, z - position.getZ()).normalise();
				}
				return null;
			}
			case Z: {
				float x = position.getX();
				if(x > box.getMaxX())
					x = box.getMaxX();
				if(x < box.getMinX())
					x = box.getMinX();
				float y = position.getY();
				if(y > box.getMaxY())
					y = box.getMaxY();
				if(y < box.getMinY())
					y = box.getMinY();
				if(Math.hypot(y - position.getY(), x - position.getX()) <= radius){
					return (Vector3f) new Vector3f(x - position.getX(), y - position.getY(), 0).normalise();
				}
				return null;
			}
			default:
				throw new RuntimeException("Unknown axis: " + axis);
			}
		}
		if(other instanceof ColliderCilinder){ //TODO I won't use this part for now...
			ColliderCilinder cil = (ColliderCilinder) other;
			switch(axis){
			case X : {
				switch(cil.axis){
				case X: {
					if(Math.hypot(position.getY() - cil.position.getY(), position.getZ() - cil.position.getZ()) <= radius + cil.radius)
						return (Vector3f) new Vector3f(0, cil.position.getY() - position.getY(), cil.position.getZ() - position.getZ()).normalise();
					return null;
				}
				case Y: {
					
				}
				case Z:
					break;
				default:
					throw new RuntimeException("Unknown axis: " + axis);
				}
			}
			case Y:
				break;
			case Z:
				break;
			default:
				throw new RuntimeException("Unknown axis: " + axis);
			}
		}
		throw new CollissionException(ColliderCilinder.class, other.getClass());
	}

	@Override
	public boolean isInside(float x, float y, float z) {
		switch(axis){
		case X : {
			if(x >= position.getX() + min && x <= position.getX() + max)
				return Math.hypot(position.getY() - y, position.getZ() - z) <= radius;
			return false;
		}
		case Y: {
			if(y >= position.getY() + min && y <= position.getY() + max)
				return Math.hypot(position.getX() - x, position.getZ() - z) <= radius;
			return false;
		}
		case Z: {
			if(z >= position.getZ() + min && z <= position.getZ() + max)
				return Math.hypot(position.getX() - x, position.getY() - y) <= radius;
			return false;
		}
		default:
			throw new RuntimeException("Invalid value for axis: " + axis);
		}
	}
}
