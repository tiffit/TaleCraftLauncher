package tiffit.launcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map.Entry;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import tiffit.launcher.AssetIndex.AssetObject;
import tiffit.launcher.downloadable.AssetDownloadable;
import tiffit.launcher.downloadable.DefaultDownloadable;
import tiffit.launcher.downloadable.Downloadable;
import tiffit.launcher.filedata.MinecraftData;
import tiffit.launcher.filedata.VersionData;
import tiffit.launcher.library.Library;
import tiffit.launcher.window.DownloadPanel;
import tiffit.launcher.window.MainTabbedPane;

public class VersionDownloadThread extends Thread {

	private TCVersion version;
	private int progress;
	private String progress_name;
	private boolean finished;

	public VersionDownloadThread(TCVersion version) {
		this.version = version;
		start();
	}

	@Override
	public void run() {
		System.out.println("Starting download!");
		VersionData vd = VersionData.VERSION_DATA;
		File tcFolder = vd.getVersionFile("tc");
		File tc = new File(tcFolder, version.version + ".jar");
		try {
			//TC
			progress = 0;
			progress_name = "Downloading TaleCraft...";
			new DefaultDownloadable(TCVersion.getModDownload(new URL(version.getURL())), tc).download();
			progress = 20;
			progress_name = "Downloading Forge...";
			//FORGE
			File forge = vd.getVersionFile("forge");
			new DefaultDownloadable(new URL(version.getForgeURL()), new File(forge, version.forge + ".jar")).download();
			progress = 40;
			progress_name = "Downloading Minecraft...";
			//MINECRAFT
			File mc = vd.getVersionFile("mc");
			new DefaultDownloadable(new URL(version.getMCURL()), new File(mc, version.mc + ".jar")).download();
			progress = 50;
			progress_name = "Downloading Library Info...";
			new DefaultDownloadable(new URL(version.getMCURL().replace(".jar", ".json")), new File(mc, version.mc + ".json")).download();
			new DefaultDownloadable(new URL(version.libs), new File(mc, version.mc + "_" + version.forge + ".json")).download();
			progress = 60;
			progress_name = "Downloading Libraries... (This May Take A While)";
			//LIBS
			for(Library lib : version.getLaunchInfo().libraries){
				if(lib != null){
					Downloadable dll =  lib.createDownloadable(version);
					if(dll != null){
						try{
							dll.download();
						}catch(RuntimeException e){
							System.out.println("Error while downloading library...   maybe its not important? Lets hope..");
						}
					}
				}
			}
			progress = 70;
			progress_name = "Downloading Assets... (This May Take A While)";
			//ASSETS
			File assetParent = vd.getVersionFile("assets");
			assetParent.mkdir();
			File indexes = new File(assetParent, "indexes");
			File indexFile = new File(indexes, version.assets + ".json");
			new DefaultDownloadable(new URL(version.getAssetsURL()), indexFile).download();
			Gson gson = new GsonBuilder().create();
			AssetIndex index = (AssetIndex) gson.fromJson(FileUtils.readFileToString(indexFile, Charsets.UTF_8), AssetIndex.class);
			System.out.println("Downloading assets.. this may take a while!");
			for (Entry<AssetObject, String> entry : index.getUniqueObjects().entrySet()) {
				AssetIndex.AssetObject object = (AssetIndex.AssetObject) entry.getKey();
				AssetDownloadable downloadable = index.getDownloadable(version, object);
				downloadable.download();
			}
			progress = 80;
			progress_name = "Getting ready for launch...";
			System.out.println("Download finished!");
			System.out.println("Starting setup!");
			MinecraftData mcdata = MinecraftData.MINECRAFT_DATA;
			File mods = new File(mcdata.getMinecraftFile(), "mods");
			mods.mkdir();
			FileUtils.cleanDirectory(mods);
			FileUtils.copyFileToDirectory(tc, mods);
			progress = 100;
			progress_name = "Launching Game!";
			final Process process = Launcher.launchMinecraft(DownloadPanel.session, Main.settings, version);
			new Thread(new Runnable(){

				@Override
				public void run() {
					while(process.isAlive()){
						{//Input
							BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
							int byt = -1;
							try {
								while((byt = reader.read()) != -1){
									MainTabbedPane.CONSOLE_STREAM.write(byt);
								}
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						{//Error
							BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
							int byt = -1;
							try {
								while((byt = reader.read()) != -1){
									MainTabbedPane.CONSOLE_STREAM.write(byt);
								}
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}
				
			}).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finished = true;
	}

	public boolean finished() {
		return finished;
	}

	public int getProgress() {
		return progress;
	}
	
	public String getProgressName() {
		return progress_name;
	}
}
