package com.konradszewczuk.shoppinglistapp.ui.dto

import java.util.*



data class ShoppingListElementDTO(
        var name: String,
        var isCompleted: Boolean,
        val timestamp: Date
)