package com.example.chatme.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatme.R
import com.example.chatme.adapter.FollowAndFollowedAdapter
import com.example.chatme.databinding.FragmentFollowAndFollowedBinding
import com.example.chatme.viewmodel.FollowAndFollowedViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FollowAndFollowedFragment : Fragment() {

    @Inject lateinit var viewModel: FollowAndFollowedViewModel
    private lateinit var binding:FragmentFollowAndFollowedBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentFollowAndFollowedBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            val incoming =it.getString("incoming")
            val authName =it.getString("authName")
            if (incoming.equals("follow")){
                viewModel.getUserFollow(authName!!)
            }else{
                viewModel.getUserFollowed(authName!!)
            }
            val adapter =FollowAndFollowedAdapter(arrayListOf(),incoming!!,viewModel,authName)
            binding.followAndFollowRecycler.adapter=adapter
            binding.followAndFollowRecycler.layoutManager=LinearLayoutManager(requireContext())
            binding.followAndFolloToolbar.profilDetailseToolbarSuccess.visibility=View.GONE
            binding.followAndFolloToolbar.profilDetailseToolbarTitle.setText(authName)
            binding.followAndFolloToolbar.profilDetailsToolbarBackPage.setOnClickListener {
                requireActivity().onBackPressed()
            }
            viewModel.progress.observe(viewLifecycleOwner){
                if (it){
                    binding.followAndFollowedLayoutProgress.visibility=View.VISIBLE
                    binding.followAndFollowRecycler.visibility=View.GONE
                }else{
                    binding.followAndFollowRecycler.visibility=View.VISIBLE
                    binding.followAndFollowedLayoutProgress.visibility=View.GONE
                }
            }
            viewModel.followItem.observe(viewLifecycleOwner){
                adapter.updateData(it)
            }
            viewModel.followedItem.observe(viewLifecycleOwner){
                adapter.updateData(it)

            }
        }
    }
}