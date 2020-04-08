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
	static final int SOURCE_PORT = 50000; // Port of the client. These need to be different for all the files as
												// they are used somehow in the setup
	static final int DESTINATION_PORT = 8080; // Port of the server
	static final String DESTINATION_NODE = "localhost"; // Name of the host for the server

	static final int HEADER_LENGTH = 2; // Fixed length of the header // a lot of these ints are for checking bytes sent are the right type etc
	static final int TYPE_POSITION = 0; // Position of the type within the header

	static final byte TYPE_STRING = 1; // Indicating a string payload
	static final int LENGTH_POS = 1;

	Terminal terminal;
	InetSocketAddress destinationAddress;
	private static Aead clientKey;

	/**
	 * Constructor
	 *
	 * Attempts to create socket at given port and create an InetSocketAddress for
	 * the destinations
	 */
	Client(Terminal terminal, String dstHost, int dstPort, int srcPort) {
		try {
			this.terminal = terminal;
			destinationAddress = new InetSocketAddress(dstHost, dstPort);
			socket = new DatagramSocket(srcPort);// attaches to desired port
			listener.go();//waits to get input
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Assume that incoming packets contain a String and print the string.
	 */
	public synchronized void onReceipt(DatagramPacket packet) {//when server sends message back
		byte[] data;

		data = packet.getData();

		terminal.println("Received Ack");//acknowledgement
		String str = new String(data, StandardCharsets.UTF_8);
		terminal.println("Received Response: " + str);// checks the response weve received
		this.notify();
	}

	/**
	 * Sender Method
	 *
	 */
	public synchronized void sendPacket(byte[] data) throws Exception {
		terminal.println("Sending packet...");
		DatagramPacket packet = new DatagramPacket(data, data.length);
		packet.setSocketAddress(destinationAddress);
		socket.send(packet);//sends packet to server
		terminal.println("Packet sent");
		this.wait();//waits for response
	}

	public synchronized void readAndSendPacket(DatagramPacket packet, byte[] input) throws Exception {
		byte[] data = new byte[HEADER_LENGTH + input.length];
		data[TYPE_POSITION] = TYPE_STRING;
		data[LENGTH_POS] = (byte) input.length;
		System.arraycopy(input, 0, data, HEADER_LENGTH, input.length);//organises the data into a packet

		sendPacket(data);//function to send packet
	}

	public synchronized void sendMessage(Aead key) throws Exception {
		byte[] data = null;
		DatagramPacket packet = null;
		String input;// = "";

		for(int i = 0; i< 10; i++){
			input = terminal.read("Enter Symptoms or type quit to exit: ");//takes in input from terminal
			Encryption encrypter = new Encryption(key);
			byte[] encrypted = encrypter.encrypt(input);//uses key to encrypt message
			readAndSendPacket(packet, encrypted);//read and send function to transfer
		}
	}

	public static class creatorThread extends Thread{
		public void run(){
			try {
				Terminal terminal = new Terminal("Client1 Port: " + DESTINATION_PORT);
				(new Client(terminal, DESTINATION_NODE, DESTINATION_PORT, SOURCE_PORT)).sendMessage(clientKey);
				// configures a client terminal using client instantiater
				terminal.println("Program completed");
			} catch (java.lang.Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void encryption(Aead key) {
		clientKey = key;
		Thread newCThread1 = new creatorThread();
		newCThread1.start(); // starts creator thread to set up terminal
	}

	/**
	 * Test method
	 *
	 * Sends a packet to a given address
	 */
	public static void main(String[] args) {
	}
}
