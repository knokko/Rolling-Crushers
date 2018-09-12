package nl.knokko.levels;

import java.io.File;
import java.util.ArrayList;

public class Levels {

	public static ArrayList<LevelFile> getLevels(File[] files){
		ArrayList<LevelFile> levels = new ArrayList<LevelFile>();
		for(File file : files){
			if(file.getName().endsWith(".level"))
				levels.add(new LevelFile(file));
		}
		return levels;
	}

}
