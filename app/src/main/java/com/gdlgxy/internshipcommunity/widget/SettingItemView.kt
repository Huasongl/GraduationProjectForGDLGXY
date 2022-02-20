package com.gdlgxy.internshipcommunity.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.gdlgxy.internshipcommunity.R
import com.gdlgxy.internshipcommunity.databinding.ViewSettingItemBinding

class SettingItemView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    val viewBinding = ViewSettingItemBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        val array = context.obtainStyledAttributes(attrs, R.styleable.SettingItemView, defStyleAttr, 0)
        viewBinding.tvTitle.text = array.getString(R.styleable.SettingItemView_setting_item_title)
        viewBinding.ivIcon.setImageDrawable(array.getDrawable(R.styleable.SettingItemView_setting_item_icon))
        array.recycle()
    }
}