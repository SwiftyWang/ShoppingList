package com.konradszewczuk.shoppinglistapp.ui.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import com.konradszewczuk.shoppinglistapp.R
import com.konradszewczuk.shoppinglistapp.ui.dto.ShoppingListElementDTO
import com.konradszewczuk.shoppinglistapp.ui.listeners.ShoppingItemCheckboxListener
import java.util.*


class ShoppingListDetailsAdapter(val list: ArrayList<ShoppingListElementDTO>, val context: Context, val listener: ShoppingItemCheckboxListener, val isArchived: Boolean) : RecyclerView.Adapter<ShoppingListDetailsAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return list.count()
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent?.getContext())
                .inflate(R.layout.item_shopping_list_element, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val item = list.get(position)
        holder?.name?.setText(item.name)
        if (item.isCompleted) {
            holder?.isCompleted?.isChecked = true
        }


        if (isArchived)
            holder?.isCompleted?.isEnabled = false
        else {
            holder?.isCompleted?.isEnabled = true
        }

        holder?.isCompleted?.setOnCheckedChangeListener { buttonView, isChecked ->
            item.isCompleted = isChecked
            listener.onClick(position, isChecked)
        }

    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var name: TextView
        var isCompleted: CheckBox
        private var viewClickListener: ShoppingItemCheckboxListener? = null


        init {
            name = view.findViewById(R.id.itemName)
            isCompleted = view.findViewById(R.id.checkbox)
        }

    }

    fun removeItem(position: Int) {
        list.removeAt(position)
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position)
    }

    fun restoreItem(item: ShoppingListElementDTO, position: Int) {
        list.add(position, item)
        // notify item added by position
        notifyItemInserted(position)
    }
}