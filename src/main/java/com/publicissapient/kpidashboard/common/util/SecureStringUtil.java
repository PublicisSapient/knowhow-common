package com.publicissapient.kpidashboard.common.util;

import java.security.SecureRandom;
import java.util.stream.Collectors;

public class SecureStringUtil {

	private static final SecureRandom SECURE_RANDOM = new SecureRandom();

	private SecureStringUtil() {
		// Utility class — prevent instantiation
	}

	/** Generates a random password containing uppercase, lowercase, and digits. */
	public static String generateRandomPassword(int length) {
		String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		return SECURE_RANDOM.ints(length, 0, chars.length()).mapToObj(i -> String.valueOf(chars.charAt(i)))
				.collect(Collectors.joining());
	}
}
