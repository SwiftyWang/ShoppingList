package com.konradszewczuk.shoppinglistapp.ui

import android.arch.lifecycle.ViewModel
import com.konradszewczuk.shoppinglistapp.db.ShoppingList
import com.konradszewczuk.shoppinglistapp.db.ShoppingListDao
import com.konradszewczuk.shoppinglistapp.db.ShoppingListItem
import io.reactivex.Flowable
import io.reactivex.Single
import java.util.*
import kotlin.collections.ArrayList


class ShoppingListViewModel(private val dataSource: ShoppingListDao) : ViewModel() {

    private fun updateShoppingLists(listName: String){
//        dataSource.updateShoppingList()
    }

    fun createShoppingList(listName: String){
        val arrayList = ArrayList<ShoppingListItem>()
        val shoppingList = ShoppingList(name = listName, isArchived = false, items = arrayList, timestamp = Date())
        dataSource.insertShoppingList(shoppingList)
    }

    fun createShoppingListItem(itemName: String, shoppingListId: Int){
        dataSource.getShoppingList(shoppingListId)
                .firstElement()
                .subscribe {
                    shoppingList: ShoppingList ->
                    val items = shoppingList.items
                    items.add(ShoppingListItem(itemName, false))
                    dataSource.updateShoppingList(shoppingList = shoppingList)
                }
    }

    //TODO remove(only for testing)
    fun createArchiveList(listName: String){
        val arrayList = ArrayList<ShoppingListItem>()
        val shoppingList = ShoppingList(name = listName, isArchived = true, items = arrayList, timestamp = Date())
        dataSource.insertShoppingList(shoppingList)
    }


    fun getShoppingLists(): Flowable<List<ShoppingList>> {
        return dataSource.getActiveShoppingLists()
                .map {
                    t -> t.sortedByDescending { it.timestamp }
                }
    }

    fun getArchivedLists(): Flowable<List<ShoppingList>> {
        return dataSource.getArchivedShoppingLists()
                .map {
                    t -> t.sortedByDescending { it.timestamp }
                }


    }

    fun getShoppingList(id: Int): Flowable<ShoppingList> {
        return dataSource.getShoppingList(id)
    }

    fun archiveItem(deletedShoppingListItem: com.konradszewczuk.shoppinglistapp.ui.ShoppingListItem) {

       dataSource.archiveShoppingList(deletedShoppingListItem.id)
    }

    fun reArchiveItem(deletedShoppingListItem: com.konradszewczuk.shoppinglistapp.ui.ShoppingListItem){

        dataSource.reArchiveShoppingList(deletedShoppingListItem.id)
    }
}