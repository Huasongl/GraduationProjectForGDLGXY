package com.gdlgxy.internshipcommunity.module.internship.base

import com.gdlgxy.internshipcommunity.module.internship.constant.PageName

/**
 * 获取页面名称通用接口
 */
interface IGetPageName {

    @PageName
    fun getPageName(): String

}