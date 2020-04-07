package app;

import java.security.GeneralSecurityException;

import org.junit.Test;

import com.google.crypto.tink.Aead;

public class AppTest {

	private Aead key;

	Thread threadA = new Thread() {
		public void start() {
			Client.encryption(key);
		}
	};

	Thread threadB = new Thread() {
		public void start() {
			Server.decryption(key);
		}
	};

	@Test
	public void TestEncryption() throws GeneralSecurityException {
		Encryption encryption = new Encryption(null);
		key = encryption.getKey();

		threadB.start();
		threadA.start();
	}
}
