package com.example.trackme

import com.example.trackme.common.ui.SpeedUnit
import com.example.trackme.common.ui.formatSpeed
import org.junit.Assert
import org.junit.Test

class FormatSpeedTest {

    @Test
    fun `when unit is mps it formats with 2 decimals`() {
        val unit = SpeedUnit.METER_PER_SECOND
        Assert.assertEquals("3.14 m/s",formatSpeed(3.1415f, unit))
    }

    @Test
    fun `when unit is kmph it converts and formats with 1 decimals`() {
        val unit = SpeedUnit.KILOMETERS_PER_HOUR
        Assert.assertEquals("36.0 km/h", formatSpeed(10f, unit))
    }

    @Test
    fun `when unit is knots it converts and formats with 1 decimals`() {
        val unit = SpeedUnit.KNOTS
        Assert.assertEquals("19.4 kn", formatSpeed(10f, unit))
    }
}