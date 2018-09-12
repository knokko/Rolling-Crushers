package nl.knokko.entity.physical;

import java.nio.ByteBuffer;

import nl.knokko.collission.Collider;
import nl.knokko.collission.ColliderBox;
import nl.knokko.collission.ColliderCilinder;
import nl.knokko.collission.ColliderSphere;
import nl.knokko.entity.category.ICollidingEntity;
import nl.knokko.entity.category.IRenderEntity;
import nl.knokko.entity.category.ISaveableEntity;
import nl.knokko.render.model.ModelTexture;
import nl.knokko.render.model.TexturedModel;
import nl.knokko.utils.Axis;
import nl.knokko.utils.Facing;
import nl.knokko.utils.Resources;
import nl.knokko.utils.physics.Position;
import nl.knokko.utils.physics.Rotation;

public abstract class EntityTile implements IRenderEntity, ICollidingEntity, ISaveableEntity {
	
	private float size;
	
	private TexturedModel model;
	private Position position;
	private Rotation rotation;
	private Collider collider;

	public EntityTile(TexturedModel model, float x, float y, float z, float pitch, float yaw, float roll, float size) {
		this.model = model;
		position = new Position(x, y, z);
		rotation = new Rotation(pitch, yaw, roll, false);
		this.size = size;
	}

	@Override
	public Position getPosition() {
		return position;
	}

