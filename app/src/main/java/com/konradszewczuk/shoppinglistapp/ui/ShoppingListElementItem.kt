package com.konradszewczuk.shoppinglistapp.ui

import java.util.*


/**
 * Created by Admin on 2017-12-23.
 */
data class ShoppingListElementItem(
        var name: String,
        var isCompleted: Boolean,
        val timestamp: Date
)