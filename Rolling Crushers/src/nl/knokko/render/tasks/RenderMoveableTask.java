package nl.knokko.render.tasks;

import nl.knokko.entity.category.IRenderEntity;
import nl.knokko.entity.physical.EntityPlayer;
import nl.knokko.main.Game;
import nl.knokko.render.model.TexturedModel;
import nl.knokko.utils.Maths;

import org.lwjgl.util.vector.Matrix4f;

public class RenderMoveableTask extends RenderTask {
	
	private IRenderEntity entity;

	protected RenderMoveableTask(IRenderEntity entity) {
		this.entity = entity;
	}
	
	public TexturedModel getModel(){
		return entity.getModel();
	}
	
	public Matrix4f getMatrix(){
		return Maths.createInvertedTransformationMatrix(entity.getPosition(), entity.getRotation(), entity.getSize());
	}
	
	public IRenderEntity getEntity(){
		return entity;
	}

	@Override
	public boolean renderNow() {
		if(Game.getSpace() != null && Game.getSpace().getCamera().isFirstPerson() && entity instanceof EntityPlayer)
			return false;
		return true;
	}
}
