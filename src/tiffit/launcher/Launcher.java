package tiffit.launcher;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.lang.text.StrSubstitutor;

import com.mojang.authlib.UserAuthentication;

import tiffit.launcher.downloadable.Downloadable;
import tiffit.launcher.filedata.FileData;
import tiffit.launcher.library.ExtractRules;
import tiffit.launcher.library.Library;
import tiffit.launcher.library.OperatingSystem;

public class Launcher {
	
	private static File nativeDir;

	public static Process launchMinecraft(UserAuthentication auth, LaunchSettings settings, TCVersion version){
		System.out.println("launching!");
		nativeDir = new File(FileData.MINECRAFT_DATA.getMinecraftFile(), "natives");
		nativeDir.mkdirs();
		try{
			unpackNatives(nativeDir);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		ArrayList<String> arguments = new ArrayList<String>();
		File minecraftFolder = FileData.MINECRAFT_DATA.getMinecraftFile();
		String classPath = "\"" + buildClassPath(version);
		classPath +=  File.pathSeparator + FileData.VERSION_DATA.getVersionFile("mc").getPath() + File.separator + version.mc + ".jar" + File.pathSeparator;
		classPath +=  FileData.VERSION_DATA.getVersionFile("forge").getPath() + File.separator + version.forge + ".jar" + "\"";
		arguments.add("java");
		arguments.add("-Xmx" + settings.ram_max);
		arguments.add("-XX:+UseConcMarkSweepGC");
		arguments.add("-XX:+CMSIncrementalMode");
		arguments.add("-XX:-UseAdaptiveSizePolicy");
		arguments.add("-Xmn" + settings.ram_min);
		arguments.add("-Djava.library.path=" + "\"" + nativeDir.getPath() + "\"");
		arguments.add("-cp");
		arguments.add(classPath);
		arguments.add(version.getLaunchInfo().mainClass);
		Map<String, String> valuesMap = new HashMap<String, String>();
		valuesMap.put("auth_player_name", auth.getSelectedProfile().getName());
		valuesMap.put("version_name", "talecraft");
		valuesMap.put("game_directory", minecraftFolder.getPath());
		valuesMap.put("assets_root", FileData.VERSION_DATA.getVersionFile("assets").getPath());
		valuesMap.put("assets_index_name", version.assets);
		valuesMap.put("auth_uuid", auth.getSelectedProfile().getId().toString());
		valuesMap.put("auth_access_token", auth.getAuthenticatedToken());
		valuesMap.put("user_properties", auth.getUserProperties().toString());
		valuesMap.put("user_type", auth.getUserType().getName());
		String mcargs = version.getLaunchInfo().minecraftArguments;
		StrSubstitutor sub = new StrSubstitutor(valuesMap);
		
		String mctokens[] = mcargs.split(" ");
		for (int i=0; i<mctokens.length;i++)
		{
			mctokens[i] = sub.replace(mctokens[i]);
		}
		
		for (String s:mctokens)arguments.add(s);
		ProcessBuilder builder = new ProcessBuilder(arguments);
		try {
			return builder.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	

	private static String buildClassPath(TCVersion version) {
		StringBuilder result = new StringBuilder();
		Collection<File> classPath = version.getClassPath();
		String separator = System.getProperty("path.separator");
		for (File file : classPath) {
			if (!file.isFile()) {
				System.out.println("Classpath file not found: " + file);
			}
			if (result.length() > 0) {
				result.append(separator);
			}
			result.append(file.getAbsolutePath());
		}
		return result.toString();
	}
	
	
	private static void unpackNatives(File targetDir) throws IOException {
		OperatingSystem os = OperatingSystem.getCurrentPlatform();
		Collection<Library> libraries = Main.selected.getRelevantLibraries();
		for (Library library : libraries) {
			Map<OperatingSystem, String> nativesPerOs = library.getNatives();
			if ((nativesPerOs != null) && (nativesPerOs.get(os) != null)) {
				File file = new File(FileData.VERSION_DATA.getVersionFile("libs"), library.getArtifactPath((String) nativesPerOs.get(os)));
				ZipFile zip = new ZipFile(file);
				ExtractRules extractRules = library.getExtractRules();
				try {
					Enumeration<? extends ZipEntry> entries = zip.entries();
					while (entries.hasMoreElements()) {
						ZipEntry entry = (ZipEntry) entries.nextElement();
						if ((extractRules == null) || (extractRules.shouldExtract(entry.getName()))) {
							File targetFile = new File(targetDir, entry.getName());
							if (targetFile.getParentFile() != null) {
								targetFile.getParentFile().mkdirs();
							}
							if (!entry.isDirectory()) {
								BufferedInputStream inputStream = new BufferedInputStream(zip.getInputStream(entry));

								byte[] buffer = new byte['?'];
								FileOutputStream outputStream = new FileOutputStream(targetFile);
								BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
								try {
									int length;
									while ((length = inputStream.read(buffer, 0, buffer.length)) != -1) {
										bufferedOutputStream.write(buffer, 0, length);
									}
								} finally {
									Downloadable.closeSilently(bufferedOutputStream);
									Downloadable.closeSilently(outputStream);
									Downloadable.closeSilently(inputStream);
								}
							}
						}
					}
				} finally {
					zip.close();
				}
			}
		}
	}
	
}
