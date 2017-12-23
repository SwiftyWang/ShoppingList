package com.konradszewczuk.shoppinglistapp.ui.utils

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.konradszewczuk.shoppinglistapp.R
import com.konradszewczuk.shoppinglistapp.ui.ShoppingListItem


class ShoppingListAdapter(val list: ArrayList<ShoppingListItem>, val context: Context, val listener: RecyclerViewClickListener) : RecyclerView.Adapter<ShoppingListAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return list.count()
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val item = list.get(position)
        holder?.name?.setText(item.name)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent?.getContext())
                .inflate(R.layout.item_shopping_list, parent, false)
        return ViewHolder(itemView, listener)
    }


    inner class ViewHolder(view: View, clickListener: RecyclerViewClickListener) : RecyclerView.ViewHolder(view), View.OnClickListener {


        var name: TextView
        var description: TextView
        var price: TextView
        var thumbnail: ImageView
        var viewBackground: RelativeLayout
        var viewForeground: RelativeLayout
        private var viewClickListener: RecyclerViewClickListener? = null


        init {
            name = view.findViewById(R.id.name)
            description = view.findViewById(R.id.description)
            price = view.findViewById(R.id.price)
            thumbnail = view.findViewById(R.id.thumbnail)
            viewBackground = view.findViewById(R.id.view_background)
            viewForeground = view.findViewById(R.id.view_foreground)
            viewClickListener = clickListener
            view.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            viewClickListener?.onClick(v, adapterPosition)

        }
    }

    fun removeItem(position: Int) {
        list.removeAt(position)
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position)
    }

    fun restoreItem(shoppingListItem: ShoppingListItem, position: Int) {
        list.add(position, shoppingListItem)
        // notify shoppingListItem added by position
        notifyItemInserted(position)
    }
}