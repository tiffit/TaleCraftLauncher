package tiffit.launcher.window;

import java.awt.Component;
import java.io.PrintStream;

import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class MainTabbedPane extends JTabbedPane {

	public static PrintStream CONSOLE_STREAM;
	
	public MainTabbedPane(){
		this.setBounds(0, 0, 800, 480);
		DownloadPanel.versioninfo = new VersionInfoPanel();
		addTCTab("Changelog", DownloadPanel.versioninfo);
		addTCTab("Settings", new SettingsPanel());
		JTextArea console = new JTextArea();
		console.setEditable(false);
		CONSOLE_STREAM = new PrintStream(new ConsoleOutputStream(console));
		System.setErr(CONSOLE_STREAM);
		System.setOut(CONSOLE_STREAM);
		JScrollPane consoleBar = new JScrollPane(console);
		addTCTab("Console", consoleBar);
		addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				MainTabbedPane.this.repaint();
			}
		});
	}
	
	public void addTCTab(String title, Component component){
		addTab(title, component);
	}
	
}
