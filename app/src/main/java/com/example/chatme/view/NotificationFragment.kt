package com.example.chatme.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.chatme.R
import com.example.chatme.databinding.FragmentNotificationBinding
import com.example.chatme.viewmodel.NotificationViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NotificationFragment : Fragment() {

    @Inject lateinit var viewModel: NotificationViewModel
    private lateinit var binnding:FragmentNotificationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binnding= FragmentNotificationBinding.inflate(layoutInflater)
        return binnding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getFollowRequestData()
        val adapter =
        viewModel.followRequest.observe(viewLifecycleOwner){
            if (it?.size!! >1){
                binnding.followRequestName.setText("${it[0].name} + ${it.size-1} diğer")
            }else if (!it.isEmpty()){
                binnding.followRequestName.setText(it[0].name)
            }
        }
        binnding.viewRequestButton.setOnClickListener {
            findNavController().navigate(R.id.action_notificationFragment_to_followRequestFragment)
        }
    }
}