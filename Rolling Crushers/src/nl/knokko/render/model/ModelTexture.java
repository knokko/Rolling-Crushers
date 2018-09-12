package nl.knokko.render.model;

import nl.knokko.materials.Material;
import nl.knokko.materials.Materials;

public class ModelTexture {
	
	private int id;
	
	private Material material;
	
	private byte[] creationID;

	public ModelTexture(int textureID, Material material, byte[] creationID) {
		id = textureID;
		this.material = material;
		this.creationID = creationID;
	}
	
	public ModelTexture(int textureID, byte[] creationID){
		id = textureID;
		material = Materials.DEFAULT;
		this.creationID = creationID;
	}
	
	@Override
	public boolean equals(Object other){
		if(other instanceof ModelTexture){
			ModelTexture m = (ModelTexture) other;
			return m.id == id && material.equals(m.material);
		}
		return false;
	}
	
	/**
	 * The creation ID of a texture describes how this texture can be recreated.
	 * @return the creation ID of this texture
	 */
	public byte[] getCreationID(){
		return creationID;
	}
	
	public int getID(){
		return id;
	}
	
	public float getShineDamper(){
		return material.getShineDamper();
	}
	
	public float getReflectivity(){
		return material.getReflectivity();
	}
	
	public Material getMaterial(){
		return material;
	}
}
