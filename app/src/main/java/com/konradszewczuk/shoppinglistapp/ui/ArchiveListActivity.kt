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
import com.konradszewczuk.shoppinglistapp.ui.utils.RecyclerViewClickListener
import com.konradszewczuk.shoppinglistapp.ui.utils.ShoppingListAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*


class ArchiveListActivity : AppCompatActivity(), RecyclerViewClickListener {
    override fun onClick(view: View, position: Int) {
        val id = shoppingList.get(position).id
        goToShoppingListDetailsActivity(id)
    }


    private lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: ShoppingListViewModel

    private val disposable = CompositeDisposable()

    private var shoppingList = ArrayList<ShoppingListItem>()

    private var mAdapter: ShoppingListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

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

    private fun goToShoppingListDetailsActivity(id: Int) {
        val intent = Intent(this, ShoppingListDetailsActivity::class.java)
        intent.putExtra("id", id)
        startActivity(intent)
    }

    override fun onStart() {
        super.onStart()
        // Subscribe to the emissions of the user name from the view model.
        // Update the user name text view, at every onNext emission.
        // In case of error, log the exception.
        viewModel.getArchivedLists()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ t ->
                    progressBar.visibility = View.GONE
                    shoppingList.clear()
                    t?.forEach {
                        val item = ShoppingListItem(it.id, it.name, it.timestamp)
                        shoppingList.add(item)
                    }

                    mAdapter?.notifyDataSetChanged()
                })
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onStop() {
        super.onStop()

        // clear all the subscription
        disposable.clear()
    }

    companion object {
        private val TAG = ArchiveListActivity::class.java.simpleName
    }

}
