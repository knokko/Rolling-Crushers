package nl.knokko.guis.render;

import java.awt.Color;

import nl.knokko.materials.Materials;
import nl.knokko.render.model.ModelTexture;
import nl.knokko.utils.Resources;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;

public class GuiTexture {
	
	protected ModelTexture texture;
	
	private Vector2f position;
	private Vector2f scale;
	
	public GuiTexture(ModelTexture modelTexture, Vector2f position, Vector2f scale) {
		super();
		this.texture = modelTexture;
		this.position = position;
		this.scale = scale;
	}
	
	public ModelTexture[] getTexture(){
		return new ModelTexture[]{texture};
	}
	
	public Vector2f getPosition(){
		return position;
	}
	
	public Vector2f getScale(){
		return scale;
	}
	
	public void setBackGroundColor(Color color){
		texture = Resources.getFilledTexture(color, Materials.DEFAULT);
	}
	
	public void setText(String text, Color color) {
		texture = Resources.getTextTexture(text, color);
	}
	
	public int getMinX(){
		return Display.getWidth() / 2 + (int) (position.x * Display.getWidth() / 2 - scale.x * Display.getWidth() / 2);
	}
	
	public int getMinY(){
		return Display.getHeight() / 2 + (int) (position.y * Display.getHeight() / 2 - scale.y * Display.getHeight() / 2);
	}
	
	public int getMaxX(){
		return Display.getWidth() / 2 + (int)(position.x * Display.getWidth() / 2 + scale.x * Display.getWidth() / 2);
	}
	
	public int getMaxY(){
		return Display.getHeight() / 2 + (int)(position.y * Display.getHeight() / 2 + scale.y * Display.getHeight() / 2);
	}
}
