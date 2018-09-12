package nl.knokko.guis.levels;

import java.awt.Color;
import java.io.File;
import java.io.IOException;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import nl.knokko.guis.Gui;
import nl.knokko.guis.IGui;
import nl.knokko.guis.buttons.*;
import nl.knokko.guis.render.GuiDoubleTexture;
import nl.knokko.levels.Level;
import nl.knokko.main.Game;
import nl.knokko.materials.Materials;
import nl.knokko.space.Light;
import nl.knokko.utils.Resources;
import nl.knokko.utils.physics.Position;

public class GuiLevelCreation extends Gui {
	
	private GuiDoubleTexture lightTexture;
	private GuiDoubleTexture errorTexture = new GuiDoubleTexture(Resources.getFilledTexture(Color.MAGENTA, Materials.DEFAULT), Resources.getTextTexture(" ", Color.BLACK), new Vector2f(0f, -0.4f), new Vector2f(0.8f, 0.15f));
	
	private String name = "Level";
	private Vector3f gravity = new Vector3f(0, -9.8f, 0);
	private Light light = new Light(0, 10000, 0, 1, 1, 1);
	
	private float startX;
	private float startY;
	private float startZ;
	
	public static GuiLevelCreation getInstance(){
		return new GuiLevelCreation();
	}

