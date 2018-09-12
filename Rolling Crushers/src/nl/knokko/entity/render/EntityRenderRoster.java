package nl.knokko.entity.render;

import java.awt.Color;

import nl.knokko.entity.category.IRenderEntity;
import nl.knokko.render.model.TexturedModel;
import nl.knokko.utils.Resources;
import nl.knokko.utils.physics.Position;
import nl.knokko.utils.physics.Rotation;

public class EntityRenderRoster implements IRenderEntity {
	
	public static final TexturedModel RED_MODEL = new TexturedModel(Resources.getSquadModel(), Resources.getRosterTexture(Color.RED));
	public static final TexturedModel GREEN_MODEL = new TexturedModel(Resources.getSquadModel(), Resources.getRosterTexture(Color.GREEN));
	public static final TexturedModel BLUE_MODEL = new TexturedModel(Resources.getSquadModel(), Resources.getRosterTexture(Color.BLUE));
	
	private final Position position;
	private final Rotation rotation;
	private final TexturedModel model;

	public EntityRenderRoster(Rotation rotation, TexturedModel model) {
		this.position = new Position(0, 0, 0);
		this.rotation = rotation;
		this.model = model;
	}
	
	public void updatePosition(Position target){
		float factor = getSize() / 32;
		int dx = (int) (target.getX() / factor);
		float x = dx * factor;
		int dy = (int) (target.getY() / factor);
		float y = dy * factor;
		int dz = (int) (target.getZ() / factor);
		float z = dz * factor;
		if(model == GREEN_MODEL)
			position.teleport(x, 0, z);
		if(model == BLUE_MODEL)
			position.teleport(0, y, z);
		if(model == RED_MODEL)
			position.teleport(x, y, 0);
	}

	@Override
	public Position getPosition() {
		return position;
	}

	@Override
	public float getSize() {
		return 32f;
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
