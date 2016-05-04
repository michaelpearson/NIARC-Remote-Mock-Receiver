import java.awt.BorderLayout;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JTextPane;


public class MockReceiver {
	private ServerSocket socket;
	private JTextPane textPanel;
	private static final int DATA_LENGTH = 6;
	public static void main(String[] args) {
		new MockReceiver();
	}
	public MockReceiver() {
		try {
			socket = new ServerSocket(1100);
		} catch (IOException e) {
			e.printStackTrace();
		}
		new Listener();
	}
	private void accept(Socket client){
		System.out.println("Accepted Connection");
		new DataReceiver(client);
	}

	private class Listener extends Thread {
		public Listener() {
			start();
		}
		@Override
		public void run() {
			while(!interrupted()){
				try {
					Socket client = MockReceiver.this.socket.accept();
					MockReceiver.this.accept(client);
				} catch (IOException e) {
					return;
				}
			}
			super.run();
		}
	}

	private class DataReceiver extends Thread {
		private Socket socket;
		public DataReceiver(Socket socket) {
			this.socket = socket;
			start();
		}
		@Override
		public void run() {
			try {
				InputStream is = socket.getInputStream();
				while(socket.isConnected()) {
					int buffer[] = new int[6];
					for(int a = 0;a < 6;a++) {
						int d = is.read();
						if(d == -1)
							throw new IOException();
						buffer[a] = d & 0xFF;
					}
					System.out.printf("\r\033[K%d\t%d\t%d\t%d\t%d\t%d", buffer[0], buffer[1], buffer[2], buffer[3], buffer[4], buffer[5]);
				}
			} catch(IOException e) {}
			System.out.println("\nThread died");
		}
	}
}
