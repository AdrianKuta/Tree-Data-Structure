package com.github.adriankuta.datastructure.tree

/**
 * The four glyph strings used to draw the tree branches in [prettyString].
 *
 * Each value is the literal text emitted at the matching position:
 * - [branch] precedes a child that is **not** its parent's last child.
 * - [lastBranch] precedes a child that **is** its parent's last child.
 * - [vertical] is accumulated into the prefix of the descendants of a non-last child (it keeps the
 *   vertical guide line going).
 * - [empty] is accumulated into the prefix of the descendants of a last child (no guide line is
 *   needed past the last branch).
 *
 * Use [Default] for the box-drawing style or [Ascii] for a plain-ASCII style, or supply your own.
 *
 * @property branch drawn before a non-last child.
 * @property vertical continuation prefix for descendants of a non-last child.
 * @property lastBranch drawn before the last child.
 * @property empty continuation prefix for descendants of a last child.
 */
public data class TreeConnectors(
    public val branch: String,
    public val vertical: String,
    public val lastBranch: String,
    public val empty: String,
) {
    public companion object {
        /** Box-drawing connectors, matching the output of the no-arg [TreeNode.prettyString]. */
        public val Default: TreeConnectors = TreeConnectors(
            branch = "├── ",
            vertical = "│   ",
            lastBranch = "└── ",
            empty = "    ",
        )

        /** Plain-ASCII connectors for terminals or fonts that lack box-drawing glyphs. */
        public val Ascii: TreeConnectors = TreeConnectors(
            branch = "|-- ",
            vertical = "|   ",
            lastBranch = "`-- ",
            empty = "    ",
        )
    }
}

/**
 * Renders this subtree as a multi-line string, one node per line, with branch connectors.
 *
 * Calling this with all defaults produces output byte-identical to the no-arg member
 * [TreeNode.prettyString]. Customise the drawing with [connectors] (e.g. [TreeConnectors.Ascii]) and
 * the per-node text with [render].
 *
 * @param connectors the glyph set used to draw the branches. Defaults to [TreeConnectors.Default].
 * @param render produces the text for each node from its `value`, its `depth` (distance from this
 *   receiver, which is `0`) and `isLast` (whether the node is its parent's last child; the root is
 *   considered `true`). Defaults to the value's string form (`"$value"`), which renders a `null`
 *   value as `"null"` to match the no-arg member.
 * @return the rendered tree, each line terminated by `\n`.
 */
public fun <T> TreeNode<T>.prettyString(
    connectors: TreeConnectors = TreeConnectors.Default,
    render: (value: T, depth: Int, isLast: Boolean) -> String = { value, _, _ -> "$value" },
): String {
    val stringBuilder = StringBuilder()
    appendPretty(stringBuilder, "", "", 0, true, connectors, render)
    return stringBuilder.toString()
}

private fun <T> TreeNode<T>.appendPretty(
    stringBuilder: StringBuilder,
    prefix: String,
    childrenPrefix: String,
    depth: Int,
    isLast: Boolean,
    connectors: TreeConnectors,
    render: (value: T, depth: Int, isLast: Boolean) -> String,
) {
    stringBuilder.append(prefix)
    stringBuilder.append(render(value, depth, isLast))
    stringBuilder.append('\n')
    val childIterator = children.iterator()
    while (childIterator.hasNext()) {
        val node = childIterator.next()
        if (childIterator.hasNext()) {
            node.appendPretty(
                stringBuilder,
                childrenPrefix + connectors.branch,
                childrenPrefix + connectors.vertical,
                depth + 1,
                false,
                connectors,
                render,
            )
        } else {
            node.appendPretty(
                stringBuilder,
                childrenPrefix + connectors.lastBranch,
                childrenPrefix + connectors.empty,
                depth + 1,
                true,
                connectors,
                render,
            )
        }
    }
}
