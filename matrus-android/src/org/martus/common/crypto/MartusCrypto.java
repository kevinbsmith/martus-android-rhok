/*

The Martus(tm) free, social justice documentation and
monitoring software. Copyright (C) 2001-2007, Beneficent
Technology, Inc. (The Benetech Initiative).

Martus is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either
version 2 of the License, or (at your option) any later
version with the additions and exceptions described in the
accompanying Martus license file entitled "license.txt".

It is distributed WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, including warranties of fitness of purpose or
merchantability.  See the accompanying Martus License and
GPL license for more details on the required license terms
for this software.

You should have received a copy of the GNU General Public
License along with this program; if not, write to the Free
Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA 02111-1307, USA.

*/

package org.martus.common.crypto;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Vector;

import javax.net.ssl.KeyManager;

import org.martus.common.MartusUtilities;
import org.martus.util.StreamableBase64;
import org.martus.util.StreamableBase64.InvalidBase64Exception;
import org.martus.util.inputstreamwithseek.InputStreamWithSeek;


public abstract class MartusCrypto
{
	// key pair stuff
	public abstract MartusKeyPair getKeyPair();
	public abstract boolean hasKeyPair();
	public abstract void clearKeyPair();
	public abstract void createKeyPair();
	public abstract void writeKeyPair(OutputStream outputStream, char[] passPhrase) throws
		Exception;
	public abstract void readKeyPair(InputStream inputStream, char[] passPhrase) throws
		IOException, InvalidKeyPairFileVersionException, AuthorizationFailedException;
	public abstract String getPublicKeyString();
	public abstract byte[] getDigestOfPartOfPrivateKey() throws CreateDigestException;
	public String getSignatureOfPublicKey() throws StreamableBase64.InvalidBase64Exception, MartusCrypto.MartusSignatureException {
		String publicKeyString = getPublicKeyString();
		byte[] publicKeyBytes = StreamableBase64.decode(publicKeyString);
		ByteArrayInputStream in = new ByteArrayInputStream(publicKeyBytes);
		byte[] sigBytes = createSignatureOfStream(in);
		String sigString = StreamableBase64.encode(sigBytes);
		return sigString;
	}

	// signature methods
	public abstract boolean verifySignature(InputStream inputStream, byte[] signature) throws
		MartusSignatureException;
	public abstract byte[] createSignatureOfStream(InputStream inputStream) throws
		MartusSignatureException;
	public abstract boolean isValidSignatureOfStream(String publicKeyString, InputStream inputStream, byte[] signature) throws
		MartusSignatureException;
	public abstract String createSignatureOfVectorOfStrings(Vector dataToSign) throws MartusCrypto.MartusSignatureException;
	public abstract boolean verifySignatureOfVectorOfStrings(Vector dataToTest, String signedBy, String sig);
	public synchronized boolean verifySignatureOfVectorOfStrings(Vector dataToTestWithSignature, String signedBy) {
		Vector dataToTest = (Vector)dataToTestWithSignature.clone();
		String sig = (String)dataToTest.remove(dataToTest.size() - 1);
		return verifySignatureOfVectorOfStrings(dataToTest, signedBy, sig);
	}
	public abstract SignatureEngine createSignatureVerifier(String signedByPublicKey) throws Exception;
		
	// session keys
	public abstract SessionKey createSessionKey();
	public abstract SessionKey encryptSessionKey(SessionKey sessionKey, String publicKey) throws
		EncryptionException;
	public abstract SessionKey decryptSessionKey(SessionKey encryptedSessionKey) throws
		DecryptionException;

	// encrypt/decrypt
	public abstract void encrypt(InputStream plainStream, OutputStream cipherStream, SessionKey sessionKey) throws
			EncryptionException,
			NoKeyPairException;
	public abstract void encrypt(InputStream plainStream, OutputStream cipherStream) throws
			NoKeyPairException,
			EncryptionException;
	public abstract void decrypt(InputStreamWithSeek cipherStream, OutputStream plainStream, SessionKey sessionKey) throws
			DecryptionException;
	public abstract void decrypt(InputStreamWithSeek cipherStream, OutputStream plainStream) throws
			NoKeyPairException,
			DecryptionException;

