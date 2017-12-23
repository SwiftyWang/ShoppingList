package com.konradszewczuk.shoppinglistapp.db

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*


@Dao
interface ShoppingListDao {

    @Query("SELECT * FROM shopping_list")
    fun getShoppingLists(): LiveData<List<ShoppingList>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertShoppingList(shoppingList: ShoppingList)

    @Update
    fun updateShoppingList(shoppingList: ShoppingList)
}