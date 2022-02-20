package com.gdlgxy.internshipcommunity.module.internship.bean

import com.gdlgxy.internshipcommunity.module.internship.base.BaseFragment
import com.gdlgxy.internshipcommunity.module.internship.constant.TabId
import kotlin.reflect.KClass

data class Tab(
    @TabId
    val id: String,
    val title: String,
    val icon: Int,
    val fragmentClz: KClass<out BaseFragment<*>>
)