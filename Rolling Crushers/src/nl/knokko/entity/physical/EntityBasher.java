package nl.knokko.entity.physical;

import java.nio.ByteBuffer;

import org.lwjgl.util.vector.Vector3f;

import nl.knokko.main.Game;
import nl.knokko.render.model.ModelTexture;
import nl.knokko.utils.Maths;
import nl.knokko.utils.Resources;

public class EntityBasher extends EntitySphere {
	
	public static final short ID = 01210;
	
	public static EntityBasher fromLevelData(ByteBuffer buffer){
		float x = buffer.getFloat();
		float y = buffer.getFloat();
		float z = buffer.getFloat();
		short textureLength = buffer.getShort();
		byte[] creationID = new byte[textureLength];
		buffer.get(creationID);
		float radius = buffer.getFloat();
		ModelTexture texture = Resources.textureFromID(creationID);
		return new EntityBasher(texture, x, y, z, radius);
	}

	public EntityBasher(ModelTexture texture, float x, float y, float z, float radius) {
		super(texture, x, y, z, 0, 0, 0, radius);
	}
	
	@Override
	public void update(){
		Vector3f dif = Game.getPlayer().getPosition().getDifference(getPosition());
		dif.normalise();
		float yaw = (float) Maths.getYaw(dif);
		float pitch = (float) Maths.getPitch(dif);
		rotation.setRotation(pitch, yaw, 0, false);
		
		super.update();
	}
	
	@Override
	public ByteBuffer storeLevelData() {
		byte[] textureCreation = model.getTexture().getCreationID();
		ByteBuffer buffer = ByteBuffer.allocate(20 + textureCreation.length);
		buffer.putShort(ID);
		position.storeData(buffer);
		buffer.putShort((short) textureCreation.length);
		buffer.put(textureCreation);
		buffer.putFloat(radius);
		return buffer;
	}
}
