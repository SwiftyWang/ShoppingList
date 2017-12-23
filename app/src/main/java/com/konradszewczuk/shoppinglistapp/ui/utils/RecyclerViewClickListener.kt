package com.konradszewczuk.shoppinglistapp.ui.utils

import android.view.View


interface RecyclerViewClickListener {

    fun onClick(view: View, position: Int)
}