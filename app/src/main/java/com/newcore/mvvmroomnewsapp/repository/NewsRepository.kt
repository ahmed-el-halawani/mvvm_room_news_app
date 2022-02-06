package com.newcore.mvvmroomnewsapp.repository

import androidx.lifecycle.LiveData
import com.androiddevs.mvvmnewsapp.api.RetrofitInstance
import com.newcore.mvvmroomnewsapp.localDb.ArticleDao
import com.newcore.mvvmroomnewsapp.localDb.ArticleDatabase
import com.newcore.mvvmroomnewsapp.models.Article
import com.newcore.mvvmroomnewsapp.models.NewsResponse
import retrofit2.Response

class NewsRepository (
    val db: ArticleDatabase
): ArticleDao {

    suspend fun searchForNews(
        q:String,
        page:Int =1,
        from:String="2022-02-03",
        sortBy:String="popularity"
    ) : Response<NewsResponse> {
        return RetrofitInstance.newsApi.searchForNews(q,page,from,sortBy)
    }


    suspend fun getBreakingNews(
        country:String="us",
        page:Int =1
    ) : Response<NewsResponse> {
        return RetrofitInstance.newsApi.getBreakingNews(country,page)
    }

    override suspend fun upsert(article: Article): Long = db.articleDao().upsert(article)

    override fun getAll(): LiveData<List<Article>> = db.articleDao().getAll()

    override suspend fun deleteArticle(article: Article) =  db.articleDao().deleteArticle(article)

}