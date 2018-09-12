package nl.knokko.materials;

public class Material {
	
	private final byte id;
	
	protected float shineDamper;
	protected float reflectivity;
	
	protected float density;
	protected float bounce;

	public Material(float shineDamper, float reflectivity, float density, float bounce) {
		this.shineDamper = shineDamper;
		this.reflectivity = reflectivity;
		this.density = density;
		this.bounce = bounce;
		this.id = (byte) Materials.materials.size();
		Materials.materials.add(this);
	}
	
	@Override
	public String toString(){
		return "Material:(shineDamper = " + shineDamper + ", reflectivity = " + reflectivity + ")";
	}
	
	@Override
	public boolean equals(Object other){
		if(other.getClass() == getClass()){
			Material mat = (Material) other;
			return mat.shineDamper == shineDamper && mat.reflectivity == reflectivity;
		}
		return false;
	}
	
	public float getShineDamper(){
		return shineDamper;
	}
	
	public float getReflectivity(){
		return reflectivity;
	}
	
	/**
	 * 
	 * @return The density of this material, in kg / m^3
	 */
	public float getDensity() {
		return density * 1000;
	}
	
	/**
	 * 
	 * @return The bounce factor of this material, it should be between 1 and 2
	 */
	public float getBounce(){
		return bounce;
	}

	public byte getID() {
		return id;
	}
}
