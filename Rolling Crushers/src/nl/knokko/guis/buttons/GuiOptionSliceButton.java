package nl.knokko.guis.buttons;

import java.awt.Color;
import java.lang.reflect.Method;

import nl.knokko.guis.IGui;
import nl.knokko.guis.render.GuiTexture;
import nl.knokko.materials.Materials;
import nl.knokko.utils.Resources;

import org.lwjgl.util.vector.Vector2f;

public class GuiOptionSliceButton extends GuiButton {
	
	protected Method getter;
	protected Method setter;
	
	protected GuiTexture marker;
	protected Object invokeTarget;
	
	protected float minValue;
	protected float deltaValue;
	protected float maxValue;
	
	public GuiOptionSliceButton(Vector2f position, Vector2f scale, Color backGround, Color textColor, String text, float minValue, float maxValue, Method setter, Method getter, Object invokeTarget){
		super(position, scale, backGround, textColor, text);
		this.minValue = minValue;
		this.maxValue = maxValue;
		deltaValue = maxValue - minValue;
		marker = new GuiTexture(Resources.getFilledTexture(textColor, Materials.DEFAULT), new Vector2f(position), new Vector2f(0.01f, scale.y));
		this.setter = setter;
		this.getter = getter;
		this.invokeTarget = invokeTarget;
	}

	@Override
	public void mouseDown(int x, int y, int button, IGui gui) {
		int ax = x - texture.getMinX();
		float mx = (float)ax / (texture.getMaxX() - texture.getMinX());
		float value = minValue + mx * deltaValue;
		try {
			setter.invoke(invokeTarget, value);
		} catch (Exception ex) {
			throw new RuntimeException("Can't use the setter " + setter + " for value " + value, ex);
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
			mx = (((Number)(getter.invoke(invokeTarget))).floatValue() - minValue) / (maxValue - minValue);
			marker.getPosition().x = texture.getPosition().x - texture.getScale().x + texture.getScale().x * mx * 2;
		} catch (Exception ex) {
			throw new RuntimeException("Can't use the getter " + getter, ex);
		} 
	}
}
