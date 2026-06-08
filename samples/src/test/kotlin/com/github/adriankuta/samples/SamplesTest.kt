package com.github.adriankuta.samples

import kotlin.test.Test
import kotlin.test.assertContains

class SamplesTest {

    @Test
    fun coreSampleRendersTreeAndTraversals() {
        val out = coreSample()
        assertContains(
            out,
            "World\n" +
                "├── North America\n" +
                "│   └── USA\n" +
                "└── Europe\n" +
                "    ├── Poland\n" +
                "    └── Germany\n",
        )
        assertContains(out, "[World, North America, USA, Europe, Poland, Germany]")
        assertContains(out, "[North America, World]") // usa.ancestors()
        assertContains(out, "[USA, Poland, Germany]") // root.leaves()
    }

    @Test
    fun serializationSampleRoundTrips() {
        val out = serializationSample()
        assertContains(out, "\"World\"")
        assertContains(out, "round-trips structurallyEquals: true")
    }

    @Test
    fun coroutinesSampleCollectsFlows() {
        val out = coroutinesSample()
        assertContains(out, "preOrderFlow():     [World, North America, USA, Europe, Poland, Germany]")
        assertContains(out, "asFlow(LevelOrder): [World, North America, Europe, USA, Poland, Germany]")
    }

    @Test
    fun immutableSampleLeavesRootUnchanged() {
        val out = immutableSample()
        assertContains(out, "root.children:   [Europe]")
        assertContains(out, "bigger.children: [Europe, Asia]")
        assertContains(out, "root unchanged:  true")
    }
}
