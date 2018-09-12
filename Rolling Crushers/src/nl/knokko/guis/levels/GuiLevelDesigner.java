package nl.knokko.guis.levels;

import java.awt.Color;
import java.io.File;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import nl.knokko.entities.finish.EntityFinishFlag;
import nl.knokko.entity.physical.EntityTile;
import nl.knokko.entity.render.EntityMarker;
import nl.knokko.entity.render.EntityRenderRoster;
import nl.knokko.guis.Gui;
import nl.knokko.guis.IGui;
import nl.knokko.guis.buttons.GuiButton;
import nl.knokko.guis.buttons.GuiFieldSliceButton;
import nl.knokko.guis.render.GuiRenderer;
import nl.knokko.guis.render.GuiTexture;
import nl.knokko.levels.Level;
import nl.knokko.levels.LevelFile;
import nl.knokko.main.Game;
import nl.knokko.materials.Materials;
import nl.knokko.render.main.MasterRenderer;
import nl.knokko.render.main.Renderer;
import nl.knokko.render.model.ModelTexture;
import nl.knokko.render.model.RawModel;
import nl.knokko.render.model.TexturedModel;
import nl.knokko.render.tasks.RenderTask;
import nl.knokko.space.Space;
import nl.knokko.utils.Axis;
import nl.knokko.utils.Facing;
import nl.knokko.utils.Maths;
import nl.knokko.utils.Resources;
import nl.knokko.utils.physics.Position;
import nl.knokko.utils.physics.Rotation;

public class GuiLevelDesigner extends Gui {
	
	public static final Color MAIN_TEXTURE_COLOR = new Color(150, 150, 150);
	public static final Color MAIN_BUTTON_COLOR = new Color(200, 200, 200);
	
	private static final ModelTexture white = Resources.getFilledTexture(Color.WHITE, Materials.DEFAULT);
	
	private Level level;
	private LevelFile file;
	static Space testSpace;
	
	private MainDesigner main;
	
	private EntityMarker startMarker;
	
	protected PlacingTile currentTile;
	
	public static GuiLevelDesigner createInstance(Level level, File file){
		GuiLevelDesigner root = new GuiLevelDesigner(level, file);
		return root.getMainDesigner();
	}
	
	public GuiLevelDesigner(Level level, File file){
		this.level = level;
		this.file = new LevelFile(file);
		startMarker = new EntityMarker(new TexturedModel(Resources.getFlagModel(1f, 0.05f, 0.5f, 0.1f, 0.2f), Resources.getFilledTexture(Color.WHITE, Materials.DEFAULT)), level.getStart(), new Rotation());
		level.spawnEntity(startMarker);
	}

	@Override
	public Color getBackGroundColor() {
		return null;
	}
	
