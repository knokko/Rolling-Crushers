package nl.knokko.collission;

import nl.knokko.entity.category.ICollidingEntity;
import nl.knokko.utils.physics.Position;

import org.lwjgl.util.vector.Vector3f;

public class ColliderSphere extends Collider {
	
	float radius;

	public ColliderSphere(Position position, ICollidingEntity entity, float radius) {
		super(position, entity);
		this.radius = radius;
	}
	
	@Override
	public String toString(){
		return "Sphere Collider:[" + position.getX() + "," + position.getY() + "," + position.getZ() + " radius " + radius + "]";
	}

	@Override
	public float getMinX() {
		return position.getX() - radius;
	}

	@Override
	public float getMinY() {
		return position.getY() - radius;
	}

	@Override
	public float getMinZ() {
		return position.getZ() - radius;
	}

	@Override
	public float getMaxX() {
		return position.getX() + radius;
	}

	@Override
	public float getMaxY() {
		return position.getY() + radius;
	}

	@Override
	public float getMaxZ() {
		return position.getZ() + radius;
	}

	@Override
	public Vector3f findIntersect(Collider other) {
		if(other instanceof ColliderSphere){
			ColliderSphere sp = (ColliderSphere) other;
			float distance = position.getDistance(sp.position);
			if(distance <= radius + sp.radius){
				Vector3f difference = position.getDifference(sp.position);
				difference.normalise();
				return difference;
			}
			return null;
		}
		if(other instanceof ColliderBox){
			ColliderBox box = (ColliderBox) other;
			float x = position.getX();
			if(x > box.getMaxX())
				x = box.getMaxX();
			else if(x < box.getMinX())
				x = box.getMinX();
			float y = position.getY();
			if(y > box.getMaxY())
				y = box.getMaxY();
			else if(y < box.getMinY())
				y = box.getMinY();
			float z = position.getZ();
			if(z > box.getMaxZ())
				z = box.getMaxZ();
			if(z < box.getMinZ())
				z = box.getMinZ();
			if(isInside(x, y, z)){
				Vector3f vector = position.getDifference(box.getCentre());
				vector.normalise();
				return vector;
			}
			return null;
		}
		throw new CollissionException(ColliderSphere.class, other.getClass());
	}
	
	@Override
	public boolean isInside(float x, float y, float z){
		return position.getDistance(x, y, z) <= radius;
	}
}
