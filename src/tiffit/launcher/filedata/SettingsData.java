package tiffit.launcher.filedata;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import tiffit.launcher.LaunchSettings;
import tiffit.launcher.Main;
import tiffit.launcher.window.DownloadPanel;

public class SettingsData extends FileData{
	
	public void store(){
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		File f = getStorageFile();
		if(!f.exists()){
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			FileWriter writer = new FileWriter(f);
			writer.write(gson.toJson(Main.settings));
			writer.close();
		} catch (JsonSyntaxException | JsonIOException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public void load(){
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		File f = getStorageFile();
		if(!f.exists()){
			Main.settings = new LaunchSettings();
			return;
		}
		try {
			Main.settings = gson.fromJson(new FileReader(f), LaunchSettings.class);
		} catch (JsonSyntaxException | JsonIOException | FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private File getStorageFile(){
		File f = new File(getDataFile(), "settings.json");
		return f;
	}
	
}
