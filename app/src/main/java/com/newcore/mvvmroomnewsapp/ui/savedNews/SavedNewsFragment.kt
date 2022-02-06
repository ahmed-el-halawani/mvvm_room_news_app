package com.newcore.mvvmroomnewsapp.ui.savedNews

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.newcore.mvvmroomnewsapp.R
import com.newcore.mvvmroomnewsapp.ui.BaseFragment
import com.newcore.mvvmroomnewsapp.databinding.FragmentSavedNewsBinding
import com.newcore.mvvmroomnewsapp.ui.adapters.NewsAdapter
import com.newcore.mvvmroomnewsapp.utils.Constants.ARTICLE

class SavedNewsFragment
    : BaseFragment<FragmentSavedNewsBinding>( FragmentSavedNewsBinding::inflate ) {

    private val newsAdapter by lazy{
        NewsAdapter().apply {
            setOnItemClickListener {
                val bundle = Bundle().apply {
                    putSerializable(ARTICLE,it)
                }
                findNavController().navigate(
                    R.id.action_savedNewsFragment_to_articleFragment,
                    bundle
                )
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecycleView()



        val itemTouchHelper = object:ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = true

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val article  = newsAdapter.differ.currentList[position]
                newsViewModel.deleteArticle(article)
                Snackbar.make(view,"article deleted successfully",Snackbar.LENGTH_LONG).apply {
                    setAction("UNDO"){
                        newsViewModel.saveArticle(article)
                    }
                    show()
                }
            }

        }

        ItemTouchHelper(itemTouchHelper).attachToRecyclerView(binding.rvSavedNews)

        newsViewModel.getSavedNews().observe(viewLifecycleOwner){
            newsAdapter.differ.submitList(it)
        }
    }

    private fun setupRecycleView(){
        binding.rvSavedNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }
}