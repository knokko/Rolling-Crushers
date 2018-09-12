package nl.knokko.guis.buttons;

import nl.knokko.guis.IGui;
import nl.knokko.guis.render.GuiTexture;

public interface IButton {
	
	GuiTexture[] getTextures();
	
	boolean isHit(int x, int y, IGui gui);
	
	void click(int x, int y, int button, IGui gui);
	
	void mouseDown(int x, int y, int button, IGui gui);
	
	void keyPressed(int key, char character, IGui gui);
	
	void keyReleased(int key, char character, IGui gui);
	
	int clickCooldown(int x, int y, IGui gui);
}
