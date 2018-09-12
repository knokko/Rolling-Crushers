package nl.knokko.entity.category;

import nl.knokko.render.model.TexturedModel;

public interface IRenderEntity extends IPositionEntity, IRotatingEntity {
	
	TexturedModel getModel();
	
	boolean canMove();
}
