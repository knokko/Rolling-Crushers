package nl.knokko.guis.buttons;

import java.awt.Color;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector2f;

import nl.knokko.guis.IGui;

public class GuiTypingButton extends GuiButton {
	
	protected String text;
	protected Color textColor;

	public GuiTypingButton(Vector2f position, Vector2f scale, Color backGround, Color textColor, String text) {
		super(position, scale, backGround, textColor, text);
		this.text = text;
		this.textColor = textColor;
	}

	@Override
	public void click(int x, int y, int button, IGui gui) {
		gui.setCurrentButton(this);
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
		else {
			text += c;
			texture.setText(text, textColor);
		}
	}
	
	public String getText(){
		return text;
	}
}
