package nl.knokko.guis.levels;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;

import org.lwjgl.util.vector.Vector2f;

import nl.knokko.guis.Gui;
import nl.knokko.guis.IScrollGui;
import nl.knokko.guis.buttons.GuiLinkButton;
import nl.knokko.guis.buttons.LevelButton;
import nl.knokko.guis.menu.GuiMainMenu;
import nl.knokko.levels.LevelFile;
import nl.knokko.levels.Levels;

public class GuiLevelDesignSelect extends Gui implements IScrollGui {
	
	private static GuiLevelDesignSelect instance;
	
	private static File folder;
	
	private ArrayList<LevelFile> levels;
	
	public static GuiLevelDesignSelect getInstance(){
		if(instance == null)
			instance = new GuiLevelDesignSelect();
		else
			instance.refreshLevels();
		return instance;
	}

	private GuiLevelDesignSelect() {
		refreshLevels();
	}
	
	private void addDefaultButtons(){
		addButton(new GuiLinkButton(new Vector2f(-0.5f, 0.55f), new Vector2f(0.3f, 0.1f), Color.GREEN, Color.BLACK, "Back to main menu", GuiMainMenu.class));
		addButton(new GuiLinkButton(new Vector2f(-0.5f, 0.3f), new Vector2f(0.3f, 0.1f), Color.GREEN, Color.BLACK, "Create New Level", GuiLevelCreation.class));
	}
	
	private void addLevelButtons(){
		for(int i = 0; i < levels.size(); i++){
			addButton(new LevelButton(new Vector2f(0.65f, i * -0.25f + 0.3f), levels.get(i)));
		}
	}

	@Override
	public float getMaximumScroll() {
		return 1.0f;
	}
	
	@Override
	public boolean renderGameWorld(){
		return false;
	}

	@Override
	public Color getBackGroundColor() {
		return Color.ORANGE;
	}
	
	private void refreshLevels(){
		getFolder();
		buttons.clear();
		addDefaultButtons();
		File[] files = folder.listFiles();
		levels = Levels.getLevels(files);
		addLevelButtons();
	}
	
	private void getFolder(){
		if(folder == null){
			folder = new File("custom levels");
			if(!folder.exists())
				folder.mkdir();
		}
	}
}
