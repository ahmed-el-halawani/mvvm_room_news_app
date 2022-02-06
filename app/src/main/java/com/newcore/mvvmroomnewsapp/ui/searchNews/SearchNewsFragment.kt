package com.newcore.mvvmroomnewsapp.ui.searchNews

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.newcore.mvvmroomnewsapp.R
import com.newcore.mvvmroomnewsapp.databinding.FragmentSearchNewsBinding
import com.newcore.mvvmroomnewsapp.ui.BaseFragment
import com.newcore.mvvmroomnewsapp.ui.adapters.NewsAdapter
import com.newcore.mvvmroomnewsapp.utils.*
import com.newcore.mvvmroomnewsapp.utils.Constants.ARTICLE
import com.newcore.mvvmroomnewsapp.utils.Constants.SEARCH_ERROR_TAG
import com.newcore.mvvmroomnewsapp.utils.Constants.SEARCH_TIME_DELAY
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchNewsFragment
    :BaseFragment<FragmentSearchNewsBinding>( FragmentSearchNewsBinding::inflate ) , ILoading {

    val pagination by lazy {
        ViewHelpers.SearchPagination(newsViewModel::searchForNews, binding.rvSearchNews,binding.etSearch)
    }

    private val newsAdapter by lazy{
        NewsAdapter().apply {
            setOnItemClickListener {
                val bundle = Bundle().apply {
                    putSerializable(ARTICLE,it)
                }
                findNavController().navigate(
                    R.id.action_searchNewsFragment_to_articleFragment,
                    bundle
                )
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecycleView()

        var job:Job? = null
        binding.etSearch.addTextChangedListener {editable->
            job?.cancel()
            job = MainScope().launch {
                delay(SEARCH_TIME_DELAY)
                editable?.let {
                    if(it.toString().isNotEmpty()){
                        newsViewModel.searchForNews(it.toString())
                    }
                }
            }
        }

        newsViewModel.searchNewsLiveData.observe(viewLifecycleOwner) { resource ->
            when (resource){
                is Resource.Loading ->showLoading()
                is Resource.Success -> {
                    hideLoading()
                    networkState.hideNoInternet()
                    resource.data?.let {newsResponse->
                        newsAdapter.differ.submitList(newsResponse.articles)
                        val totalNumberOfAvailablePages = newsResponse.totalResults/ Constants.TOTAL_NUMBER_OF_ITEMS_PER_REQUEST +2// 2 is 1 for rounded of integer value and 1 for last page because it always empty
                        pagination.isLastPage = newsViewModel.searchNewsPage >= totalNumberOfAvailablePages
                    }
                }
                is Resource.Error -> {
                    hideLoading()

                    when (resource.message){
                        Constants.No_INTERNET_CONNECTION -> networkState.showNoInternet()
                        else -> Log.e(Constants.BREAKING_ERROR_TAG, resource.message?:"" )
                    }

                }
            }
        }



    }

    private fun setupRecycleView(){
        binding.rvSearchNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    override fun showLoading() {
        binding.paginationProgressBar.visibility=  View.VISIBLE
        pagination.isLoading = true
    }

    override fun hideLoading() {
        binding.paginationProgressBar.visibility=  View.INVISIBLE
        pagination.isLoading = false
    }




}