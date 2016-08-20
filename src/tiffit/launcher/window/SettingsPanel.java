package tiffit.launcher.window;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStreamReader;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.google.gson.Gson;
import com.google.gson.JsonParser;

import tiffit.launcher.Main;

public class SettingsPanel extends JPanel {
	public static JTextField minRam;
	public static JTextField maxRam;

	public SettingsPanel(){
		setLayout(null);
		setBounds(0, 0, 800, 480);
		if(isOutdated()){
			JLabel version = new JLabel("The current version of the launcher is outdated!");
			version.setBounds(10, 11, 231, 14);
			version.setForeground(Color.RED);
			add(version);
		}
		
		minRam = new JTextField();
		minRam.setBounds(68, 47, 86, 20);
		add(minRam);
		minRam.setColumns(10);
		
		maxRam = new JTextField();
		maxRam.setColumns(10);
		maxRam.setBounds(68, 80, 86, 20);
		add(maxRam);
		
		JLabel maxRamLB = new JLabel("Max Ram");
		maxRamLB.setBounds(10, 83, 46, 14);
		maxRam.setText(Main.settings.ram_max);
		add(maxRamLB);
		
		JLabel minRamLB = new JLabel("Min Ram");
		minRamLB.setBounds(10, 50, 46, 14);
		minRam.setText(Main.settings.ram_min);
		add(minRamLB);
		
		JButton btnSave = new JButton("Save");
		btnSave.setBounds(39, 111, 80, 25);
		btnSave.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Main.settings.ram_max = maxRam.getText();
				Main.settings.ram_min = minRam.getText();
			}
		});
		add(btnSave);
	}
	
	private boolean isOutdated(){
		try{
			Gson gson = new Gson();
			JsonParser parser = new JsonParser();
			InputStreamReader isr = new InputStreamReader(new URL("https://raw.githubusercontent.com/tiffit/TaleCraftLauncher/master/version_info.json").openStream());
			String version = parser.parse(isr).getAsJsonObject().get("latest").getAsString();
			isr.close();
			return !version.equals(Main.VERSION);
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}
}
