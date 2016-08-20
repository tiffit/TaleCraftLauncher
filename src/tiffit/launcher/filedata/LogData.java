package tiffit.launcher.filedata;

import java.io.File;
import java.io.IOException;

public class LogData extends FileData {

	public File getLogFile(){
		File logs = new File(getDataFile(), "logs");
		logs.mkdir();
		File log = new File(logs, "log.txt");
		if(!log.exists()){
			try {
				log.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return log;
	}
	
}
