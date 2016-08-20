package tiffit.launcher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import tiffit.launcher.filedata.FileData;
import tiffit.launcher.filedata.VersionData;
import tiffit.launcher.library.Library;
import tiffit.launcher.typeadapter.LowerCaseEnumTypeAdapterFactory;

public class TCVersion {
	public String version;
	public String mc;
	public String forge;
	public String libs;
	public int id;
	public String type;
	public String assets;
	public String curse;
	public String[] changelog;

	private LaunchInfo launchInfo;

	public String getURL() {
		return "https://minecraft.curseforge.com/projects/talecraft/files/" + id + "/download";
	}

	public String getForgeURL() {
		return "http://files.minecraftforge.net/maven/net/minecraftforge/forge/" + forge + "/forge-" + forge
				+ "-universal.jar";
	}

	public String getMCURL() {
		return "https://s3.amazonaws.com/Minecraft.Download/versions/" + mc + "/" + mc + ".jar";
	}

	public String getAssetsURL() {
		return "https://s3.amazonaws.com/Minecraft.Download/indexes/" + assets + ".json";
	}

	public String getAssetObjectURL(String hash) {
		return "http://resources.download.minecraft.net/" + hash.substring(0, 2) + "/" + hash;
	}

	public VersionDownloadThread download() {
		return new VersionDownloadThread(this);
	}

	public File getLibsFolder() {
		return VersionData.VERSION_DATA.getVersionFile("libs");
	}
	
	public Collection<Library> getRelevantLibraries() {
		List<Library> result = new ArrayList<Library>();
		for (Library library : getLaunchInfo().libraries) {
			if (library.appliesToCurrentEnvironment()) {
				result.add(library);
			}
		}
		return result;
	}
	
	public Collection<File> getClassPath() {
		Collection<Library> libraries = getRelevantLibraries();
		Collection<File> result = new ArrayList<File>();
		for (Library library : libraries) {
			if (library.getNatives() == null) {
				String artifactPath = library.getArtifactPath();
				if(artifactPath != null){
					result.add(new File(getLibsFolder(), artifactPath));
				}
			}
		}

		return result;
	}

	public LaunchInfo getLaunchInfo() {
		if (launchInfo != null)
			return launchInfo;
		Gson gson = new GsonBuilder().registerTypeAdapterFactory(new LowerCaseEnumTypeAdapterFactory()).create();
		JsonParser parser = new JsonParser();
		try {
			LaunchInfo mcinfo = gson.fromJson(new FileReader(new File(FileData.VERSION_DATA.getVersionFile("mc"), mc + ".json")), LaunchInfo.class);
			JsonObject forgeInfoObject = parser.parse(new FileReader(new File(FileData.VERSION_DATA.getVersionFile("mc"), mc + "_" + forge + ".json"))).getAsJsonObject();
			JsonElement element = forgeInfoObject.getAsJsonObject("versionInfo");
			LaunchInfo forgeinfo =  gson.fromJson(element, LaunchInfo.class);
			return LaunchInfo.merge(mcinfo, forgeinfo);
		} catch (JsonSyntaxException | JsonIOException | FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static URL getModDownload(URL url) throws IOException {
		URLConnection urlc = url.openConnection();
		urlc.connect();
		try {
//			for(String key : urlc.getHeaderFields().keySet()){
//				List<String> headers = urlc.getHeaderFields().get(key);
//				for(String header : headers){
//					System.out.print(key + ": " + header + ", ");
//				}
//				System.out.print("\n");
//			}
			String newLocation = urlc.getHeaderField("Location");
			if (newLocation == null || newLocation.trim().equals("")) {
				System.out.println("No mod download!");
				return url;
			}
			return new URL(newLocation.replace("http:", "https:"));
		} finally {
			try {
				urlc.getInputStream().close();
			} catch (Exception eee) {
			}
		}
	}

	public String toString() {
		return version;
	}

	public static class LaunchInfo {
		public String mainClass;
		public String minecraftArguments;
		public Library[] libraries;
		
		public static LaunchInfo merge(LaunchInfo mc, LaunchInfo forge){
			LaunchInfo info = new LaunchInfo();
			info.mainClass = forge.mainClass;
			info.minecraftArguments = forge.minecraftArguments;
			Library[] libs = new Library[mc.libraries.length + forge.libraries.length];
			for(int i = 0; i < mc.libraries.length; i++){
				libs[i] = mc.libraries[i];
			}
			for(int i = 0; i < forge.libraries.length; i++){
				libs[mc.libraries.length + i] = forge.libraries[i];
			}
			info.libraries = libs;
			return info;
		}
	}

	public static List<TCVersion> getVersions() {
		Gson gson = new Gson();
		try {
			InputStreamReader r = new InputStreamReader(new URL("https://raw.githubusercontent.com/tiffit/TaleCraft/master/version_info/info.json").openStream());
			JsonParser jsonParser = new JsonParser();
			JsonObject tc = (JsonObject) jsonParser.parse(r);
			JsonArray files = tc.getAsJsonArray("versions");
			List<TCVersion> versions = new ArrayList<TCVersion>();
			for (JsonElement element : files) {
				TCVersion version = gson.fromJson(element, TCVersion.class);
				versions.add(version);
			}
			return versions;
		} catch (IOException e) {
			e.printStackTrace();
			return Lists.newArrayList();
		}
	}


}
