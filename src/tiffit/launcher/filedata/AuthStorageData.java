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

import tiffit.launcher.window.DownloadPanel;

public class AuthStorageData extends FileData{

	private static StorageData data;
	
	public String getClientToken(){
		if(data.clientToken == null){
			data.clientToken = UUID.randomUUID().toString();
		}
		return data.clientToken;
	}
	
	public boolean hasMap(){
		return data.map != null;
	}
	
	public Map<String, Object> getMap(){
		if(data == null) return null;
		return data.map;
	}
	
	public void setMap(Map<String, Object> map){
		data.map = map;
	}
	
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
			if(DownloadPanel.session != null)data.map = DownloadPanel.session.saveForStorage();
			else{
				data.map = null;
				data.clientToken = null;
			}
			writer.write(gson.toJson(data));
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
			data = new StorageData();
			return;
		}
		try {
			data = gson.fromJson(new FileReader(f), StorageData.class);
		} catch (JsonSyntaxException | JsonIOException | FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private File getStorageFile(){
		File f = new File(getDataFile(), "auth.json");
		return f;
	}
	
	
	public static class StorageData{
		public String clientToken;
		public Map<String, Object> map;
	}
	
}
