package tiffit.launcher.window;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.mojang.authlib.UserAuthentication;

import tiffit.launcher.DownloadBarUpdateThread;
import tiffit.launcher.LaunchSettings;
import tiffit.launcher.Launcher;
import tiffit.launcher.Main;
import tiffit.launcher.TCVersion;
import tiffit.launcher.VersionDownloadThread;
import tiffit.launcher.window.model.VersionSelectModel;

public class DownloadPanel extends JPanel {

	public static JTextField USERNAME;
	public static JPasswordField PASSWORD;
	public static JProgressBar DOWNLOAD_PROGRESS;
	public static VersionInfoPanel versioninfo;
	public static UserAuthentication session;
	private JLabel lblTalecraftVersion;
	private UserDetailsPanel userdetailsPanel;
	
	public DownloadPanel(){
		setBorder(new EmptyBorder(2, 2, 2, 2));
		setBackground(new Color(240, 240, 240));
		setLayout(null);
		setBounds(0, 0, 800, 600);
		JButton play = new JButton("Play");
		play.setFont(new Font("Arial", Font.PLAIN, 45));
		play.setBounds(157, 508, 303, 81);
		play.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				if(session != null && session.canLogIn()){
					VersionDownloadThread vdt = Main.selected.download();
					new DownloadBarUpdateThread(vdt);
				}else{
					System.out.println("Not logged in! Won't launch.");
					JOptionPane.showMessageDialog(Main.frame, "You are not logged in!", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		add(play);
		
		userdetailsPanel = new UserDetailsPanel();
		add(userdetailsPanel);
		
		add(new MainTabbedPane());
		
		DOWNLOAD_PROGRESS = new JProgressBar(0, 100);
		DOWNLOAD_PROGRESS.setForeground(new Color(0, 100, 0));
		DOWNLOAD_PROGRESS.setValue(0);
		DOWNLOAD_PROGRESS.setBounds(0, 483, 799, 17);
		DOWNLOAD_PROGRESS.setString("");
		DOWNLOAD_PROGRESS.setStringPainted(true);
		DOWNLOAD_PROGRESS.setVisible(false);
		add(DOWNLOAD_PROGRESS);
		
		JComboBox<TCVersion> versionselection = new JComboBox<TCVersion>(new VersionSelectModel());
		versionselection.setSelectedItem(Main.selected);
		versionselection.setBounds(10, 527, 100, 20);
		add(versionselection);
		
		lblTalecraftVersion = new JLabel("TaleCraft Version:");
		lblTalecraftVersion.setBounds(10, 508, 111, 20);
		add(lblTalecraftVersion);
	}
}
