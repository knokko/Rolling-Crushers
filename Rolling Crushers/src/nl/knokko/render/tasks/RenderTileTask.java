package nl.knokko.render.tasks;

import org.lwjgl.util.vector.Matrix4f;

import nl.knokko.entity.category.IRenderEntity;
import nl.knokko.utils.Maths;

public class RenderTileTask extends RenderMoveableTask {
	
	private Matrix4f matrix;

	RenderTileTask(IRenderEntity entity) {
		super(entity);
		matrix = Maths.createTransformationMatrix(entity.getPosition(), entity.getRotation(), entity.getSize());
	}
	
	public Matrix4f getMatrix(){
		return matrix;
	}
}
