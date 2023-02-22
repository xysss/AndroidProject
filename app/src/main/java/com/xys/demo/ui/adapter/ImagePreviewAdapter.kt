package com.xys.demo.ui.adapter

import android.content.Context
import android.view.ViewGroup
import com.github.chrisbanes.photoview.PhotoView
import com.xys.demo.app.AppAdapter
import com.xys.demo.http.glide.GlideApp
import com.xys.demo.R

/**
 *    desc   : 图片预览适配器
 */
class ImagePreviewAdapter constructor(context: Context) : AppAdapter<String?>(context) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder()
    }

    inner class ViewHolder : AppViewHolder(R.layout.image_preview_item) {

        private val photoView: PhotoView by lazy { getItemView() as PhotoView }

        override fun onBindView(position: Int) {
            GlideApp.with(getContext())
                .load(getItem(position))
                .into(photoView)
        }
    }
}