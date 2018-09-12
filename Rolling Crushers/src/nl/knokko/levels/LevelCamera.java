package nl.knokko.levels;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

import nl.knokko.entity.category.ICamera;
import nl.knokko.entity.category.IUpdatingEntity;
import nl.knokko.utils.Maths;
import nl.knokko.utils.Options;
import nl.knokko.utils.physics.Position;
import nl.knokko.utils.physics.Rotation;

public class LevelCamera implements ICamera, IUpdatingEntity {
	
	private Position position;
	private Rotation rotation;
	
	private float speed;

	public LevelCamera(Position position) {
		this.position = position;
		this.rotation = new Rotation();
		this.speed = 0.03f;
	}

	@Override
	public Position getPosition() {
		return position;
	}

	@Override
	public float getSize() {
		return 0;
	}

	@Override
	public Rotation getRotation() {
		return rotation;
	}

	@Override
	public void update() {
		float prevSpeed = speed;
		if(Mouse.isButtonDown(0)){
			int dx = Mouse.getDX();
			int dy = -Mouse.getDY();
			rotation.increaseRotation(dy * Options.getMouseSensitivity(), dx * Options.getMouseSensitivity(), 0, false);
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_W)){
			Vector3f vector = Maths.getRotationVector(rotation);
			vector.scale(speed);
			position.move(vector.x, vector.y, vector.z);
			speed *= 1.01f;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_S)){
			Vector3f vector = Maths.getRotationVector(rotation);
			vector.scale(-speed);
			position.move(vector.x, vector.y, vector.z);
			speed *= 1.01f;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)){
			position.move(0, speed, 0);
			speed *= 1.01f;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)){
			position.move(0, -speed, 0);
			speed *= 1.01f;
		}
		if(speed == prevSpeed)
			speed = 0.03f;
	}

}
