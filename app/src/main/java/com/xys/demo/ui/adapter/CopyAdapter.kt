package com.xys.demo.ui.adapter

import android.content.Context
import android.view.ViewGroup
import com.xys.demo.app.AppAdapter
import com.xys.demo.R

/**
 *    desc   : 可进行拷贝的副本
 */
class CopyAdapter constructor(context: Context) : AppAdapter<String?>(context) {

    override fun getItemCount(): Int {
        return 10
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder()
    }

    inner class ViewHolder : AppViewHolder(R.layout.copy_item) {
        override fun onBindView(position: Int) {}
    }
}