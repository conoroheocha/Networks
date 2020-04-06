package app;

import java.security.GeneralSecurityException;
import java.util.Base64;

import com.google.crypto.tink.Aead;
import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.aead.AeadFactory;
import com.google.crypto.tink.aead.AeadKeyTemplates;
import com.google.crypto.tink.config.TinkConfig;

public class Encryption {
	private static Aead aead;

	Encryption() throws GeneralSecurityException{
	    TinkConfig.register();
		
	    KeysetHandle keysetHandle = KeysetHandle.generateNew(AeadKeyTemplates.AES128_GCM);
	    aead = AeadFactory.getPrimitive(keysetHandle);
	}
	
	byte[] encrypt(String toEncrypt) throws GeneralSecurityException {
		//System.out.println("String to Encrypt: " + toEncrypt);
		byte[] toEncryptBytes = toEncrypt.getBytes();
		//System.out.println("Bytes to Encrypt: " + toEncryptBytes);
	    byte[] ciphertext = aead.encrypt(toEncryptBytes, null);
	    //System.out.println("Encrypted Bytes: " + ciphertext);
	    
		return ciphertext;
	}
	
	String decrypt(byte[] toDecrypt) throws GeneralSecurityException {
		//System.out.println("Bytes to Decrypt: " + toDecrypt);
		byte[] decrypted = aead.decrypt(toDecrypt, null);
		//System.out.println("Decrypted Bytes: " + decrypted);
	    String decryptedString = new String(decrypted);
	    //System.out.println("Decrypted String: " + decryptedString);
	    
		return decryptedString;
	}
}
