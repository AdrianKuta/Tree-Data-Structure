package com.github.adriankuta.datastructure.tree.serialization

import com.github.adriankuta.datastructure.tree.structurallyEquals
import com.github.adriankuta.datastructure.tree.tree
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TreeNodeSerializationTest {

    @Test
    fun roundTripsThroughJson() {
        val original = tree("World") {
            child("North America") { child("USA") }
            child("Europe") {
                child("Poland")
                child("Germany")
            }
        }

        val json = Json.encodeToString(original.toDto())
        val restored = Json.decodeFromString<TreeNodeDto<String>>(json).toTreeNode()

        assertTrue(original.structurallyEquals(restored))
    }

    @Test
    fun dtoMirrorsTreeShape() {
        val dto = tree(1) {
            child(2)
            child(3) { child(4) }
        }.toDto()

        assertEquals(1, dto.value)
        assertEquals(2, dto.children.size)
        assertEquals(4, dto.children[1].children[0].value)
    }
}