	public GuiLevelCreation() {
		addButton(new GuiLinkButton(new Vector2f(-0.4f, -0.85f), new Vector2f(0.3f, 0.1f), Color.ORANGE, Color.BLACK, "Back", GuiLevelDesignSelect.class));
		addButton(new GuiButton(new Vector2f(0.4f, -0.85f), new Vector2f(0.3f, 0.1f), Color.GREEN, Color.BLACK, "Create"){

			@Override
			public void click(int x, int y, int button, IGui gui) {
				GuiLevelCreation glc = (GuiLevelCreation) gui;
				glc.name = ((GuiTypingButton) glc.buttons.get(2)).getText();
				float lightX = ((GuiTypingFloatButton) glc.buttons.get(6)).getValue();
				float lightY = ((GuiTypingFloatButton) glc.buttons.get(7)).getValue();
				float lightZ = ((GuiTypingFloatButton) glc.buttons.get(8)).getValue();
				if(lightX != lightX){
					glc.errorTexture.setText("Can't read the X position of the light!", Color.RED);
					return;
				}
				if(lightY != lightY){
					glc.errorTexture.setText("Can't read the Y position of the light!", Color.RED);
					return;
				}
				if(lightZ != lightZ){
					glc.errorTexture.setText("Can't read the Z position of the light!", Color.RED);
					return;
				}
				glc.light.getPosition().teleport(lightX, lightY, lightZ);
				float startX = ((GuiTypingFloatButton) glc.buttons.get(9)).getValue();
				float startY = ((GuiTypingFloatButton) glc.buttons.get(10)).getValue();
				float startZ = ((GuiTypingFloatButton) glc.buttons.get(11)).getValue();
				if(startX != startX){
					glc.errorTexture.setText("Can't read the X position of the start!", Color.RED);
					return;
				}
				if(startY != startY){
					glc.errorTexture.setText("Can't read the Y position of the start!", Color.RED);
					return;
				}
				if(startZ != startZ){
					glc.errorTexture.setText("Can't read the Z position of the start!", Color.RED);
					return;
				}
				glc.startX = startX;
				glc.startY = startY;
				glc.startZ = startZ;
				float gravX = ((GuiTypingFloatButton) glc.buttons.get(12)).getValue();
				float gravY = ((GuiTypingFloatButton) glc.buttons.get(13)).getValue();
				float gravZ = ((GuiTypingFloatButton) glc.buttons.get(14)).getValue();
				if(gravX != gravX){
					glc.errorTexture.setText("Can't read the X force of the gravity!", Color.RED);
					return;
				}
				if(gravY != gravY){
					glc.errorTexture.setText("Can't read the Y force of the gravity!", Color.RED);
					return;
				}
				if(gravZ != gravZ){
					glc.errorTexture.setText("Can't read the Z force of the gravity!", Color.RED);
					return;
				}
				glc.gravity.x = gravX;
				glc.gravity.y = gravY;
				glc.gravity.z = gravZ;
				new File("custom levels").mkdir();
				File file = new File("custom levels/" + glc.name + ".level");
				if(file.exists()){
					glc.errorTexture.setText("A level with this name already exists!", Color.RED);
					return;
				}
				try {
					if(!file.createNewFile()){
						glc.errorTexture.setText("A level with this name can't be created!", Color.RED);
						return;
					}
				} catch (IOException e) {
					glc.errorTexture.setText("A level with this name can't be created: " + e.getMessage(), Color.RED);
					System.out.println("Can't create file       custom levels/" + glc.name + ".level");
					e.printStackTrace();
					return;
				}
				file.delete();
				Level level = glc.create();
				Game.setCurrentGUI(GuiLevelDesigner.createInstance(level, file));
			}
			
		});
		addTexture(new GuiDoubleTexture(Resources.getFilledTexture(Color.green, Materials.DEFAULT), Resources.getTextTexture("Level Name:", Color.BLACK), new Vector2f(-0.3f, 0.85f), new Vector2f(0.3f, 0.1f)));
		addButton(new GuiTypingButton(new Vector2f(0.5f, 0.85f), new Vector2f(0.4f, 0.1f), Color.WHITE, Color.BLACK, "name"));
		lightTexture = new GuiDoubleTexture(Resources.getFilledTexture(Color.WHITE, Materials.DEFAULT), Resources.getTextTexture("Light Color", Color.BLACK), new Vector2f(-0.65f, 0.6f), new Vector2f(0.3f, 0.1f));
		addTexture(lightTexture);
		try {
			addButton(new GuiFieldSliceButton(new Vector2f(-0.1f, 0.6f), new Vector2f(0.2f, 0.1f), Color.WHITE, Color.RED, "red", 0, 1, light.getClass().getField("red"), light));
			addButton(new GuiFieldSliceButton(new Vector2f(0.325f, 0.6f), new Vector2f(0.2f, 0.1f), Color.WHITE, Color.GREEN, "green", 0, 1, light.getClass().getField("green"), light));
			addButton(new GuiFieldSliceButton(new Vector2f(0.75f, 0.6f), new Vector2f(0.2f, 0.1f), Color.WHITE, Color.BLUE, "blue", 0, 1, light.getClass().getField("blue"), light));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		addTexture(new GuiDoubleTexture(Resources.getFilledTexture(Color.WHITE, Materials.DEFAULT), Resources.getTextTexture("Light Location:", Color.BLACK), new Vector2f(-0.65f, 0.35f), new Vector2f(0.3f, 0.1f)));
		addButton(new GuiTypingFloatButton(new Vector2f(-0.1f, 0.35f), new Vector2f(0.2f, 0.1f), Color.WHITE, Color.BLACK, "0.0"));
		addButton(new GuiTypingFloatButton(new Vector2f(0.325f, 0.35f), new Vector2f(0.2f, 0.1f), Color.WHITE, Color.BLACK, "100000.0"));
		addButton(new GuiTypingFloatButton(new Vector2f(0.75f, 0.35f), new Vector2f(0.2f, 0.1f), Color.WHITE, Color.BLACK, "0.0"));
		addTexture(new GuiDoubleTexture(Resources.getFilledTexture(Color.ORANGE, Materials.DEFAULT), Resources.getTextTexture("Start Location", Color.BLACK), new Vector2f(-0.65f, 0.1f), new Vector2f(0.3f, 0.1f)));
		addButton(new GuiTypingFloatButton(new Vector2f(-0.1f, 0.1f), new Vector2f(0.2f, 0.1f), Color.orange, Color.BLACK, "0.0"));
		addButton(new GuiTypingFloatButton(new Vector2f(0.325f, 0.1f), new Vector2f(0.2f, 0.1f), Color.orange, Color.BLACK, "0.0"));
		addButton(new GuiTypingFloatButton(new Vector2f(0.75f, 0.1f), new Vector2f(0.2f, 0.1f), Color.orange, Color.BLACK, "0.0"));
		addTexture(new GuiDoubleTexture(Resources.getFilledTexture(Color.GRAY, Materials.DEFAULT), Resources.getTextTexture("Default Gravity:", Color.BLACK), new Vector2f(-0.65f, -0.15f), new Vector2f(0.3f, 0.1f)));
		addButton(new GuiTypingFloatButton(new Vector2f(-0.1f, -0.15f), new Vector2f(0.2f, 0.1f), Color.GRAY, Color.BLACK, "0.0"));
		addButton(new GuiTypingFloatButton(new Vector2f(0.325f, -0.15f), new Vector2f(0.2f, 0.1f), Color.GRAY, Color.BLACK, "-9.8"));
		addButton(new GuiTypingFloatButton(new Vector2f(0.75f, -0.15f), new Vector2f(0.2f, 0.1f), Color.GRAY, Color.BLACK, "0.0"));
		addTexture(errorTexture);
	}
	
	@Override
	public void update(){
		super.update();
		lightTexture.setText("Light Color", new Color(light.red, light.green, light.blue));
	}
	
	@Override
	public boolean renderGameWorld(){
		return false;
	}

	@Override
	public Color getBackGroundColor() {
		return Color.MAGENTA;
	}
	
	public Level create(){
		return new Level(name, gravity, new Position(startX, startY, startZ), light);
	}
}
