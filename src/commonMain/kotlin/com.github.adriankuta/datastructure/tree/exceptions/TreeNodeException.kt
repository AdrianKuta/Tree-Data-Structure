package com.github.adriankuta.datastructure.tree.exceptions

import kotlin.jvm.JvmOverloads

public class TreeNodeException @JvmOverloads constructor(message: String? = null, cause: Throwable? = null) :
    RuntimeException(message, cause)