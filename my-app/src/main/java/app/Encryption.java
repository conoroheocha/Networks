package app;

import java.security.GeneralSecurityException;

import com.google.crypto.tink.Aead;
import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.aead.AeadFactory;
import com.google.crypto.tink.aead.AeadKeyTemplates;
import com.google.crypto.tink.config.TinkConfig;

public class Encryption {
	private static Aead key;

	Encryption(Aead aead) throws GeneralSecurityException {// Encryption Constructor, generates a new key if argument is
															// null, otherwise makes new instance with key
		if (aead == null) {
			TinkConfig.register();

			KeysetHandle keysetHandle = KeysetHandle.generateNew(AeadKeyTemplates.AES128_GCM);
			aead = AeadFactory.getPrimitive(keysetHandle);// key generation
		}

		key = aead;
	}

	byte[] encrypt(String toEncrypt) throws GeneralSecurityException {// encrypts a given String with the instance's key
		// System.out.println("String to Encrypt: " + toEncrypt);
		byte[] toEncryptBytes = toEncrypt.getBytes();// turns string to bytes
		// System.out.println("Bytes to Encrypt: " + toEncryptBytes);
		byte[] ciphertext = key.encrypt(toEncryptBytes, null);// uses key to encrypt bytes
		// System.out.println("Encrypted Bytes: " + ciphertext);

		return ciphertext;
	}

	String decrypt(byte[] toDecrypt) throws GeneralSecurityException {// decrypts a given String with the instance's key
		// System.out.println("Bytes to Decrypt: " + toDecrypt);
		byte[] decrypted = key.decrypt(toDecrypt, null);// decrypts bytes
		// System.out.println("Decrypted Bytes: " + decrypted);
		String decryptedString = new String(decrypted);// turns bytes to string
		// System.out.println("Decrypted String: " + decryptedString);

		return decryptedString;
	}

	Aead getKey() {
		return key;
	}
}