	// cipher streams
	public abstract OutputStream createEncryptingOutputStream(OutputStream cipherStream, SessionKey sessionKey)
		throws EncryptionException;
	public abstract InputStream createDecryptingInputStream(InputStreamWithSeek cipherStream, SessionKey sessionKey)
		throws	DecryptionException;

	// other
	public abstract KeyManager [] createKeyManagers() throws Exception;
	public abstract byte[] getSessionKeyCache() throws IOException, NoKeyPairException, EncryptionException, MartusSignatureException;
	public abstract void setSessionKeyCache(byte[] encryptedCacheBundle) throws IOException, NoKeyPairException, DecryptionException, MartusSignatureException, AuthorizationFailedException;
	public abstract void flushSessionKeyCache();
	
	// Secret Share of Private Key
	public abstract Vector buildKeyShareBundles();
	public abstract void recoverFromKeyShareBundles(Vector shares) throws KeyShareException;
	
	public abstract byte[] createSignedBundle(byte[] dataBytes) throws MartusSignatureException, IOException;
	public abstract byte[] extractFromSignedBundle(byte[] dataBundle) throws IOException, MartusSignatureException, AuthorizationFailedException;
	public abstract byte[] extractFromSignedBundle(byte[] dataBundle, Vector authorizedKeys) throws IOException, MartusSignatureException, AuthorizationFailedException;
	


	public void readKeyPair(File keyPairFile, char[] combinedPassPhrase) throws
	IOException, InvalidKeyPairFileVersionException, AuthorizationFailedException
	{
		FileInputStream inputStream = new FileInputStream(keyPairFile);
		try
		{
			readKeyPair(inputStream, combinedPassPhrase);
		}
		finally
		{
			inputStream.close();
		}
	}
	
	public void saveEncryptedStringToFile(File file, String stringToSave) throws UnsupportedEncodingException, MartusSignatureException, IOException, FileNotFoundException
	{
		byte[] bytes = stringToSave.getBytes("UTF-8");
		byte[] encryptedBytes = createSignedBundle(bytes);
		
		FileOutputStream out = new FileOutputStream(file);
		out.write(encryptedBytes);
		out.close();
	}
	
	public String loadEncryptedStringFromFile(File file) throws FileNotFoundException, IOException, MartusSignatureException, AuthorizationFailedException
	{
		byte[] encryptedBytes = new byte[(int)file.length()];
		FileInputStream in = new FileInputStream(file);
		in.read(encryptedBytes);
		in.close();
	
		byte[] bytes = extractFromSignedBundle(encryptedBytes);
		return new String(bytes, "UTF-8");
	}
	
	
	// public codes
	public static String computePublicCode(String publicKeyString) throws
		StreamableBase64.InvalidBase64Exception
	{
		String digest = null;
		try
		{
			digest = MartusCrypto.createDigestString(publicKeyString);
		}
		catch(Exception e)
		{
			System.out.println("MartusApp.computePublicCode: " + e);
			return "";
		}
	
		final int codeSizeChars = 20;
		char[] buf = new char[codeSizeChars];
		int dest = 0;
		for(int i = 0; i < codeSizeChars/2; ++i)
		{
			int value = StreamableBase64.getValue(digest.charAt(i));
			int high = value >> 3;
			int low = value & 0x07;
	
			buf[dest++] = (char)('1' + high);
			buf[dest++] = (char)('1' + low);
		}
		return new String(buf);
	}

	public static String computeFormattedPublicCode(String publicKeyString) throws
		StreamableBase64.InvalidBase64Exception
	{
		String rawCode = computePublicCode(publicKeyString);
		return MartusCrypto.formatPublicCode(rawCode);
	}

