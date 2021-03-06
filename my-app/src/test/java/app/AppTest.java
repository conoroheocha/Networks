//USE TESTRUN.JAVA INSTEAD
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

	Thread threadC = new Thread() {
		public void start() {
			Client2.encryption(key);
		}
	};

	Thread threadD = new Thread() {
		public void start() {
			GlobalServer.decryption(key);
		}
	};

	@Test
	public void TestEncryption() throws GeneralSecurityException {
		Encryption encryption = new Encryption(null);
		key = encryption.getKey();
		System.out.println("mmm");
		threadD.start();
		threadB.start();

		System.out.println("nnn");
		threadA.start();
		System.out.println("ooo");
		threadC.start();
		while (true) {

		}
	}
}
