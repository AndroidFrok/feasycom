package com.feasycom.feasyblue.dialogFragment

import android.view.Gravity
import androidx.recyclerview.widget.LinearLayoutManager
import com.feasycom.feasyblue.R
import com.feasycom.feasyblue.adapter.SelectModeAdapter
import kotlinx.android.synthetic.main.select_mode_dialog.*

class SelectFlieDialogFragment: BaseDialogFragment() {

    var mCallback: ((position: Int) -> Unit)? = null
    override val layout: Int
        get() = R.layout.select_mode_dialog

    override fun initView() {
        recycler.apply {
            layoutManager = LinearLayoutManager(context)
            SelectModeAdapter().apply {
                mOnClickListener = {
                    mCallback?.invoke(it)
                    dialog?.dismiss()
                }
                adapter = this
            }
        }
        close.setOnClickListener {
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.run {
            // 下面这些设置必须在此方法(onStart())中才有效
            // 如果不设置这句代码, 那么弹框就会与四边都有一定的距离
            setBackgroundDrawableResource(android.R.color.transparent)
            setWindowAnimations(R.style.bottomDialog)
            val params = attributes!!
            params.gravity = Gravity.BOTTOM
            // 如果不设置宽度,那么即使你在布局中设置宽度为 match_parent 也不会起作用
            params.width = resources.displayMetrics.widthPixels
            attributes = params
        }
    }


}