package com.gdlgxy.internshipcommunity.module.homeimport com.google.gson.annotations.SerializedNamedata class HomeData(    @SerializedName("id") val id: Int,    @SerializedName("name") val name: String,    @SerializedName("description") val description: String?,    @SerializedName("owner") val owner: Owner,    @SerializedName("forks_count") val startCount: Int)