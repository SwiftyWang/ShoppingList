package com.konradszewczuk.shoppinglistapp.ui

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.konradszewczuk.shoppinglistapp.R

/**
 * Created by Admin on 2017-12-22.
 */
class ShoppingListAdapter(val list: ArrayList<Item>, val context: Context) : RecyclerView.Adapter<ShoppingListAdapter.ViewHolder>() {

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
        return ViewHolder(itemView)
    }


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var name: TextView
        var description: TextView
        var price: TextView
        var thumbnail: ImageView
        var viewBackground: RelativeLayout
        var viewForeground: RelativeLayout

        init {
            name = view.findViewById(R.id.name)
            description = view.findViewById(R.id.description)
            price = view.findViewById(R.id.price)
            thumbnail = view.findViewById(R.id.thumbnail)
            viewBackground = view.findViewById(R.id.view_background)
            viewForeground = view.findViewById(R.id.view_foreground)
        }
    }

    fun removeItem(position: Int) {
        list.removeAt(position)
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position)
    }

    fun restoreItem(item: Item, position: Int) {
        list.add(position, item)
        // notify item added by position
        notifyItemInserted(position)
    }
}