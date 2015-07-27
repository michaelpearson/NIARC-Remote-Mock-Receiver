import java.awt.BorderLayout;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JTextPane;


public class Main extends JFrame {
	private ServerSocket socket;
	private JTextPane textPanel;
	private static final int DATA_LENGTH = 5;
	public static void main(String[] args) {
		new Main();
	}
	public Main() {
		try {
			socket = new ServerSocket(1100);
		} catch (IOException e) {
			e.printStackTrace();
		}
		new Listener();
		
		this.setLayout(new BorderLayout());
		
		textPanel = new JTextPane();
		textPanel.setText("No Data");
		this.add(textPanel);
		
		setSize(500, 200);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		setVisible(true);
	}
	private void accept(Socket client){
		System.out.println("Accepted Connection");
		new DataReceiver(client);
	}
	private void dataReady(byte[] buffer) {
		textPanel.setText(String.format("%d\t%d\t%d\t%d\t%d", buffer[0] & 0xFF, buffer[1] & 0xFF, buffer[2] & 0xFF, buffer[3] & 0xFF, buffer[4] & 0xFF));
	}
	private class Listener extends Thread {
		public Listener() {
			start();
		}
		@Override
		public void run() {
			while(!interrupted()){
				try {
					Socket client = Main.this.socket.accept();
					Main.this.accept(client);
				} catch (IOException e) {
					return;
				}
			}
			super.run();
		}
	}
	private class DataReceiver extends Thread {
		private Socket socket;
		private InputStream inputStream;
		public DataReceiver(Socket socket) {
			this.socket = socket;
			start();
		}
		@Override
		public void run() {
			try {
				this.inputStream = socket.getInputStream();
			} catch (IOException e) {}
			while(socket.isConnected()) {
				try {
					int length = inputStream.available();
					if(length >= DATA_LENGTH) {
						byte[] buffer = new byte[DATA_LENGTH];
						inputStream.read(buffer, 0, DATA_LENGTH);
						Main.this.dataReady(buffer);						
					}
					sleep(10);
				} catch(IOException | InterruptedException e) {
					return;
				}
			}
		}
	}
}