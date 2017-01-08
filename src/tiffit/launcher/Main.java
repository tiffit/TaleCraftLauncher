package tiffit.launcher;

import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import tiffit.launcher.filedata.AuthStorageData;
import tiffit.launcher.filedata.FileData;
import tiffit.launcher.login.Authenticator;
import tiffit.launcher.window.DownloadPanel;
import tiffit.launcher.window.Frame;

public class Main {

	public static final String VERSION = "1.0.1";
	
	
	public static Frame frame;
	public static LaunchSettings settings;
	public static TCVersion selected;
	public static List<TCVersion> versions;
	
	public static void main(String[] args) {
		System.out.println("Getting versions list...");
		versions = TCVersion.getVersions();
		selected = versions.get(0);
		System.out.println("Loading auth data...");
		FileData.AUTH_STORAGE_DATA.load();
		System.out.println("Loading settings data...");
		FileData.SETTINGS_DATA.load();
		DownloadPanel.session = Authenticator.fromMap(AuthStorageData.AUTH_STORAGE_DATA.getMap());
		Runtime.getRuntime().addShutdownHook(new Thread(){
			@Override
			public void run() {
				FileData.AUTH_STORAGE_DATA.store();
				FileData.SETTINGS_DATA.store();
			}
		});
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
            	try {
            	    UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            	    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            	} catch(Exception e){
            		e.printStackTrace();
            	}
            	frame = new Frame();
            	System.out.println("Launching launcher...");
            }
        });
		
	}

}
