package nl.knokko.entities.finish;

import java.awt.Color;
import java.nio.ByteBuffer;

import nl.knokko.collission.ColliderBox;
import nl.knokko.entity.category.*;
import nl.knokko.entity.physical.EntityPlayer;
import nl.knokko.main.Game;
import nl.knokko.render.model.TexturedModel;
import nl.knokko.utils.Resources;
import nl.knokko.utils.physics.Position;
import nl.knokko.utils.physics.Rotation;

public class EntityFinishFlag implements IUpdatingEntity, IRenderEntity, ISaveableEntity {
	
	public static final short ID = 02020;
	
	private Position position;
	private Rotation rotation;
	
	private TexturedModel model;
	private ColliderBox collider;

	public EntityFinishFlag(float x, float y, float z, float yaw) {
		position = new Position(x, y, z);
		rotation = new Rotation(0, yaw, 0, false);
		model = new TexturedModel(Resources.getFlagModel(2f, 0.1f, 0.7f, 0.2f, 0.4f), Resources.getFlagTexture(new Color(200, 100, 0), Color.YELLOW));
		collider = new ColliderBox(position, null, -0.1f, 0f, -0.1f, 0.1f, 2f, 0.1f);
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
	public boolean canMove() {
		return false;
	}

	@Override
	public void update() {
		EntityPlayer player = Game.getPlayer();
		if(player != null && player.getCollider().findIntersect(collider) != null)
			Game.getSpace().finishLevel();
	}

	@Override
	public ByteBuffer storeLevelData() {
		ByteBuffer buffer = ByteBuffer.allocate(18);
		buffer.putShort(ID);
		buffer.putFloat(position.getX());
		buffer.putFloat(position.getY());
		buffer.putFloat(position.getZ());
		buffer.putFloat(rotation.getDegreeYaw());
		return buffer;
	}
	
	public static EntityFinishFlag fromLevelData(ByteBuffer buffer){
		return new EntityFinishFlag(buffer.getFloat(), buffer.getFloat(), buffer.getFloat(), buffer.getFloat());
	}
}
