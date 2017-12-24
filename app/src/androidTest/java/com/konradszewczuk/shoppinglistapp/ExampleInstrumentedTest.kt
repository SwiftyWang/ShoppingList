package com.konradszewczuk.shoppinglistapp

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.konradszewczuk.shoppinglistapp.db.ShoppingList

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getTargetContext()
        assertEquals("com.konradszewczuk.shoppinglistapp", appContext.packageName)
    }

    @Test
    fun gowno(){
        val dataSource = Injection.provideUserDataSource(InstrumentationRegistry.getTargetContext())
         dataSource.getActiveShoppingLists()
                 .subscribe {
                     list: List<ShoppingList>? ->
                     list?.toString()
                 }

    }
}
