package tiffit.launcher.window;

import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JTextArea;

public class ConsoleOutputStream extends OutputStream {

	private final JTextArea console;
	
	public ConsoleOutputStream(JTextArea console) {
		this.console = console;
	}
	
	@Override
	public void write(int b) throws IOException {
		console.append(String.valueOf((char)b));
		console.setCaretPosition(console.getDocument().getLength());
		console.repaint();
	}

}
