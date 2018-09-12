package nl.knokko.space;

import java.util.ArrayList;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

import nl.knokko.collission.Collider;
import nl.knokko.entity.category.ICollidingEntity;
import nl.knokko.entity.category.IRenderEntity;
import nl.knokko.entity.category.ISaveableEntity;
import nl.knokko.entity.category.IUpdatingEntity;
import nl.knokko.entity.physical.*;
import nl.knokko.guis.IGui;
import nl.knokko.main.Game;
import nl.knokko.render.main.MasterRenderer;
import nl.knokko.render.model.ModelTexture;
import nl.knokko.render.model.TexturedModel;
import nl.knokko.utils.Facing;
import nl.knokko.utils.Resources;
import nl.knokko.utils.physics.Position;

public class Space {
	
	private ArrayList<IUpdatingEntity> updatingEntities = new ArrayList<IUpdatingEntity>();
	private ArrayList<ICollidingEntity> collidingEntities = new ArrayList<ICollidingEntity>();
	
	private Camera camera;
	private EntityPlayer player;
	
	private Light light;
	private Vector3f gravity;
	
	private MasterRenderer renderer;
	private IGui finishGui;
	
	public Space(ArrayList<ISaveableEntity> entities, Light light, Vector3f gravity, Position start, IGui finishGui){
		this.player = new EntityPlayer(start.getX(), start.getY(), start.getZ(), 180);
		this.camera = new Camera(player);
		this.light = light;
		this.gravity = gravity;
		this.renderer = new MasterRenderer();
		this.finishGui = finishGui;
		for(ISaveableEntity entity : entities)
			spawnEntity(entity);
		spawnEntity(player);
	}
	
	public void buildUp(){
		camera = new Camera(player);
		/*
		ModelTexture texture = Resources.getFilledTexture(new Color(150, 50, 50), Materials.IRON);
		addTile(texture, -3, -1, -100, 3, 1, 100);
		addTile(texture, -3, -1, -100, -2, 5, 100);
		addTile(texture, 2, -1, -100, 3, 5, 100);
		
		addTile(texture, -100, -1, -103, 100, 1, -97);
		addTile(texture, -3, -1, -10, 3, 5, -8);
		*/
	}
	
	protected void addTile(ModelTexture texture, float minX, float minY, float minZ, float maxX, float maxY, float maxZ){
		spawnEntity(new EntityTile.Box(new TexturedModel(Resources.createBox(minX, minY, minZ, maxX, maxY, maxZ), texture), Facing.UP, 0, 0, 0, minX, minY, minZ, maxX, maxY, maxZ, 1));
	}
	
	public void spawnEntity(Object entity){
		if(entity instanceof IRenderEntity)
			renderer.addEntity((IRenderEntity) entity);
		if(entity instanceof IUpdatingEntity)
			updatingEntities.add((IUpdatingEntity) entity);
		if(entity instanceof ICollidingEntity)
			collidingEntities.add((ICollidingEntity) entity);
	}
	
	public void update(){
		camera.update();
		for(int i = 0; i < updatingEntities.size(); i++)
			updatingEntities.get(i).update();
	}
	
	public void render(){
		renderer.render(getLight(), getCamera(), true);
	}
	
	public Camera getCamera(){
		return camera;
	}
	
	public Light getLight(){
		return light;
	}
	
	/**
	 * 
	 * @param collider
	 * @return An ArrayList that contains all colliders that could collide with the collider parameter.
	 */
	public ArrayList<Collider> getColliders(Collider collider){
		ArrayList<Collider> colliders = new ArrayList<Collider>();
		for(ICollidingEntity entity : collidingEntities){
			Collider col = entity.getCollider();
			//if(col.couldIntersect(collider))
			if(entity != collider.getOwner())
				colliders.add(col);
		}
		return colliders;
	}
	
	public Vector3f getGravity(Collider collider, float mass){
		return new Vector3f(gravity.x * mass, gravity.y * mass, gravity.z * mass);
	}
	
	public void cleanUp(){
		renderer.cleanUp();
	}

	public EntityPlayer getPlayer() {
		return player;
	}
	
	public void finishLevel(){
		Game.setCurrentGUI(finishGui);
		Mouse.setGrabbed(false);
		finishGui.activate();
	}
}
