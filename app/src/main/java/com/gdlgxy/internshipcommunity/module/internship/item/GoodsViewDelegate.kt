package com.gdlgxy.internshipcommunity.module.internship.item

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gdlgxy.internshipcommunity.databinding.ItemGoodsBinding
import com.gdlgxy.internshipcommunity.module.internship.base.list.base.BaseItemViewDelegate

class GoodsViewDelegate : BaseItemViewDelegate<GoodsViewData, GoodsViewDelegate.ViewHolder>() {

    override fun onCreateViewHolder(inflater: LayoutInflater, context: Context, parent: ViewGroup): ViewHolder {
        return ViewHolder(ItemGoodsBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, item: GoodsViewData) {
        super.onBindViewHolder(holder, item)
    }

    class ViewHolder(val viewBinding: ItemGoodsBinding) : RecyclerView.ViewHolder(viewBinding.root) {

    }
}