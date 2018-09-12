package nl.knokko.guis.menu;

import java.awt.Color;

import org.lwjgl.util.vector.Vector2f;

import nl.knokko.guis.Gui;
import nl.knokko.guis.buttons.GuiLinkButton;
import nl.knokko.guis.levels.GuiLevelDesignSelect;

public class GuiMainMenu extends Gui {
	
	public static final Color BUTTON_COLOR = new Color(0, 0, 150);
	
	private static GuiMainMenu instance;
	
	public static GuiMainMenu getInstance(){
		if(instance == null)
			instance = new GuiMainMenu();
		return instance;
	}

	private GuiMainMenu() {
		addButton(GuiLinkButton.createCloseButton(new Vector2f(0, 0.5f), new Vector2f(0.4f, 0.15f), BUTTON_COLOR, Color.BLACK, "Play Game"));
		addButton(new GuiLinkButton(new Vector2f(0, 0), new Vector2f(0.4f, 0.15f), BUTTON_COLOR, Color.BLACK, "Level Designer", GuiLevelDesignSelect.class));
		addButton(GuiLinkButton.createQuitButton(new Vector2f(0, -0.5f), new Vector2f(0.4f, 0.15f), BUTTON_COLOR, Color.BLACK));
	}
	
	@Override
	public boolean renderGameWorld(){
		return false;
	}

	@Override
	public Color getBackGroundColor() {
		return Color.RED;
	}
}
