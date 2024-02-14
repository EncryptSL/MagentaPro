package com.github.encryptsl.magenta.api.report

enum class ReportCategories(val message: String) {
    CHEATING("Bylo nahlášeno (Hacking, Cheating)"),
    EVENT_CHEATING("Bylo nahlášeno podvádění při eventu."),
    NEVHODNE_CHOVANI("Bylo nahlášeno nevhodné chování."),
    REKLAMA("Byla nahlášena reklama nebo jiná propagace.")
}