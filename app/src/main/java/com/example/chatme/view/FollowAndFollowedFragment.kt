package com.example.chatme.view

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.chatme.R

class FollowAndFollowedFragment : Fragment() {

    companion object {
        fun newInstance() = FollowAndFollowedFragment()
    }

    private lateinit var viewModel: FollowAndFollowedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_follow_and_followed, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(FollowAndFollowedViewModel::class.java)
        // TODO: Use the ViewModel
    }

}