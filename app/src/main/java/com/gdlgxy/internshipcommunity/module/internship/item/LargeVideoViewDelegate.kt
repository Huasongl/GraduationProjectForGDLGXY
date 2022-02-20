package com.gdlgxy.internshipcommunity.module.internship.item

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gdlgxy.internshipcommunity.databinding.ItemLargeVideoBinding
import com.gdlgxy.internshipcommunity.module.internship.base.list.base.BaseItemViewDelegate

class LargeVideoViewDelegate : BaseItemViewDelegate<LargeVideoViewData, LargeVideoViewDelegate.ViewHolder>() {

    override fun onCreateViewHolder(inflater: LayoutInflater, context: Context, parent: ViewGroup): ViewHolder {
        return ViewHolder(ItemLargeVideoBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, item: LargeVideoViewData) {
        super.onBindViewHolder(holder, item)
    }

    class ViewHolder(val viewBinding: ItemLargeVideoBinding) : RecyclerView.ViewHolder(viewBinding.root) {

    }
}