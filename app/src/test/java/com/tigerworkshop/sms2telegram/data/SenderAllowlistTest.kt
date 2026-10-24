package com.tigerworkshop.sms2telegram.data

import org.junit.Assert.*
import org.junit.Test
import java.util.Locale

class SenderAllowlistTest {
    @Test fun disabledFilterPreservesExistingBehavior() {
        assertTrue(SenderAllowlist().allows("OtherSender"))
        assertTrue(SenderAllowlist().allows(null))
    }
    @Test fun enabledEmptyListBlocksEverything() {
        assertFalse(SenderAllowlist(true).allows("HelloBike"))
    }
    @Test fun namesMatchExactlyIgnoringCaseAndOuterWhitespace() {
        val list = SenderAllowlist(true, setOf(" HelloBike "))
        assertTrue(list.allows("HelloBike"))
        assertTrue(list.allows(" hellobike "))
        assertFalse(list.allows("HelloBike-Fake"))
        assertFalse(list.allows("OtherSender"))
        assertFalse(list.allows("Your HelloBike OTP is TEST"))
    }
    @Test fun missingAndBlankAddressesAreBlocked() {
        val list = SenderAllowlist(true, setOf("HelloBike", ""))
        assertFalse(list.allows(null))
        assertFalse(list.allows(" "))
    }
    @Test fun phoneNumbersRequireTheFullAddress() {
        val list = SenderAllowlist(true, setOf("+6581234567", "12345"))
        assertTrue(list.allows("+6581234567"))
        assertTrue(list.allows("12345"))
        assertFalse(list.allows("81234567"))
        assertFalse(list.allows("+65812345678"))
    }
    @Test fun wildcardsAreNotInterpreted() {
        assertFalse(SenderAllowlist(true, setOf("Hello*")).allows("HelloBike"))
    }
    @Test fun cleaningTrimsDropsBlanksAndDeduplicates() {
        assertEquals(setOf("HelloBike", "12345"), SenderAllowlist.clean(listOf(" HelloBike ", "hellobike", "", " ", "12345")))
    }
    @Test fun matchingIsIndependentOfDeviceLocale() {
        val previous = Locale.getDefault()
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"))
            assertTrue(SenderAllowlist(true, setOf("HELLOBIKE")).allows("hellobike"))
            assertEquals(1, SenderAllowlist.clean(listOf("HELLOBIKE", "hellobike")).size)
        } finally { Locale.setDefault(previous) }
    }
}
