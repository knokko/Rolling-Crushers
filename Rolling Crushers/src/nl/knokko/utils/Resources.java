package nl.knokko.utils;

import static java.lang.Math.min;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import org.lwjgl.util.vector.Vector3f;

import nl.knokko.input.Loader;
import nl.knokko.materials.Material;
import nl.knokko.materials.Materials;
import nl.knokko.render.model.ModelTexture;
import nl.knokko.render.model.RawModel;

public final class Resources {
	
	private static final byte ID_FILLED = 0;
	private static final byte ID_TEXT = 1;
	private static final byte ID_BORDER = 2;
	private static final byte ID_PLAYER = 3;
	private static final byte ID_ROSTER = 4;
	
	private static final byte ID_CUBE = 0;
	private static final byte ID_BOX = 1;
	private static final byte ID_SPHERE = 2;
	private static final byte ID_FLAG = 3;
	private static final byte ID_SQUAD = 4;
	private static final byte ID_AXIS = 5;
	private static final byte ID_CILINDER = 6;
	
	private static HashMap<FlagSettings, RawModel> flagModels = new HashMap<FlagSettings, RawModel>();
	
	private static HashMap<FilledColor, ModelTexture> filledTextures = new HashMap<FilledColor, ModelTexture>();
	private static HashMap<ColorText, ModelTexture> textTextures = new HashMap<ColorText, ModelTexture>();
	private static HashMap<Color, ModelTexture> borderTextures = new HashMap<Color, ModelTexture>();
	private static HashMap<Color, ModelTexture> rosterTextures = new HashMap<Color, ModelTexture>();
	private static HashMap<FlagColor, ModelTexture> flagTextures = new HashMap<FlagColor, ModelTexture>();
	
	private static RawModel squad;
	private static RawModel cube;
	private static RawModel sphere;
	private static RawModel axis;
	
	private static ModelTexture playerTexture;
	
	public static RawModel modelFromID(byte[] creationID){
		ByteBuffer buffer = ByteBuffer.wrap(creationID);
		byte id = buffer.get();
		if(id == ID_CUBE)
			return getCubeModel();
		if(id == ID_SPHERE)
			return getSphereModel();
		if(id == ID_BOX)
			return createBox(buffer.getFloat(), buffer.getFloat(), buffer.getFloat(), buffer.getFloat(), buffer.getFloat(), buffer.getFloat());
		if(id == ID_FLAG)
			return getFlagModel(buffer.getFloat(), buffer.getFloat(), buffer.getFloat(), buffer.getFloat(), buffer.getFloat());
		if(id == ID_CILINDER)
			return createCilinder(buffer.getFloat(), buffer.getFloat(), buffer.getFloat());
		throw new IllegalArgumentException("Unknown model creation ID: " + id);
	}
	
	public static RawModel getSquadModel(){
		if(squad == null){
			ModelData data = new ModelData();
			addFourangle(data, -1, 0, -1, -1, 0, 1, 1, 0, 1, 1, 0, -1, 0, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0);
			squad = data.create(new byte[]{ID_SQUAD});
		}
		return squad;
	}
	
	public static RawModel getCubeModel(){
		if(cube == null){
			ModelData data = new ModelData();
			addSquad(data, 0, 0.5f, 0, 1, 0, 1, 0, 0, 0, 1, 1, Facing.UP);
			addSquad(data, 0, -0.5f, 0, 1, 0, -1, 0, 0, 0, 1, 1, Facing.DOWN);
			addSquad(data, 0.5f, 0, 0, 1, 1, 0, 0, 0, 0, 1, 1, Facing.EAST);
			addSquad(data, -0.5f, 0, 0, 1, -1, 0, 0, 0, 0, 1, 1, Facing.WEST);
			addSquad(data, 0, 0, 0.5f, 1, 0, 0, 1, 0, 0, 1, 1, Facing.SOUTH);
			addSquad(data, 0, 0, -0.5f, 1, 0, 0, -1, 0, 0, 1, 1, Facing.NORTH);
			cube = data.create(new byte[]{ID_CUBE});
		}
		return cube;
	}
	
