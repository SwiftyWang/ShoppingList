package com.konradszewczuk.shoppinglistapp.ui

import android.arch.lifecycle.ViewModel
import com.konradszewczuk.shoppinglistapp.db.ShoppingList
import com.konradszewczuk.shoppinglistapp.db.ShoppingListDao
import com.konradszewczuk.shoppinglistapp.db.ShoppingListItem
import io.reactivex.Flowable
import java.util.*
import kotlin.collections.ArrayList

class ShoppingListViewModel(private val dataSource: ShoppingListDao) : ViewModel() {

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
                     items.add(ShoppingListItem(itemName, false, Date()))

                    dataSource.updateShoppingList(shoppingList = shoppingList)
                }
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

    fun removeShoppingListItem(deletedItem: ShoppingListElementItem, shoppingListId: Int) {
        dataSource.getShoppingList(shoppingListId)
                .firstElement()
                .subscribe {
                    shoppingList: ShoppingList ->
                    val items: ArrayList<ShoppingListItem> = shoppingList.items

                    val filter = items.filter {
                        it.timestamp != deletedItem.timestamp
                    }

                    dataSource.updateShoppingList(shoppingList = ShoppingList(id = shoppingList.id,
                            name = shoppingList.name,
                            isArchived = shoppingList.isArchived,
                            timestamp = shoppingList.timestamp,
                            items = filter as ArrayList<ShoppingListItem>
                            ))
                }
    }

    fun restoreShoppingListItem(deletedItem: ShoppingListElementItem, shoppingListId: Int) {
        dataSource.getShoppingList(shoppingListId)
                .firstElement()
                .subscribe {
                    shoppingList: ShoppingList ->
                    val items = shoppingList.items
                    items.add(ShoppingListItem(deletedItem.name, deletedItem.isCompleted, deletedItem.timestamp))
                    dataSource.updateShoppingList(shoppingList = ShoppingList(id = shoppingList.id,
                            name = shoppingList.name,
                            isArchived = shoppingList.isArchived,
                            timestamp = shoppingList.timestamp,
                            items = items
                    ))
                }
    }
}