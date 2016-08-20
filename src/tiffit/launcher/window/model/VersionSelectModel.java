package tiffit.launcher.window.model;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataListener;

import tiffit.launcher.Main;
import tiffit.launcher.TCVersion;
import tiffit.launcher.window.DownloadPanel;

public class VersionSelectModel implements ComboBoxModel<TCVersion> {

	private List<ListDataListener> listeners = new ArrayList<ListDataListener>();
	
	@Override
	public void addListDataListener(ListDataListener l) {
		listeners.add(l);
	}

	@Override
	public TCVersion getElementAt(int index) {
		return Main.versions.get(index);
	}

	@Override
	public int getSize() {
		return Main.versions.size();
	}

	@Override
	public void removeListDataListener(ListDataListener l) {
		listeners.remove(l);
	}

	@Override
	public Object getSelectedItem() {
		return Main.selected;
	}

	@Override
	public void setSelectedItem(Object anItem) {
		Main.selected = (TCVersion) anItem;
		DownloadPanel.versioninfo.refresh();
	}

}
