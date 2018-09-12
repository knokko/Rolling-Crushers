package nl.knokko.render.model;

public class TexturedModel {
	
	private RawModel model;
	private ModelTexture texture;

	public TexturedModel(RawModel model, ModelTexture texture) {
		this.model = model;
		this.texture = texture;
	}
	
	@Override
	public boolean equals(Object other){
		if(other == this)
			return true;
		if(other instanceof TexturedModel){
			TexturedModel t = (TexturedModel) other;
			return t.model.equals(model) && t.texture.equals(texture);
		}
		return false;
	}
	
	public RawModel getModel(){
		return model;
	}
	
	public ModelTexture getTexture(){
		return texture;
	}
}
