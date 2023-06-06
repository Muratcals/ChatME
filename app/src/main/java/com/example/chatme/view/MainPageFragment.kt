package com.example.chatme.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.chatme.databinding.FragmentMainPageBinding
import com.example.chatme.viewmodel.MainPageViewModel

class MainPageFragment : Fragment() {

    lateinit var viewModel: MainPageViewModel
    private lateinit var binding:FragmentMainPageBinding
    val  PICK_IMAGE_REQUEST_CODE=100


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentMainPageBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }



}