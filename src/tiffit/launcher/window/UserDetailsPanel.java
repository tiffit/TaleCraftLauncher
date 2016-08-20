package tiffit.launcher.window;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

import com.mojang.authlib.UserAuthentication;

import tiffit.launcher.Main;
import tiffit.launcher.filedata.FileData;
import tiffit.launcher.login.Authenticator;

public class UserDetailsPanel extends JPanel{

	public UserDetailsPanel(){
		setLayout(null);
		setBackground(new Color(240, 240, 240));
		setBounds(470, 508, 329, 81);
		addComps();
	}
	
	public void addComps(){
		removeAll();
		if(DownloadPanel.session == null || !DownloadPanel.session.canLogIn()){
			addLoginComps();
		}else{
			addLoggedInComps();
		}
		repaint();
	}
	
	private void addLoginComps(){
		JLabel usernameLabel = new JLabel("Username:");
		usernameLabel.setBounds(5 + 50, 0, 86, 20);
		add(usernameLabel);
		
		JLabel passwordLabel = new JLabel("Password:");
		passwordLabel.setBounds(5 + 50, 30, 86, 20);
		add(passwordLabel);
		
		DownloadPanel.USERNAME = new JFormattedTextField();
		DownloadPanel.USERNAME.setBounds(60 + 50, 0, 180, 25);
		add(DownloadPanel.USERNAME);
		
		DownloadPanel.PASSWORD = new JPasswordField();
		DownloadPanel.PASSWORD.setBounds(60 + 50, 30, 180, 25);
		add(DownloadPanel.PASSWORD);
		
		JButton login = new JButton("Login");
		login.setBounds(3 + 50, 60, 180 + 60, 20);
		login.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String username = DownloadPanel.USERNAME.getText();
				String password = new String(DownloadPanel.PASSWORD.getPassword());
				if(username == null || username.equals("") || password == null || password.equals("")){
					JOptionPane.showMessageDialog(Main.frame, "Username or password is empty!", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				UserAuthentication authen = Authenticator.login(username, password);
				if(authen != null){
					DownloadPanel.session = authen;
					FileData.AUTH_STORAGE_DATA.setMap(DownloadPanel.session.saveForStorage());
					UserDetailsPanel.this.addComps();
				}
			}
		});
		add(login);
	}
	
	private void addLoggedInComps(){
		JLabel loggedInLabel = new JLabel("Logged in as " + DownloadPanel.session.saveForStorage().get("username"));
		loggedInLabel.setBounds(15, 0, 300, 25);
		loggedInLabel.setFont(new Font("Ariel", Font.PLAIN, 16));
		add(loggedInLabel);
		
		JButton logout = new JButton("Logout");
		logout.setBounds(15, 30, 180 + 60, 20);
		logout.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				DownloadPanel.session.logOut();
				DownloadPanel.session = null;
				UserDetailsPanel.this.addComps();
			}
		});
		add(logout);
	}
	
}
