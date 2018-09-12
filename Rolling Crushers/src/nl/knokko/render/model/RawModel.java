package nl.knokko.render.model;

public class RawModel {
	
	private byte[] creationID;
	
	private int id;
	private int vertexCount;

	public RawModel(int modelID, int vertexCount, byte[] creationID) {
		id = modelID;
		this.vertexCount = vertexCount;
		this.creationID = creationID;
	}
	
	@Override
	public boolean equals(Object other){
		if(other == this)
			return true;
		if(other instanceof RawModel){
			RawModel m = (RawModel) other;
			return m.id == id && m.vertexCount == vertexCount;
		}
		return false;
	}
	
	public int getID(){
		return id;
	}
	
	public int getVertexCount(){
		return vertexCount;
	}
	
	/**
	 * This id can be used to recreate this model.
	 * @return The creation ID of this model
	 */
	public byte[] getCreationID(){
		return creationID;
	}
}
