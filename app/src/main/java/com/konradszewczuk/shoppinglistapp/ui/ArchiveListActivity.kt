package com.konradszewczuk.shoppinglistapp.ui

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.konradszewczuk.shoppinglistapp.Injection
import com.konradszewczuk.shoppinglistapp.R
import com.konradszewczuk.shoppinglistapp.db.ShoppingListItem
import com.konradszewczuk.shoppinglistapp.ui.dto.ShoppingListDTO
import com.konradszewczuk.shoppinglistapp.ui.listeners.RecyclerViewClickListener
import com.konradszewczuk.shoppinglistapp.ui.adapters.ShoppingListAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_archive_list.*


class ArchiveListActivity : AppCompatActivity(), RecyclerViewClickListener {

    private lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel: ShoppingListViewModel
    private val disposable = CompositeDisposable()
    private var shoppingList = ArrayList<ShoppingListDTO>()
    private var mAdapter: ShoppingListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_archive_list)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewModelFactory = Injection.provideViewModelFactory(this)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ShoppingListViewModel::class.java)

        shoppingList = ArrayList()
        mAdapter = ShoppingListAdapter(shoppingList, this, this)

        val mLayoutManager = LinearLayoutManager(applicationContext)
        recyclerView.setLayoutManager(mLayoutManager)
        recyclerView.setItemAnimator(DefaultItemAnimator())
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        recyclerView.setAdapter(mAdapter)

    }

    private fun goToShoppingListDetailsActivity(id: Int, isArchived: Boolean) {
        val intent = Intent(this, ShoppingListDetailsActivity::class.java)
        intent.putExtra("id", id)
        intent.putExtra("isArchived", isArchived)
        startActivity(intent)
    }

    override fun onStart() {
        super.onStart()
        disposable.add(viewModel.getArchivedLists()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ t ->
                    progressBar.visibility = View.GONE
                    shoppingList.clear()
                    t?.forEach {
                        val completed = it.items.filter(ShoppingListItem::isCompleted)
                        val item = ShoppingListDTO(it.id, it.name, it.timestamp, it.isArchived, completed.size, it.items.size)
                        shoppingList.add(item)
                    }

                }))
    }

    override fun onClick(view: View, position: Int) {
        val id = shoppingList.get(position).id
        val isArchived = shoppingList.get(position).isArchived
        goToShoppingListDetailsActivity(id, isArchived)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onStop() {
        super.onStop()
        disposable.clear()
    }

}
