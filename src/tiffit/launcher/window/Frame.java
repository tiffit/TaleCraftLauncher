package tiffit.launcher.window;

import javax.swing.JFrame;

public class Frame extends JFrame {

	public static Frame INSTANCE;
	
	public Frame(){
		INSTANCE = this;
		setVisible(true);
		setTitle("TaleCraft Launcher");
		setSize(800, 650);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);
		add(new DownloadPanel());
	}
}
