package com.konradszewczuk.shoppinglistapp.ui.utils

import android.content.Context
import android.content.DialogInterface
import android.support.v7.app.AlertDialog
import android.widget.EditText
import android.view.LayoutInflater
import com.konradszewczuk.shoppinglistapp.R



class ShoppingListDialog(val context: Context) {

    fun getShoppingListDialog(): AlertDialog? {
        val layoutInflaterAndroid = LayoutInflater.from(context)
        val mView = layoutInflaterAndroid.inflate(R.layout.dialog_input_name, null)
        val alertDialogBuilderUserInput = AlertDialog.Builder(context)
        alertDialogBuilderUserInput.setView(mView)

        val userInputDialogEditText = mView.findViewById(R.id.userInputDialog) as EditText
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton("Send", DialogInterface.OnClickListener { dialogBox, id ->
                    // ToDo get user input here
                })

                .setNegativeButton("Cancel",
                        DialogInterface.OnClickListener { dialogBox, id -> dialogBox.cancel() })

        return alertDialogBuilderUserInput.create()
    }


}

