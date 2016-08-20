package tiffit.launcher.library;

import java.io.File;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.text.StrSubstitutor;

import tiffit.launcher.TCVersion;
import tiffit.launcher.downloadable.ChecksummedDownloadable;
import tiffit.launcher.downloadable.Downloadable;
import tiffit.launcher.downloadable.PreHashedDownloadable;
import tiffit.launcher.library.LibraryDownloadInfo.DownloadInfo;

public class Library {
	private static final StrSubstitutor SUBSTITUTOR = new StrSubstitutor(new HashMap<Object, Object>() {});
	private String name;
	private List<CompatibilityRule> rules;
	private Map<OperatingSystem, String> natives;
	private ExtractRules extract;
	private String url;
	private LibraryDownloadInfo downloads;

	public Library() {
	}

	public Library(String name) {
		if ((name == null) || (name.length() == 0)) {
			throw new IllegalArgumentException("Library name cannot be null or empty");
		}
		this.name = name;
	}

	public Library(Library library) {
		this.name = library.name;
		this.url = library.url;
		if (library.extract != null) {
			this.extract = new ExtractRules(library.extract);
		}
		if (library.rules != null) {
			this.rules = new ArrayList<CompatibilityRule>();
			for (CompatibilityRule compatibilityRule : library.rules) {
				this.rules.add(new CompatibilityRule(compatibilityRule));
			}
		}
		if (library.natives != null) {
			this.natives = new LinkedHashMap<OperatingSystem, String>();
			for (Map.Entry<OperatingSystem, String> entry : library.getNatives().entrySet()) {
				this.natives.put(entry.getKey(), entry.getValue());
			}
		}
	}

	public String getName() {
		return this.name;
	}

	public Library addNative(OperatingSystem operatingSystem, String name) {
		if ((operatingSystem == null) || (!operatingSystem.isSupported())) {
			throw new IllegalArgumentException("Cannot add native for unsupported OS");
		}
		if ((name == null) || (name.length() == 0)) {
			throw new IllegalArgumentException("Cannot add native for null or empty name");
		}
		if (this.natives == null) {
			this.natives = new EnumMap<OperatingSystem, String>(OperatingSystem.class);
		}
		this.natives.put(operatingSystem, name);
		return this;
	}

	public List<CompatibilityRule> getCompatibilityRules() {
		return this.rules;
	}

	public boolean appliesToCurrentEnvironment() {
		if (this.rules == null) {
			return true;
		}
		CompatibilityRule.Action lastAction = CompatibilityRule.Action.DISALLOW;
		for (CompatibilityRule compatibilityRule : this.rules) {
			CompatibilityRule.Action action = compatibilityRule.getAppliedAction();
			if (action != null) {
				lastAction = action;
			}
		}
		return lastAction == CompatibilityRule.Action.ALLOW;
	}

	public Map<OperatingSystem, String> getNatives() {
		return this.natives;
	}

	public ExtractRules getExtractRules() {
		return this.extract;
	}

	public Library setExtractRules(ExtractRules rules) {
		this.extract = rules;
		return this;
	}

	public String getArtifactBaseDir() {
		if (this.name == null) {
			throw new IllegalStateException("Cannot get artifact dir of empty/blank artifact");
		}
		String[] parts = this.name.split(":", 3);
		return String.format("%s/%s/%s", new Object[] { parts[0].replaceAll("\\.", "/"), parts[1], parts[2] });
	}

	public String getArtifactPath() {
		return getArtifactPath(null);
	}

	public String getArtifactPath(String classifier) {
		if(name.equals("@artifact@")){
			return null;
		}
		if (this.name == null) {
			throw new IllegalStateException("Cannot get artifact path of empty/blank artifact");
		}
		return String.format("%s/%s", new Object[] { getArtifactBaseDir(), getArtifactFilename(classifier) });
	}

	public String getArtifactFilename(String classifier) {
		if (this.name == null) {
			throw new IllegalStateException("Cannot get artifact filename of empty/blank artifact");
		}
		String[] parts = this.name.split(":", 3);
		String result = "";
		if(classifier != null){
			result = String.format("%s-%s%s.jar", new Object[] { parts[1], parts[2], "-" + classifier });
		}else{
			result = String.format("%s-%s.jar", new Object[] { parts[1], parts[2]});
		}

		return SUBSTITUTOR.replace(result);
	}

	public String toString() {
		return "Library{name='" + this.name + '\'' + ", rules=" + this.rules + ", natives=" + this.natives
				+ ", extract=" + this.extract + '}';
	}

	public Downloadable createDownloadable(TCVersion version) throws MalformedURLException {
		String path = null;
		String classifier = null;
		if (getNatives() != null) {
			classifier = (String) getNatives().get(OperatingSystem.getCurrentPlatform());
			if (classifier != null) {
				path = getArtifactPath(classifier);
			}
		} else {
			path = getArtifactPath();
		}
		if (path != null) {
			File local = new File(version.getLibsFolder(), path);

			if (this.url != null) {
				URL url = new URL(this.url + path);
				if (!local.isFile()) {
					return new ChecksummedDownloadable(Proxy.NO_PROXY, url, local, false);
				}
			} else {
				if (this.downloads == null) {
					String baseURL = "https://libraries.minecraft.net/";
					if(url != null) baseURL = url;
					URL url = new URL(baseURL + path);
					return new ChecksummedDownloadable(Proxy.NO_PROXY, url, local, false);
				}
				DownloadInfo info = this.downloads.getDownloadInfo(SUBSTITUTOR.replace(classifier));
				if (info != null) {
					return new PreHashedDownloadable(Proxy.NO_PROXY, info.getUrl(), local, false, info.getSha1());
				}
			}
		}
		return null;
	}
}