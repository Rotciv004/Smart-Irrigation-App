package com.example.smartirrigation.core.util

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class IpAddressValidatorTest {
    @Test
    fun valid_ipv4_isAccepted() {
        assertTrue(IpAddressValidator.isValid("192.168.0.10"))
        assertTrue(IpAddressValidator.isValid("192.168.1.55"))
    }

    @Test
    fun invalid_ipv4_isRejected() {
        assertFalse(IpAddressValidator.isValid("999.1.1.1"))
        assertFalse(IpAddressValidator.isValid(""))
    }

    @Test
    fun urls_areRejected() {
        assertFalse(IpAddressValidator.isValid("http://192.168.1.55"))
    }

    @Test
    fun hostnames_areRejected() {
        assertFalse(IpAddressValidator.isValid("esp32.local"))
    }
}