	public static RawModel getSphereModel(){
		if(sphere == null){
			ModelData data = new ModelData();
			int parts = 50;
			for(int i = 0; i < parts; i++){
				float f = ((float)i / (parts - 1));
				float angle = (float) (-0.5f * Math.PI + f * Math.PI);
				addCircleVertices(data, 0, (float) (1f * Math.sin(angle)), 0, (float) Math.abs(1f * Math.cos(angle)), parts, f, 0);
			}
			for(int y = 0; y < parts - 1; y++){
				for(int a = 0; a < parts - 1; a++){
					bindFourangle(data, y * parts + a, y * parts + a + 1, (y + 1) * parts + a + 1, (y + 1) * parts + a);
				}
			}
			sphere = data.create(new byte[]{ID_SPHERE});
		}
		return sphere;
	}
	
	public static RawModel getFlagModel(float stickLength, float stickRadius, float flagLength, float flagWidth, float flagHeight){
		FlagSettings settings = new FlagSettings(stickLength, stickRadius, flagLength, flagWidth, flagHeight);
		RawModel model = flagModels.get(settings);
		if(model == null)
			model = createFlagModel(settings);
		return model;
	}
	
	public static RawModel getAxisModel(){
		if(axis == null){
			ModelData data = new ModelData();
			addBox(data, -100, -0.01f, -0.01f, 100, 0.01f, 0.01f, 0, 0, 1, 1);//x
			addBox(data, -0.01f, -100, -0.01f, 0.01f, 100, 0.01f, 0, 0, 1, 1);//y
			addBox(data, -0.01f, -0.01f, -100, 0.01f, 0.01f, 100, 0, 0, 1, 1);
			axis = data.create(new byte[]{ID_AXIS});
		}
		return axis;
	}
	
	private static void addCircleVertices(ModelData data, float x, float y, float z, float radius, int parts, float v, float centerY){
		float delta = (float) (2 * Math.PI / (parts - 1));
		for(int i = 0; i < parts; i++){
			float angle = delta * i;
			float dx = (float) (Math.cos(angle) * radius);
			float dz = (float) (Math.sin(angle) * radius);
			addVertice(data, x + dx, y, z + dz);
			addTexture(data, (float) (angle / (2 * Math.PI)), v);
			Vector3f vector = new Vector3f(dx, y - centerY, dz);
			vector.normalise();
			addNormal(data, vector.x, vector.y, vector.z);
		}
	}
	
	public static RawModel createBox(float minX, float minY, float minZ, float maxX, float maxY, float maxZ){
		ModelData data = new ModelData();
		addBox(data, minX, minY, minZ, maxX, maxY, maxZ, 0, 0, 1, 1);
		ByteBuffer buffer = ByteBuffer.allocate(25);
		buffer.put(ID_BOX);
		buffer.putFloat(minX);
		buffer.putFloat(minY);
		buffer.putFloat(minZ);
		buffer.putFloat(maxX);
		buffer.putFloat(maxY);
		buffer.putFloat(maxZ);
		return data.create(buffer.array());
	}
	
	public static RawModel createCilinder(float min, float max, float radius){
		ModelData data = new ModelData();
		addCilinder(data, 0, min, 0, max, radius, 0, 1);
		ByteBuffer buffer = ByteBuffer.allocate(13);
		buffer.put(ID_CILINDER);
		buffer.putFloat(min);
		buffer.putFloat(max);
		buffer.putFloat(radius);
		return data.create(buffer.array());
	}
	
	private static RawModel createFlagModel(FlagSettings settings){
		ByteBuffer buffer = ByteBuffer.allocate(21);
		buffer.put(ID_FLAG);
		buffer.putFloat(settings.stickLength);
		buffer.putFloat(settings.stickRadius);
		buffer.putFloat(settings.flagLength);
		buffer.putFloat(settings.flagWidth);
		buffer.putFloat(settings.flagHeight);
		ModelData data = new ModelData();
		addBox(data, -settings.stickRadius, settings.stickLength, -settings.stickRadius, -settings.stickRadius + settings.flagLength, settings.stickLength + settings.flagHeight, -settings.stickRadius + settings.flagWidth, 0, 0.8f, 1, 1);
		addCilinder(data, 0, 0, 0, settings.stickLength, settings.stickRadius, 0, 0.8f);
		return data.create(buffer.array());
	}
	
