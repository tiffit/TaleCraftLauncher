package tiffit.launcher.window;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;

import tiffit.launcher.Main;
import javax.swing.border.EmptyBorder;

public class VersionInfoPanel extends JPanel {

	public VersionInfoPanel(){
		this.setLayout(null);
		this.setBorder(new EmptyBorder(20, 20, 20, 20));
		this.setBackground(Color.WHITE);
		this.setBounds(0, 0, 800, 480);
		refresh();
	}
	
	public void refresh(){
		removeAll();
		JLabel label = new JLabel("TaleCraft " + Main.selected.version);
		label.setBounds(0, 0, 800, 50);
		label.setFont(new Font("Dialog", Font.BOLD, 50));
		this.add(label);
		JLabel changelog = new JLabel("Changelog:");
		changelog.setFont(new Font("Dialog", Font.BOLD, 20));
		changelog.setBounds(0, 50, 800, 30);
		this.add(changelog);
		for(int i = 0; i < Main.selected.changelog.length; i++){
			JLabel change = new JLabel("-" + Main.selected.changelog[i]);
			change.setFont(new Font("Dialog", Font.PLAIN, 20));
			change.setBounds(5, 75 + i * 25, 800, 30);
			this.add(change);
		}
		repaint();
	}
}
