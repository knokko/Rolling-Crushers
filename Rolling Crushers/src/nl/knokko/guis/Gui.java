package nl.knokko.guis;

import java.awt.Color;
import java.util.ArrayList;

import nl.knokko.guis.buttons.IButton;
import nl.knokko.guis.render.GuiRenderer;
import nl.knokko.guis.render.GuiTexture;
import nl.knokko.space.Space;
import nl.knokko.utils.Options;
import nl.knokko.utils.Resources;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector2f;

public abstract class Gui implements IGui {
	
	protected ArrayList<GuiTexture> textures = new ArrayList<GuiTexture>();
	protected ArrayList<IButton> buttons = new ArrayList<IButton>();
	
	protected IButton currentButton;
	protected GuiTexture markTexture;
	
	protected float currentScroll;
	
	public Gui(){
		markTexture = new GuiTexture(Resources.getBorderTexture(Color.RED), new Vector2f(), new Vector2f());
		textures.add(markTexture);
	}
	
	public ArrayList<GuiTexture> getGuiTextures(){
		if(currentButton != null){
			GuiTexture tex = currentButton.getTextures()[0];
			markTexture.getPosition().x = tex.getPosition().x;
			markTexture.getPosition().y = tex.getPosition().y;
			markTexture.getScale().x = tex.getScale().x;
			markTexture.getScale().y = tex.getScale().y;
			textures.add(markTexture);
		}
		return textures;
	}
	
	public ArrayList<IButton> getButtons(){
		return buttons;
	}
	
	public int[] mouseButtonInput(){
		return new int[]{0};
	}
	
	public boolean processMouseInput(int button){
		int[] input = mouseButtonInput();
		for(int i : input)
			if(i == button)
				return true;
		return false;
	}
	
	public void click(int x, int y, int button){
		if(currentButton != null && currentButton.isHit(x, y, this))
			currentButton.click(x, y, button, this);
		setCurrentButton(null);
		for(IButton but : buttons){
			if(but.isHit(x, y, this)){
				but.click(x, y, button, this);
			}
		}
	}
	
	public void mouseDown(int x, int y, int button){
		for(IButton but : buttons){
			if(but.isHit(x, y, this))
				but.mouseDown(x, y, button, this);
		}
	}
	
	public void scroll(int amount){
		if(this instanceof IScrollGui){
			IScrollGui scroll = (IScrollGui) this;
			currentScroll += amount * Options.getScrollSensitivity();
			if(currentScroll > scroll.getMaximumScroll())
				currentScroll = scroll.getMaximumScroll();
			if(currentScroll < 0)
				currentScroll = 0;
			
		}
	}
	
	public void update(){
		while(Mouse.next()){
			if(Mouse.getEventButtonState()){
				if(processMouseInput(Mouse.getEventButton()))
					click(Mouse.getEventX(), Mouse.getEventY(), Mouse.getEventButton());
			}
		}
		int[] input = mouseButtonInput();
		for(int i = 0; i < input.length; i++){
			if(Mouse.isButtonDown(input[i]))
				mouseDown(Mouse.getX(), Mouse.getY(), input[i]);
		}
		while(Keyboard.next()){
			if(Keyboard.getEventKeyState())
				keyPressed(Keyboard.getEventKey(), Keyboard.getEventCharacter());
			else
				keyReleased(Keyboard.getEventKey(), Keyboard.getEventCharacter());
		}
		int wheel = Mouse.getDWheel();
		if(wheel != 0)
			scroll(-wheel);
	}
	
	@Override
	public void render(GuiRenderer renderer){
		renderer.render(getGuiTextures(), getButtons(), this);
	}
	
	public void addTexture(GuiTexture texture){
		textures.add(texture);
	}
	
	public void addButton(IButton button){
		buttons.add(button);
	}
	
	public float getCurrentScroll(){
		return currentScroll;
	}
	
	public void setCurrentButton(IButton button){
		markTexture.getScale().x = 0;
		currentButton = button;
	}
	
	public IButton getCurrentButton(){
		return currentButton;
	}
	
	public void keyPressed(int key, char c){
		if(currentButton != null)
			currentButton.keyPressed(key, c, this);
	}
	
	public void keyReleased(int key, char c){
		if(currentButton != null)
			currentButton.keyReleased(key, c, this);
	}
	
	@Override
	public boolean canClose(){
		return true;
	}
	
	@Override
	public boolean renderGameWorld(){
		return true;
	}
	
	@Override
	public Space getSpace(){
		return null;
	}
	
	@Override
	public void activate(){}
}