	private static void addBox(ModelData data, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, float minU, float minV, float maxU, float maxV){
		addFourangle(data, minX, maxY, maxZ, maxX, maxY, maxZ, maxX, maxY, minZ, minX, maxY, minZ, 0, 1, 0, minU, maxV, maxU, maxV, maxU, minV, minU, minV);
		addFourangle(data, minX, minY, maxZ, maxX, minY, maxZ, maxX, minY, minZ, minX, minY, minZ, 0, -1, 0, minU, maxV, maxU, maxV, maxU, minV, minU, minV);
		addFourangle(data, minX, minY, maxZ, maxX, minY, maxZ, maxX, maxY, maxZ, minX, maxY, maxZ, 0, 0, 1, minU, maxV, maxU, maxV, maxU, minV, minU, minV);
		addFourangle(data, minX, minY, minZ, maxX, minY, minZ, maxX, maxY, minZ, minX, maxY, minZ, 0, 0, -1, minU, maxV, maxU, maxV, maxU, minV, minU, minV);
		
		addFourangle(data, minX, minY, minZ, minX, minY, maxZ, minX, maxY, maxZ, minX, maxY, minZ, -1, 0, 0, minU, maxV, maxU, maxV, maxU, minV, minU, minV);
		addFourangle(data, maxX, minY, minZ, maxX, minY, maxZ, maxX, maxY, maxZ, maxX, maxY, minZ, 1, 0, 0, minU, maxV, maxU, maxV, maxU, minV, minU, minV);
	}
	
	private static void addCilinder(ModelData data, float x, float minY, float z, float maxY, float radius, float minV, float maxV){
		int index = data.vertices.size() / 3;
		int parts = 50;
		addCircleVertices(data, x, minY, z, radius, parts, minV, minY);
		addCircleVertices(data, x, maxY, z, radius, parts, maxV, maxY);
		for(int i = 0; i < parts - 1; i++)
			bindFourangle(data, index + i, index + parts + i, index + parts + i + 1, index + i + 1);
	}
	
	private static void addFourangle(ModelData data, float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4, float normalX, float normalY, float normalZ, float u1, float v1, float u2, float v2, float v3, float u3, float u4, float v4){
		int index = data.vertices.size() / 3;
		addVertice(data, x1, y1, z1);
		addVertice(data, x2, y2, z2);
		addVertice(data, x3, y3, z3);
		addVertice(data, x4, y4, z4);
		addNormal(data, normalX, normalY, normalZ, 4);
		addTexture(data, u1, v1);
		addTexture(data, u2, v2);
		addTexture(data, u3, v3);
		addTexture(data, u4, v4);
		bindFourangle(data, index, index + 1, index + 2, index + 3);
	}
	
	private static void addSquad(ModelData data, float x, float y, float z, float width, float nx, float ny, float nz, float u1, float v1, float u2, float v2, Facing facing){
		float w = width / 2;
		if(facing == Facing.UP || facing == Facing.DOWN)
			addFourangle(data, x - w, y, z + w, x + w, y, z + w, x + w, y, z - w, x - w, y, z - w, nx, ny, nz, u1, v2, u2, v2, v2, u1, v1, u1);
		if(facing == Facing.WEST || facing == Facing.EAST)
			addFourangle(data, x, y + w, z - w, x, y + w, z + w, x, y - w, z + w, x, y - w, z - w, nx, ny, nz, u1, v2, u2, v2, v2, u1, v1, u1);
		if(facing == Facing.NORTH || facing == Facing.SOUTH)
			addFourangle(data, x - w, y + w, z, x + w, y + w, z, x + w, y - w, z, x - w, y - w, z, nx, ny, nz, u1, v2, u2, v2, v2, u1, v1, u1);
	}
	
	private static void addVertice(ModelData data, float x, float y, float z){
		data.vertices.add(x);
		data.vertices.add(y);
		data.vertices.add(z);
	}
	
	private static void addNormal(ModelData data, float nx, float ny, float nz){
		data.normals.add(nx);
		data.normals.add(ny);
		data.normals.add(nz);
	}
	
	private static void addNormal(ModelData data, float nx, float ny, float nz, int times){
		for(int i = 0; i < times; i++)
			addNormal(data, nx, ny, nz);
	}
	
	private static void addTexture(ModelData data, float u, float v){
		data.textures.add(u);
		data.textures.add(v);
	}
	
	private static void bindTriangle(ModelData data, int index1, int index2, int index3){
		data.indices.add(index1);
		data.indices.add(index2);
		data.indices.add(index3);
	}
	
	private static void bindFourangle(ModelData data, int index1, int index2, int index3, int index4){
		bindTriangle(data, index1, index2, index3);
		bindTriangle(data, index1, index4, index3);
	}
	
