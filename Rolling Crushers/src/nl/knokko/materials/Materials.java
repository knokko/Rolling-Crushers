package nl.knokko.materials;

import java.util.ArrayList;

public final class Materials {
	
	static ArrayList<Material> materials = new ArrayList<Material>();

	public static final Material IRON = new Material(1f, 0.2f, 7.87f, 1.0f);
	public static final Material DEFAULT = new Material(1f, 0.1f, 1.0f, 1.0f);
	
	public static Material fromID(int id){
		return materials.get(id);
	}
}
