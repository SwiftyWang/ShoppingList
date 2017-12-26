package com.konradszewczuk.shoppinglistapp

import android.content.Context
import com.konradszewczuk.shoppinglistapp.db.ShoppingListDao
import com.konradszewczuk.shoppinglistapp.db.ShoppingListDatabase
import com.konradszewczuk.shoppinglistapp.ui.ViewModelFactory

object Injection {

        fun provideUserDataSource(context: Context): ShoppingListDao {
            val database = ShoppingListDatabase.getInstance(context)
            return database.shoppingListDao()
        }

        fun provideViewModelFactory(context: Context): ViewModelFactory {
            val dataSource = provideUserDataSource(context)
            return ViewModelFactory(dataSource)
        }
}