package com.konradszewczuk.shoppinglistapp.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_shopping_list.*
import android.arch.lifecycle.ViewModelProviders
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.support.design.widget.Snackbar
import android.support.design.widget.TextInputLayout
import android.support.v7.app.AlertDialog
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.jakewharton.rxbinding2.widget.RxTextView
import com.konradszewczuk.shoppinglistapp.Injection
import com.konradszewczuk.shoppinglistapp.R
import com.konradszewczuk.shoppinglistapp.db.ShoppingListItem
import com.konradszewczuk.shoppinglistapp.ui.dto.ShoppingListDTO
import com.konradszewczuk.shoppinglistapp.ui.listeners.RecyclerItemTouchHelper
import com.konradszewczuk.shoppinglistapp.ui.listeners.RecyclerViewClickListener
import com.konradszewczuk.shoppinglistapp.ui.adapters.ShoppingListAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.content_shopping_list.*
import java.util.*


class ShoppingListActivity : AppCompatActivity(), RecyclerItemTouchHelper.RecyclerItemTouchHelperListener, RecyclerViewClickListener {

    private lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel: ShoppingListViewModel
    private val disposable = CompositeDisposable()
    private var shoppingList = ArrayList<ShoppingListDTO>()
    private var mAdapter: ShoppingListAdapter? = null
    private var dialogCreateNamePositiveButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shopping_list)
        setSupportActionBar(toolbar)

        viewModelFactory = Injection.provideViewModelFactory(this)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ShoppingListViewModel::class.java)

        mAdapter = ShoppingListAdapter(shoppingList, this, this)

        val mLayoutManager = LinearLayoutManager(applicationContext)
        recyclerView.setLayoutManager(mLayoutManager)
        recyclerView.setItemAnimator(DefaultItemAnimator())
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        recyclerView.setAdapter(mAdapter)

        fab.setOnClickListener { view ->
            val alertDialogAndroid = getShoppingListDialog()
            alertDialogAndroid?.show()

            dialogCreateNamePositiveButton = alertDialogAndroid?.getButton(DialogInterface.BUTTON_POSITIVE)
            dialogCreateNamePositiveButton?.isEnabled = false

        }

        val itemTouchHelperCallback1 = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // Row is swiped from recycler view
                // remove it from adapter
                if (viewHolder is ShoppingListAdapter.ViewHolder) {
                    // get the removed item name to display it in snack bar
                    val name = shoppingList[viewHolder.adapterPosition].name

                    // backup of removed item for undo purpose
                    val deletedItem = shoppingList[viewHolder.adapterPosition]
                    val deletedIndex = viewHolder.adapterPosition

                    // remove the item from recycler view
                    mAdapter?.removeItem(viewHolder.adapterPosition)
                    viewModel.archiveItem(deletedItem)

                    // showing snack bar with Undo option
                    val snackbar = Snackbar
                            .make(coordinatorLayout, name + " is archived!", Snackbar.LENGTH_LONG)
                    snackbar.setAction("UNDO", View.OnClickListener {
                        // undo is selected, restore the deleted item
                        mAdapter?.restoreItem(deletedItem, deletedIndex)
                        viewModel.reArchiveItem(deletedItem)

                    })
                    snackbar.setActionTextColor(Color.YELLOW)
                    snackbar.show()
                }
                Log.v("Test" ,"test")
            }

            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }

        ItemTouchHelper(itemTouchHelperCallback1).attachToRecyclerView(recyclerView)

    }


    override fun onStart() {
        super.onStart()
        disposable.add(viewModel.getShoppingLists()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ t ->
                    shoppingList.clear()
                    t?.forEach {
                        val completed = it.items.filter(ShoppingListItem::isCompleted)
                        val item = ShoppingListDTO(it.id, it.name, it.timestamp, it.isArchived, completed.size, it.items.size)
                        shoppingList.add(item)
                    }

                    mAdapter?.notifyDataSetChanged()
                }))

    }

    override fun onStop() {
        super.onStop()
        disposable.clear()
    }

    fun goToArchiveListActivity(){
        val intent = Intent(this, ArchiveListActivity::class.java)
        startActivity(intent)
    }

    private fun goToShoppingListDetailsActivity(id: Int, isArchived: Boolean) {
        val intent = Intent(this, ShoppingListDetailsActivity::class.java)
        intent.putExtra("id", id)
        intent.putExtra("isArchived", isArchived)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main3, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_archived_list -> goToArchiveListActivity()
            else -> return super.onOptionsItemSelected(item)
        }

        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int, position: Int) {

    }

    override fun onClick(view: View, position: Int) {
        val id = shoppingList.get(position).id
        val isArchived = shoppingList.get(position).isArchived
        goToShoppingListDetailsActivity(id, isArchived)
    }

    fun getShoppingListDialog(): AlertDialog? {
        val layoutInflaterAndroid = LayoutInflater.from(this)
        val mView = layoutInflaterAndroid.inflate(R.layout.dialog_input_name, null)
        val alertDialogBuilderUserInput = AlertDialog.Builder(this)
        alertDialogBuilderUserInput.setView(mView)


        val nameInputDialogTextInputLayout =  mView.findViewById(R.id.nameInputDialogTextInputLayout) as TextInputLayout
        val userInputDialogEditText = mView.findViewById(R.id.nameInputDialog) as EditText
        val itemInputNameObservable = RxTextView.textChanges(userInputDialogEditText)
                .map { inputText: CharSequence -> inputText.isEmpty() }
                .distinctUntilChanged()

        itemInputNameObservable.subscribe{inputIsEmpty: Boolean ->
            Log.v("itemInputNameObservable", inputIsEmpty.toString())

            nameInputDialogTextInputLayout.setError("Name must not be empty")
            nameInputDialogTextInputLayout.setErrorEnabled(inputIsEmpty)

            dialogCreateNamePositiveButton?.isEnabled = !inputIsEmpty
        }

        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton("Create", { _, _ ->
                    viewModel.createShoppingList(userInputDialogEditText.text.toString())
                })

                .setNegativeButton("Cancel",
                        { dialogBox, _ -> dialogBox.cancel() })

        return alertDialogBuilderUserInput.create()
    }
}
