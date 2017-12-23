package com.konradszewczuk.shoppinglistapp.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.konradszewczuk.shoppinglistapp.Injection
import com.konradszewczuk.shoppinglistapp.R
import com.konradszewczuk.shoppinglistapp.db.ShoppingList
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: ShoppingListViewModel

    private val disposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val viewModelFactory = ViewModelFactory()

//        val model = ViewModelProviders.of(this).get(ShoppingListViewModel::class.java)
//        val model = ViewModelProviders.of(this, viewModelFactory).get(ShoppingListViewModel::class.java)

        viewModelFactory = Injection.provideViewModelFactory(this)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ShoppingListViewModel::class.java)


//        model.getCounter().observe(this, Observer { counter ->
//            textView2.setText(counter.toString())
//        })
//
//        buttonGoToFirstActivity.setOnClickListener {
//
//            model.setCounter(model.getCounter().value as Int + 1)
//
//            val intent = Intent(this, Main2Activity::class.java)
//            startActivity(intent)
//        }
    }

    override fun onStart() {
        super.onStart()
        // Subscribe to the emissions of the user name from the view model.
        // Update the user name text view, at every onNext emission.
        // In case of error, log the exception.
//        disposable.add(viewModel.getShoppingLists()
//                .observe(this, Observer {
//                    t ->
//                })
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe({ list: List<ShoppingList>? ->  },
//                        { error -> Log.e(TAG, "Unable to get username", error) }))
    }

    override fun onStop() {
        super.onStop()

        // clear all the subscription
        disposable.clear()
    }
    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }

}
