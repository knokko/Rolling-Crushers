package nl.knokko.space;

import nl.knokko.utils.physics.Position;

import org.lwjgl.util.vector.Vector3f;

public class Light {
	
	public float red;
	public float green;
	public float blue;
	
	private Position position;

	public Light(float x, float y, float z, float red, float green, float blue) {
		position = new Position(x, y, z);
		this.red = red;
		this.green = green;
		this.blue = blue;
	}
	
	public Vector3f getColor(){
		return new Vector3f(red, green, blue);
	}
	
	public Vector3f getPositionVector(){
		return position.toVector();
	}
	
	public Position getPosition(){
		return position;
	}
}
