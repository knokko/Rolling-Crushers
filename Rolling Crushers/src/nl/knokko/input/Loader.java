package nl.knokko.input;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import nl.knokko.render.model.RawModel;
import static org.lwjgl.opengl.GL11.*;

public class Loader {
	
	private static ArrayList<Integer> vaos = new ArrayList<Integer>();
	private static ArrayList<Integer> vbos = new ArrayList<Integer>();
	private static ArrayList<Integer> textures = new ArrayList<Integer>();

	public static RawModel loadToVAO(float[] positions, float[] textureCoords, float[] normals, int[] indices, byte[] creationID){
		int vaoID = createVAO();
		bindIndicesBuffer(indices);
		storeDataInAttributeList(0, 3, positions);
		storeDataInAttributeList(1, 2, textureCoords);
		storeDataInAttributeList(2, 3, normals);
		unbindVAO();
		return new RawModel(vaoID, indices.length, creationID);
	}
	
	public static RawModel loadToVAO(float[] positions){
		int vaoID = createVAO();
		storeDataInAttributeList(0, 2, positions);
		unbindVAO();
		return new RawModel(vaoID, positions.length / 2, null);
	}
	
	/*
	public static RawModel loadObjToVAO(String fileName){
		FileReader fr = null;
        try {
            fr = new FileReader(Loader.class.getClassLoader().getResource("models/" + fileName + ".obj").getFile());
        } catch (FileNotFoundException e) {
            System.err.println("Couldn't load file!");
            e.printStackTrace();
        }
        BufferedReader reader = new BufferedReader(fr);
        List<Vector3f> vertices = new ArrayList<Vector3f>();
        List<Vector2f> textures = new ArrayList<Vector2f>();
        List<Vector3f> normals = new ArrayList<Vector3f>();
        List<Integer> indices = new ArrayList<Integer>();
        float[] verticesArray = null;
        float[] normalsArray = null;
        float[] textureArray = null;
        int[] indicesArray = null;
        try {
        	String line = reader.readLine();
            while (line != null) {
                String[] currentLine = line.split(" ");
                if(currentLine.length > 1 && currentLine[1].isEmpty())
            		currentLine = new String[]{currentLine[0], currentLine[2], currentLine[3], currentLine[4]};
                if (line.startsWith("v ")) {
                    Vector3f vertex = new Vector3f(Float.parseFloat(currentLine[1]),
                            Float.parseFloat(currentLine[2]), Float.parseFloat(currentLine[3]));
                    vertices.add(vertex);
                } else if (line.startsWith("vt ")) {
                    Vector2f texture = new Vector2f(Float.parseFloat(currentLine[1]),
                            Float.parseFloat(currentLine[2]));
                    textures.add(texture);
                } else if (line.startsWith("vn ")) {
                    Vector3f normal = new Vector3f(Float.parseFloat(currentLine[1]),
                            Float.parseFloat(currentLine[2]), Float.parseFloat(currentLine[3]));
                    normals.add(normal);
                }
                line = reader.readLine();
            }
            reader.close();
            fr = new FileReader(new File("resources/models/" + fileName + ".obj"));
            reader = new BufferedReader(fr);
            textureArray = new float[vertices.size() * 2];
            normalsArray = new float[vertices.size() * 3];
            line = reader.readLine();
            while (line != null) {
                if (!line.startsWith("f ")) {
                    line = reader.readLine();
                    continue;
                }
                String[] currentLine = line.split(" ");
                String[] vertex1 = currentLine[1].split("/");
                String[] vertex2 = currentLine[2].split("/");
                String[] vertex3 = currentLine[3].split("/");
                 
                processVertex(vertex1,indices,textures,normals,textureArray,normalsArray);
                processVertex(vertex2,indices,textures,normals,textureArray,normalsArray);
                processVertex(vertex3,indices,textures,normals,textureArray,normalsArray);
                line = reader.readLine();
            }
            reader.close();
 
        } catch (Exception e) {
            e.printStackTrace();
        }
         
        verticesArray = new float[vertices.size()*3];
        indicesArray = new int[indices.size()];
         
        int vertexPointer = 0;
        for(Vector3f vertex:vertices){
            verticesArray[vertexPointer++] = vertex.x;
            verticesArray[vertexPointer++] = vertex.y;
            verticesArray[vertexPointer++] = vertex.z;
        }
         
        for(int i=0;i<indices.size();i++){
            indicesArray[i] = indices.get(i);
        }
        save(verticesArray, textureArray, normalsArray, indicesArray, fileName + ".model");
        return loadToVAO(verticesArray, textureArray, normalsArray, indicesArray);
 
	}
	*/
	
