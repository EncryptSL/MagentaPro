package com.github.encryptsl.magenta.api.menu.modules.shop.economy

interface EconomyTransaction {
    fun transaction(economy: Economy): TransactionProcess?
}