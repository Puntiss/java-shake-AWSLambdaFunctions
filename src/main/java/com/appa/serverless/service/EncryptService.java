package com.appa.serverless.service;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class EncryptService {

	private final static String PUBLIC_KEY = "";
	private final static String PRIVATE_KEY = "";

	private static PublicKey getPublicKey() {
		PublicKey publicKey = null;
		try {
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(PUBLIC_KEY.getBytes()));
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			publicKey = keyFactory.generatePublic(keySpec);
			return publicKey;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		}
		return publicKey;
	}

	private static PrivateKey getPrivateKey() {
		PrivateKey privateKey = null;
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(PRIVATE_KEY.getBytes()));
		KeyFactory keyFactory = null;
		try {
			keyFactory = KeyFactory.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		try {
			privateKey = keyFactory.generatePrivate(keySpec);
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		}
		return privateKey;
	}

	private static byte[] encrypt(String data) throws BadPaddingException, IllegalBlockSizeException,
			InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException {
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, getPrivateKey());
		return cipher.doFinal(data.getBytes());
	}

	private static String decrypt(byte[] data) throws NoSuchPaddingException, NoSuchAlgorithmException,
			InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, getPublicKey());
		return new String(cipher.doFinal(data));
	}

	public static String encryptString(String data) throws BadPaddingException, IllegalBlockSizeException,
			InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException {
		return Base64.getEncoder().encodeToString(encrypt(data));
	}

	public static String decrypt(String data) throws IllegalBlockSizeException, InvalidKeyException,
			BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
		return decrypt(Base64.getDecoder().decode(data.getBytes()));
	}

	public static void main(String[] args)
			throws IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, BadPaddingException {
		try {
			String encryptedString = encryptString("12345");
			System.out.println(encryptedString);

			String decryptedString = decrypt(
					"gjD5uRbd1fWG+fKnYFu9fCv6OWWcNCfhg3xyi1MO2H5Zr1z3NlzoJwjrMxrRX3TmXbvk6uL7Afajwu4uFJQ3I0j6Dw33A6/2D8rORTKq97/RiT77f2YjE8K5EXg07Z/g5S+jkTGJfjpryUaRJkRy0QSrHXsB/jRYUtc0JqGFnds=");
			System.out.println(decryptedString);
		} catch (NoSuchAlgorithmException e) {
			System.err.println(e.getMessage());
		}

	}

}