	@Override
	public void update(){
		if(testSpace == null){
			super.update();
			level.update();
		}
		else
			testSpace.update();
		if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)){
			Game.setCurrentGUI(getMainDesigner());
			testSpace = null;
			Mouse.setGrabbed(false);
		}
	}
	
	@Override
	public void render(GuiRenderer renderer){
		if(testSpace != null)
			testSpace.render();
		else {
			level.render(true);
			renderer.render(getGuiTextures(), getButtons(), this);
		}
	}
	
	@Override
	public boolean renderGameWorld(){
		return false;
	}
	
	@Override
	public Space getSpace(){
		return testSpace;
	}
	
	public void save(){
		file.save(level);
	}
	
	public MainDesigner getMainDesigner(){
		if(main == null)
			main = new MainDesigner();
		return main;
	}
	
	public class MainDesigner extends GuiLevelDesigner {
		
		private LevelOptions levelOptions;
		private AddOptions addOptions;

		public MainDesigner() {
			super(GuiLevelDesigner.this.level, GuiLevelDesigner.this.file.getFile());
			levelOptions = new LevelOptions();
			addOptions = new AddOptions();
			addTexture(new GuiTexture(Resources.getFilledTexture(MAIN_TEXTURE_COLOR, Materials.DEFAULT), new Vector2f(-1, 0.9f), new Vector2f(2, 0.1f)));
			addButton(new GuiButton(new Vector2f(-0.85f, 0.9f), new Vector2f(0.1f, 0.07f), MAIN_BUTTON_COLOR, Color.BLACK, "Level"){
				@Override
				public void click(int x, int y, int button, IGui gui) {
					Game.setCurrentGUI(levelOptions);
				}
			});
			addButton(new GuiButton(new Vector2f(-0.6f, 0.9f), new Vector2f(0.1f, 0.07f), MAIN_BUTTON_COLOR, Color.BLACK, "Add"){
				@Override
				public void click(int x, int y, int button, IGui gui) {
					Game.setCurrentGUI(addOptions);
				}
			});
			addButton(new GuiButton(new Vector2f(0.75f, 0.9f), new Vector2f(0.2f, 0.07f), MAIN_BUTTON_COLOR, Color.RED, "Back to level select"){
				@Override
				public void click(int x, int y, int button, IGui gui) {
					Game.setCurrentGUI(GuiLevelDesignSelect.getInstance());
				}
			});
		}
		
		@Override
		public void activate(){
			testSpace = null;
		}
		
		public class LevelOptions extends MainSubGui {
			
			public LevelOptions(){
				addTexture(new GuiTexture(Resources.getFilledTexture(MAIN_TEXTURE_COLOR, Materials.DEFAULT), new Vector2f(), new Vector2f(0.4f, 0.7f)));
				addButton(new GuiButton(new Vector2f(0, 0.55f), new Vector2f(0.3f, 0.1f), MAIN_BUTTON_COLOR, Color.BLACK, "Back"){
					@Override
					public void click(int x, int y, int button, IGui gui) {
						Game.setCurrentGUI(MainDesigner.this);
					}
				});
				addButton(new GuiButton(new Vector2f(0, 0.3f), new Vector2f(0.3f, 0.1f), MAIN_BUTTON_COLOR, Color.BLACK, "Save"){
					@Override
					public void click(int x, int y, int button, IGui gui) {
						GuiLevelDesigner.this.save();
					}
				});
				addButton(new GuiButton(new Vector2f(0, 0.05f), new Vector2f(0.3f, 0.1f), MAIN_BUTTON_COLOR, Color.BLACK, "Test"){
					@Override
					public void click(int x, int y, int button, IGui gui) {
						testSpace = level.createSpace(getMainDesigner());
						testSpace.buildUp();
						Game.setCurrentGUI(getMainDesigner());
						Mouse.setGrabbed(true);
					}
				});
			}
		}
		
		public class AddOptions extends MainSubGui {
			
			private AddTile addTile;
			private AddFinish addFinish;
			
			public AddOptions(){
				addTile = new AddTile();
				addFinish = new AddFinish();
				addTexture(new GuiTexture(Resources.getFilledTexture(MAIN_TEXTURE_COLOR, Materials.DEFAULT), new Vector2f(), new Vector2f(0.4f, 0.7f)));
				addButton(new GuiButton(new Vector2f(0, 0.55f), new Vector2f(0.3f, 0.1f), MAIN_BUTTON_COLOR, Color.BLACK, "Back"){
					@Override
					public void click(int x, int y, int button, IGui gui) {
						Game.setCurrentGUI(MainDesigner.this);
					}
				});
				addButton(new GuiButton(new Vector2f(0, 0.3f), new Vector2f(0.3f, 0.1f), MAIN_BUTTON_COLOR, Color.BLACK, "Tile"){
					@Override
					public void click(int x, int y, int button, IGui gui) {
						Game.setCurrentGUI(addTile);
					}
				});
				addButton(new GuiButton(new Vector2f(0, 0.05f), new Vector2f(0.3f, 0.1f), MAIN_BUTTON_COLOR, Color.BLACK, "Finish"){
					@Override
					public void click(int x, int y, int button, IGui gui) {
						Game.setCurrentGUI(addFinish);
					}
				});
			}
			
			public class AddTile extends MainSubGui {
				
				private GuiTexture textureText = new GuiTexture(Resources.getTextTexture("none", Color.BLACK), new Vector2f(0.35f, 0.4f), new Vector2f(0.3f, 0.1f));
				private GuiTexture formText = new GuiTexture(Resources.getTextTexture("none", Color.BLACK), new Vector2f(0.35f, 0.25f), new Vector2f(0.3f, 0.1f));
				
				private ModelTexture tileTexture;
				private Form tileForm;
				
				private SelectForm formSelect = new SelectForm();
				private SelectTexture textureSelect = new SelectTexture();
				
				public AddTile(){
					addTexture(new GuiTexture(Resources.getFilledTexture(MAIN_TEXTURE_COLOR, Materials.DEFAULT), new Vector2f(), new Vector2f(0.7f, 0.7f)));
					addTexture(textureText);
					addTexture(formText);
					addButton(new GuiButton(new Vector2f(-0.35f, 0.55f), new Vector2f(0.3f, 0.1f), MAIN_BUTTON_COLOR, Color.BLACK, "Back"){
						@Override
						public void click(int x, int y, int button, IGui gui) {
							Game.setCurrentGUI(AddOptions.this);
						}
					});
					addButton(new GuiButton(new Vector2f(-0.35f, 0.3f), new Vector2f(0.3f, 0.1f), MAIN_BUTTON_COLOR, Color.BLACK, "Change Texture"){
						@Override
						public void click(int x, int y, int button, IGui gui) {
							Game.setCurrentGUI(textureSelect);
						}
					});
					addButton(new GuiButton(new Vector2f(-0.35f, 0.05f), new Vector2f(0.3f, 0.1f), MAIN_BUTTON_COLOR, Color.BLACK, "Change Form"){
						@Override
						public void click(int x, int y, int button, IGui gui) {
							Game.setCurrentGUI(formSelect);
						}
					});
					addButton(new GuiButton(new Vector2f(-0.35f, -0.2f), new Vector2f(0.3f, 0.1f), MAIN_BUTTON_COLOR, Color.GREEN, "Next"){
						@Override
						public void click(int x, int y, int button, IGui gui) {
							if(tileTexture != null && tileForm != null){
								currentTile = new PlacingTile(tileForm, tileTexture);
								Game.setCurrentGUI(new EntityPlacer(currentTile));
							}
						}
					});
				}
				
				public class SelectForm extends MainSubGui {
					
					public SelectForm(){
						addTexture(new GuiTexture(Resources.getFilledTexture(MAIN_TEXTURE_COLOR, Materials.DEFAULT), new Vector2f(), new Vector2f(0.4f, 0.7f)));
						addButton(new GuiButton(new Vector2f(0, 0.55f), new Vector2f(0.3f, 0.1f), MAIN_BUTTON_COLOR, Color.BLACK, "Back"){
							@Override
							public void click(int x, int y, int button, IGui gui) {
								Game.setCurrentGUI(AddTile.this);
							}
						});
						addButton(new GuiButton(new Vector2f(0, 0.3f), new Vector2f(0.3f, 0.1f), MAIN_BUTTON_COLOR, Color.BLACK, "Box"){
							@Override
							public void click(int x, int y, int button, IGui gui) {
								AddTile.this.setForm(Form.BOX);
								Game.setCurrentGUI(AddTile.this);
							}
						});
						addButton(new GuiButton(new Vector2f(0, 0.05f), new Vector2f(0.3f, 0.1f), MAIN_BUTTON_COLOR, Color.BLACK, "Sphere"){
							@Override
							public void click(int x, int y, int button, IGui gui) {
								AddTile.this.setForm(Form.SPHERE);
								Game.setCurrentGUI(AddTile.this);
							}
						});
						addButton(new GuiButton(new Vector2f(0, -0.2f), new Vector2f(0.3f, 0.1f), MAIN_BUTTON_COLOR, Color.BLACK, "Cilinder X"){
							@Override
							public void click(int x, int y, int button, IGui gui){
								AddTile.this.setForm(Form.CILINDER_X);
								Game.setCurrentGUI(AddTile.this);
							}
						});
						addButton(new GuiButton(new Vector2f(0, -0.55f), new Vector2f(0.3f, 0.1f), MAIN_BUTTON_COLOR, Color.BLACK, "Cilinder Y"){
							@Override
							public void click(int x, int y, int button, IGui gui){
								AddTile.this.setForm(Form.CILINDER_Y);
								Game.setCurrentGUI(AddTile.this);
							}
						});
						addButton(new GuiButton(new Vector2f(0, -0.9f), new Vector2f(0.3f, 0.1f), MAIN_BUTTON_COLOR, Color.BLACK, "Cilinder Z"){
							@Override
							public void click(int x, int y, int button, IGui gui){
								AddTile.this.setForm(Form.CILINDER_Z);
								Game.setCurrentGUI(AddTile.this);
							}
						});
					}
				}
				
				public class SelectTexture extends MainSubGui {
					
					public SelectTexture(){
						addTexture(new GuiTexture(Resources.getFilledTexture(MAIN_TEXTURE_COLOR, Materials.DEFAULT), new Vector2f(), new Vector2f(0.4f, 0.7f)));
						addButton(new GuiButton(new Vector2f(0, 0.55f), new Vector2f(0.3f, 0.1f), MAIN_BUTTON_COLOR, Color.BLACK, "Back"){
							@Override
							public void click(int x, int y, int button, IGui gui) {
								Game.setCurrentGUI(AddTile.this);
							}
						});
						addButton(new GuiButton(new Vector2f(0, 0.3f), new Vector2f(0.3f, 0.1f), MAIN_BUTTON_COLOR, Color.BLACK, "Filled Texture"){
							@Override
							public void click(int x, int y, int button, IGui gui) {
								Game.setCurrentGUI(new FilledTexture());
							}
						});
					}
					
					public class FilledTexture extends MainSubGui {
						
						public float red = 1;
						public float green = 1;
						public float blue = 1;
						
						public FilledTexture(){
							addTexture(new GuiTexture(Resources.getFilledTexture(MAIN_TEXTURE_COLOR, Materials.DEFAULT), new Vector2f(), new Vector2f(0.4f, 0.7f)));
							addButton(new GuiButton(new Vector2f(0, 0.55f), new Vector2f(0.3f, 0.1f), MAIN_BUTTON_COLOR, Color.BLACK, "Back"){
								@Override
								public void click(int x, int y, int button, IGui gui) {
									Game.setCurrentGUI(SelectTexture.this);
								}
							});
							try {
								addButton(new GuiFieldSliceButton(new Vector2f(0, 0.3f), new Vector2f(0.3f, 0.1f), Color.RED, Color.BLACK, "Color Red", 0, 1, getClass().getField("red"), this));
								addButton(new GuiFieldSliceButton(new Vector2f(0, 0.05f), new Vector2f(0.3f, 0.1f), Color.GREEN, Color.BLACK, "Color Green", 0, 1, getClass().getField("green"), this));
								addButton(new GuiFieldSliceButton(new Vector2f(0, -0.2f), new Vector2f(0.3f, 0.1f), Color.BLUE, Color.BLACK, "Color Blue", 0, 1, getClass().getField("blue"), this));
							} catch(Exception ex){
								throw new RuntimeException("Can't find field 'red' for FilledTexture:", ex);
							}
							addButton(new GuiButton(new Vector2f(0, -0.45f), new Vector2f(0.3f, 0.1f), Color.WHITE, Color.BLACK, "Done"){
								@Override
								public void click(int x, int y, int button, IGui gui) {
									AddTile.this.setTexture(Resources.getFilledTexture(new Color(red, green, blue), Materials.DEFAULT), "Filled Color(" + red + "," + green + "," + blue + ")");
									Game.setCurrentGUI(AddTile.this);
								}
							});
						}
					}
				}
				
				public void setForm(Form form){
					this.tileForm = form;
					if(form != null)
						formText.setText(form.name().toLowerCase(), Color.GREEN);
					else
						formText.setText("none", Color.BLACK);
				}
				
				public void setTexture(ModelTexture texture, String name){
					this.tileTexture = texture;
					if(texture != null)
						textureText.setText(name, Color.GREEN);
					else
						textureText.setText("none", Color.BLACK);
				}
			}
			
			public class AddFinish extends MainSubGui {
				
				public AddFinish(){
					addTexture(new GuiTexture(Resources.getFilledTexture(MAIN_TEXTURE_COLOR, Materials.DEFAULT), new Vector2f(), new Vector2f(0.7f, 0.7f)));
					addButton(new GuiButton(new Vector2f(-0.35f, 0.55f), new Vector2f(0.2f, 0.1f), MAIN_BUTTON_COLOR, Color.BLACK, "Back"){
						@Override
						public void click(int x, int y, int button, IGui gui) {
							Game.setCurrentGUI(AddOptions.this);
						}
					});
					addButton(new GuiButton(new Vector2f(-0.35f, 0.3f), new Vector2f(0.2f, 0.1f), MAIN_BUTTON_COLOR, Color.BLACK, "Flag"){
						@Override
						public void click(int x, int y, int button, IGui gui) {
							currentTile = new PlacingTile(Form.FLAG, Resources.getFlagTexture(new Color(200, 100, 0), Color.YELLOW));
							Game.setCurrentGUI(new EntityPlacer(currentTile));
						}
					});
				}
			}
		}
		
		private abstract class MainSubGui extends SubGui {
			 
			@Override
			public void render(GuiRenderer renderer){
				MainDesigner.this.render(renderer);
				renderer.render(textures, buttons, this);
			}
		}
	}
	
	public class EntityPlacer extends GuiLevelDesigner {
		
		private GuiTexture text1;
		
		private RenderPlacingTask task = new RenderPlacingTask();
		private RenderLinerTask lineTask = new RenderLinerTask();
		private PlacingTile currentTile;
		private MasterRenderer master;
		
		private Position position1;
		private Position position2;
		
		private EntityRenderRoster axisX;
		private EntityRenderRoster axisY;
		private EntityRenderRoster axisZ;
		
		private int stepTime = 10;
		private int stepTimer = 0;
		
		private byte state;

		public EntityPlacer(PlacingTile currentTile) {
			super(GuiLevelDesigner.this.level, GuiLevelDesigner.this.file.getFile());
			this.currentTile = currentTile;
			initMasterRenderer();
			text1 = new GuiTexture(null, new Vector2f(-0.2f, 0.9f), new Vector2f(0.75f, 0.1f));
			if(currentTile.form == Form.BOX)
				text1.setText("Select the first corner of the tile.", Color.BLACK);
			if(currentTile.form == Form.SPHERE)
				text1.setText("Select the centre of the sphere.", Color.BLACK);
			if(currentTile.form == Form.FLAG)
				text1.setText("Select the position of the flag.", Color.BLACK);
			if(currentTile.form == Form.CILINDER_X)
				text1.setText("Select the mid Y, min/max X and mid Z of the cilinder.", Color.BLACK);
			if(currentTile.form == Form.CILINDER_Y)
				text1.setText("Select the mid X, min/max Y and mid Z of the cilinder.", Color.BLACK);
			if(currentTile.form == Form.CILINDER_Z)
				text1.setText("Select the mid X, min/max Z and mid Y of the cilinder.", Color.BLACK);
			addTexture(new GuiTexture(Resources.getFilledTexture(MAIN_TEXTURE_COLOR, Materials.DEFAULT), new Vector2f(0, 0.9f), new Vector2f(1, 0.1f)));
			addButton(new GuiButton(new Vector2f(0.775f, 0.9f), new Vector2f(0.2f, 0.1f), MAIN_BUTTON_COLOR, Color.BLACK, "Help"){
				@Override
				public void click(int x, int y, int button, IGui gui) {
					Game.setCurrentGUI(new GuiPlacingHelp1());
				}
			});
			addTexture(text1);
			Position cam = level.getCamera().getPosition();
			Vector3f vector = Maths.getRotationVector(level.getCamera().getRotation());
			position1 = new Position((int)(cam.getX() + vector.x), (int)(cam.getY() + vector.y), (int)(cam.getZ() + vector.z));
			state = 1;
		}
		
		private float stepSize(){
			return Keyboard.isKeyDown(Keyboard.KEY_T) ? 0.1f : 1.0f;
		}
		
		private void initMasterRenderer(){
			master = new MasterRenderer();
			axisX = new EntityRenderRoster(new Rotation(90, 0, 0), EntityRenderRoster.RED_MODEL);
			axisY = new EntityRenderRoster(new Rotation(0, 0, 0), EntityRenderRoster.GREEN_MODEL);
			axisZ = new EntityRenderRoster(new Rotation(90, 90, 0), EntityRenderRoster.BLUE_MODEL);
			master.addEntity(axisX);
			master.addEntity(axisY);
			master.addEntity(axisZ);
		}
		
		@Override
		public void click(int x, int y, int button){
			if(y >= text1.getMaxY() || button != 1)
				super.click(x, y, button);
			else {
				if(state == 1){
					position2 = new Position(position1.getX() + 1, position1.getY() + 1, position1.getZ() + 1);
					if(currentTile.form == Form.BOX)
						text1.setText("Select the second corner of the tile.", Color.BLACK);
					if(currentTile.form == Form.SPHERE)
						text1.setText("Select the radius of the sphere.", Color.BLACK);
					if(currentTile.form == Form.CILINDER_X)
						text1.setText("Select the min/max X and radius of the cilinder.", Color.BLACK);
					if(currentTile.form == Form.CILINDER_Y)
						text1.setText("Select the min/max Y and radius of the cilinder.", Color.BLACK);
					if(currentTile.form == Form.CILINDER_Z)
						text1.setText("Select the min/max Z and radius of the cilinder.", Color.BLACK);
					state = 2;
					if(currentTile.form == Form.FLAG){
						level.spawnEntity(currentTile.create(position1, position2));
						Game.setCurrentGUI(getMainDesigner());
						state = 0;
					}
				}
				else if(state == 2){
					if(currentTile.form == Form.BOX)
						text1.setText("Select the first corner of the tile again.", Color.BLACK);
					if(currentTile.form == Form.SPHERE)
						text1.setText("Select the centre of the sphere again.", Color.BLACK);
					if(currentTile.form == Form.CILINDER_X)
						text1.setText("Select the centre of the cilinder again.", Color.BLACK);
					if(currentTile.form == Form.CILINDER_Y)
						text1.setText("Select the centre of the cilinder again.", Color.BLACK);
					if(currentTile.form == Form.CILINDER_Z)
						text1.setText("Select the centre of the cilinder again.", Color.BLACK);
					state = 3;
				}
				else if(state == 3){
					level.spawnEntity(currentTile.create(position1, position2));
					Game.setCurrentGUI(getMainDesigner());
					state = 0;
				}
			}
		}
		
		@Override
		public int[] mouseButtonInput(){
			return new int[]{0,1};
		}
		
		@Override
		public void render(GuiRenderer renderer){
			if(state == 1)
				level.render(false, lineTask);
			else if(state == 2 || state == 3)
				level.render(false, lineTask, task);
			else
				level.render(false);
			Renderer.render(master.getEntities());
			renderer.render(getGuiTextures(), getButtons(), this);
		}
		
		@Override
		public void update(){
			super.update();
			if(stepTimer < stepTime)
				++stepTimer;
			else {
				boolean rs = Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
				if(state == 1 || state == 3){
					if(Keyboard.isKeyDown(Keyboard.KEY_UP)){
						if(rs)
							position1.move(0, stepSize(), 0);
						else
							position1.move(0, 0, -stepSize());
					}
					if(Keyboard.isKeyDown(Keyboard.KEY_DOWN)){
						if(rs)
							position1.move(0, -stepSize(), 0);
						else
							position1.move(0, 0, stepSize());
					}
					if(Keyboard.isKeyDown(Keyboard.KEY_LEFT))
						position1.move(-stepSize(), 0, 0);
					if(Keyboard.isKeyDown(Keyboard.KEY_RIGHT))
						position1.move(stepSize(), 0, 0);
				}
				if(state == 2){
					if(Keyboard.isKeyDown(Keyboard.KEY_UP)){
						if(rs)
							position2.move(0, stepSize(), 0);
						else
							position2.move(0, 0, -stepSize());
					}
					if(Keyboard.isKeyDown(Keyboard.KEY_DOWN)){
						if(rs)
							position2.move(0, -stepSize(), 0);
						else
							position2.move(0, 0, stepSize());
					}
					if(Keyboard.isKeyDown(Keyboard.KEY_LEFT))
						position2.move(-stepSize(), 0, 0);
					if(Keyboard.isKeyDown(Keyboard.KEY_RIGHT))
						position2.move(stepSize(), 0, 0);
				}
				axisX.updatePosition(position1);
				axisY.updatePosition(position1);
				axisZ.updatePosition(position1);
				stepTimer = 0;
			}
		}
		
		public class GuiPlacingHelp1 extends SubGui {
			
			public GuiPlacingHelp1(){
				super();
				addButton(new GuiButton(new Vector2f(-0.35f, 0.9f), new Vector2f(0.3f, 0.1f), MAIN_BUTTON_COLOR, Color.BLACK, "Back"){
					@Override
					public void click(int x, int y, int button, IGui gui) {
						Game.setCurrentGUI(EntityPlacer.this);
					}
				});
				addButton(new GuiButton(new Vector2f(0.3f, 0.9f), new Vector2f(0.3f, 0.1f), MAIN_BUTTON_COLOR, Color.BLACK, "Next Page"){
					@Override
					public void click(int x, int y, int button, IGui gui) {
						// TODO Auto-generated method stub
						
					}
				});
				addTexture(new GuiTexture(Resources.getTextTexture("You have to select the right position for the new object.", Color.BLACK), new Vector2f(0, 0.7f), new Vector2f(0.95f, 0.1f)));
				addTexture(new GuiTexture(Resources.getTextTexture("The current location is marked with the place where the white lines cross.", Color.BLACK), new Vector2f(0, 0.5f), new Vector2f(0.95f, 0.1f)));
				addTexture(new GuiTexture(Resources.getTextTexture("Move the current location to the west with the left arrow.", Color.BLACK), new Vector2f(0, 0.3f), new Vector2f(0.95f, 0.1f)));
				addTexture(new GuiTexture(Resources.getTextTexture("Move the current location to the east with the right arrow.", Color.BLACK), new Vector2f(0, 0.1f), new Vector2f(0.95f, 0.1f)));
				addTexture(new GuiTexture(Resources.getTextTexture("Move the current location to the north with the up arrow.", Color.BLACK), new Vector2f(0, -0.1f), new Vector2f(0.95f, 0.1f)));
				addTexture(new GuiTexture(Resources.getTextTexture("Move the current location to the south with the down arrow.", Color.BLACK), new Vector2f(0, -0.3f), new Vector2f(0.95f, 0.1f)));
				addTexture(new GuiTexture(Resources.getTextTexture("Hold shift while pressing the up arrow to move the current location up.", Color.BLACK), new Vector2f(0, -0.5f), new Vector2f(0.95f, 0.1f)));
				addTexture(new GuiTexture(Resources.getTextTexture("Hold shift while pressing the down arrow to move the current location down.", Color.BLACK), new Vector2f(0, -0.7f), new Vector2f(0.95f, 0.1f)));
				addTexture(new GuiTexture(Resources.getTextTexture("Right click to confirm the current location and move on to the next position.", Color.BLACK), new Vector2f(0, -0.9f), new Vector2f(0.95f, 0.1f)));
			}
			
			@Override
			public Color getBackGroundColor(){
				return MAIN_TEXTURE_COLOR;
			}
			
		}
		
		public class RenderPlacingTask extends RenderTask {
			
			private final Rotation defaultRotation = new Rotation();
			private final Rotation rotationCilinderX = new Rotation(0, 0, 270);
			private final Rotation rotationCilinderZ = new Rotation(90, 0, 0);//TODO cilinders not rendering properly
			private final Matrix4f defaultMatrix = Maths.createInvertedTransformationMatrix(new Position(), defaultRotation, 1);
			
			private Position previous1;
			private Position previous2;
			
			private RawModel model;
			private TexturedModel texModel;

			@Override
			public TexturedModel getModel() {
				if(position1 != null && position2 != null){
					if(model == null || !position1.equals(previous1) || !position2.equals(previous2)){
						previous1 = position1.clone();
						previous2 = position2.clone();
						if(currentTile.form == Form.BOX){
							Vector3f min = position1.getMin(position2);
							Vector3f max = position1.getMax(position2);
							model = Resources.createBox(min.x, min.y, min.z, max.x, max.y, max.z);
							texModel = new TexturedModel(model, white);
						}
						if(currentTile.form == Form.SPHERE){
							model = Resources.getSphereModel();
							texModel = new TexturedModel(model, white);
						}
						if(currentTile.form == Form.FLAG){
							model = Resources.getFlagModel(2f, 0.1f, 0.7f, 0.2f, 0.4f);
							texModel = new TexturedModel(model, white);
						}
						if(currentTile.form == Form.CILINDER_X){
							float radius = (float) Math.hypot(position2.getY() - position1.getY(), position2.getZ() - position1.getZ());
							if(radius > 0.001f){
								model = Resources.createCilinder(0, Math.abs(position2.getX() - position1.getX()), radius);
								texModel = new TexturedModel(model, white);
							}
						}
						if(currentTile.form == Form.CILINDER_Y){
							float radius = (float) Math.hypot(position2.getX() - position1.getX(), position2.getZ() - position1.getZ());
							if(radius > 0.001f){
								model = Resources.createCilinder(0, Math.abs(position2.getY() - position1.getY()), radius);
								texModel = new TexturedModel(model, white);
							}
						}
						if(currentTile.form == Form.CILINDER_Z){
							float radius = (float) Math.hypot(position2.getX() - position1.getX(), position2.getY() - position1.getY());
							if(radius > 0.001f){
								model = Resources.createCilinder(0, Math.abs(position2.getZ() - position1.getZ()), radius);
								texModel = new TexturedModel(model, white);
							}
						}
					}
				}
				return texModel;
			}

			@Override
			public Matrix4f getMatrix() {
				if(currentTile.form == Form.BOX)
					return defaultMatrix;
				if(currentTile.form == Form.SPHERE)
					return Maths.createTransformationMatrix(position1, defaultRotation, position1.getDistance(position2));
				if(currentTile.form == Form.FLAG)
					return Maths.createTransformationMatrix(position1, defaultRotation, 1);
				if(currentTile.form == Form.CILINDER_X)
					return Maths.createTransformationMatrix(position1, rotationCilinderX, 1);
				if(currentTile.form == Form.CILINDER_Y)
					return Maths.createTransformationMatrix(position1, defaultRotation, 1);
				if(currentTile.form == Form.CILINDER_Z)
					return Maths.createTransformationMatrix(position1, rotationCilinderZ, 1);
				throw new RuntimeException("Unknown form: " + currentTile.form);
			}

			@Override
			public boolean renderNow() {
				if(currentTile.form == Form.BOX || currentTile.form == Form.SPHERE || currentTile.form == Form.CILINDER_X || currentTile.form == Form.CILINDER_Y || currentTile.form == Form.CILINDER_Z)
					return state > 1;
				if(currentTile.form == Form.FLAG)
					return true;
				throw new RuntimeException("Unknown form: " + currentTile.form);
			}
		}
		
		public class RenderLinerTask extends RenderTask {
			  
			private TexturedModel model = new TexturedModel(Resources.getAxisModel(), white);
			private final Rotation defaultRotation = new Rotation();

			@Override
			public TexturedModel getModel() {
				return model;
			}

			@Override
			public Matrix4f getMatrix() {
				if(state == 1 || state == 3)
					return Maths.createInvertedTransformationMatrix(position1, defaultRotation, 1);
				return Maths.createInvertedTransformationMatrix(position2, defaultRotation, 1);
			}

			@Override
			public boolean renderNow() {
				return state > 0;
			}
		}
	}
	
	private abstract class SubGui extends Gui {
		
		@Override
		public Color getBackGroundColor() {
			return null;
		}
		
		@Override
		public boolean renderGameWorld(){
			return false;
		}
	}
	
	private static enum Form {
		
		BOX,
		SPHERE,
		FLAG,
		CILINDER_X,
		CILINDER_Y,
		CILINDER_Z;
	}
	
	private static class PlacingTile {
		
		protected Form form;
		protected ModelTexture texture;
		
		private PlacingTile(Form form, ModelTexture texture){
			this.form = form;
			this.texture = texture;
		}
		
		private Object create(Position position1, Position position2){
			Vector3f min = position1.getMin(position2);
			Vector3f max = position1.getMax(position2);
			if(form == Form.BOX){
				RawModel model = Resources.createBox(min.x, min.y, min.z, max.x, max.y, max.z);
				return new EntityTile.Box(new TexturedModel(model, texture), Facing.NORTH, 0, 0, 0, min.x, min.y, min.z, max.x, max.y, max.z, 1);
			}
			if(form == Form.SPHERE) {
				float radius = position1.getDistance(position2);
				return new EntityTile.Sphere(texture, (min.x + max.x) / 2, (min.y + max.y) / 2, (min.z + max.z) / 2, radius / 2);
			}
			if(form == Form.FLAG){
				return new EntityFinishFlag(position1.getX(), position1.getY(), position1.getZ(), 0);
			}
			if(form == Form.CILINDER_X){
				float radius = (float) Math.hypot(position2.getY() - position1.getY(), position2.getZ() - position1.getZ());
				return new EntityTile.Cilinder(texture, min.x, position1.getY(), position1.getZ(), 0, max.x - min.x, radius, Axis.X);
			}
			if(form == Form.CILINDER_Y){
				float radius = (float) Math.hypot(position2.getX() - position1.getX(), position2.getZ() - position1.getZ());
				return new EntityTile.Cilinder(texture, position1.getX(), min.y, position1.getZ(), 0, max.y - min.y, radius, Axis.Y);
			}
			if(form == Form.CILINDER_Z){
				float radius = (float) Math.hypot(position2.getX() - position1.getX(), position2.getY() - position1.getY());
				return new EntityTile.Cilinder(texture, position1.getX(), position1.getY(), min.z, 0, max.z - min.z, radius, Axis.Z);
			}
			throw new RuntimeException("Unknown Form: " + form);
		}
	}
}
