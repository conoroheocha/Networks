package app;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;

import com.google.crypto.tink.Aead;

public class GlobalServer extends Node {
	static Aead key;

	static final int DEFAULT_PORT = 8090; // to talk to server1

	static final int HEADER_LENGTH = 2; // giving header
	static final int TYPE_POS = 0;

	static final byte TYPE_STRING = 1;
	static final int LENGTH_POS = 1;

	static final byte TYPE_ACK = 2;

	static int localStats = 500;
	static int globalStats = 10000;

	Terminal terminal;

	GlobalServer(Terminal terminal, int port) {
		try {
			this.terminal = terminal;
			socket = new DatagramSocket(port);// listens for server1 as a client at a port
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
				Encryption decrypter = new Encryption(key); //decrypts received message

				content = decrypter.decrypt(buffer);
				terminal.println("|" + content + "|");// print out recieved
				terminal.println("Length: " + content.length());
				try {
					localStats = Integer.parseInt(content);//if it recives an int it changes its count
					globalStats = 9500 + localStats;
				}
				catch(NumberFormatException nfe){
					System.out.println("NumberFormatException: " + nfe.getMessage());
				}

				String stats = (localStats) + "," + (globalStats);
				data = stats.getBytes(StandardCharsets.UTF_8);//converts string to bytes
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

	public static class GThread1 extends Thread {
		public void run() {
			try {
				Terminal terminal = new Terminal("Global Server Port: " + DEFAULT_PORT);
				(new GlobalServer(terminal, DEFAULT_PORT)).start();
				// configures a global server terminal using global server instantiater
				terminal.println("Program completed");
			} catch (java.lang.Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void decryption(Aead aead) {
		key = aead;
		try {
			Thread newThread1 = new GThread1();
			newThread1.start();// thread to receive from server 1

		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
	}
}
