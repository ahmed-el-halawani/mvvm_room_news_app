package com.newcore.mvvmroomnewsapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.newcore.mvvmroomnewsapp.NewsApplication
import com.newcore.mvvmroomnewsapp.localDb.ArticleDatabase
import com.newcore.mvvmroomnewsapp.repository.NewsRepository
import com.newcore.mvvmroomnewsapp.R
import com.newcore.mvvmroomnewsapp.databinding.ActivityMainBinding
import com.newcore.mvvmroomnewsapp.utils.INetwork

class MainActivity : AppCompatActivity() , INetwork {
    lateinit var viewModel: NewsViewModel

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val viewModelFactory = NewsViewModel.NewsViewModelFactory(
            this.applicationContext as NewsApplication,
            NewsRepository(ArticleDatabase(this))
        )

        viewModel = ViewModelProvider(this,viewModelFactory).get(NewsViewModel::class.java)

        val navHostFragment =
            supportFragmentManager.findFragmentById(binding.newsNavHostFragment.id) as NavHostFragment

        binding.bottomNavigationView.setupWithNavController(navHostFragment.navController)
    }

    override fun showNoInternet() {
        binding.tvNoInternetConnection.visibility = View.VISIBLE
    }

    override fun hideNoInternet() {
        binding.tvNoInternetConnection.visibility = View.GONE
    }
}
