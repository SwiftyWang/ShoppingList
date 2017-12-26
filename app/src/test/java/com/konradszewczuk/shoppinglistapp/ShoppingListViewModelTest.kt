package com.konradszewczuk.shoppinglistapp

import com.konradszewczuk.shoppinglistapp.db.ShoppingList
import com.konradszewczuk.shoppinglistapp.db.ShoppingListDao
import com.konradszewczuk.shoppinglistapp.db.ShoppingListItem
import com.konradszewczuk.shoppinglistapp.ui.ShoppingListViewModel
import com.konradszewczuk.shoppinglistapp.ui.dto.ShoppingListElementDTO
import io.reactivex.Flowable
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.*
import org.mockito.junit.MockitoJUnitRunner
import java.util.*
import kotlin.collections.ArrayList

@RunWith(MockitoJUnitRunner::class)
class ShoppingListViewModelTest {

    @Mock
    private lateinit var dataSource: ShoppingListDao

    private lateinit var viewModel: ShoppingListViewModel

    @Captor
    private lateinit var shoppingListArgumentCaptor: ArgumentCaptor<ShoppingList>


    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        viewModel = ShoppingListViewModel(dataSource)
        STUB_SHOPPING_LIST_ITEMS.add(ShoppingListItem("test", false, Date(123456)))
        STUB_SHOPPING_LIST_ITEMS.add(ShoppingListItem("test2", false, Date(123457)))
        STUB_SHOPPING_LIST_ITEMS.add(ShoppingListItem("test3", false, Date(123458)))
    }

    @After
    fun clearStub() {
        STUB_SHOPPING_LIST_ITEMS.clear()
    }

    @Test
    fun getActiveShoppingList_whenNoShoppingListSaved() {
        // Given that the ShoppingListDataSource returns an empty list of active shopping list
        Mockito.`when`(dataSource.getActiveShoppingLists()).thenReturn(Flowable.empty<List<ShoppingList>>())

        //When getting the active shopping lists
        viewModel.getShoppingLists()
                .test()
                .assertNoValues()
    }

    @Test
    fun getShoppingList_whenActiveShoppingListSaved() {
        // Given that the ShoppingListDataSource returns a active shopping list
        val activeShoppingList = ShoppingList(
                SHOPPING_LIST_ID,
                SHOPPING_LIST_NAME,
                NOT_ARCHIVED,
                DATE,
                EMPTY_SHOPPING_LIST_ITEMS)

        val listOfActiveShoppingList = ArrayList<ShoppingList>()
        listOfActiveShoppingList.add(activeShoppingList)

        Mockito.`when`(dataSource.getActiveShoppingLists()).thenReturn(Flowable.just(listOfActiveShoppingList))

        val testAllShoppingList = viewModel.getShoppingLists()
                .test()

        //Return no empty values
        testAllShoppingList.assertNoErrors()
                .assertValue { list: List<ShoppingList> ->
                    list.size != 0
                }

        //All of the returned lists are not archived
        val testSingleShoppingList = viewModel.getShoppingLists()
                .flatMapIterable { list: List<ShoppingList> ->
                    list
                }
                .test()

        testSingleShoppingList.assertValue { shoppingList: ShoppingList ->
            shoppingList.isArchived == false
        }
    }

    @Test
    fun getArchivedShoppingList_whenNoShoppingListSaved() {
        // Given that the ShoppingListDataSource returns an empty list of active shopping list
        Mockito.`when`(dataSource.getArchivedShoppingLists()).thenReturn(Flowable.empty<List<ShoppingList>>())

        //When getting the active shopping lists
        viewModel.getArchivedLists()
                .test()
                .assertNoValues()
    }

    @Test
    fun getArchivedShoppingList_whenArchivedShoppingListSaved() {
        // Given that the ShoppingListDataSource returns an archived shopping list
        val archivedShoppingList = ShoppingList(
                SHOPPING_LIST_ID,
                SHOPPING_LIST_NAME,
                ARCHIVED,
                DATE,
                EMPTY_SHOPPING_LIST_ITEMS)

        val listOfArchivedShoppingList = ArrayList<ShoppingList>()
        listOfArchivedShoppingList.add(archivedShoppingList)

        Mockito.`when`(dataSource.getArchivedShoppingLists()).thenReturn(Flowable.just(listOfArchivedShoppingList))

        val testAllShoppingList = viewModel.getArchivedLists()
                .test()

        testAllShoppingList.assertNoErrors()
                .assertValue { list: List<ShoppingList> ->
                    list.size != 0
                }

        val testSingleShoppingList = viewModel.getArchivedLists()
                .flatMapIterable { list: List<ShoppingList> ->
                    list
                }
                .test()

        testSingleShoppingList
                .assertNoErrors()
                .assertValue { shoppingList: ShoppingList ->
                    shoppingList.isArchived == true
                }
    }


    @Test
    fun createShoppingListItem_addsNewItemToShoppingList() {
        val activeShoppingList = ShoppingList(
                SHOPPING_LIST_ID,
                SHOPPING_LIST_NAME,
                NOT_ARCHIVED,
                DATE,
                EMPTY_SHOPPING_LIST_ITEMS)

        Mockito.`when`(dataSource.getShoppingList(SHOPPING_LIST_ID)).thenReturn(Flowable.just(activeShoppingList))

        viewModel.createShoppingListItem("item1", SHOPPING_LIST_ID)

        Mockito.verify<ShoppingListDao>(dataSource).updateShoppingList(capture(shoppingListArgumentCaptor))

        assertEquals(1, shoppingListArgumentCaptor.value.items.size)
        assertTrue(shoppingListArgumentCaptor.value.id == SHOPPING_LIST_ID)
        assertTrue(shoppingListArgumentCaptor.value.items[0].name.equals("item1"))
    }

    @Test
    fun removeShoppingListItem_removesItemsFromShoppingList() {
        val activeShoppingList = ShoppingList(
                SHOPPING_LIST_ID,
                SHOPPING_LIST_NAME,
                NOT_ARCHIVED,
                DATE,
                STUB_SHOPPING_LIST_ITEMS)

        Mockito.`when`(dataSource.getShoppingList(SHOPPING_LIST_ID)).thenReturn(Flowable.just(activeShoppingList))


        viewModel.removeShoppingListItem(
                ShoppingListElementDTO(
                        STUB_SHOPPING_LIST_ITEMS[0].name,
                        STUB_SHOPPING_LIST_ITEMS[0].isCompleted,
                        STUB_SHOPPING_LIST_ITEMS[0].timestamp
                ),
                SHOPPING_LIST_ID)

        Mockito.verify<ShoppingListDao>(dataSource).updateShoppingList(capture(shoppingListArgumentCaptor))

        assertEquals(STUB_SHOPPING_LIST_ITEMS.size - 1, shoppingListArgumentCaptor.value.items.size)
        assertTrue(!shoppingListArgumentCaptor.value.items.contains(STUB_SHOPPING_LIST_ITEMS[0]))
        assertTrue(shoppingListArgumentCaptor.value.id == SHOPPING_LIST_ID)
    }

    @Test
    fun restoreShoppingListItem_restoresItemsFromShoppingList(){
        val stubShoppingListItemsWithoutFirstItem = STUB_SHOPPING_LIST_ITEMS.subList(1, 3)

        val shoppingListItems = ArrayList<ShoppingListItem>()
        shoppingListItems += stubShoppingListItemsWithoutFirstItem

        val activeShoppingList = ShoppingList(
                SHOPPING_LIST_ID,
                SHOPPING_LIST_NAME,
                NOT_ARCHIVED,
                DATE,
                shoppingListItems)

        Mockito.`when`(dataSource.getShoppingList(SHOPPING_LIST_ID)).thenReturn(Flowable.just(activeShoppingList))

        viewModel.restoreShoppingListItem( ShoppingListElementDTO(
                STUB_SHOPPING_LIST_ITEMS[0].name,
                STUB_SHOPPING_LIST_ITEMS[0].isCompleted,
                STUB_SHOPPING_LIST_ITEMS[0].timestamp
        ), SHOPPING_LIST_ID)

        Mockito.verify<ShoppingListDao>(dataSource).updateShoppingList(capture(shoppingListArgumentCaptor))

        assertEquals(STUB_SHOPPING_LIST_ITEMS.size, shoppingListArgumentCaptor.value.items.size)
        assertTrue(shoppingListArgumentCaptor.value.items.contains(STUB_SHOPPING_LIST_ITEMS[0]))
        assertTrue(shoppingListArgumentCaptor.value.id == SHOPPING_LIST_ID)
    }

    @Test
    fun updateShoppingList_updatesItemsInShoppingList(){
        val activeShoppingList = ShoppingList(
                SHOPPING_LIST_ID,
                SHOPPING_LIST_NAME,
                NOT_ARCHIVED,
                DATE,
                STUB_SHOPPING_LIST_ITEMS)

        //given activeShoppingList item with all NOT completed items
        Mockito.`when`(dataSource.getShoppingList(SHOPPING_LIST_ID)).thenReturn(Flowable.just(activeShoppingList))


        val shoppingListElementDTOAfterUpdate = ArrayList<ShoppingListElementDTO>()

        shoppingListElementDTOAfterUpdate.add(ShoppingListElementDTO(
                STUB_SHOPPING_LIST_ITEMS[0].name,
                !STUB_SHOPPING_LIST_ITEMS[0].isCompleted,
                STUB_SHOPPING_LIST_ITEMS[0].timestamp
        ))

        shoppingListElementDTOAfterUpdate.add(ShoppingListElementDTO(
                STUB_SHOPPING_LIST_ITEMS[1].name,
                !STUB_SHOPPING_LIST_ITEMS[1].isCompleted,
                STUB_SHOPPING_LIST_ITEMS[1].timestamp
        ))

        shoppingListElementDTOAfterUpdate.add(ShoppingListElementDTO(
                STUB_SHOPPING_LIST_ITEMS[2].name,
                !STUB_SHOPPING_LIST_ITEMS[2].isCompleted,
                STUB_SHOPPING_LIST_ITEMS[2].timestamp
        ))

        viewModel.updateShoppingList(shoppingListElementDTOAfterUpdate, SHOPPING_LIST_ID)

        //verify that all items in activeShoppingList are completed now
        Mockito.verify<ShoppingListDao>(dataSource).updateShoppingList(capture(shoppingListArgumentCaptor))
        shoppingListArgumentCaptor.value.items.forEach {
            assert(it.isCompleted)
        }
    }

    companion object {
        const val SHOPPING_LIST_ID = 1
        const val SHOPPING_LIST_NAME = "test"
        const val NOT_ARCHIVED = false
        const val ARCHIVED = true
        val DATE = Date()
        val EMPTY_SHOPPING_LIST_ITEMS = ArrayList<ShoppingListItem>()
        val STUB_SHOPPING_LIST_ITEMS = ArrayList<ShoppingListItem>()
    }
}
