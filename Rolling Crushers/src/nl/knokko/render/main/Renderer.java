package nl.knokko.render.main;

import java.util.ArrayList;
import java.util.HashMap;

import nl.knokko.render.model.*;
import nl.knokko.render.tasks.RenderTask;
import nl.knokko.shaders.StaticShader;

import org.lwjgl.opengl.*;
import org.lwjgl.util.vector.Matrix4f;

public class Renderer {
	
	private static final float FOV = 70;
	private static final float NEAR_PLANE = 0.1f;
	private static final float FAR_PLANE = 1000;
	
	private static Matrix4f projectionMatrix;
	private static StaticShader shader;
	
	public static void start(StaticShader shader){
		createProjectionMatrix();
		Renderer.shader = shader;
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
	}
	
	public static void prepare(){
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClearColor(0, 0, 1, 1);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}
	
	public static void render(HashMap<TexturedModel, ArrayList<RenderTask>> entities){
		for(TexturedModel model : entities.keySet()){
			ArrayList<RenderTask> batch = entities.get(model);
			prepareModel(model);
			for(RenderTask task : batch){
				if(task.renderNow()){
					prepareTask(task);
					GL11.glDrawElements(GL11.GL_TRIANGLES, model.getModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
				}
			}
			unbindModel();
		}
		GL11.glDisable(GL11.GL_BLEND);
	}
	
	public static void renderSingle(RenderTask task){
		prepareModel(task.getModel());
		prepareTask(task);
		GL11.glDrawElements(GL11.GL_TRIANGLES, task.getModel().getModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
		unbindModel();
		GL11.glDisable(GL11.GL_BLEND);
	}
	
	private static void prepareTask(RenderTask task){
		Matrix4f matrix = task.getMatrix();
		shader.loadTransformationMatrix(matrix);
	}
	
	private static void prepareModel(TexturedModel model){
		prepareModel(model.getModel());
		prepareTexture(model.getTexture());
	}
	
	private static void prepareModel(RawModel model){
		GL30.glBindVertexArray(model.getID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
	}
	
	private static void prepareTexture(ModelTexture texture){
		shader.loadShine(texture.getShineDamper(), texture.getReflectivity());
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getID());
	}
	
	private static void unbindModel(){
		GL30.glBindVertexArray(0);
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
	}
	
	private static void createProjectionMatrix(){
        float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
        float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))) * aspectRatio);
        float x_scale = y_scale / aspectRatio;
        float frustum_length = FAR_PLANE - NEAR_PLANE;
        projectionMatrix = new Matrix4f();
        projectionMatrix.m00 = x_scale;
        projectionMatrix.m11 = y_scale;
        projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
        projectionMatrix.m23 = -1;
        projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
        projectionMatrix.m33 = 0;
    }
}
