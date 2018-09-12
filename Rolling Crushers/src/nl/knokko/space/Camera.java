package nl.knokko.space;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

import nl.knokko.entity.category.ICamera;
import nl.knokko.entity.category.IUpdatingEntity;
import nl.knokko.entity.physical.EntityPhysical;
import nl.knokko.utils.Options;
import nl.knokko.utils.physics.Position;
import nl.knokko.utils.physics.Rotation;

public class Camera implements ICamera, IUpdatingEntity {
	
	private Position position;
	private Rotation rotation;
	
	private Position target;
	private EntityPhysical entityTarget;
	
	private float distance = 5;

	public Camera(EntityPhysical entityTarget) {
		position = new Position(0, 0, 0);
		rotation = new Rotation(90, 0, 0);
		this.entityTarget = entityTarget;
		this.target = entityTarget.getPosition();
	}

	@Override
	public Rotation getRotation() {
		return rotation;
	}

	@Override
	public Position getPosition() {
		Vector3f vec = rotation.getRotationVector();
		position.teleport(target.getX() - vec.x * distance, target.getY() - vec.y * distance, target.getZ() - vec.z * distance);
		return position;
	}

	@Override
	public float getSize() {
		return 0;
	}

	@Override
	public void update() {
		int dx = Mouse.getDX();
		int dy = Mouse.getDY();
		if(dx != 0 || dy != 0)
			rotation.increaseRotation(-dy * 0.05f, dx * 0.05f, 0, false);
		if(Keyboard.isKeyDown(Keyboard.KEY_W))
			position.move(0, 0, -0.1f);
		if(Keyboard.isKeyDown(Keyboard.KEY_S))
			position.move(0, 0, 0.1f);
		if(Keyboard.isKeyDown(Keyboard.KEY_A))
			position.move(-0.1f, 0, 0);
		if(Keyboard.isKeyDown(Keyboard.KEY_D))
			position.move(0.1f, 0, 0);
		if(Keyboard.isKeyDown(Keyboard.KEY_SPACE))
			position.move(0, 0.1f, 0);
		if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
			position.move(0, -0.1f, 0);
		int wheel = Mouse.getDWheel();
		if(wheel != 0)
			distance -= wheel * Options.getZoomSensitivity();
		if(distance < 0.0f)
			distance = 0.0f;
		if(entityTarget != null && isFirstPerson()){
			Rotation r = entityTarget.getRotation();
			r.setRotation(r.getDegreeX(), rotation.getDegreeY() - 180, r.getDegreeZ(), false);
		}
	}
	
	public boolean isFirstPerson(){
		return distance < 1.0f;
	}
}
