package nl.knokko.render.main;

import java.util.ArrayList;
import java.util.HashMap;

import nl.knokko.entity.category.ICamera;
import nl.knokko.entity.category.IRenderEntity;
import nl.knokko.render.model.TexturedModel;
import nl.knokko.render.tasks.RenderTask;
import nl.knokko.render.tasks.RenderTasks;
import nl.knokko.shaders.StaticShader;
import nl.knokko.space.Light;

public class MasterRenderer {
	
	private StaticShader shader = new StaticShader();
	
	private HashMap<TexturedModel, ArrayList<RenderTask>> entities = new HashMap<TexturedModel, ArrayList<RenderTask>>();
	
	public MasterRenderer(){
		Renderer.start(shader);
	}
	
	public void render(Light sun, ICamera camera, boolean stopAfter, RenderTask... extraTasks){
		Renderer.prepare();
		shader.start();
		shader.loadLight(sun);
		shader.loadViewMatrix(camera);
		Renderer.render(entities);
		for(RenderTask task : extraTasks)
			Renderer.renderSingle(task);
		if(stopAfter)
			shader.stop();
	}
	
	public void addEntity(IRenderEntity entity){
		ArrayList<RenderTask> list = entities.get(entity.getModel());
		if(list != null)
			list.add(RenderTasks.getTask(entity));
		else {
			list = new ArrayList<RenderTask>();
			list.add(RenderTasks.getTask(entity));
			entities.put(entity.getModel(), list);
		}
	}
	
	public HashMap<TexturedModel, ArrayList<RenderTask>> getEntities(){
		return entities;
	}
	
	public void cleanUp(){
		shader.cleanUp();
	}
}
