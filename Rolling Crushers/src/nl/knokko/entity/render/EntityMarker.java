package nl.knokko.entity.render;

import nl.knokko.entity.category.IRenderEntity;
import nl.knokko.render.model.TexturedModel;
import nl.knokko.utils.physics.Position;
import nl.knokko.utils.physics.Rotation;

public class EntityMarker implements IRenderEntity {
	
	protected Position position;
	protected Rotation rotation;
	protected TexturedModel model;

	public EntityMarker(TexturedModel model, Position position, Rotation rotation) {
		this.model = model;
		this.position = position;
		this.rotation = rotation;
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
		return true;
	}

}