	public static String formatPublicCode(String publicCode)
	{
		String formatted = "";
		while(publicCode.length() > 0)
		{
			String portion = publicCode.substring(0, 4);
			formatted += portion + "." ;
			publicCode = publicCode.substring(4);
		}
		if(formatted.endsWith("."))
			formatted = formatted.substring(0,formatted.length()-1);
		return formatted;
	}
	
	
	static public String getHexDigest(String anyString)
	{
		try
		{
			byte[] rawDigest = MartusCrypto.createDigestBytes(anyString);
			return MartusUtilities.byteArrayToHexString(rawDigest);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public static String removeNonDigits(String userEnteredPublicCode)
	{
		String normalizedPublicCode = "";
		for (int i=0 ; i < userEnteredPublicCode.length(); ++i)
		{
			if ("0123456789".indexOf(userEnteredPublicCode.substring(i, i+1)) >= 0)
				normalizedPublicCode += userEnteredPublicCode.substring(i, i+1);
		}
		return normalizedPublicCode;
	}

	public static String getFormattedPublicCode(String accountId)
		throws StreamableBase64.InvalidBase64Exception
	{
		return formatPublicCode(MartusCrypto.computePublicCode(accountId));
	}

	public static String formatAccountIdForLog(String accountId)
	{
		try
		{
			return formatPublicCode(MartusCrypto.computePublicCode(accountId));
		}
		catch (InvalidBase64Exception ignoreForLoggingUseAccountId)
		{
			return accountId;
		}
	}
	

	// exceptions
	public static class CryptoException extends Exception
	{
		public CryptoException()
		{
		}
		
		public CryptoException(Exception causedBy)
		{
			super(causedBy);
		}
	}

	public static class CryptoInitializationException extends CryptoException
	{
	}

	public static class InvalidKeyPairFileVersionException extends CryptoException 
	{
	}

	public static class AuthorizationFailedException extends CryptoException 
	{
		public AuthorizationFailedException()
		{
		}
		
		public AuthorizationFailedException(Exception causedBy)
		{
			super(causedBy);
		}
	}

	public static class VerifySignatureException extends CryptoException 
	{
	}

	public static class NoKeyPairException extends CryptoException 
	{
	}

	public static class EncryptionException extends CryptoException 
	{
		public EncryptionException()
		{
		}

		public EncryptionException(Exception causedBy)
		{
			super(causedBy);
		}
	}

	public static class DecryptionException extends CryptoException 
	{
	}

	public static class MartusSignatureException extends CryptoException 
	{
	}

	public static class CreateDigestException extends CryptoException 
	{
	}

	
	public static class KeyShareException extends Exception	
	{
		public KeyShareException()
		{
		}
		
		public KeyShareException(String message)
		{
			super(message);
		}

	}
	
	public static class InvalidJarException extends Exception
	{
		public InvalidJarException(String message)
		{
			super(message);
		}
	}

	public static String createDigestString(String inputText) throws CreateDigestException {
		try
		{
			byte[] result = createDigestBytes(inputText);
			return StreamableBase64.encode(result);
		}
		catch (Exception e)
		{
			throw new CreateDigestException();
		}
	}
	
	public static byte[] createDigestBytes(String inputText) throws UnsupportedEncodingException, CreateDigestException, IOException {
		byte[] bytesToDigest = inputText.getBytes("UTF-8");
		return createDigest(bytesToDigest);
	}
	public static byte[] createDigest(byte[] bytesToDigest) throws CreateDigestException, IOException {
		ByteArrayInputStream in = new ByteArrayInputStream(bytesToDigest);
		byte[] result = MartusSecurity.createDigest(in);
		in.close();
		return result;
	}
	
	protected static final int bitsInSessionKey = 256;
	protected static final int bitsInPublicKey = 2048;
	protected static final int SALT_BYTE_COUNT = 8;
	protected static final int ITERATION_COUNT = 1000;
	protected static final int IV_BYTE_COUNT = 16;
	protected static final int TOKEN_BYTE_COUNT = 16;

}
