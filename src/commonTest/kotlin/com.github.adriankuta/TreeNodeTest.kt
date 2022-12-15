package com.github.adriankuta

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class TreeNodeTest {

    @Test
    fun removeNodeTest() {
        val root = TreeNode("Root")
        val beveragesNode = TreeNode("Beverages")
        val curdNode = TreeNode("Curd")
        root.addChild(beveragesNode)
        root.addChild(curdNode)

        val teaNode = TreeNode("tea")
        val coffeeNode = TreeNode("coffee")
        val milkShakeNode = TreeNode("Milk Shake")
        beveragesNode.addChild(teaNode)
        beveragesNode.addChild(coffeeNode)
        beveragesNode.addChild(milkShakeNode)

        val gingerTeaNode = TreeNode("ginger tea")
        val normalTeaNode = TreeNode("normal tea")
        teaNode.addChild(gingerTeaNode)
        teaNode.addChild(normalTeaNode)

        val yogurtNode = TreeNode("yogurt")
        val lassiNode = TreeNode("lassi")
        curdNode.addChild(yogurtNode)
        curdNode.addChild(lassiNode)

        assertEquals(
            "Root\n" +
                    "├── Beverages\n" +
                    "│   ├── tea\n" +
                    "│   │   ├── ginger tea\n" +
                    "│   │   └── normal tea\n" +
                    "│   ├── coffee\n" +
                    "│   └── Milk Shake\n" +
                    "└── Curd\n" +
                    "    ├── yogurt\n" +
                    "    └── lassi\n",
            root.prettyString(),
            "Pretty print test"
        )

        println("Remove: ${curdNode.value}")
        root.removeChild(curdNode)
        println("Remove: ${gingerTeaNode.value}")
        root.removeChild(gingerTeaNode)
        assertEquals(
            "Root\n" +
                    "└── Beverages\n" +
                    "    ├── tea\n" +
                    "    │   └── normal tea\n" +
                    "    ├── coffee\n" +
                    "    └── Milk Shake\n",
            root.prettyString(),
            "Remove node test"
        )
    }

    @Test
    fun clearTest() {
        val root = TreeNode("Root")
        val beveragesNode = TreeNode("Beverages")
        val curdNode = TreeNode("Curd")
        root.addChild(beveragesNode)
        root.addChild(curdNode)

        val teaNode = TreeNode("tea")
        val coffeeNode = TreeNode("coffee")
        val milkShakeNode = TreeNode("Milk Shake")
        beveragesNode.addChild(teaNode)
        beveragesNode.addChild(coffeeNode)
        beveragesNode.addChild(milkShakeNode)

        val gingerTeaNode = TreeNode("ginger tea")
        val normalTeaNode = TreeNode("normal tea")
        teaNode.addChild(gingerTeaNode)
        teaNode.addChild(normalTeaNode)

        val yogurtNode = TreeNode("yogurt")
        val lassiNode = TreeNode("lassi")
        curdNode.addChild(yogurtNode)
        curdNode.addChild(lassiNode)

        println(root.toString())
        println(curdNode.height())

        root.clear()
        assertEquals(root.children, emptyList())
        assertEquals(beveragesNode.children, emptyList())
        assertEquals(curdNode.children, emptyList())
        assertEquals(teaNode.children, emptyList())
        assertEquals(coffeeNode.children, emptyList())
        assertEquals(milkShakeNode.children, emptyList())
        assertEquals(gingerTeaNode.children, emptyList())
        assertEquals(normalTeaNode.children, emptyList())
        assertEquals(yogurtNode.children, emptyList())
        assertEquals(lassiNode.children, emptyList())

        assertNull(root.parent)
        assertNull(beveragesNode.parent)
        assertNull(curdNode.parent)
        assertNull(teaNode.parent)
        assertNull(coffeeNode.parent)
        assertNull(milkShakeNode.parent)
        assertNull(gingerTeaNode.parent)
        assertNull(normalTeaNode.parent)
        assertNull(yogurtNode.parent)
        assertNull(lassiNode.parent)
    }

    @Test
    fun kotlinExtTest() {
        val root = TreeNode("World")
        val northA = TreeNode("North America")
        val europe = TreeNode("Europe")
        root.addChild(northA)
        root.addChild(europe)

        val usa = TreeNode("USA")
        northA.addChild(usa)

        val poland = TreeNode("Poland")
        val france = TreeNode("France")
        europe.addChild(poland)
        europe.addChild(france)

        val rootExt = tree("World") {
            child("North America") {
                child("USA")
            }
            child("Europe") {
                child("Poland")
                child("France")
            }
        }
        assertEquals(root.prettyString(), rootExt.prettyString())
    }
}