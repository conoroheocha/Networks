package app;

import java.security.GeneralSecurityException;

import com.google.crypto.tink.Aead;

public class TestRun {

	private static Aead key;

	static Thread threadA = new Thread() {
		public void start() {
			Client.encryption(key);
		}
	};

	static Thread threadB = new Thread() {
		public void start() {
			Server.decryption(key);
		}
	};

	static Thread threadC = new Thread() {
		public void start() {
			Client2.encryption(key);
		}
	};

	static Thread threadD = new Thread() {
		public void start() {
			GlobalServer.decryption(key);
		}
	};

	public static void main(String[] args) throws GeneralSecurityException {
		Encryption encryption = new Encryption(null);
		key = encryption.getKey();//creates the encryption key

		threadD.start(); //global server
		threadB.start(); //server1

		threadA.start(); //client1
		threadC.start(); //client2
	}
}
