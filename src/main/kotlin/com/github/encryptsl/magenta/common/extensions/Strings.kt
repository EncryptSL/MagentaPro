package com.github.encryptsl.magenta.common.extensions

import com.github.keelar.exprk.Expressions
import java.util.*

private const val AVATAR = "https://visage.surgeplay.com/bust/%s.png?,shadow&y=-40"

fun UUID.trimUUID() = this.toString().replace("-","")
fun UUID.toMinotarAvatar() = AVATAR.format(this.trimUUID())

fun expressionCalculation(string: String): String {
    val expression = Expressions()
    return expression.eval(string).toString()
}