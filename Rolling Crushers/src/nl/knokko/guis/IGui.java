package nl.knokko.guis;

import java.awt.Color;
import java.util.ArrayList;

import nl.knokko.guis.buttons.IButton;
import nl.knokko.guis.render.GuiRenderer;
import nl.knokko.guis.render.GuiTexture;
import nl.knokko.space.Space;

public interface IGui {
	
	ArrayList<GuiTexture> getGuiTextures();
	
	ArrayList<IButton> getButtons();
	
	IButton getCurrentButton();
	
	Color getBackGroundColor();
	
	Space getSpace();
	
	void update();
	
	void activate();
	
	void render(GuiRenderer renderer);
	
	void setCurrentButton(IButton button);
	
	void keyPressed(int key, char character);
	
	void keyReleased(int key, char character);
	
	boolean canClose();
	
	boolean renderGameWorld();
}
