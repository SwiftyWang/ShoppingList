package com.konradszewczuk.shoppinglistapp.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context


@Database(entities = [ShoppingList::class], version = 1)
@TypeConverters(ShoppingListDatabaseConverters::class)
abstract class ShoppingListDatabase : RoomDatabase() {

    abstract fun shoppingListDao(): ShoppingListDao

    companion object {

        @Volatile
        private var INSTANCE: ShoppingListDatabase? = null

        fun getInstance(context: Context): ShoppingListDatabase =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
                }

        private fun buildDatabase(context: Context) =
                Room.databaseBuilder(context.applicationContext,
                        ShoppingListDatabase::class.java, "shopping_list.db")
                        .build()
    }
}
