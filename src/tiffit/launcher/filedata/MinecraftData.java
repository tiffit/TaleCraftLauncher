package tiffit.launcher.filedata;

import java.io.File;

public class MinecraftData extends FileData{

	public File getMinecraftFile(){
		File minecraft = new File(getDataFile(), "minecraft");
		minecraft.mkdir();
		return minecraft;
	}
	
	
}
