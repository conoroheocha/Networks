package app;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import com.google.crypto.tink.Aead;


public class Server extends Node {
	static Aead key;

	static final int SOURCE_PORT = 50010;// just necessary for client function. These need to be different for all
												// the files as they are used in the setup
	static final int DEFAULT_PORT = 8080; // listen for client 1
	static final int DEFAULT_PORT2 = 8085; // listen for client 2
	static final int DESTINATION_PORT = 8090;// talk to global server
	static final String DESTINATION_NODE = "localhost";

	static final int HEADER_LENGTH = 2; // giving header
	static final int TYPE_POS = 0;
	static final byte TYPE_STRING = 1;
	static final int LENGTH_POS = 1;

	static int localStats = 500;
	static int globalStats = 10000;

	Terminal terminal;

	Server(Terminal terminal, int port) {
		try {
			this.terminal = terminal;
			socket = new DatagramSocket(port);// listens for client at a port
			listener.go();
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized void onReceipt(DatagramPacket packet) {//when it receives from client
		try {
			String content;
			byte[] data;
			byte[] buffer;

			data = packet.getData();
			if (data[TYPE_POS]==TYPE_STRING) {//if message is a string
				buffer = new byte[data[LENGTH_POS]];
				System.arraycopy(data, HEADER_LENGTH, buffer, 0, buffer.length);
				Encryption decrypter = new Encryption(key);//decrypts received message

				content = decrypter.decrypt(buffer);
				terminal.println("|" + content + "|");// print out recieved
				terminal.println("Length: " + content.length());
				if (content.equals("covid")) {//if it recives "covid" it increase its count
					localStats++;
				}

				String stats = (localStats) + "," + (globalStats);
				data = stats.getBytes(StandardCharsets.UTF_8);// converts string to bytes
				String str = new String(data, StandardCharsets.UTF_8);
				terminal.println("Response to send: " + str);//prep stats to send

				DatagramPacket response;
				response = new DatagramPacket(data, data.length);
				response.setSocketAddress(packet.getSocketAddress());
				socket.send(response);// send back stats
			}
			else{
				terminal.println("Unexpected packet" + packet.toString());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized void start() throws Exception {
		terminal.println("Waiting for contact");
		this.wait();//server waits to receive message from client
	}

	public static class Thread1 extends Thread {
		public void run() {
			try {
				Terminal terminal = new Terminal("Server1 Port: " + DEFAULT_PORT);
				(new Server(terminal, DEFAULT_PORT)).start();
				// configures a server terminal using server instantiater
				terminal.println("Program completed");
			} catch (java.lang.Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static class Thread2 extends Thread {
		public void run() {
			try {
				Terminal terminal = new Terminal("Server1 Port: " + DEFAULT_PORT2);
				(new Server(terminal, DEFAULT_PORT2)).start();
				// configures a server terminal using server instantiater
				terminal.println("Program completed");
			} catch (java.lang.Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static class sendThread extends Thread {
		public void run() {
			try {
				Terminal terminal = new Terminal("Server1 client facility Port: " + DESTINATION_PORT);
				(new Client(terminal, DESTINATION_NODE, DESTINATION_PORT, SOURCE_PORT)).sendMessage(key);
				//configures a client terminal using client instantiater from client.java
				terminal.println("Program completed");
			} catch (java.lang.Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void decryption(Aead aead) {
		key = aead;

		try {
			Thread newThread1 = new Thread1();
			newThread1.start();// thread to listen for client 1

			Thread newThread2 = new Thread2();
			newThread2.start();// thread to listen to client 2

			Thread sendThread1 = new sendThread();
			sendThread1.start();// thread to send to global server

		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
	}
}