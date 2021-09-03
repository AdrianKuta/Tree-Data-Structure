package com.github.adriankuta.datastructure.tree

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThat
import org.junit.Test

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
                    "    └── lassi\n", root.toString()
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
                    "    └── Milk Shake\n", root.toString()
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
        assertThat(root.children, `is`(emptyList()))
        assertThat(beveragesNode.children, `is`(emptyList()))
        assertThat(curdNode.children, `is`(emptyList()))
        assertThat(teaNode.children, `is`(emptyList()))
        assertThat(coffeeNode.children, `is`(emptyList()))
        assertThat(milkShakeNode.children, `is`(emptyList()))
        assertThat(gingerTeaNode.children, `is`(emptyList()))
        assertThat(normalTeaNode.children, `is`(emptyList()))
        assertThat(yogurtNode.children, `is`(emptyList()))
        assertThat(lassiNode.children, `is`(emptyList()))

        assertThat(root.parent, `is`(nullValue()))
        assertThat(beveragesNode.parent, `is`(nullValue()))
        assertThat(curdNode.parent, `is`(nullValue()))
        assertThat(teaNode.parent, `is`(nullValue()))
        assertThat(coffeeNode.parent, `is`(nullValue()))
        assertThat(milkShakeNode.parent, `is`(nullValue()))
        assertThat(gingerTeaNode.parent, `is`(nullValue()))
        assertThat(normalTeaNode.parent, `is`(nullValue()))
        assertThat(yogurtNode.parent, `is`(nullValue()))
        assertThat(lassiNode.parent, `is`(nullValue()))
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