	/*
	private static void processVertex(String[] vertexData, List<Integer> indices, List<Vector2f> textures, List<Vector3f> normals, float[] texturesArray, float[] normalsArray){
		int currentVertexPointer = Integer.parseInt(vertexData[0]) - 1;
        indices.add(currentVertexPointer);
        int texIndex = Integer.parseInt(vertexData[1])-1;
        Vector2f currentTex = textures.get(texIndex);
        texturesArray[currentVertexPointer*2] = currentTex.x;
        texturesArray[currentVertexPointer*2+1] = 1 - currentTex.y;
        if(vertexData.length > 2){
        int normIndex = Integer.parseInt(vertexData[2])-1;
        if(normIndex < normals.size()){
        	Vector3f currentNorm = normals.get(normIndex);
        	normalsArray[currentVertexPointer*3] = currentNorm.x;
        	normalsArray[currentVertexPointer*3+1] = currentNorm.y;
        	normalsArray[currentVertexPointer*3+2] = currentNorm.z; 
        	}
        }
	}
	*/
	
	/*
	public static RawModel loadModelToVAO(String fileName){
		byte[] bytes = null;
		try {
			FileInputStream stream = new FileInputStream(new File("resources/models/" + fileName + ".model"));
			//InputStream stream = Loader.class.getClassLoader().getResource("models/" + fileName + ".model").openStream();
			bytes = new byte[stream.available()];
			stream.read(bytes);
			stream.close();
		} catch(IOException ex){
			ex.printStackTrace();
		}
		ByteBuffer buffer = ByteBuffer.wrap(bytes);
		int length = buffer.getInt();
		float[] vertices = new float[length * 3];
		float[] textureCoords = new float[length * 2];
		float[] normals = new float[length * 3];
		int[] indices = new int[(buffer.limit() - (32 * length + 4)) / 4];
		int i = 0;
		while(i < length){
			float x = buffer.getFloat();
			float y = buffer.getFloat();
			float z = buffer.getFloat();
			vertices[i * 3 + 0] = x;
			vertices[i * 3 + 1] = y;
			vertices[i * 3 + 2] = z;
			++i;
		}
		i = 0;
		while(i < length){
			float u = buffer.getFloat();
			float v = buffer.getFloat();
			textureCoords[i * 2 + 0] = u;
			textureCoords[i * 2 + 1] = v;
			++i;
		}
		i = 0;
		while(i < length){
			float x = buffer.getFloat();
			float y = buffer.getFloat();
			float z = buffer.getFloat();
			normals[i * 3 + 0] = x;
			normals[i * 3 + 1] = y;
			normals[i * 3 + 2] = z;
			++i;
		}
		i = 0;
		while(i < indices.length){
			indices[i] = buffer.getInt();
			++i;
		}
		return loadToVAO(vertices, textureCoords, normals, indices);
	}
	*/
	
