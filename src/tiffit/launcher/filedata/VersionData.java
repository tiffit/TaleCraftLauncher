package tiffit.launcher.filedata;

import java.io.File;

public class VersionData extends FileData{

	public File getVersionFile(String type){
		File versions = new File(getDataFile(), "versions");
		versions.mkdir();
		File folder = new File(versions, type);
		folder.mkdir();
		return folder;
	}
	
}
