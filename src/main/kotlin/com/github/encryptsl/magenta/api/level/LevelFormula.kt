package com.github.encryptsl.magenta.api.level

import com.google.common.base.Strings


object LevelFormula {

    fun getProgressBar(current: Int, max: Int, totalBars: Int, symbol: String, completedColor: String, notCompletedColor: String): String {
        val percent = (current.toDouble() / max.toDouble())
        val progressBars = (totalBars * percent)

        return (Strings.repeat("" + completedColor + symbol, progressBars.toInt())
                + Strings.repeat("" + notCompletedColor + symbol, totalBars - progressBars.toInt()))
    }
}