package com.newcore.mvvmroomnewsapp.ui

import androidx.lifecycle.*
import com.newcore.mvvmroomnewsapp.NewsApplication
import com.newcore.mvvmroomnewsapp.models.Article
import com.newcore.mvvmroomnewsapp.models.NewsResponse
import com.newcore.mvvmroomnewsapp.repository.NewsRepository
import com.newcore.mvvmroomnewsapp.services.ReCallService
import com.newcore.mvvmroomnewsapp.utils.Constants.No_INTERNET_CONNECTION
import com.newcore.mvvmroomnewsapp.utils.NetworkingHelper
import com.newcore.mvvmroomnewsapp.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class NewsViewModel(
    private val application:NewsApplication,
    private val repository: NewsRepository
) : AndroidViewModel(application) {


    val breakingNewsLiveData: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var breakingNewsPage = 1
    private var breakingNewsResponse:NewsResponse? = null
    private var lastCountry = "us"


    val searchNewsLiveData: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchNewsPage: Int = 1
    private var searchNewsResponse:NewsResponse? = null
    private var lastQuery = ""

    val loadArticleLiveData: MutableLiveData<Resource<Article>> = MutableLiveData()


    init {
        getBreakingNews(lastCountry)
    }

    fun loadArticle(article: Article){
        if(NetworkingHelper.hasInternet(application))
            loadArticleLiveData.postValue(Resource.Success(article))
        else{
            ReCallService.recall("loadArticle",{loadArticle(article)},application)
            loadArticleLiveData.postValue(Resource.Error(No_INTERNET_CONNECTION))
        }
    }

    fun getBreakingNews(country:String=lastCountry,page:Int=breakingNewsPage) = viewModelScope.launch {
        safeBreakingCall(country,page)
    }

    private suspend fun safeBreakingCall(country:String=lastCountry, page:Int=breakingNewsPage){
        lastCountry = country
        breakingNewsLiveData.postValue(Resource.Loading())

        try {
            if(NetworkingHelper.hasInternet(application)){
                val response = repository.getBreakingNews(country,page)
                breakingNewsLiveData.postValue(handleBreakingNewsRemoteResponse(response))
            }else{

                ReCallService.recall(
                    "safeBreakingCall",
                    {
                        safeBreakingCall(country,page)
                    },
                    application
                )

                breakingNewsLiveData.postValue(Resource.Error(No_INTERNET_CONNECTION))
            }
        }catch (t:Throwable){
            when(t){
                is IOException->breakingNewsLiveData.postValue(Resource.Error("Server error"))
                else ->breakingNewsLiveData.postValue(Resource.Error("conversion error"))
            }
        }
    }

    fun searchForNews(query:String,page:Int=searchNewsPage) = viewModelScope.launch {
        safeSearchCall(query,page)
    }

    private suspend fun safeSearchCall(query:String,page:Int=searchNewsPage){
        searchNewsLiveData.postValue(Resource.Loading())

        try {
            if(NetworkingHelper.hasInternet(application)){
                if(query != lastQuery) {
                    searchNewsPage = if(page!=searchNewsPage) page else 1
                    searchNewsResponse = null
                }

                val response = repository.searchForNews(query,searchNewsPage)
                lastQuery = query
                searchNewsLiveData.postValue(handleSearchRemoteResponse(response))
            }else{
                ReCallService.recall(
                    "safeSearchCall",
                    {
                        safeSearchCall(query,page)
                    },
                    application
                )
                searchNewsLiveData.postValue(Resource.Error(No_INTERNET_CONNECTION))
            }
        }catch (t:Throwable){
            when(t){
                is IOException->searchNewsLiveData.postValue(Resource.Error("Server error"))
                else ->searchNewsLiveData.postValue(Resource.Error("conversion error"))
            }
        }
    }

    private fun handleSearchRemoteResponse(response:Response<NewsResponse>):Resource<NewsResponse>{
        if(response.isSuccessful){
            searchNewsPage++
            response.body()?.let { newResponse->
                if (searchNewsResponse==null)
                    searchNewsResponse = newResponse
                else{
                    val oldResponseArticles= searchNewsResponse?.articles
                    oldResponseArticles?.addAll(newResponse.articles)
                }
            }
            return Resource.Success(searchNewsResponse?:NewsResponse.emptyResponse())
        }
        return Resource.Error(response.message())
    }

    private fun handleBreakingNewsRemoteResponse(response:Response<NewsResponse>):Resource<NewsResponse>{
        if(response.isSuccessful){
            breakingNewsPage++
            response.body()?.let { newResponse->
                if (breakingNewsResponse==null)
                    breakingNewsResponse = newResponse
                else{
                    val oldResponseArticles= breakingNewsResponse?.articles
                    oldResponseArticles?.addAll(newResponse.articles)
                }
            }
            return Resource.Success(breakingNewsResponse?:NewsResponse.emptyResponse())
        }
        return Resource.Error(response.message())
    }





    fun saveArticle(article:Article) =  viewModelScope.launch {
        repository.upsert(article)
    }

    fun getSavedNews() = repository.getAll()

    fun deleteArticle(article: Article) = viewModelScope.launch {
        repository.deleteArticle(article)
    }

    class NewsViewModelFactory(private val app:NewsApplication,private val repository: NewsRepository) : ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return NewsViewModel(app,repository) as T
        }
    }
}

