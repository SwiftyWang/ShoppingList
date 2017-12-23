package com.konradszewczuk.shoppinglistapp.ui

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_main2.*
import android.arch.lifecycle.ViewModelProviders
import android.content.DialogInterface
import android.support.v7.app.AlertDialog
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import com.konradszewczuk.shoppinglistapp.Injection
import com.konradszewczuk.shoppinglistapp.R
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.content_main2.*
import java.util.*


class Main2Activity : AppCompatActivity() {

    private lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: ShoppingListViewModel

    private val disposable = CompositeDisposable()


    private var cartList = ArrayList<Item>()
    private var mAdapter: ShoppingListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        setSupportActionBar(toolbar)

        viewModelFactory = Injection.provideViewModelFactory(this)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ShoppingListViewModel::class.java)


        cartList = ArrayList()
        mAdapter = ShoppingListAdapter(cartList, this)

        val mLayoutManager = LinearLayoutManager(applicationContext)
        recyclerView.setLayoutManager(mLayoutManager)
        recyclerView.setItemAnimator(DefaultItemAnimator())
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        recyclerView.setAdapter(mAdapter)

        fab.setOnClickListener { view ->
            val alertDialogAndroid = getShoppingListDialog()
            alertDialogAndroid?.show()

//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show()
        }

    }

    override fun onStart() {
        super.onStart()
        viewModel.getShoppingLists()
                .observe(this, Observer {

                    t ->
                    cartList.clear()
                    t?.forEach {
                               val item = Item(it.name, it.timestamp)
                                cartList.add(item)
                            }
                            cartList.sortByDescending { it.timeStamp }
                            // refreshing recycler view
                            mAdapter?.notifyDataSetChanged()
                            Log.i(Main2Activity.TAG, "Unable to get username")
                })

    }

    override fun onStop() {
        super.onStop()

        // clear all the subscription
        disposable.clear()
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main3, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }

    fun getShoppingListDialog(): AlertDialog? {
        val layoutInflaterAndroid = LayoutInflater.from(this)
        val mView = layoutInflaterAndroid.inflate(R.layout.dialog_input_name, null)
        val alertDialogBuilderUserInput = AlertDialog.Builder(this)
        alertDialogBuilderUserInput.setView(mView)

        val userInputDialogEditText = mView.findViewById(R.id.userInputDialog) as EditText
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton("Send", DialogInterface.OnClickListener { dialogBox, id ->
                    viewModel.createShoppingList(userInputDialogEditText.text.toString())
                })

                .setNegativeButton("Cancel",
                        DialogInterface.OnClickListener { dialogBox, id -> dialogBox.cancel() })

        return alertDialogBuilderUserInput.create()
    }
}
