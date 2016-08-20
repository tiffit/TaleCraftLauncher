package tiffit.launcher.filedata;

import java.io.File;

public class FileData {
	
	public static FileData FILE_DATA = new FileData();
	public static VersionData VERSION_DATA = new VersionData();
	public static AuthStorageData AUTH_STORAGE_DATA = new AuthStorageData();
	public static MinecraftData MINECRAFT_DATA = new MinecraftData();
	public static LogData LOG_DATA = new LogData();
	public static SettingsData SETTINGS_DATA = new SettingsData();

	public File getDataFile(){
		File f = new File(new File("."), "tclauncher_data");
		f.mkdir();
		return f;
	}
	
	
}
