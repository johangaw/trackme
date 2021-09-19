package com.trackme.android.ui.tracking

class Interpolation(srcFrom: Float, srcTo: Float, targetFrom: Float, targetTo: Float) {
    private var scaleFactor: Float = 1f
    private var offset: Float = 0f

    init {
        scaleFactor = (targetTo - targetFrom) / (srcTo - srcFrom)
        offset = targetTo - scaleFactor * srcTo
    }

    fun interpolate(value: Float): Float {
        return offset + scaleFactor * value
    }
}