package nl.knokko.render.tasks;

import nl.knokko.render.model.TexturedModel;

import org.lwjgl.util.vector.Matrix4f;

public abstract class RenderTask {
	
	public abstract TexturedModel getModel();
	
	public abstract Matrix4f getMatrix();
	
	public abstract boolean renderNow();
}
