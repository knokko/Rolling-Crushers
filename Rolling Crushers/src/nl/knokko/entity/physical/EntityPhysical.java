package nl.knokko.entity.physical;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector3f;

import nl.knokko.collission.*;
import nl.knokko.entity.category.*;
import nl.knokko.main.Game;
import nl.knokko.render.model.TexturedModel;
import nl.knokko.space.Space;
import nl.knokko.utils.physics.Motion;
import nl.knokko.utils.physics.Position;
import nl.knokko.utils.physics.Rotation;
import nl.knokko.utils.physics.SpeedUnit;

public abstract class EntityPhysical implements IRenderEntity, IUpdatingEntity, ICollidingEntity, ISaveableEntity {
	
	private static final float DELTA = 0.01f;
	private static final float MAX_FORCE = 2000f;
	
	protected final Position position;
	protected final Rotation rotation;
	protected final Motion motion;
	
	protected Collider collider;
	protected TexturedModel model;
	
	protected Space world;
	protected Vector3f normal;

	public EntityPhysical(TexturedModel model, float x, float y, float z, float pitch, float yaw, float roll) {
		position = new Position(x, y, z);
		rotation = new Rotation(pitch, yaw, roll);
		motion = new Motion();
		this.model = model;
		world = Game.getSpace();
	}
	
	protected void addNormal(Vector3f newNormal){
		if(normal == null)
			normal = newNormal;
		else
			Vector3f.add(normal, newNormal, normal);
	}
	
	protected void finishNormal(){
		/*
		if(normal != null && normal.length() > 0)
			normal.normalise();
			*/
	}
	
	@Override
	public void update(){
		if(world == null)
			world = Game.getSpace();
		if(motion.getMass() == 0)
			motion.setMass(getMass());
		motion.addForce(world.getGravity(getCollider(), getMass()));
		move(motion.getX(SpeedUnit.METER_PER_TICK), motion.getY(SpeedUnit.METER_PER_TICK), motion.getZ(SpeedUnit.METER_PER_TICK));
		finishNormal();
		float speed = motion.getSpeed(SpeedUnit.METER_PER_SECOND);
		if(speed != 0){
			float airFriction = speed * speed * getK();
			Vector3f frictionVector = motion.getSpeedVector(SpeedUnit.METER_PER_TICK);
			frictionVector.normalise();
			frictionVector.scale(-airFriction);
			motion.addForce(frictionVector);
		}
		if(normal != null){
			Vector3f force = motion.getSpeedVector(SpeedUnit.METER_PER_TICK);
			force.normalise();
			force.negate();
			force.scale(normal.length() * getC());
			motion.addFrictionForce(force);
		}
	}
	
	@Override
	public float getBounceFactor(){
		return model.getTexture().getMaterial().getBounce();
	}
	
	public void move(float x, float y, float z){
		normal = null;
		float targetX = position.getX() + x;
		float targetY = position.getY() + y;
		float targetZ = position.getZ() + z;
		Collider expanded = collider.expandedBox(x, y, z);
		ArrayList<Collider> colliders = world.getColliders(expanded);
		colliders.remove(collider);
		if(true) {
			float distance = (float) Math.sqrt(x * x + y * y + z * z);
			int steps = (int) (distance / DELTA);
			float deltaX = x / distance * DELTA;
			float deltaY = y / distance * DELTA;
			float deltaZ = z / distance * DELTA;
			for(int i = 0; i < steps; i++){
				position.move(deltaX, 0, 0);
				for(Collider col : colliders){
					Vector3f hit = col.findIntersect(collider);
					if(hit != null){
						addNormal(hit);
						position.move(-deltaX, 0, 0);
						applyIntersectForce(hit, col, 0);
						x = 0;
						y = targetY - position.getY();
						z = targetZ - position.getZ();
						distance = (float) Math.sqrt(y * y + z * z);
						if(distance == 0)
							return;
						steps = (int) (distance / DELTA);
						i = 0;
						deltaX = 0;
						targetX = position.getX();
						deltaY = y / distance * DELTA;
						deltaZ = z / distance * DELTA;
						break;
					}
				}
				position.move(0, deltaY, 0);
				for(Collider col : colliders){
					Vector3f hit = col.findIntersect(collider);
					if(hit != null){
						addNormal(hit);
						position.move(0, -deltaY, 0);
						applyIntersectForce(hit, col, 1);
						x = targetX - position.getX();
						y = 0;
						z = targetZ - position.getZ();
						targetY = position.getY();
						distance = (float) Math.sqrt(x * x + z * z);
						if(distance == 0)
							return;
						steps = (int) (distance / DELTA);
						i = 0;
						deltaX = x / distance * DELTA;
						deltaY = 0;
						targetY = position.getY();
						deltaZ = z / distance * DELTA;
						break;
					}
				}
				position.move(0, 0, deltaZ);
				for(Collider col : colliders){
					Vector3f hit = col.findIntersect(collider);
					if(hit != null){
						addNormal(hit);
						position.move(0, 0, -deltaZ);
						applyIntersectForce(hit, col, 2);
						x = targetX - position.getX();
						y = targetY - position.getY();
						z = 0;
						distance = (float) Math.sqrt(y * y + x * x);
						if(distance == 0)
							return;
						steps = (int) (distance / DELTA);
						i = 0;
						deltaX = x / distance * DELTA;
						deltaY = y / distance * DELTA;
						deltaZ = 0;
						targetZ = position.getZ();
						break;
					}
				}
				if(distance == 0)
					return;
			}
		}
	}
	
	private void applyIntersectForce(Vector3f hit, Collider col, int axis){
		float punch;
		float bounce = col.getOwner().getBounceFactor();
		if(axis == 0)
			punch = motion.getPunchX() * bounce;
		else if(axis == 1)
			punch = motion.getPunchY() * bounce;
		else
			punch = motion.getPunchZ() * bounce;
		punch = Math.min(punch, MAX_FORCE);
		hit.scale(punch);
		addPunch(hit);
		col.getOwner().addPunch(-hit.x, -hit.y, -hit.z);
	}

	@Override
	public Position getPosition() {
		return position;
	}

	@Override
	public float getSize() {
		return 1;
	}

	@Override
	public Rotation getRotation() {
		return rotation;
	}

	@Override
	public TexturedModel getModel() {
		return model;
	}
	
	@Override
	public boolean canMove(){
		return true;
	}
	
	@Override
	public Collider getCollider(){
		ensureCollider();
		return collider;
	}
	
	@Override
	public void addPunch(float punchX, float punchY, float punchZ){
		motion.addPunch(punchX, punchY, punchZ);
	}
	
	public void addPunch(Vector3f punch){
		addPunch(punch.x, punch.y, punch.z);
	}
	
	public float getMass(){
		return 10;
	}
	
	public boolean canFly(){
		return false;
	}
	
	protected int levelDataLength(){
		return 12;
	}
	
	protected void ensureCollider(){
		if(collider == null)
			collider = createCollider();
	}
	
	protected abstract Collider createCollider();
	
	/**
	 * @return The factor for the air friction
	 */
	protected abstract float getK();
	
	/**
	 * @return The factor for the roll friction
	 */
	protected abstract float getC();
}
