package com.newcore.mvvmroomnewsapp.utils

import android.widget.AbsListView
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

object ViewHelpers {

    class Pagination(val request: () -> Unit, private val rv: RecyclerView) :
        RecyclerView.OnScrollListener() {
        private var isScrolling = false
        var isLoading = false
        var isLastPage = false
            set(value) {
                if (value)
                    rv.setPadding(0, 0, 0, 0)
                field = value
            }

        init {
            rv.addOnScrollListener(this)
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstItem = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount


            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtBeginning = firstItem >= 0
            val isAtLast = firstItem + visibleItemCount >= totalItemCount
            val isTotalMoreThanVisible =
                totalItemCount >= Constants.TOTAL_NUMBER_OF_ITEMS_PER_REQUEST //total items get from api
            val shouldPaginate = isNotLoadingAndNotLastPage &&
                    isAtBeginning &&
                    isAtLast &&
                    isTotalMoreThanVisible &&
                    isScrolling

            if (shouldPaginate) {
                request()
                println("new request from search or breaking news")
                isScrolling = false
            }
        }
    }

    class SearchPagination(
        val request: (String) -> Unit,
        private val rv: RecyclerView,
        private val et: EditText
    ) : RecyclerView.OnScrollListener() {
        private var isScrolling = false
        var isLoading = false
        var isLastPage = false
            set(value) {
                if (value)
                    rv.setPadding(0, 0, 0, 0)
                field = value
            }


        init {
            rv.addOnScrollListener(this)
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstItem = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount


            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtBeginning = firstItem >= 0
            val isAtLast = firstItem + visibleItemCount >= totalItemCount
            val isTotalMoreThanVisible =
                totalItemCount >= Constants.TOTAL_NUMBER_OF_ITEMS_PER_REQUEST //total items get from api
            val shouldPaginate = isNotLoadingAndNotLastPage &&
                    isAtBeginning &&
                    isAtLast &&
                    isTotalMoreThanVisible &&
                    isScrolling

            if (shouldPaginate) {
                request(et.text.toString())
                println("new request from search or breaking news")
                isScrolling = false
            }
        }
    }
}