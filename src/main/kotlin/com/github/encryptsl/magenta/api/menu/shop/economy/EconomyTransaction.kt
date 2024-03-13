package com.github.encryptsl.magenta.api.menu.shop.economy

interface EconomyTransaction {
    fun transaction(economy: Economy): TransactionProcess?
}