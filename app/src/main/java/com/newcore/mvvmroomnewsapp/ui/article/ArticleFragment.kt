package com.newcore.mvvmroomnewsapp.ui.article

import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import com.newcore.mvvmroomnewsapp.databinding.FragmentArticleBinding
import com.newcore.mvvmroomnewsapp.ui.BaseFragment
import com.newcore.mvvmroomnewsapp.utils.Constants
import com.newcore.mvvmroomnewsapp.utils.INetwork
import com.newcore.mvvmroomnewsapp.utils.Resource

class ArticleFragment
    : BaseFragment<FragmentArticleBinding>( FragmentArticleBinding::inflate ) {

    private val articleArgs:ArticleFragmentArgs by navArgs()

    val webView by lazy{
        WebViewClient()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        newsViewModel.loadArticle(articleArgs.article)

        binding.webView.apply {
            webViewClient = webView

        }



        newsViewModel.loadArticleLiveData.observe(viewLifecycleOwner) { resource ->
            when(resource){
                is Resource.Loading -> {}
                is Resource.Success -> {
                    networkState.hideNoInternet()
                    binding.webView.apply {
                        resource.data?.let {
                            loadUrl(it.url)
                        }
                    }
                }
                is Resource.Error -> {
                    when (resource.message){
                        Constants.No_INTERNET_CONNECTION -> networkState.showNoInternet()
                        else -> Log.e(Constants.BREAKING_ERROR_TAG, resource.message?:"" )
                    }

                }
            }
        }



        binding.fab.setOnClickListener {
            newsViewModel.saveArticle(articleArgs.article)
            Toast.makeText(context, "saved successfully", Toast.LENGTH_SHORT).show()
        }
    }

}