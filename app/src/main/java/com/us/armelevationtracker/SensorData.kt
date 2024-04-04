package com.us.armelevationtracker

data class DataPoint(val angle: Float, val x: Float, val y: Float)

class EwmaFilter(alpha: Float) {
    private var alpha = alpha
    private var yPrevious = 0f

    fun filter(input: Float): Float {
        val output = alpha * input + (1 - alpha) * yPrevious
        yPrevious = output
        return output
    }
}
