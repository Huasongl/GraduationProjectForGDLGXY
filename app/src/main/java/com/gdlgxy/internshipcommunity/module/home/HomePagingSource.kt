package com.gdlgxy.internshipcommunity.module.homeimport androidx.paging.PagingSourceimport androidx.paging.PagingStateimport java.lang.Exceptionclass HomePagingSource(private val gitHubService: Service,private var int: Int) : PagingSource<Int, HomeData>() {    override fun getRefreshKey(state: PagingState<Int, HomeData>): Int? = null    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, HomeData> {        return try {            val page = params.key ?: 1            val pageSize = params.loadSize            val repoResponse = when (int) {                HomeFragment.ANDROID_TYPE -> {                    gitHubService.searchReposAndroid(page, pageSize)                }                HomeFragment.C -> {                    gitHubService.searchReposC(page, pageSize)                }                HomeFragment.JAVA_TYPE -> {                    gitHubService.searchReposJava(page, pageSize)                }                else -> {                    gitHubService.searchReposKotlin(page, pageSize)                }            }            val repoItems = repoResponse.item            val prevKey = if (page > 1) page - 1 else null            val nextKey = if (repoItems.isNotEmpty()) page + 1 else null            LoadResult.Page(repoItems, prevKey, nextKey)        } catch (e: Exception) {            LoadResult.Error(e)        }    }}