package app;

import static org.junit.Assert.*;

import java.security.GeneralSecurityException;

import org.junit.Test;

public class EncryptionTest {

	@Test
	public void test() throws GeneralSecurityException {
		Encryption encryption = new Encryption();
		String testString = "Test";
		byte[] encrypted = encryption.encrypt(testString);
		System.out.println(encrypted);
		String decrypted = encryption.decrypt(encrypted);
		System.out.println(decrypted);
		assertEquals(testString,decrypted);
	}

}
