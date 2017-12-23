package com.konradszewczuk.shoppinglistapp.db

import android.arch.persistence.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

/**
 * Created by Admin on 2017-12-23.
 */
class ShoppingListDatabaseConverters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return if (value == null) null else Date(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun stringToShoppingListItems(json: String): List<ShoppingListItem> {

        val gson = Gson()
        val shoppingListItems : List<ShoppingListItem> = gson.fromJson(json, object : TypeToken<List<ShoppingListItem>>() {}.type)

        return shoppingListItems
    }

    @TypeConverter
    fun shoppingListItemsToString(list: List<ShoppingListItem>): String {
        val gson = Gson()
        val type = object : TypeToken<List<ShoppingListItem>>() {

        }.type
        return gson.toJson(list, type)
    }
}