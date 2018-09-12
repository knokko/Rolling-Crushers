package nl.knokko.guis.buttons;

import java.awt.Color;
import java.lang.reflect.Field;

import nl.knokko.guis.IGui;
import nl.knokko.guis.render.GuiTexture;
import nl.knokko.materials.Materials;
import nl.knokko.utils.Resources;

import org.lwjgl.util.vector.Vector2f;

public class GuiFieldSliceButton extends GuiButton {
	
	protected Field field;
	
	protected GuiTexture marker;
	protected Object invokeTarget;
	
	protected float minValue;
	protected float deltaValue;
	protected float maxValue;
	
	public GuiFieldSliceButton(Vector2f position, Vector2f scale, Color backGround, Color textColor, String text, float minValue, float maxValue, Field field, Object invokeTarget){
		super(position, scale, backGround, textColor, text);
		this.minValue = minValue;
		this.maxValue = maxValue;
		deltaValue = maxValue - minValue;
		marker = new GuiTexture(Resources.getFilledTexture(textColor, Materials.DEFAULT), new Vector2f(position), new Vector2f(0.01f, scale.y));
		this.invokeTarget = invokeTarget;
		this.field = field;
	}

	@Override
	public void mouseDown(int x, int y, int button, IGui gui) {
		int ax = x - texture.getMinX();
		float mx = (float)ax / (texture.getMaxX() - texture.getMinX());
		float value = minValue + mx * deltaValue;
		try {
			field.setFloat(invokeTarget, value);
		} catch (Exception ex) {
			throw new RuntimeException("Can't set the field " + field + " to value " + value, ex);
		}
	}
	
	@Override
	public void click(int x, int y, int button, IGui gui) {
		mouseDown(x, y, button, gui);
	}
	
	@Override
	public GuiTexture[] getTextures(){
		refreshMarker();
		return new GuiTexture[]{texture, marker};
	}
	
	@Override
	public int clickCooldown(int x, int y, IGui gui){
		return 0;
	}
	
	protected void refreshMarker(){
		float mx;
		try {
			mx = (field.getFloat(invokeTarget) - minValue) / (maxValue - minValue);
			marker.getPosition().x = texture.getPosition().x - texture.getScale().x + texture.getScale().x * mx * 2;
		} catch (Exception ex) {
			throw new RuntimeException("Can't get the value of field " + field, ex);
		} 
	}

}
