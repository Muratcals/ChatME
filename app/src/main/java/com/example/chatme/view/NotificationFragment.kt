package com.example.chatme.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatme.R
import com.example.chatme.adapter.FollowRquestRecyclerAdapter
import com.example.chatme.databinding.FragmentNotificationBinding
import com.example.chatme.viewmodel.NotificationViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NotificationFragment : Fragment() {

    @Inject lateinit var viewModel: NotificationViewModel
    private lateinit var binding:FragmentNotificationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentNotificationBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getFollowRequestData()
        viewModel.getAllNotification()
        val adapter =FollowRquestRecyclerAdapter(arrayListOf(),viewModel.database,viewModel.getAuth)
        binding.notificationRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.notificationRecycler.adapter = adapter
        viewModel.notification.observe(viewLifecycleOwner){
            adapter.updateData(it?: arrayListOf())
        }
        viewModel.followRequest.observe(viewLifecycleOwner){
            if (it?.size!! >1){
                binding.followRequestName.setText("${it[0].name} + ${it.size-1} diğer")
            }else if (!it.isEmpty()){
                binding.followRequestName.setText(it[0].name)
            }
        }
        binding.viewRequestButton.setOnClickListener {
            findNavController().navigate(R.id.action_notificationFragment_to_followRequestFragment)
        }
    }
}