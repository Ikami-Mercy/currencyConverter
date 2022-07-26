package com.ikami.simplepay.util

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.WindowManager
import com.ikami.simplepay.R


class ProgressDialog {
    private lateinit var dialog: Dialog
    fun show(context: Context): Dialog? {
        val inflator = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflator.inflate(R.layout.progress_dialog, null,)
        dialog = Dialog(context, R.style.LoadingDialog)
        dialog.setContentView(view)
        dialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false)
        dialog.show()

        return dialog
    }

    fun dismiss() {
        try {
            dialog.dismiss()
        } catch (e: UninitializedPropertyAccessException) {
            e.printStackTrace()
        }

    }

}