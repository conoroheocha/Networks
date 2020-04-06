package app;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

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
	static final int DEFAULT_SRC_PORT = 50000; // Port of the client
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

	/**
	 * Constructor
	 *
	 * Attempts to create socket at given port and create an InetSocketAddress for
	 * the destinations
	 */
	Client(Terminal terminal, String dstHost, int dstPort, int srcPort) {
		try {
			this.terminal = terminal;
			dstAddress = new InetSocketAddress(dstHost, dstPort);
			socket = new DatagramSocket(srcPort);
			listener.go();
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
		switch (data[TYPE_POS]) {
		case TYPE_ACK:
			terminal.println("Received ack");
			this.notify();
			break;
		default:
			terminal.println("Unexpected packet" + packet.toString());
		}
	}

	/**
	 * Sender Method
	 *
	 */
	public synchronized void sendPacket(DatagramPacket packet, byte[] data) throws Exception {
		terminal.println("Sending packet...");
		packet = new DatagramPacket(data, data.length);
		packet.setSocketAddress(dstAddress);
		socket.send(packet);
		terminal.println("Packet sent");
		this.wait();
	}


	public synchronized void sendMessage() throws Exception {
		byte[] data = null;
		byte[] buffer = null;
		DatagramPacket packet = null;
		String input;

		input = terminal.read("Enter Username: ");
		buffer = input.getBytes();
		data = new byte[HEADER_LENGTH + buffer.length];
		data[TYPE_POS] = TYPE_STRING;
		data[LENGTH_POS] = (byte) buffer.length;
		System.arraycopy(buffer, 0, data, HEADER_LENGTH, buffer.length);

		sendPacket(packet, data);
		/*terminal.println("Sending packet...");
		packet = new DatagramPacket(data, data.length);
		packet.setSocketAddress(dstAddress);
		socket.send(packet);
		terminal.println("Packet sent");
		this.wait();*/

		input = terminal.read("Enter Password: ");
		buffer = input.getBytes();
		data = new byte[HEADER_LENGTH + buffer.length];
		data[TYPE_POS] = TYPE_STRING;
		data[LENGTH_POS] = (byte) buffer.length;
		System.arraycopy(buffer, 0, data, HEADER_LENGTH, buffer.length);

		sendPacket(packet, data);
		/*terminal.println("Sending packet...");
		packet = new DatagramPacket(data, data.length);
		packet.setSocketAddress(dstAddress);
		socket.send(packet);
		terminal.println("Packet sent");
		this.wait();*/

		input = terminal.read("Enter Name, Breed and Age of Pet: ");
		buffer = input.getBytes();
		data = new byte[HEADER_LENGTH + buffer.length];
		data[TYPE_POS] = TYPE_STRING;
		data[LENGTH_POS] = (byte) buffer.length;
		System.arraycopy(buffer, 0, data, HEADER_LENGTH, buffer.length);

		sendPacket(packet, data);
		/*terminal.println("Sending packet...");
		packet = new DatagramPacket(data, data.length);
		packet.setSocketAddress(dstAddress);
		socket.send(packet);
		terminal.println("Packet sent");
		this.wait();*/

	}

	/**
	 * Test method
	 *
	 * Sends a packet to a given address
	 */
	public static void main(String[] args) {
		try {
			Terminal terminal = new Terminal("Client Port: " + DEFAULT_DST_PORT);
			(new Client(terminal, DEFAULT_DST_NODE, DEFAULT_DST_PORT, DEFAULT_SRC_PORT)).sendMessage();
			terminal.println("Program completed");
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}
	}
}
