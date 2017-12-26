package com.konradszewczuk.shoppinglistapp

import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.konradszewczuk.shoppinglistapp.db.ShoppingList
import com.konradszewczuk.shoppinglistapp.db.ShoppingListDatabase
import com.konradszewczuk.shoppinglistapp.db.ShoppingListItem
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
class ShoppingListDaoTest {

    private lateinit var database: ShoppingListDatabase

    @Before
    fun initDb() {
        database = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(),
                ShoppingListDatabase::class.java)
                // allowing main thread queries, just for testing
                .allowMainThreadQueries()
                .build()
    }

    @After
    fun closeDb() {
        database.close()
    }

    @Test
    fun getShoppingListWhenNoShoppingListInserted() {
        database.shoppingListDao().getShoppingList(324)
                .test()
                .assertNoValues()
    }

    @Test
    fun insertAndGetShoppingLists() {

        database.shoppingListDao().insertShoppingList(ACTIVE_SHOPPING_LIST)
        database.shoppingListDao().insertShoppingList(ACTIVE_SHOPPING_LIST2)


        val firstEmmission = database.shoppingListDao().getActiveShoppingLists()
                .take(1)
                .test()

        firstEmmission.awaitTerminalEvent()

        firstEmmission
                .assertNoErrors()
                .assertValue {
                    it.size == 2 && !it[0].isArchived && !it[1].isArchived
                }
    }

    @Test
    fun insertAndGetArchivedShoppingLists() {

        database.shoppingListDao().insertShoppingList(ARCHIVED_SHOPPING_LIST)
        database.shoppingListDao().insertShoppingList(ARCHIVED_SHOPPING_LIST2)

        val firstEmmission = database.shoppingListDao().getArchivedShoppingLists()
                .take(1)
                .test()

        firstEmmission.awaitTerminalEvent()

        firstEmmission
                .assertNoErrors()
                .assertValue {
                    it.size == 2 && it[0].isArchived && it[1].isArchived
                }
    }

    @Test
    fun insertAndGetSingleShoppingList() {

        database.shoppingListDao().insertShoppingList(ACTIVE_SHOPPING_LIST)

        val firstEmmission = database.shoppingListDao().getShoppingList(ACTIVE_SHOPPING_LIST.id)
                .take(1)
                .test()

        firstEmmission.awaitTerminalEvent()

        firstEmmission
                .assertNoErrors()
                .assertValue {
                    it.name.equals(ACTIVE_SHOPPING_LIST.name) && !it.isArchived
                }
    }

    @Test
    fun updateAndGetShoppingList() {
        database.shoppingListDao().insertShoppingList(ACTIVE_SHOPPING_LIST)

        val updatedShoppingList = ShoppingList(
                ACTIVE_SHOPPING_LIST.id,
                timestamp = Date(),
                isArchived = false,
                name = "test_updated",
                items = ArrayList<ShoppingListItem>()
                )
        database.shoppingListDao().insertShoppingList(updatedShoppingList)

        val firstEmmission = database.shoppingListDao().getActiveShoppingLists()
                .take(1)
                .test()

        firstEmmission.awaitTerminalEvent()

        firstEmmission
                .assertNoErrors()
                .assertValue {
                    it.size == 1 && it[0].name.equals(updatedShoppingList.name)
                }
    }

    @Test
    fun testActiveShoppingListIsArchived(){
        database.shoppingListDao().insertShoppingList(ACTIVE_SHOPPING_LIST)

        val firstEmmission = database.shoppingListDao().getShoppingList(ACTIVE_SHOPPING_LIST.id)
                .take(1)
                .test()

        firstEmmission.awaitTerminalEvent()

        firstEmmission
                .assertNoErrors()
                .assertValue {
                    it.name.equals(ACTIVE_SHOPPING_LIST.name) && !it.isArchived
                }

        database.shoppingListDao().archiveShoppingList(ACTIVE_SHOPPING_LIST.id)

        val secondEmmission = database.shoppingListDao().getShoppingList(ACTIVE_SHOPPING_LIST.id)
                .take(1)
                .test()

        secondEmmission.awaitTerminalEvent()

        secondEmmission
                .assertNoErrors()
                .assertValue {
                    it.name.equals(ACTIVE_SHOPPING_LIST.name) && it.isArchived
                }
    }

    @Test
    fun testArchivedShoppingListIsReactivated(){
        database.shoppingListDao().insertShoppingList(ARCHIVED_SHOPPING_LIST)

        val firstEmmission = database.shoppingListDao().getShoppingList(ARCHIVED_SHOPPING_LIST.id)
                .take(1)
                .test()

        firstEmmission.awaitTerminalEvent()

        firstEmmission
                .assertNoErrors()
                .assertValue {
                    it.name.equals(ARCHIVED_SHOPPING_LIST.name) && it.isArchived
                }

        database.shoppingListDao().reArchiveShoppingList(ARCHIVED_SHOPPING_LIST.id)

        val secondEmmission = database.shoppingListDao().getShoppingList(ARCHIVED_SHOPPING_LIST.id)
                .take(1)
                .test()

        secondEmmission.awaitTerminalEvent()

        secondEmmission
                .assertNoErrors()
                .assertValue {
                    it.name.equals(ARCHIVED_SHOPPING_LIST.name) && !it.isArchived
                }
    }

    companion object {
        private val ACTIVE_SHOPPING_LIST = ShoppingList(
                id = 1,
                timestamp = Date(),
                isArchived = false,
                name = "test_active",
                items = ArrayList<ShoppingListItem>()
        )

        private val ACTIVE_SHOPPING_LIST2 = ShoppingList(
                id = 2,
                timestamp = Date(),
                isArchived = false,
                name = "test_active2",
                items = ArrayList<ShoppingListItem>()
        )

        private val ARCHIVED_SHOPPING_LIST = ShoppingList(
                id = 3,
                timestamp = Date(),
                isArchived = true,
                name = "test_archived",
                items = ArrayList<ShoppingListItem>()
        )

        private val ARCHIVED_SHOPPING_LIST2 = ShoppingList(
                id = 4,
                timestamp = Date(),
                isArchived = true,
                name = "test_archived2",
                items = ArrayList<ShoppingListItem>()
        )
    }
}