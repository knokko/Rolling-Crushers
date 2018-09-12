package nl.knokko.guis.levels;

import java.awt.Color;

import org.lwjgl.util.vector.Vector2f;

import nl.knokko.guis.Gui;
import nl.knokko.guis.IGui;
import nl.knokko.guis.buttons.GuiButton;
import nl.knokko.guis.buttons.GuiLinkButton;
import nl.knokko.guis.render.GuiDoubleTexture;
import nl.knokko.levels.LevelFile;
import nl.knokko.main.Game;
import nl.knokko.materials.Materials;
import nl.knokko.utils.Resources;

public class GuiLoadLevel extends Gui {

	public GuiLoadLevel(final LevelFile level) {
		addTexture(new GuiDoubleTexture(Resources.getFilledTexture(Color.BLUE, Materials.DEFAULT), Resources.getTextTexture("Level", Color.BLACK), new Vector2f(-0.35f, 0.8f), new Vector2f(0.3f, 0.1f)));
		addTexture(new GuiDoubleTexture(Resources.getFilledTexture(Color.BLUE, Materials.DEFAULT), Resources.getTextTexture(level.getName(), Color.BLACK), new Vector2f(0.35f, 0.8f), new Vector2f(0.3f, 0.1f)));
		addButton(new GuiLinkButton(new Vector2f(-0.5f, 0.4f), new Vector2f(0.3f, 0.1f), Color.BLUE, Color.BLACK, "Back", GuiLevelDesignSelect.class));
		addButton(new GuiButton(new Vector2f(-0.5f, 0), new Vector2f(0.3f, 0.1f), Color.RED, Color.BLACK, "Delete Level"){
			@Override
			public void click(int x, int y, int button, IGui gui) {
				level.delete();
				Game.setCurrentGUI(GuiLevelDesignSelect.getInstance());
			}
		});
		addButton(new GuiButton(new Vector2f(0.5f, 0), new Vector2f(0.3f, 0.1f), Color.BLUE, Color.BLACK, "Edit Level"){
			@Override
			public void click(int x, int y, int button, IGui gui) {
				Game.setCurrentGUI(GuiLevelDesigner.createInstance(level.load(), level.getFile()));
			}
		});
	}

	@Override
	public Color getBackGroundColor() {
		return Color.MAGENTA;
	}
	
	@Override
	public boolean renderGameWorld(){
		return false;
	}
}