	/**
	 * Create a model texture from it's creation id
	 * @param id The creation id of the ModelTexture
	 * @return the owner of the creation ID
	 */
	public static ModelTexture textureFromID(byte[] creationID){
		ByteBuffer buffer = ByteBuffer.wrap(creationID);
		byte id = buffer.get();
		if(id == ID_FILLED){
			Material material = Materials.fromID(buffer.get());
			Color color = new Color(buffer.getInt());
			return getFilledTexture(color, material);
		}
		if(id == ID_TEXT){
			Color color = new Color(buffer.getInt());
			short length = buffer.getShort();
			String text = "";
			for(int i = 0; i < length; i++)
				text += buffer.getChar();
			return getTextTexture(text, color);
		}
		if(id == ID_BORDER){
			Color color = new Color(buffer.getInt());
			return getBorderTexture(color);
		}
		if(id == ID_PLAYER)
			return getPlayerTexture();
		if(id == ID_ROSTER)
			return getRosterTexture(new Color(buffer.getInt()));
		throw new RuntimeException("Unknown creation identifier: " + id);
	}
	
	public static ModelTexture getFilledTexture(Color color, Material material){
		ModelTexture texture = filledTextures.get(color);
		if(texture == null){
			FilledColor fc = new FilledColor(color, material);
			texture = createFilledTexture(fc);
			filledTextures.put(fc, texture);
		}
		return texture;
	}
	
	public static ModelTexture getTextTexture(String text, Color color){
		ColorText ct = new ColorText(color, text);
		ModelTexture texture = textTextures.get(ct);
		if(texture == null)
			texture = createTextTexture(color, text);
		return texture;
	}
	
	public static ModelTexture getBorderTexture(Color color){
		ModelTexture texture = borderTextures.get(color);
		if(texture == null)
			texture = createBorderTexture(color);
		return texture;
	}
	
	public static ModelTexture getFlagTexture(Color stickColor, Color flagColor){
		FlagColor fc = new FlagColor(stickColor, flagColor);
		ModelTexture texture = flagTextures.get(fc);
		if(texture == null){
			texture = createFlagTexture(fc);
			flagTextures.put(fc, texture);
		}
		return texture;
	}
	
	private static ModelTexture createBorderTexture(Color color){
		ByteBuffer buffer = ByteBuffer.allocate(5);
		buffer.put(ID_BORDER);
		buffer.putInt(color.getRGB());
		ModelTexture texture = new ModelTexture(Loader.loadTexture(createBorderImage(color), true), Materials.DEFAULT, buffer.array());
		borderTextures.put(color, texture);
		return texture;
	}
	
