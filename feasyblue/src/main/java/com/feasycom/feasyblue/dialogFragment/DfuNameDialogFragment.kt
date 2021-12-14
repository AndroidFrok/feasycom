package com.feasycom.feasyblue.dialogFragment

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.DialogFragment
import com.feasycom.feasyblue.R
import kotlinx.android.synthetic.main.dfu_name_fragment.*


class DfuNameDialogFragment: DialogFragment() {

    var onClickComplete: ((name: String)-> Unit)? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        /**
         * remove the system dialog title and border background
         */
        dialog?.window!!.setBackgroundDrawable(ColorDrawable(0))
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return inflater.inflate(R.layout.dfu_name_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.setCancelable(false)
        dialog?.setCanceledOnTouchOutside(false)
        ok.setOnClickListener {
            onClickComplete?.invoke(dfu_name.text.toString())
            dialog?.dismiss()
        }
        close.setOnClickListener {
            dialog?.dismiss()
        }
    }

    override fun onResume() {
        val params: ViewGroup.LayoutParams = dialog?.window!!.attributes
        /**
         * remove the system dialog title and border background
         */
        params.width = WindowManager.LayoutParams.MATCH_PARENT
        Log.e("TAG", "onResume: " + WindowManager.LayoutParams.MATCH_PARENT )
        Log.e("TAG", "onResume: " + (WindowManager.LayoutParams.MATCH_PARENT - 40))
        params.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog!!.window!!.attributes = params as WindowManager.LayoutParams

        super.onResume()

    }
}