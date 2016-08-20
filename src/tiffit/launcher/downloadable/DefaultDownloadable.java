package tiffit.launcher.downloadable;

import java.io.File;
import java.io.IOException;
import java.net.Proxy;
import java.net.URL;

import org.apache.commons.io.FileUtils;

public class DefaultDownloadable extends Downloadable {

	private URL url;
	private File file;
	
	public DefaultDownloadable(URL url, File file) {
		super(Proxy.NO_PROXY, url, file, false);
		this.url = url;
		this.file = file;
	}

	@Override
	public String download() throws IOException {
		if (file.exists()) {
			return "No need to download " + (file.getParentFile().getName() + "/" + file.getName()) + ". Already exists!";
		}
		file.getParentFile().mkdirs();
		System.out.println("Downloading from " + url.toString());
		FileUtils.copyURLToFile(url, file);
		return "Downloaded " + (file.getParentFile().getName() + "/" + file.getName()) + "!";
	}

}
