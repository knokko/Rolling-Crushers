package nl.knokko.shaders;

import org.lwjgl.util.vector.Matrix4f;

public class GuiShader extends ShaderProgram{
    
    private static final String VERTEX_FILE = "nl/knokko/shaders/gui.vshader";
    private static final String FRAGMENT_FILE = "nl/knokko/shaders/gui.fshader";
     
    private int location_transformationMatrix;
 
    public GuiShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }
     
    public void loadTransformation(Matrix4f matrix){
        super.loadMatrix(location_transformationMatrix, matrix);
    }
 
    @Override
    protected void getAllUniformLocations() {
        location_transformationMatrix = super.getUniformLocation("transformationMatrix");
    }
 
    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }
     
     
     
 
}
