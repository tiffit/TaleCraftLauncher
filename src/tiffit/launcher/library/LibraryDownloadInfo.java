package tiffit.launcher.library;

import java.net.URL;
import java.util.Map;

public class LibraryDownloadInfo{
  private DownloadInfo artifact;
  private Map<String, DownloadInfo> classifiers;
  
  public DownloadInfo getDownloadInfo(String classifier){
    if (classifier == null) {
      return this.artifact;
    }
    return (DownloadInfo)this.classifiers.get(classifier);
  }
  
	public static class DownloadInfo {
		protected URL url;
		protected String sha1;
		protected int size;

		public URL getUrl() {
			return this.url;
		}

		public String getSha1() {
			return this.sha1;
		}

		public int getSize() {
			return this.size;
		}
	}
}