package com.github.adriankuta.datastructure.tree

import kotlin.test.Test
import kotlin.test.assertEquals

class TreeNodePrettyPrintTest {

    private fun sampleTree(): TreeNode<String> {
        val root = TreeNode("Root")
        val beverages = TreeNode("Beverages")
        val curd = TreeNode("Curd")
        root.addChild(beverages)
        root.addChild(curd)

        val tea = TreeNode("tea")
        val coffee = TreeNode("coffee")
        beverages.addChild(tea)
        beverages.addChild(coffee)

        tea.addChild(TreeNode("ginger tea"))
        tea.addChild(TreeNode("normal tea"))

        curd.addChild(TreeNode("yogurt"))
        curd.addChild(TreeNode("lassi"))
        return root
    }

    @Test
    fun defaultConnectorsMatchMemberPrettyString() {
        val root = sampleTree()
        assertEquals(root.prettyString(), root.prettyString(connectors = TreeConnectors.Default))
    }

    @Test
    fun defaultRenderMatchesMemberForNullValues() {
        // The member prettyString() appends the value via StringBuilder, rendering null as "null".
        // The all-defaults extension must stay byte-identical, including for null-valued nodes.
        val root = TreeNode<String?>(null)
        root.addChild(TreeNode("child"))
        root.addChild(TreeNode<String?>(null))
        assertEquals(root.prettyString(), root.prettyString(connectors = TreeConnectors.Default))
        assertEquals(
            "null\n" +
                "├── child\n" +
                "└── null\n",
            root.prettyString(),
        )
    }

    @Test
    fun asciiConnectorsRenderPlainAscii() {
        val root = sampleTree()
        assertEquals(
            "Root\n" +
                "|-- Beverages\n" +
                "|   |-- tea\n" +
                "|   |   |-- ginger tea\n" +
                "|   |   `-- normal tea\n" +
                "|   `-- coffee\n" +
                "`-- Curd\n" +
                "    |-- yogurt\n" +
                "    `-- lassi\n",
            root.prettyString(connectors = TreeConnectors.Ascii),
        )
    }

    @Test
    fun customRenderIsApplied() {
        val root = sampleTree()
        assertEquals(
            "ROOT\n" +
                "├── BEVERAGES\n" +
                "│   ├── TEA\n" +
                "│   │   ├── GINGER TEA\n" +
                "│   │   └── NORMAL TEA\n" +
                "│   └── COFFEE\n" +
                "└── CURD\n" +
                "    ├── YOGURT\n" +
                "    └── LASSI\n",
            root.prettyString { value, _, _ -> value.uppercase() },
        )
    }

    @Test
    fun depthAndIsLastArePassedToRender() {
        val root = sampleTree()
        assertEquals(
            "Root depth=0 last=true\n" +
                "├── Beverages depth=1 last=false\n" +
                "│   ├── tea depth=2 last=false\n" +
                "│   │   ├── ginger tea depth=3 last=false\n" +
                "│   │   └── normal tea depth=3 last=true\n" +
                "│   └── coffee depth=2 last=true\n" +
                "└── Curd depth=1 last=true\n" +
                "    ├── yogurt depth=2 last=false\n" +
                "    └── lassi depth=2 last=true\n",
            root.prettyString { value, depth, isLast -> "$value depth=$depth last=$isLast" },
        )
    }
}
