package com.feasycom.feasyblue.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.feasycom.feasyblue.App
import com.feasycom.feasyblue.R
import kotlinx.android.synthetic.main.select_mode_info.view.*
import java.util.*

class SelectModeAdapter: RecyclerView.Adapter<SelectModeAdapter.ViewHolder>(){

    private var models = arrayOf("a", "b", "c")

    init {
        val locale: Locale = App.context.resources.configuration.locale
        val language: String = locale.language
        models = if(language.endsWith("zh")){
            App.context.resources.getStringArray(R.array.select_url_zh)
        }else{
            App.context.resources.getStringArray(R.array.select_url_en)
        }
    }

    var mOnClickListener: ((position: Int) -> Unit)? = null

    class ViewHolder(view: View): RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        LayoutInflater.from(parent.context).inflate(R.layout.select_mode_info, parent, false).apply {
            val viewHolder = ViewHolder(this)
            setOnClickListener {
                mOnClickListener?.invoke(viewHolder.adapterPosition)
            }
            return viewHolder
        }
    }

    override fun getItemCount(): Int = models.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val str = models[position]
        holder.itemView.model_name.setText(str)

        if (position == models.size -1){
            holder.itemView.divider9.visibility = View.INVISIBLE
        }
    }
}