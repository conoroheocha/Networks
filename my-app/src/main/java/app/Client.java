package app;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import com.google.crypto.tink.Aead;

/**
 *
 * Client class -
 *
 * An instance accepts input from the user It then leads this into a datagram,
 * sends it to a server and wait for reply When a packet has been received, the
 * type of the packet is checked and if it is an acknowledgement. Message is
 * printed and the waiting main method is being notified
 *
 */
public class Client extends Node {
	static final int DEFAULT_SRC_PORT = 50000; // Port of the client. These need to be different for all the files as
												// they are used somehow in the setup
	static final int DEFAULT_DST_PORT = 8080; // Port of the server
	static final String DEFAULT_DST_NODE = "localhost"; // Name of the host for the server

	static final int HEADER_LENGTH = 2; // Fixed length of the header
	static final int TYPE_POS = 0; // Position of the type within the header

	static final byte TYPE_UNKNOWN = 0;

	static final byte TYPE_STRING = 1; // Indicating a string payload
	static final int LENGTH_POS = 1;

	static final byte TYPE_ACK = 2; // Indicating an acknowledgement

	Terminal terminal;
	InetSocketAddress dstAddress;
	private static Aead clientKey;

	/**
	 * Constructor
	 *
	 * Attempts to create socket at given port and create an InetSocketAddress for
	 * the destinations
	 */
	Client(Terminal terminal, String dstHost, int dstPort, int srcPort) {
		try {
			terminal.println("clienting");
			this.terminal = terminal;
			dstAddress = new InetSocketAddress(dstHost, dstPort);
			socket = new DatagramSocket(srcPort);
			listener.go();
			System.out.println("check2");
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Assume that incoming packets contain a String and print the string.
	 */
	public synchronized void onReceipt(DatagramPacket packet) {
		byte[] data;

		data = packet.getData();

		terminal.println("Received Ack");

		String str = new String(data, StandardCharsets.UTF_8);
		terminal.println("Passed String: " + str);

		this.notify();
		/*
		 * switch (data[TYPE_POS]) { case TYPE_ACK: terminal.println("Received ack");
		 * terminal.println("Unexpected packet" + data.toString()); this.notify();
		 * break; default: terminal.println("Unexpected packet" + packet.toString()); }
		 */
	}

	/**
	 * Sender Method
	 *
	 */
	public synchronized void sendPacket(DatagramPacket packet, byte[] data) throws Exception {
		terminal.println("Sending packet...");
		// String dataString = data.toString();
		// byte[] encrypted = Encryption.encrypt(dataString); not working
		packet = new DatagramPacket(data, data.length);
		packet.setSocketAddress(dstAddress);
		socket.send(packet);
		terminal.println("Packet sent");
		this.wait();
	}

	public synchronized void readAndSendPacket(DatagramPacket packet, byte[] input, byte[] data) throws Exception {
		data = new byte[HEADER_LENGTH + input.length];
		data[TYPE_POS] = TYPE_STRING;
		data[LENGTH_POS] = (byte) input.length;
		System.arraycopy(input, 0, data, HEADER_LENGTH, input.length);

		sendPacket(packet, data);
	}

	public synchronized void sendMessage(Aead key) throws Exception {
		byte[] data = null;
		DatagramPacket packet = null;
		String input = "";

		for(int i = 0; i< 10; i++){//while (!input.equalsIgnoreCase("quit")) {//
			input = terminal.read("Enter Symptoms or type quit to exit: ");
			Encryption encrypter = new Encryption(key);
			byte[] encrypted = encrypter.encrypt(input);
			readAndSendPacket(packet, encrypted, data);
		}
	}

	public static class creatorThread extends Thread{
		public void run(){
			try {
				Terminal terminal = new Terminal("Client1 Port: " + DEFAULT_DST_PORT);
				System.out.println("check1");
				(new Client(terminal, DEFAULT_DST_NODE, DEFAULT_DST_PORT, DEFAULT_SRC_PORT)).sendMessage(clientKey);
				terminal.println("Program completed");
			} catch (java.lang.Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void encryption(Aead key) {
		clientKey = key;
		Thread newCThread1 = new creatorThread();
		newCThread1.start();
	}

	/**
	 * Test method
	 *
	 * Sends a packet to a given address
	 */
	public static void main(String[] args) {
//		try {
//			Terminal terminal = new Terminal("Client1 Port: " + DEFAULT_DST_PORT);
//			(new Client(terminal, DEFAULT_DST_NODE, DEFAULT_DST_PORT, DEFAULT_SRC_PORT)).sendMessage(null);
//			terminal.println("Program completed");
//		} catch (java.lang.Exception e) {
//			e.printStackTrace();
//		}
	}
}