	@Override
	public float getSize() {
		return size;
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
	public boolean canMove() {
		return true;
	}
	
	@Override
	public Collider getCollider(){
		if(collider == null)
			collider = createCollider(size);
		return collider;
	}
	
	/**
	 * Tiles can not be pushed away.
	 */
	@Override
	public void addPunch(float forceX, float forceY, float forceZ){}
	
	public abstract Collider createCollider(float size);
	
	public static class Box extends EntityTile {
		
		public static final short ID = 00000;
		
		private final float minX,minY,minZ,maxX,maxY,maxZ;
		
		public Box(TexturedModel model, Facing facing, float spawnX, float spawnY, float spawnZ, float minX, float minY, float minZ, final float maxX, final float maxY, final float maxZ, float size){
			super(model, spawnX, spawnY, spawnZ, facing.rotationX, facing.rotationY, 0, size);
			this.minX = minX;
			this.minY = minY;
			this.minZ = minZ;
			this.maxX = maxX;
			this.maxY = maxY;
			this.maxZ = maxZ;
		}
		
		@Override
		public Collider createCollider(float size) {
			return new ColliderBox(getPosition(), this, minX * size, minY * size, minZ * size, maxX * size, maxY * size, maxZ * size);
		}

		@Override
		public float getBounceFactor() {
			return getModel().getTexture().getMaterial().getBounce();
		}
		
		public static Box fromLevelData(ByteBuffer buffer){
			float size = buffer.getFloat();
			float x = buffer.getFloat();
			float y = buffer.getFloat();
			float z = buffer.getFloat();
			float pitch = buffer.getFloat();
			float yaw = buffer.getFloat();
			float roll = buffer.getFloat();
			byte[] textureCreation = new byte[buffer.getShort()];
			buffer.get(textureCreation);
			byte[] modelCreation = new byte[buffer.getShort()];
			buffer.get(modelCreation);
			float minX = buffer.getFloat();
			float minY = buffer.getFloat();
			float minZ = buffer.getFloat();
			float maxX = buffer.getFloat();
			float maxY = buffer.getFloat();
			float maxZ = buffer.getFloat();
			return new Box(new TexturedModel(Resources.modelFromID(modelCreation), Resources.textureFromID(textureCreation)), Facing.fromRotation(pitch, yaw, roll), x, y, z, minX, minY, minZ, maxX, maxY, maxZ, size);
		}

		@Override
		public ByteBuffer storeLevelData() {
			byte[] textureCreation = getModel().getTexture().getCreationID();
			byte[] modelCreation = getModel().getModel().getCreationID();
			ByteBuffer buffer = ByteBuffer.allocate(58 + textureCreation.length + modelCreation.length);
			buffer.putShort(ID);
			buffer.putFloat(getSize());
			getPosition().storeData(buffer);
			getRotation().storeData(buffer);
			buffer.putShort((short) textureCreation.length);
			buffer.put(textureCreation);
			buffer.putShort((short) modelCreation.length);
			buffer.put(modelCreation);
			buffer.putFloat(minX);
			buffer.putFloat(minY);
			buffer.putFloat(minZ);
			buffer.putFloat(maxX);
			buffer.putFloat(maxY);
			buffer.putFloat(maxZ);
			return buffer;
		}
	}
	
	public static class Sphere extends EntityTile {
		
		public static final short ID = 00010;
		
		public Sphere(ModelTexture texture, float spawnX, float spawnY, float spawnZ, float radius){
			super(new TexturedModel(Resources.getSphereModel(), texture), spawnX, spawnY, spawnZ, 0, 0, 0, radius);
		}
		
		@Override
		public Collider createCollider(float size) {
			return new ColliderSphere(getPosition(), this, size);
		}
		
		@Override
		public float getBounceFactor() {
			return getModel().getTexture().getMaterial().getBounce();
		}
		
		public static Sphere fromLevelData(ByteBuffer buffer){
			float x = buffer.getFloat();
			float y = buffer.getFloat();
			float z = buffer.getFloat();
			byte[] textureCreation = new byte[buffer.getShort()];
			buffer.get(textureCreation);
			float radius = buffer.getFloat();
			return new Sphere(Resources.textureFromID(textureCreation), x, y, z, radius);
		}

		@Override
		public ByteBuffer storeLevelData() {
			byte[] textureCreation = getModel().getTexture().getCreationID();
			ByteBuffer buffer = ByteBuffer.allocate(20 + textureCreation.length);
			buffer.putShort(ID);
			getPosition().storeData(buffer);
			buffer.putShort((short) textureCreation.length);
			buffer.put(textureCreation);
			buffer.putFloat(getSize());
			return buffer;
		}
	}
	
	public static class Cilinder extends EntityTile {
		
		public static final short ID = 00020;
		
		private Axis axis;
		
		private float min;
		private float max;
		private float radius;

		public Cilinder(ModelTexture texture, float x, float y, float z, float min, float max, float radius, Axis axis) {
			super(new TexturedModel(Resources.createCilinder(min, max, radius), texture), x, y, z, axis == Axis.Z ? 90 : 0, 0, axis == Axis.X ? 270 : 0, 1);
			this.axis = axis;
			this.min = min;
			this.max = max;
			this.radius = radius;
		}

		@Override
		public float getBounceFactor(){
			return getModel().getTexture().getMaterial().getBounce();
		}
		
		public static Cilinder fromLevelData(ByteBuffer buffer){
			float x = buffer.getFloat();
			float y = buffer.getFloat();
			float z = buffer.getFloat();
			float min = buffer.getFloat();
			float max = buffer.getFloat();
			float radius = buffer.getFloat();
			Axis axis = Axis.values()[buffer.get()];
			byte[] textureCreation = new byte[buffer.getShort()];
			buffer.get(textureCreation);
			return new Cilinder(Resources.textureFromID(textureCreation), x, y, z, min, max, radius, axis);
		}

		@Override
		public ByteBuffer storeLevelData(){
			byte[] textureCreation = getModel().getTexture().getCreationID();
			ByteBuffer buffer = ByteBuffer.allocate(29 + textureCreation.length);
			buffer.putShort(ID);
			getPosition().storeData(buffer);
			buffer.putFloat(min);
			buffer.putFloat(max);
			buffer.putFloat(radius);
			buffer.put((byte) axis.ordinal());
			buffer.putShort((short) textureCreation.length);
			buffer.put(textureCreation);
			return buffer;
		}

		@Override
		public Collider createCollider(float size){
			return new ColliderCilinder(getPosition(), this, axis, min, max, radius);
		}
		
	}
}
