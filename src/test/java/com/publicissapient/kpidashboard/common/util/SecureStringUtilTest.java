package com.publicissapient.kpidashboard.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/** Unit tests for {@link SecureStringUtil}. */
@ExtendWith(SpringExtension.class)
class SecureStringUtilTest {

	@Test
	void testGenerateRandomPasswordLength() {
		String result = SecureStringUtil.generateRandomPassword(10);
		assertNotNull(result);
		assertEquals(10, result.length());
	}
}
