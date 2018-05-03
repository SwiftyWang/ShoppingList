package com.konradszewczuk.shoppinglistapp.ui

import android.arch.lifecycle.ViewModel
import com.konradszewczuk.shoppinglistapp.db.ShoppingList
import com.konradszewczuk.shoppinglistapp.db.ShoppingListDao
import com.konradszewczuk.shoppinglistapp.db.ShoppingListItem
import com.konradszewczuk.shoppinglistapp.ui.dto.ShoppingListDTO
import com.konradszewczuk.shoppinglistapp.ui.dto.ShoppingListElementDTO
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import java.util.*
import kotlin.collections.ArrayList

class ShoppingListViewModel(private val dataSource: ShoppingListDao) : ViewModel() {

    fun createShoppingList(listName: String) {
        val arrayList = ArrayList<ShoppingListItem>()
        val shoppingList = ShoppingList(name = listName, isArchived = false, items = arrayList, timestamp = Date())
        Completable.fromCallable{dataSource.insertShoppingList(shoppingList)}.subscribeOn(Schedulers.io()).subscribe()
    }

    fun createShoppingListItem(itemName: String, shoppingListId: Int) {
        dataSource.getShoppingList(shoppingListId)
                .firstElement()
                .subscribe { shoppingList: ShoppingList ->
                    val items = shoppingList.items
                    items.add(ShoppingListItem(itemName, false, Date()))

                    dataSource.updateShoppingList(shoppingList = shoppingList)
                }
    }

    fun getShoppingLists(): Flowable<List<ShoppingList>> {
        return dataSource.getActiveShoppingLists()
                .map { t ->
                    t.sortedByDescending { it.timestamp }
                }
    }

    fun getArchivedLists(): Flowable<List<ShoppingList>> {
        return dataSource.getArchivedShoppingLists()
                .map { t ->
                    t.sortedByDescending { it.timestamp }
                }

    }

    fun getShoppingList(id: Int): Flowable<ShoppingList> {
        return dataSource.getShoppingList(id)
    }

    fun archiveItem(deletedShoppingListItem: ShoppingListDTO) {
        Completable.fromCallable {dataSource.archiveShoppingList(deletedShoppingListItem.id)}.subscribeOn(Schedulers.io()).subscribe()
    }

    fun reArchiveItem(deletedShoppingListItem: ShoppingListDTO) {
        Completable.fromCallable {dataSource.reArchiveShoppingList(deletedShoppingListItem.id)}.subscribeOn(Schedulers.io()).subscribe()
    }

    fun removeShoppingListItem(deletedItem: ShoppingListElementDTO, shoppingListId: Int) {
        dataSource.getShoppingList(shoppingListId)
                .firstElement()
                .subscribe { shoppingList: ShoppingList ->
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

    fun restoreShoppingListItem(deletedItem: ShoppingListElementDTO, shoppingListId: Int) {
        dataSource.getShoppingList(shoppingListId)
                .firstElement()
                .subscribe { shoppingList: ShoppingList ->
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

    fun updateShoppingList(shoppingList: ArrayList<ShoppingListElementDTO>, shoppingListId: Int) {
        dataSource.getShoppingList(shoppingListId)
                .firstElement()
                .subscribe { t: ShoppingList ->

                    val dbShoppingList = ArrayList<ShoppingListItem>()
                    shoppingList.forEach { it ->
                        dbShoppingList.add(ShoppingListItem(it.name, it.isCompleted, it.timestamp))
                    }

                    dataSource.updateShoppingList(shoppingList = ShoppingList(id = t.id,
                            name = t.name,
                            isArchived = t.isArchived,
                            timestamp = t.timestamp,
                            items = dbShoppingList
                    ))
                }
    }
}