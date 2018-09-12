package nl.knokko.guis.buttons;

import java.awt.Color;

import nl.knokko.guis.IGui;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector2f;

public class GuiTypingFloatButton extends GuiTypingButton {

	public GuiTypingFloatButton(Vector2f position, Vector2f scale, Color backGround, Color textColor, String text) {
		super(position, scale, backGround, textColor, text);
	}
	
	@Override
	public void keyPressed(int key, char c, IGui gui){
		if(key == Keyboard.KEY_BACK){
			if(text.length() > 0)
				text = text.substring(0, text.length() - 1);
			texture.setText(text, textColor);
		}
		else if(key == Keyboard.KEY_ESCAPE)
			gui.setCurrentButton(null);
		else if(isNumber(c)){
			text += c;
			texture.setText(text, textColor);
		}
	}
	
	public float getValue(){
		try {
			float value = Float.parseFloat(text);
			return value;
		} catch(Exception ex){
			return Float.NaN;
		}
	}
	
	public static boolean isNumber(char c){
		return c == '0' || c == '1' || c == '2' || c == '3' || c == '4' || c == '5' || c == '5' || c == '6' || c == '9' || c =='.'; 
	}
}
