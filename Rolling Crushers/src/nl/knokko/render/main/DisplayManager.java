package nl.knokko.render.main;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.*;

public class DisplayManager {
	
	public static final int FPS_CAP = 60;

	public static void createDisplay(){
		try {
			ContextAttribs attribs = new ContextAttribs(3, 2).withForwardCompatible(true).withProfileCore(true);
			Display.setDisplayModeAndFullscreen(Display.getDesktopDisplayMode());
			Display.setFullscreen(true);
			Display.create(new PixelFormat(), attribs);
			Display.setTitle("Rolling Crushers");
			Display.setVSyncEnabled(true);
			GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
		} catch (LWJGLException ex) {
			ex.printStackTrace();
		}
	}
	
	public static void updateDisplay(){
		Display.sync(FPS_CAP);
		Display.update();
	}
	
	public static void closeDisplay(){
		Display.destroy();
	}

}
