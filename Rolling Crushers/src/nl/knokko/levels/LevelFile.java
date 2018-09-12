package nl.knokko.levels;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import org.lwjgl.util.vector.Vector3f;

import nl.knokko.entities.finish.EntityFinishFlag;
import nl.knokko.entity.category.ISaveableEntity;
import nl.knokko.entity.physical.EntityBasher;
import nl.knokko.entity.physical.EntityPlayer;
import nl.knokko.entity.physical.EntitySphere;
import nl.knokko.entity.physical.EntityTile;
import nl.knokko.space.Light;
import nl.knokko.utils.physics.Position;

public class LevelFile {
	
	public static final HashMap<Short, Class<?>> ID_TO_CLASS = new HashMap<Short, Class<?>>();
	public static final HashMap<Class<?>, Short> CLASS_TO_ID = new HashMap<Class<?>, Short>();
	
	/**
	 * the first character is not used so far,
	 * the second character defines the root class,
	 * the third character defines what kind of physical entity this is,
	 * the fourth character defines the form,
	 * the fifth character is not used so far.
	 */
	static {
		put(EntitySphere.ID, EntitySphere.class);
		put(EntityPlayer.ID, EntityPlayer.class);
		put(EntityTile.Box.ID, EntityTile.Box.class);
		put(EntityTile.Sphere.ID, EntityTile.Sphere.class);
		put(EntityFinishFlag.ID, EntityFinishFlag.class);
		put(EntityTile.Cilinder.ID, EntityTile.Cilinder.class);
		put(EntityBasher.ID, EntityBasher.class);
	}
	
	private static final byte START_PROPERTIES = -128;
	private static final byte END_PROPERTIES = -127;
	
	private static final byte ID_GRAVITY = 0;
	private static final byte ID_LIGHT = 1;
	private static final byte ID_START = 2;
	
	private static final byte START_ENTITIES = -126;
	private static final byte END_ENTITIES = -125;
	
	private final File file;
	private final String name;

	public LevelFile(File file) {
		this.file = file;
		name = file.getName().substring(0, file.getName().length() - 6);
	}
	
	public File getFile(){
		return file;
	}
	
	public String getName(){
		return name;
	}

	public void save(Level level) {
		try {
			FileOutputStream stream = new FileOutputStream(file);
			saveProperties(stream, level);
			saveEntities(stream, level);
			stream.close();
		} catch (Exception e) {
			System.out.println("Can't save level " + level.getName() + " to file " + file + ":");
			System.out.println();
			e.printStackTrace();
		}
	}
	
	public Level load(){
		try {
			ArrayList<Byte> bytes = new ArrayList<Byte>();
			FileInputStream stream = new FileInputStream(file);
			while(true){
				int data = stream.read();
				if(data >= 0)
					bytes.add((byte) (data));
				else
					break;
			}
			stream.close();
			ByteBuffer buffer = ByteBuffer.allocate(bytes.size());
			for(Byte b : bytes)
				buffer.put(b);
			buffer.flip();
			byte state = 0;
			Vector3f gravity = new Vector3f();
			Light light = new Light(0, 0, 0, 1, 1, 1);
			Position start = new Position();
			ArrayList<Object> entities = new ArrayList<Object>();
			while(buffer.hasRemaining()){
				if(state == 0){
					byte next = buffer.get();
					if(next == START_PROPERTIES)
						state = 1;
					else if(next == START_ENTITIES)
						state = 2;
					else
						throw new RuntimeException("Unknown start ID: " + next);
				}
				else if(state == 1){
					byte next = buffer.get();
					if(next == ID_GRAVITY){
						gravity = new Vector3f(buffer.getFloat(), buffer.getFloat(), buffer.getFloat());
					}
					else if(next == ID_LIGHT){
						light.getPosition().teleport(buffer.getFloat(), buffer.getFloat(), buffer.getFloat());
						light.red = buffer.getFloat();
						light.green = buffer.getFloat();
						light.blue = buffer.getFloat();
					}
					else if(next == ID_START){
						start.teleport(buffer.getFloat(), buffer.getFloat(), buffer.getFloat());
					}
					else if(next == END_PROPERTIES)
						state = 0;
					else
						throw new RuntimeException("Unknown level property ID: " + next);
				}
				else if(state == 2){
					byte next = buffer.get();
					if(next == END_ENTITIES)
						state = 0;
					else {
						byte second = buffer.get();
						short id = ByteBuffer.wrap(new byte[]{next, second}).getShort();
						Class<?> entityClass = ID_TO_CLASS.get(id);
						entities.add(entityClass.getMethod("fromLevelData", ByteBuffer.class).invoke(null, buffer));
					}
				}
			}
			Level level = new Level(name, gravity, start, light);
			for(Object entity : entities)
				level.spawnEntity(entity);
			return level;
		} catch(Exception ex){
			System.out.println("Failed to load level from file " + file);
			System.out.println();
			ex.printStackTrace();
			return null;
		}
	}
	
	private void saveProperties(FileOutputStream stream, Level level) throws Exception {
		stream.write(START_PROPERTIES);
		ByteBuffer buffer = ByteBuffer.allocate(1 + 12 + 1 + 24 + 1 + 12);
		buffer.put(ID_GRAVITY);
		buffer.putFloat(level.getGravity().x);
		buffer.putFloat(level.getGravity().y);
		buffer.putFloat(level.getGravity().z);
		buffer.put(ID_LIGHT);
		buffer.putFloat(level.getLight().getPosition().getX());
		buffer.putFloat(level.getLight().getPosition().getY());
		buffer.putFloat(level.getLight().getPosition().getZ());
		buffer.putFloat(level.getLight().red);
		buffer.putFloat(level.getLight().green);
		buffer.putFloat(level.getLight().blue);
		buffer.put(ID_START);
		buffer.putFloat(level.getStart().getX());
		buffer.putFloat(level.getStart().getY());
		buffer.putFloat(level.getStart().getZ());
		stream.write(buffer.array());
		stream.write(END_PROPERTIES);
	}
	
	private void saveEntities(FileOutputStream stream, Level level) throws Exception {
		stream.write(START_ENTITIES);
		for(ISaveableEntity entity : level.getEntities()){
			stream.write(entity.storeLevelData().array());
		}
		stream.write(END_ENTITIES);
	}
	
	private static void put(short id, Class<?> clas){
		ID_TO_CLASS.put(id, clas);
		CLASS_TO_ID.put(clas, id);
	}

	public void delete() {
		file.delete();
	}
	
	/*
	public Level load(){
		
	}
	*/
}
