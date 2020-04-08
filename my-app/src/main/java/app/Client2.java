package app;

import java.net.DatagramPacket;
import java.net.InetSocketAddress;

import com.google.crypto.tink.Aead;

public class Client2 extends Node {
	static final int DEFAULT_SRC_PORT = 50005; // Port of the client. These need to be different for all the files as
												// they are used somehow in the setup
	static final int DEFAULT_DST_PORT = 8085; // Port of the server
	static final String DEFAULT_DST_NODE = "localhost"; // Name of the host for the server
	// a lot of these ints are for checking bytes sent are the right type etc
	static final int TYPE_POS = 0; // Position of the type within the header

	static final byte TYPE_ACK = 2; // Indicating an acknowledgement

	Terminal terminal;

	private static Aead clientKey;

	public synchronized void onReceipt(DatagramPacket packet) {//when server sends message back
		byte[] data;

		data = packet.getData();
		if (data[TYPE_POS]==TYPE_ACK) {
			terminal.println("Received ack");//acknowledgement if response is the right type
			this.notify();
		}
		else{
			terminal.println("Unexpected packet" + packet.toString());
		}
	}

	public static class creatorThread extends Thread{
		public void run(){
			try {
				Terminal terminal = new Terminal("Client2 Port: " + DEFAULT_DST_PORT);
				(new Client(terminal, DEFAULT_DST_NODE, DEFAULT_DST_PORT, DEFAULT_SRC_PORT)).sendMessage(clientKey);
				//configures a client terminal using client instantiater from client.java
				terminal.println("Program completed");
			} catch (java.lang.Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void encryption(Aead key) {
		clientKey = key;
		Thread newCThread1 = new creatorThread();
		newCThread1.start();// starts creator thread to set up terminal
	}

	public static void main(String[] args) {
	}
}
