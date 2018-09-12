package nl.knokko.main;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import nl.knokko.entity.physical.EntityPlayer;
import nl.knokko.guis.IGui;
import nl.knokko.guis.menu.GuiMainMenu;
import nl.knokko.guis.render.GuiRenderer;
import nl.knokko.input.Loader;
import nl.knokko.render.main.DisplayManager;
import nl.knokko.space.Space;
import nl.knokko.utils.Natives;

public final class Game {
	
	private static Space space;
	private static IGui gui;
	private static GuiRenderer guiRenderer;
	
	private static boolean isStopping;

	public static void main(String[] args) {
		prepareScreen();
		prepareGame();
		prepareGui();
		//prepareSpace();
		while(!shouldStop()){
			update();
			render();
		}
		close();
	}
	
	private static void prepareScreen(){
		Natives.prepare();
		DisplayManager.createDisplay();
	}
	
	private static void prepareGui(){
		setCurrentGUI(GuiMainMenu.getInstance());
	}
	
	private static void prepareGame(){
		guiRenderer = new GuiRenderer();
	}
	
	private static void close(){
		guiRenderer.cleanUp();
		if(space != null)
			space.cleanUp();
		Loader.cleanUp();
		DisplayManager.closeDisplay();
	}
	
	public static Space getSpace(){
		if(space == null && gui != null)
			return gui.getSpace();
		return space;
	}
	
	public static EntityPlayer getPlayer(){
		Space space = getSpace();
		return space != null ? space.getPlayer() : null;
	}
	
	public static IGui getCurrentGUI(){
		return gui;
	}
	
	public static void setCurrentGUI(IGui gui){
		Game.gui = gui;
		Mouse.setGrabbed(false);
	}
	
	public static void closeCurrentGUI(){
		gui = null;
		Mouse.setGrabbed(true);
	}
	
	public static void stop(){
		isStopping = true;
	}
	
	private static void update(){
		if(gui != null)
			gui.update();
		else {
			space.update();
		}
	}
	
	private static void render(){
		if(gui != null && gui.renderGameWorld())
			System.out.println("corrupt GUI = " + gui);
		if(gui == null || gui.renderGameWorld())
			space.render();
		if(gui != null){
			gui.render(guiRenderer);
		}
		DisplayManager.updateDisplay();
	}
	
	private static boolean shouldStop(){
		return Display.isCloseRequested() || isStopping;
	}
}
