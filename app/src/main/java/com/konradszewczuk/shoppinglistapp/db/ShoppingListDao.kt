package com.konradszewczuk.shoppinglistapp.db

import android.arch.persistence.room.*
import io.reactivex.Flowable


@Dao
interface ShoppingListDao {
    @Query("SELECT * FROM shopping_list where id = :id limit 1")
    fun getShoppingList(id: Int): Flowable<ShoppingList>

    @Query("SELECT * FROM shopping_list where not is_archived")
    fun getActiveShoppingLists(): Flowable<List<ShoppingList>>

    @Query("SELECT * FROM shopping_list where is_archived")
    fun getArchivedShoppingLists(): Flowable<List<ShoppingList>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertShoppingList(shoppingList: ShoppingList)

    @Update
    fun updateShoppingList(shoppingList: ShoppingList)

    @Query("UPDATE shopping_list SET is_archived = 1 where id = :id")
    fun archiveShoppingList(id: Int)

    @Query("UPDATE shopping_list SET  is_archived = 0 where id = :id")
    fun reArchiveShoppingList(id: Int)
}