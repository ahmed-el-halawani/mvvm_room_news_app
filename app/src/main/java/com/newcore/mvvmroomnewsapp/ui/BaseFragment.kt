package com.newcore.mvvmroomnewsapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.newcore.mvvmroomnewsapp.utils.INetwork

abstract class BaseFragment<T : ViewBinding>(val viewBindingInflater:(LayoutInflater)->T): Fragment() {
    val  newsViewModel: NewsViewModel by lazy {
        (activity as MainActivity).viewModel
    }

    val networkState: INetwork by lazy{
        activity as INetwork
    }

    val binding by lazy {
        viewBindingInflater(layoutInflater)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            return binding.root
    }

}