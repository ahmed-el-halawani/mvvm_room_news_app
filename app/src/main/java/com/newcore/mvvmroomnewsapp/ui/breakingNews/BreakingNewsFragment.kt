package com.newcore.mvvmroomnewsapp.ui.breakingNews

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.newcore.mvvmroomnewsapp.R
import com.newcore.mvvmroomnewsapp.ui.adapters.NewsAdapter
import com.newcore.mvvmroomnewsapp.databinding.FragmentBreakingNewsBinding
import com.newcore.mvvmroomnewsapp.ui.BaseFragment
import com.newcore.mvvmroomnewsapp.utils.*
import com.newcore.mvvmroomnewsapp.utils.Constants.ARTICLE
import com.newcore.mvvmroomnewsapp.utils.Constants.BREAKING_ERROR_TAG
import com.newcore.mvvmroomnewsapp.utils.Constants.MAX_RESULT_FOR_FREE_API
import com.newcore.mvvmroomnewsapp.utils.Constants.No_INTERNET_CONNECTION
import com.newcore.mvvmroomnewsapp.utils.Constants.TOTAL_NUMBER_OF_ITEMS_PER_REQUEST

class BreakingNewsFragment
    : BaseFragment<FragmentBreakingNewsBinding>( FragmentBreakingNewsBinding::inflate ) , ILoading {

    private val pagination by lazy {
        ViewHelpers.Pagination(newsViewModel::getBreakingNews,binding.rvBreakingNews)
    }

    private val newsAdapter by lazy{
        NewsAdapter().apply {
            setOnItemClickListener {
                val bundle = Bundle().apply {
                    putSerializable(ARTICLE,it)
                }
                findNavController().navigate(
                    R.id.action_breakingNewsFragment_to_articleFragment,
                    bundle
                )
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecycleView()

        newsViewModel.breakingNewsLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> showLoading()
                is Resource.Success -> {

                    hideLoading()
                    networkState.hideNoInternet()
                    it.data?.let { newsResponse->
                        newsAdapter.differ.submitList(newsResponse.articles.toList())
                        val totalNumberOfAvailablePages =
                            (if(newsResponse.totalResults>100) MAX_RESULT_FOR_FREE_API else newsResponse.totalResults)/
                                    TOTAL_NUMBER_OF_ITEMS_PER_REQUEST+2 // 2 is 1 for rounded of integer value and 1 for last page because it always empty
                        pagination.isLastPage = newsViewModel.breakingNewsPage >= totalNumberOfAvailablePages
                        if (pagination.isLastPage){
                            binding.rvBreakingNews.setPadding(0,0,0,0)
                        }
                    }
                }
                is Resource.Error -> {
                    hideLoading()

                    when (it.message){
                        No_INTERNET_CONNECTION-> networkState.showNoInternet()
                        else -> Log.e(BREAKING_ERROR_TAG, it.message?:"" )
                    }

                }
            }
        }
    }

    private fun setupRecycleView(){
        binding.rvBreakingNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    override fun showLoading(){
        binding.paginationProgressBar.visibility = View.VISIBLE
        pagination.isLoading = true
    }

    override fun hideLoading(){
        binding.paginationProgressBar.visibility = View.INVISIBLE
        pagination.isLoading = false
    }


}