	public static void save(float[] vertices, float[] textureCoords, float[] normals, int[] indices, String fileName){
		ByteBuffer buffer = ByteBuffer.allocate(vertices.length * 4 + textureCoords.length * 4 + normals.length * 4 + indices.length * 4 + 4);
		buffer.putInt(vertices.length / 3);
		int i = 0;
		while(i < vertices.length){
			buffer.putFloat(vertices[i]);
			++i;
		}
		i = 0;
		while(i < textureCoords.length){
			buffer.putFloat(textureCoords[i]);
			++i;
		}
		i = 0;
		while(i < normals.length){
			buffer.putFloat(normals[i]);
			++i;
		}
		i = 0;
		while(i < indices.length){
			buffer.putInt(indices[i]);
			++i;
		}
		try {
			FileOutputStream stream = new FileOutputStream(fileName);
			stream.write(buffer.array());
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static int loadTexture(String fileName){
		Texture texture = null;
		try {
			texture = TextureLoader.getTexture("PNG", Loader.class.getClassLoader().getResource("textures/" + fileName + ".png").openStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		int id = texture.getTextureID();
		textures.add(id);
		return id;
	}
	
	public static int loadTexture(BufferedImage image, boolean allowAlpha){
	    ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * (allowAlpha ? 4 : 3)); //4 for RGBA, 3 for RGB
	    for(int y = 0; y < image.getHeight(); y++){
	        for(int x = 0; x < image.getWidth(); x++){
	        	Color color = new Color(image.getRGB(x, y));
	        	if(allowAlpha)
	        		color = new Color(color.getRed(), color.getGreen(), color.getBlue(), image.getAlphaRaster().getPixel(x, y, new int[1])[0]);
	            buffer.put((byte) color.getRed());     // Red component
	            buffer.put((byte) color.getGreen());      // Green component
	            buffer.put((byte) color.getBlue());               // Blue component
	            if(allowAlpha)
	            	buffer.put((byte) color.getAlpha());    // Alpha component. Only for RGBA
	        }
	    }

	    buffer.flip(); //FOR THE LOVE OF GOD DO NOT FORGET THIS

	        // You now have a ByteBuffer filled with the color data of each pixel.
	        // Now just create a texture ID and bind it. Then you can load it using 
	        // whatever OpenGL method you want, for example:

	    int textureID = glGenTextures(); //Generate texture ID
	    glBindTexture(GL_TEXTURE_2D, textureID); //Bind texture ID
	        
	        //Setup wrap mode
	    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
	    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

	        //Setup texture scaling filtering
	    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
	    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
	        
	        //Send texel data to OpenGL
	    glTexImage2D(GL_TEXTURE_2D, 0, allowAlpha ? GL_RGBA8 : GL_RGB8, image.getWidth(), image.getHeight(), 0, allowAlpha ? GL_RGBA : GL_RGB, GL_UNSIGNED_BYTE, buffer);
	      
	        //Return the texture ID so we can bind it later again
	    return textureID;
	}
	
	public static void cleanUp(){
		for(int vao : vaos)
			GL30.glDeleteVertexArrays(vao);
		for(int vbo : vbos)
			GL15.glDeleteBuffers(vbo);
		for(int texture : textures)
			GL11.glDeleteTextures(texture);
	}
	
	private static int createVAO(){
		int vaoID = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vaoID);
		vaos.add(vaoID);
		return vaoID;
	}
	
	private static void storeDataInAttributeList(int attributeNumber, int coordinateSize, float[] data){
		int vboID = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
		vbos.add(vboID);
		FloatBuffer buffer = storeDataInFloatBuffer(data);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(attributeNumber, coordinateSize, GL11.GL_FLOAT, false, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
	
	private static void unbindVAO(){
		GL30.glBindVertexArray(0);
	}
	
	private static void bindIndicesBuffer(int[] indices){
		int vboID = GL15.glGenBuffers();
		vbos.add(vboID);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
		IntBuffer buffer = storeDataInIntBuffer(indices);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
	}
	
	private static IntBuffer storeDataInIntBuffer(int[] data){
		IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}
	
	private static FloatBuffer storeDataInFloatBuffer(float[] data){
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}
}
