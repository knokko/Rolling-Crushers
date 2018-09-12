package nl.knokko.entity.physical;

import java.nio.ByteBuffer;

import nl.knokko.collission.Collider;
import nl.knokko.collission.ColliderSphere;
import nl.knokko.materials.Material;
import nl.knokko.render.model.ModelTexture;
import nl.knokko.render.model.TexturedModel;
import nl.knokko.utils.Resources;

public class EntitySphere extends EntityPhysical {
	
	public static final short ID = 01110;
	
	private static final float MASS_FACTOR = (float) (4 * Math.PI / 3);
	
	protected float radius;
	protected float k;
	
	protected Material material;
	
	public static EntitySphere fromLevelData(ByteBuffer buffer){
		float x = buffer.getFloat();
		float y = buffer.getFloat();
		float z = buffer.getFloat();
		float pitch = buffer.getFloat();
		float yaw = buffer.getFloat();
		float roll = buffer.getFloat();
		short textureLength = buffer.getShort();
		byte[] creationID = new byte[textureLength];
		buffer.get(creationID);
		float radius = buffer.getFloat();
		ModelTexture texture = Resources.textureFromID(creationID);
		return new EntitySphere(texture, x, y, z, pitch, yaw, roll, radius);
	}

	public EntitySphere(ModelTexture texture, float x, float y, float z, float pitch, float yaw, float roll, float radius) {
		super(new TexturedModel(Resources.getSphereModel(), texture), x, y, z, pitch, yaw, roll);
		this.radius = radius;
		this.k = (float) (0.5 * 0.47 * (Math.PI * radius * radius) * 1);
		material = texture.getMaterial();
	}
	
	@Override
	public void move(float dx, float dy, float dz){
		float x1 = position.getX();
		float z1 = position.getZ();
		super.move(dx, dy, dz);
		float distance = position.getDistance(x1, position.getY(), z1);
		//float rounds = distance / (2PI * radius);
		//float extraRot = (distance / 2PI*radius) * 2PI --> extraRot = 2PI(distance / 2PIradius) = 2PIdistance  / 2PIradius = distance / radius;
		rotation.increaseRotation(distance / radius, 0, 0, true);
	}

	@Override
	protected Collider createCollider() {
		return new ColliderSphere(position, this, radius);
	}
	
	@Override
	public float getSize(){
		return radius;
	}
	
	@Override
	public float getMass(){
		return material.getDensity() * MASS_FACTOR * radius * radius * radius;
	}
	
	@Override
	public float getK(){
		return k;
	}
	
	@Override
	public float getC(){
		return 0.57f;
		//TODO not sure this is the right one
	}

	@Override
	public ByteBuffer storeLevelData() {
		byte[] textureCreation = model.getTexture().getCreationID();
		ByteBuffer buffer = ByteBuffer.allocate(32 + textureCreation.length);
		buffer.putShort(ID);
		position.storeData(buffer);
		rotation.storeData(buffer);
		buffer.putShort((short) textureCreation.length);
		buffer.put(textureCreation);
		buffer.putFloat(radius);
		return buffer;
	}
}
