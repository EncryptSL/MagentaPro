package com.github.encryptsl.magenta.api.halloween

import java.time.LocalDate

abstract class HalloweenAPI : Halloween {

    override fun isHalloweenSeason(): Boolean {
        val localDate: LocalDate = LocalDate.now()

        val day: Int = localDate.dayOfMonth
        val month: Int = localDate.monthValue
        return month == 10 && day >= 20 || month == 11 && day <= 3
    }

    override fun isHalloweenDay(): Boolean {
        val localDate: LocalDate = LocalDate.now()
        return localDate.monthValue == 10 && localDate.dayOfMonth == 31
    }
}