	private static BufferedImage createBorderImage(Color color){
		BufferedImage image = new BufferedImage(256, 64, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = image.createGraphics();
		g2.setColor(color);
		g2.drawLine(0, 0, image.getWidth() - 1, 0);
		g2.drawLine(0, image.getHeight() - 1, image.getWidth() - 1, image.getHeight() - 1);
		g2.drawLine(0, 0, 0, image.getHeight() - 1);
		g2.drawLine(image.getWidth() - 1, 0, image.getWidth() - 1, image.getHeight() - 1);
		g2.dispose();
		return image;
	}
	
	private static ModelTexture createTextTexture(Color color, String text){
		ByteBuffer buffer = ByteBuffer.allocate(1 + 6 + text.length() * 2);
		buffer.put(ID_TEXT);
		buffer.putInt(color.getRGB());
		buffer.putShort((short) text.length());
		for(int i = 0; i < text.length(); i++)
			buffer.putChar(text.charAt(i));
		ModelTexture texture = new ModelTexture(Loader.loadTexture(createTextImage(text, color), true), Materials.DEFAULT, buffer.array());
		textTextures.put(new ColorText(color, text), texture);
		return texture;
	}
	
	private static BufferedImage createTextImage(String text, Color color){
		int power = (int) Math.max(Math.log10(text.length() * 30) / Math.log10(2), 4);
		int s = (int) Math.pow(2, power);
		BufferedImage image = new BufferedImage(s, s, BufferedImage.TYPE_INT_ARGB);
		if(text.length() > 0){
			Graphics2D g2 = image.createGraphics();
			g2.setFont(new Font("TimesRoman", 0, 40));
			Rectangle2D bounds = g2.getFontMetrics().getStringBounds(text, g2);
			if(bounds.getWidth() == 0 || bounds.getHeight() == 0)
				return image;
			double preferredWidth = image.getWidth() * 0.9;
			double preferredHeight = image.getHeight() * 0.9;
			double factor = min(preferredWidth / bounds.getWidth(), preferredHeight / bounds.getHeight());
			g2.setColor(color);
			g2.setFont(new Font("TimesRoman", 0, (int) (40 * factor)));
			Rectangle2D newBounds = g2.getFontMetrics().getStringBounds(text, g2);
			int x = (int) ((image.getWidth() - newBounds.getWidth()) / 2);
			int y = (int) ((image.getHeight() - newBounds.getCenterY()) / 2);
			g2.drawString(text, x, y);
			g2.setColor(Color.BLACK);
			int w = (int)newBounds.getWidth();
			int h = (int) newBounds.getHeight();
			int width = w;
			int height = h;
			y -= newBounds.getHeight() / 1.5f;
			double logW = Math.log10(w) / Math.log10(2);
			if((int)logW != logW)
				w = (int) Math.pow(2, (int)logW + 1);
			double logH = Math.log10(h) / Math.log10(2);
			if((int)logH != logH)
				h = (int) Math.pow(2, (int)logH + 1);
			image = resizeImage(image, x, y, w, h, width, height);
			g2 = image.createGraphics();
			g2.setColor(Color.BLACK);
			g2.drawLine(0, 0, image.getWidth() - 1, 0);
			g2.drawLine(0, 0, 0, image.getHeight() - 1);
			g2.drawLine(0, image.getHeight() - 1, image.getWidth() - 1, image.getHeight() - 1);
			g2.drawLine(image.getWidth() - 1, 0, image.getWidth() - 1, image.getHeight() - 1);
		}//TODO trace the massive immage producing
		return image;
	}
	
	private static BufferedImage resizeImage(BufferedImage original, int x, int y, int width, int height, int preferredWidth, int preferredHeight){
		BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		int spaceX = (width - preferredWidth) / 2;
		int spaceY = (height - preferredHeight) / 2;
		newImage.createGraphics().drawImage(original, spaceX, spaceY, width + spaceX, height + spaceY, x, y, x + width, y + height, null);
		return newImage;
	}
	
	private static ModelTexture createFlagTexture(FlagColor color){
		BufferedImage image = new BufferedImage(128, 256, BufferedImage.TYPE_INT_RGB);
		int y = 0;
		while(y < image.getHeight() * 0.8f){
			for(int x = 0; x < image.getWidth(); x++)
				image.setRGB(x, y, color.stickColor.getRGB());
			++y;
		}
		while(y < image.getHeight()){
			for(int x = 0; x < image.getWidth(); x++)
				image.setRGB(x, y, color.flagColor.getRGB());
			++y;
		}
		return new ModelTexture(Loader.loadTexture(image, false), Materials.DEFAULT, null);
	}
	
	private static ModelTexture createFilledTexture(FilledColor fc){
		BufferedImage image = new BufferedImage(4, 4, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = image.createGraphics();
		g.setColor(fc.color);
		g.fillRect(0, 0, image.getWidth(), image.getHeight());
		g.dispose();
		ByteBuffer buffer = ByteBuffer.allocate(6);
		buffer.put(ID_FILLED);
		buffer.put(fc.material.getID());
		buffer.putInt(fc.color.getRGB());
		return new ModelTexture(Loader.loadTexture(image, false), fc.material, buffer.array());
	}
	
	public static ModelTexture getRosterTexture(Color color){
		ModelTexture texture = rosterTextures.get(color);
		if(texture == null){
			texture = createRosterTexture(color);
			rosterTextures.put(color, texture);
		}
		return texture;
	}
	
	private static ModelTexture createRosterTexture(Color color){
		BufferedImage image = new BufferedImage(2048, 2048, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		g.setColor(color);
		for(int x = 0; x < image.getWidth(); x += 32){
			g.drawLine(x, 0, x, image.getHeight() - 1);
			g.drawLine(x - 1, 0, x - 1, image.getHeight() - 1);
		}
		for(int y = 0; y < image.getHeight(); y += 32){
			g.drawLine(0, y, image.getWidth() - 1, y);
			g.drawLine(0, y - 1, image.getWidth() - 1, y - 1);
		}
		ByteBuffer buffer = ByteBuffer.allocate(5);
		buffer.put(ID_ROSTER);
		buffer.putInt(color.getRGB());
		return new ModelTexture(Loader.loadTexture(image, true), Materials.DEFAULT, buffer.array());
	}
	
	public static ModelTexture getPlayerTexture(){
		if(playerTexture == null)
			playerTexture = createPlayerTexture();
		return playerTexture;
	}
	
	private static ModelTexture createPlayerTexture(){
		BufferedImage image = new BufferedImage(128, 128, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = image.createGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, image.getWidth() - 1, image.getHeight() - 1);
		g.setColor(Color.RED);
		g.fillRect(22, 64, 20, 20);
		g.setColor(Color.BLUE);
		g.fillRect(86, 64, 20, 20);
		g.dispose();
		return new ModelTexture(Loader.loadTexture(image, false), Materials.IRON, new byte[]{ID_PLAYER});
	}
	
	private static class ModelData {
		
		private ArrayList<Float> vertices = new ArrayList<Float>();
		private ArrayList<Float> normals = new ArrayList<Float>();
		private ArrayList<Float> textures = new ArrayList<Float>();
		private ArrayList<Integer> indices = new ArrayList<Integer>();
		
		private RawModel create(byte[] creationID){
			float[] verticeArray = new float[vertices.size()];
	        float[] textureArray = new float[textures.size()];
	        float[] normalArray = new float[normals.size()];
	        int[] indiceArray = new int[indices.size()];
	        for(int i = 0; i < vertices.size(); i++)
	            verticeArray[i] = vertices.get(i);
	        for(int i = 0; i < textures.size(); i++)
	            textureArray[i] = textures.get(i);
	        for(int i = 0; i < normals.size(); i++)
	            normalArray[i] = normals.get(i);
	        for(int i = 0; i < indices.size(); i++)
	            indiceArray[i] = indices.get(i);
	        return Loader.loadToVAO(verticeArray, textureArray, normalArray, indiceArray, creationID);
		}
		
		@Override
		public String toString(){
			return "Model Data:[vertices = " + vertices + ", normals = " + normals + ", textures = " + textures + ", indices = " + indices + "]";
		}
	}
	
	private static class FilledColor {
		
		private final Color color;
		private final Material material;
		
		private FilledColor(Color color, Material material){
			this.color = color;
			this.material = material;
		}
		
		@Override
		public boolean equals(Object other){
			if(other instanceof FilledColor){
				FilledColor fc = (FilledColor) other;
				return fc.color.equals(color) && fc.material.equals(material);
			}
			return false;
		}
	}
	
	private static class ColorText {
		
		private final Color color;
		private final String text;
		
		private ColorText(Color color, String text){
			this.color = color;
			this.text = text;
		}
		
		@Override
		public boolean equals(Object other){
			if(other instanceof ColorText){
				ColorText ct = (ColorText) other;
				return ct.color.equals(color) && ct.text.equals(text);
			}
			return false;
		}
	}
	
	private static class FlagSettings {
		
		private final float stickLength;
		private final float stickRadius;
		
		private final float flagLength;
		private final float flagWidth;
		private final float flagHeight;
		
		private FlagSettings(float stickLength, float stickRadius, float flagLength, float flagWidth, float flagHeight){
			this.stickLength = stickLength;
			this.stickRadius = stickRadius;
			this.flagLength = flagLength;
			this.flagWidth = flagWidth;
			this.flagHeight = flagHeight;
		}
		
		@Override
		public boolean equals(Object other){
			if(other instanceof FlagSettings){
				FlagSettings flag = (FlagSettings) other;
				return flag.stickLength == stickLength && flag.stickRadius == stickRadius && flag.flagLength == flagLength && flag.flagWidth == flagWidth && flag.flagHeight == flagHeight;
			}
			return false;
		}
	}
	
	private static class FlagColor {
		
		private Color stickColor;
		private Color flagColor;
		
		private FlagColor(Color stickColor, Color flagColor){
			this.stickColor = stickColor;
			this.flagColor = flagColor;
		}
		
		@Override
		public boolean equals(Object other){
			if(other instanceof FlagColor){
				FlagColor f = (FlagColor) other;
				return f.stickColor.equals(stickColor) && f.flagColor.equals(flagColor);
			}
			return false;
		}
	}
}
