package nl.knokko.levels;

import java.util.ArrayList;

import nl.knokko.entity.category.*;
import nl.knokko.guis.IGui;
import nl.knokko.render.main.MasterRenderer;
import nl.knokko.render.tasks.RenderTask;
import nl.knokko.space.Light;
import nl.knokko.space.Space;
import nl.knokko.utils.physics.Position;

import org.lwjgl.util.vector.Vector3f;

public class Level {
	
	private ArrayList<IUpdatingEntity> updatingEntities = new ArrayList<IUpdatingEntity>();
	private ArrayList<ICollidingEntity> collidingEntities = new ArrayList<ICollidingEntity>();
	private ArrayList<ISaveableEntity> entities = new ArrayList<ISaveableEntity>();
	
	private String name;
	private Vector3f gravity;
	private Light light;
	private Position startPoint;
	
	private LevelCamera camera;
	
	private MasterRenderer renderer;

	public Level(String name, Vector3f gravity, Position startPoint, Light light) {
		this.name = name;
		this.gravity = gravity;
		this.startPoint = startPoint;
		this.light = light;
		this.camera = new LevelCamera(new Position(startPoint.getX(), startPoint.getY(), startPoint.getZ() + 5));
		this.renderer = new MasterRenderer();
	}
	
	public Space createSpace(IGui finishGui){
		return new Space(entities, light, gravity, startPoint, finishGui);
	}
	
	public void update(){
		camera.update();
	}
	
	public void render(boolean stopAfter, RenderTask... extraTasks){
		renderer.render(light, camera, stopAfter, extraTasks);
	}
	
	public ArrayList<ISaveableEntity> getEntities(){
		return entities;
	}
	
	/**
	 * 
	 * @return the default gravity
	 */
	public Vector3f getGravity(){
		return gravity;
	}
	
	public Position getStart(){
		return startPoint;
	}
	
	public String getName(){
		return name;
	}
	
	public Light getLight(){
		return light;
	}
	
	public LevelCamera getCamera(){
		return camera;
	}
	
	public void setGravity(Vector3f gravity){
		this.gravity = gravity;
	}
	
	public void spawnEntity(Object entity){
		if(entity instanceof IRenderEntity)
			renderer.addEntity((IRenderEntity) entity);
		if(entity instanceof IUpdatingEntity)
			updatingEntities.add((IUpdatingEntity) entity);
		if(entity instanceof ICollidingEntity)
			collidingEntities.add((ICollidingEntity) entity);
		if(entity instanceof ISaveableEntity)
			entities.add((ISaveableEntity) entity);
	}
}
