package com.github.adriankuta.treedatastructure

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.github.adriankuta.datastructure.tree.tree

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val root =
            tree("World") {
                child("North America") {
                    child("USA") {
                        child("LA")
                        child("New York")
                    }
                }
                child("Europe") {
                    child("Poland")
                    child("Germany")
                }
            }
        Log.d("DEBUG_TAG", root.prettyString())
    }
}
