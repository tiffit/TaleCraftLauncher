package tiffit.launcher;

import tiffit.launcher.window.DownloadPanel;
import tiffit.launcher.window.Frame;

public class DownloadBarUpdateThread implements Runnable {

	private VersionDownloadThread vdt;
	private Thread thread;
	
	public DownloadBarUpdateThread(VersionDownloadThread vdt) {
		this.vdt = vdt;
		thread = new Thread(this);
		thread.start();
	}
	
	@Override
	public void run() {
		DownloadPanel.DOWNLOAD_PROGRESS.setVisible(true);
		while(!vdt.finished()){
			DownloadPanel.DOWNLOAD_PROGRESS.setValue(vdt.getProgress());
			DownloadPanel.DOWNLOAD_PROGRESS.setString(vdt.getProgressName());
			Frame.INSTANCE.repaint();
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		DownloadPanel.DOWNLOAD_PROGRESS.setVisible(false);
	}

}
