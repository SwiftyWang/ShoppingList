package com.konradszewczuk.shoppinglistapp.ui.utils

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import com.konradszewczuk.shoppinglistapp.R
import com.konradszewczuk.shoppinglistapp.ui.ShoppingListElementItem

/**
 * Created by Admin on 2017-12-23.
 */
class ShoppingListDetailsAdapter(val list: ArrayList<ShoppingListElementItem>, val context: Context, val listener: RecyclerViewClickListener, val isArchived: Boolean) : RecyclerView.Adapter<ShoppingListDetailsAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return list.count()
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent?.getContext())
                .inflate(R.layout.item_shopping_list_element, parent, false)
        return ViewHolder(itemView, listener)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val item = list.get(position)
        holder?.name?.setText(item.name)
        if(item.isCompleted){
            holder?.isCompleted?.isChecked = true
        }
        else{
            holder?.isCompleted?.isChecked = false
        }

        if(isArchived)
        holder?.isCompleted?.isEnabled = false
        else{
            holder?.isCompleted?.isEnabled = true
        }

        holder?.isCompleted?.setOnCheckedChangeListener{
            buttonView, isChecked ->
        }
    }


    inner class ViewHolder(view: View, clickListener: RecyclerViewClickListener) : RecyclerView.ViewHolder(view), View.OnClickListener {


        var name: TextView
        var isCompleted: CheckBox
        private var viewClickListener: RecyclerViewClickListener? = null


        init {
            name = view.findViewById(R.id.itemName)
            isCompleted = view.findViewById(R.id.checkbox)
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

    fun restoreItem(item: ShoppingListElementItem, position: Int) {
        list.add(position, item)
        // notify item added by position
        notifyItemInserted(position)
    }
}