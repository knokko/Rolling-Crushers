package nl.knokko.entity.physical;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import nl.knokko.render.main.DisplayManager;
import nl.knokko.utils.Maths;
import nl.knokko.utils.Resources;

public class EntityPlayer extends EntitySphere {
	
	public static final short ID = 01010;
	
	private byte jumpCooldown;

	public EntityPlayer(float x, float y, float z, float yaw) {
		super(Resources.getPlayerTexture(), x, y, z, 0, yaw, 0, 0.5f);
	}
	
	@Override
	public void update(){
		if(Keyboard.isKeyDown(Keyboard.KEY_W))
			motion.addForce((Vector3f) Maths.getRotationVector(0, rotation.getDegreeYaw(), rotation.getDegreeRoll()).scale(-4000));
		if(Keyboard.isKeyDown(Keyboard.KEY_S))
			motion.addForce((Vector3f) Maths.getRotationVector(0, rotation.getDegreeYaw(), rotation.getDegreeRoll()).scale(4000));
		if(Keyboard.isKeyDown(Keyboard.KEY_A))
			rotation.increaseRotation(0, -2.5f, 0, false);
		if(Keyboard.isKeyDown(Keyboard.KEY_D))
			rotation.increaseRotation(0, 2.5f, 0, false);
		if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)){
			if(normal != null && jumpCooldown <= 0){
				motion.addPunch(normal.normalise(null), 20000);
				jumpCooldown = DisplayManager.FPS_CAP / 2;
			}
		}
		if(jumpCooldown > 0)
			--jumpCooldown;
		super.update();
	}
}
