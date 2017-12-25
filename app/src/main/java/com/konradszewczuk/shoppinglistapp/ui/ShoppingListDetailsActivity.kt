package com.konradszewczuk.shoppinglistapp.ui

import android.arch.lifecycle.ViewModelProviders
import android.content.DialogInterface
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import com.konradszewczuk.shoppinglistapp.Injection
import com.konradszewczuk.shoppinglistapp.R
import com.konradszewczuk.shoppinglistapp.ui.dto.ShoppingListElementDTO
import com.konradszewczuk.shoppinglistapp.ui.listeners.RecyclerItemTouchHelper
import com.konradszewczuk.shoppinglistapp.ui.listeners.ShoppingItemCheckboxListener
import com.konradszewczuk.shoppinglistapp.ui.adapters.ShoppingListDetailsAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_shopping_list_item.*

import kotlinx.android.synthetic.main.content_shopping_list_item.*
import java.util.*

class ShoppingListDetailsActivity : AppCompatActivity(), RecyclerItemTouchHelper.RecyclerItemTouchHelperListener, ShoppingItemCheckboxListener {

    private lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel: ShoppingListViewModel
    private var intExtra: Int? = null
    private var isArchived: Boolean? = null
    private val disposable = CompositeDisposable()
    private var shoppingList = ArrayList<ShoppingListElementDTO>()
    private var mAdapter: ShoppingListDetailsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shopping_list)
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        intExtra = getIntent().getIntExtra("id", 0)
        isArchived = getIntent().getBooleanExtra("isArchived", false)

        viewModelFactory = Injection.provideViewModelFactory(this)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ShoppingListViewModel::class.java)

        mAdapter = ShoppingListDetailsAdapter(shoppingList, this, this, isArchived!!)

        val mLayoutManager = LinearLayoutManager(applicationContext)
        recyclerView.setLayoutManager(mLayoutManager)
        recyclerView.setItemAnimator(DefaultItemAnimator())
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        recyclerView.setAdapter(mAdapter)

        if (isArchived as Boolean) {
            fab.visibility = View.GONE
        }
        else{
            fab.setOnClickListener { view ->
                val alertDialogAndroid = getShoppingListDialog()
                alertDialogAndroid?.show()
            }
        }

        val itemTouchHelperCallback1 = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // Row is swiped from recycler view
                // remove it from adapter
                if (viewHolder is ShoppingListDetailsAdapter.ViewHolder) {
                    // get the removed item name to display it in snack bar
                    val name = shoppingList[viewHolder.adapterPosition].name

                    // backup of removed item for undo purpose
                    val deletedItem = shoppingList[viewHolder.adapterPosition]
                    val deletedIndex = viewHolder.adapterPosition

                    // remove the item from recycler view
                    mAdapter?.removeItem(viewHolder.adapterPosition)
                    viewModel.removeShoppingListItem(deletedItem, intExtra!!)


                    // showing snack bar with Undo option
                    val snackbar = Snackbar
                            .make(coordinatorLayout, name + " is deleted!", Snackbar.LENGTH_LONG)
                    snackbar.setAction("UNDO", View.OnClickListener {
                        // undo is selected, restore the deleted item
                        mAdapter?.restoreItem(deletedItem, deletedIndex)
                        viewModel.restoreShoppingListItem(deletedItem, intExtra!!)
                    })
                    snackbar.setActionTextColor(Color.YELLOW)
                    snackbar.show()
                }
                Log.v("Test", "test")
            }

            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }

        if (!isArchived!!)
            ItemTouchHelper(itemTouchHelperCallback1).attachToRecyclerView(recyclerView)
    }

    fun getShoppingListDialog(): AlertDialog? {
        val layoutInflaterAndroid = LayoutInflater.from(this)
        val mView = layoutInflaterAndroid.inflate(R.layout.dialog_input_name, null)
        val alertDialogBuilderUserInput = AlertDialog.Builder(this)
        alertDialogBuilderUserInput.setView(mView)

        val userInputDialogEditText = mView.findViewById(R.id.userInputDialog) as EditText
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton("Create", DialogInterface.OnClickListener { dialogBox, id ->
                    viewModel.createShoppingListItem(userInputDialogEditText.text.toString(), intExtra!!)
                })

                .setNegativeButton("Cancel",
                        DialogInterface.OnClickListener { dialogBox, id -> dialogBox.cancel() })

        return alertDialogBuilderUserInput.create()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onStart() {
        super.onStart()
        if (intExtra != null)
            disposable.add(viewModel.getShoppingList(intExtra!!)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ t ->
                        shoppingList.clear()
                        t.items.forEach {
                            val item = ShoppingListElementDTO(it.name, it.isCompleted, it.timestamp)
                            shoppingList.add(item)
                        }


                        mAdapter?.notifyDataSetChanged()
                    }))
    }

    override fun onStop() {
        super.onStop()

        // clear all the subscription
        disposable.clear()
    }

    override fun onClick(position: Int, isChecked: Boolean) {
        shoppingList.get(position).isCompleted = isChecked
        viewModel.updateShoppingList(shoppingList, intExtra!!)
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int, position: Int) {
    }

}