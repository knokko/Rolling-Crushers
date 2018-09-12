package nl.knokko.guis.render;

import java.awt.Color;
import java.util.ArrayList;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;

import nl.knokko.guis.IGui;
import nl.knokko.guis.IScrollGui;
import nl.knokko.guis.buttons.IButton;
import nl.knokko.input.Loader;
import nl.knokko.render.model.ModelTexture;
import nl.knokko.render.model.RawModel;
import nl.knokko.shaders.GuiShader;
import nl.knokko.utils.Maths;

public class GuiRenderer {
	
	private static final float[] QUADS = {-1,1, -1,-1, 1,1, 1,-1};
	
	private final RawModel quad;
	
	private GuiShader shader;

	public GuiRenderer() {
		quad = Loader.loadToVAO(QUADS);
		shader = new GuiShader();
	}
	
	public void render(ArrayList<GuiTexture> guis, ArrayList<IButton> buttons, IGui igui){
		shader.start();
		GL30.glBindVertexArray(quad.getID());
		GL20.glEnableVertexAttribArray(0);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		Color back = igui.getBackGroundColor();
		if(back != null){
			GL11.glClearColor(back.getRed() / 255f, back.getGreen() / 255f, back.getBlue() / 255f, 1);
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		}
		for(GuiTexture gui : guis)
			renderGuiTexture(gui, igui);
		for(IButton button : buttons)
			for(GuiTexture gui : button.getTextures())
				renderGuiTexture(gui, igui);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		shader.stop();
	}
	
	private void renderGuiTexture(GuiTexture gui, IGui igui){
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		Vector2f translation = new Vector2f(gui.getPosition());
		if(igui instanceof IScrollGui)
			translation.y += ((IScrollGui) igui).getCurrentScroll() * 2;
		Matrix4f matrix = Maths.createTransformationMatrix(translation, gui.getScale());
		shader.loadTransformation(matrix);
		for(ModelTexture texture : gui.getTexture()){
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getID());
			GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
		}
	}
	
	public void cleanUp(){
		shader.cleanUp();
	}
}
