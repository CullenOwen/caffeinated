package com.example.caffeinated

import java.io.Serializable
import java.util.*
import android.util.Log

class MyDataPoint : Serializable {
    private var x: Double
    private var y: Double

    constructor(x: Date, y: Double) {
        this.x = x.time.toDouble()
        this.y = y
    }

    override fun toString(): String {
        return "[$x/$y]"
    }

    companion object {
        private const val serialVersionUID = 1428263322645L
    }
}