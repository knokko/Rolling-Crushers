package nl.knokko.guis.buttons;

import java.awt.Color;
import java.lang.reflect.Method;

import nl.knokko.guis.IGui;
import nl.knokko.main.Game;

import org.lwjgl.util.vector.Vector2f;

public class GuiLinkButton extends GuiButton {
	
	protected Method instance;

	public GuiLinkButton(Vector2f position, Vector2f scale, Color backGround, Color textColor, String text, Class<?> target) {
		super(position, scale, backGround, textColor, text);
		try {
			instance = target.getMethod("getInstance");
		} catch (Exception ex) {
			throw new RuntimeException("Can't find getInstance() method of class " + target.getName(), ex);
		} 
	}

	@Override
	public void click(int x, int y, int button, IGui gui) {
		try {
			Game.setCurrentGUI((IGui) instance.invoke(null));
		} catch (Exception ex) {
			throw new RuntimeException("Can't invoke method " + instance.getName(), ex);
		} 
	}
	
	public static GuiButton createCloseButton(Vector2f location, Vector2f scale, Color backGroundColor, Color textColor, String text){
		return new GuiButton(location, scale, backGroundColor, textColor, text){

			@Override
			public void click(int x, int y, int button, IGui gui) {
				Game.closeCurrentGUI();
			}
			
		};
	}
	
	public static GuiButton createCloseButton(Vector2f location, Vector2f scale, Color backGroundColor, Color textColor){
		return createCloseButton(location, scale, backGroundColor, textColor, "back to game");
	}
	
	public static GuiButton createQuitButton(Vector2f location, Vector2f scale, Color backGroundColor, Color textColor){
		return new GuiButton(location, scale, backGroundColor, textColor, "save and quit"){

			@Override
			public void click(int x, int y, int button, IGui gui) {
				Game.stop();
			}
			
		};
	}
}
