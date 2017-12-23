package com.konradszewczuk.shoppinglistapp.ui

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.konradszewczuk.shoppinglistapp.db.ShoppingList
import com.konradszewczuk.shoppinglistapp.db.ShoppingListDao
import com.konradszewczuk.shoppinglistapp.db.ShoppingListItem
import io.reactivex.Observable
import io.reactivex.Single
import java.util.*


/**
 * Created by Admin on 2017-12-22.
 */
class ShoppingListViewModel(private val dataSource: ShoppingListDao) : ViewModel() {

    fun updateShoppingLists(listName: String){
//        dataSource.updateShoppingList()
    }

    fun createShoppingList(listName: String){
        val arrayList = ArrayList<ShoppingListItem>()
        val shoppingList = ShoppingList(name = listName, isArchived = false, items = arrayList, timestamp = Date())
        dataSource.insertShoppingList(shoppingList)
    }

    fun getShoppingLists(): LiveData<List<ShoppingList>> {
        return dataSource.getShoppingLists()
    }
}