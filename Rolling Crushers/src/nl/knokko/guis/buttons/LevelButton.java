package nl.knokko.guis.buttons;

import java.awt.Color;

import nl.knokko.guis.IGui;
import nl.knokko.guis.levels.GuiLoadLevel;
import nl.knokko.levels.LevelFile;
import nl.knokko.main.Game;

import org.lwjgl.util.vector.Vector2f;

public class LevelButton extends GuiButton {
	
	private LevelFile level;

	public LevelButton(Vector2f position, LevelFile level) {
		super(position, new Vector2f(0.3f, 0.1f), Color.BLUE, Color.BLACK, level.getName());
		this.level = level;
	}

	@Override
	public void click(int x, int y, int button, IGui gui) {
		Game.setCurrentGUI(new GuiLoadLevel(level));
	}
	
	public LevelFile getLevel(){
		return level;
	}
}
