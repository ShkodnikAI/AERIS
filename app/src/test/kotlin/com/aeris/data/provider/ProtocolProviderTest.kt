package com.aeris.data.provider

import org.junit.Assert.*
import org.junit.Test

class ProtocolProviderTest {
    @Test
    fun test_count_is_8() {
        assertEquals(8, ProtocolProvider.allProtocols.size)
    }

    @Test
    fun test_ids_are_unique() {
        val ids = ProtocolProvider.allProtocols.map { it.id }
        assertEquals(ids.size, ids.distinct().size)
    }
}
