package com.example.smartirrigation.core.util

object IpAddressValidator {
    private val ipv4Regex = Regex(
        pattern = "^(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}$",
    )

    fun isValid(value: String): Boolean {
        if (value.isBlank()) return false
        if (value.contains("://") || value.contains('/') || value.contains('?')) return false
        return ipv4Regex.matches(value.trim())
    }
}

