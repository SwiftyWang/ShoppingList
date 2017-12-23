package com.konradszewczuk.shoppinglistapp.db

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.util.*
import kotlin.collections.ArrayList


@Entity(tableName = "shopping_list")
data class ShoppingList(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "id")
        val id: Int = 0,
        @ColumnInfo(name = "name")
        val name: String,
        @ColumnInfo(name = "is_archived")
        val isArchived: Boolean,
        @ColumnInfo(name = "timestamp")
        val timestamp: Date,
        @ColumnInfo(name = "items")
        val items: ArrayList<ShoppingListItem>
)

data class ShoppingListItem(
        val name: String,
        val isCompleted: Boolean
)