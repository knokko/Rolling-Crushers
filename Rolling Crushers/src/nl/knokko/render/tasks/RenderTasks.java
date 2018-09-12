package nl.knokko.render.tasks;

import nl.knokko.entity.category.IRenderEntity;

public class RenderTasks {
	
	public static RenderMoveableTask getTask(IRenderEntity entity){
		if(entity.canMove())
			return new RenderMoveableTask(entity);
		else
			return new RenderTileTask(entity);
	}
}
