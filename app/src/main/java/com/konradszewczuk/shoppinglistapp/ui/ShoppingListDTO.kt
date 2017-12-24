package com.konradszewczuk.shoppinglistapp.ui

import java.util.*



data class ShoppingListDTO(
    var id: Int,
    var name: String,
    var timeStamp: Date,
    var isArchived: Boolean,
    var itemsCompletedCount: Int,
    var itemsAllCount: Int
    )