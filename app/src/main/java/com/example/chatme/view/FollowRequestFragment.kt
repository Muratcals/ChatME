package com.example.chatme.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatme.R
import com.example.chatme.adapter.FollowRquestRecyclerAdapter
import com.example.chatme.databinding.FragmentFollowRequestBinding
import com.example.chatme.viewmodel.NotificationViewModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FollowRequestFragment : Fragment() {

    private lateinit var binding: FragmentFollowRequestBinding
    @Inject
    lateinit var viewModel: NotificationViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFollowRequestBinding.inflate(layoutInflater)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.followRequestToollbar.profilDetailseToolbarTitle.setText("Takip istekleri")
        viewModel.getFollowRequestData()
        binding.followRequestToollbar.profilDetailseToolbarSuccess.visibility=View.GONE
        viewModel.progress.observe(viewLifecycleOwner){
            if (it){
                binding.followRequestProgress.visibility=View.VISIBLE
                binding.followRequestMainLayout.visibility=View.GONE
            }else{
                binding.followRequestProgress.visibility=View.GONE
                binding.followRequestMainLayout.visibility=View.VISIBLE
            }
        }
        binding.followRequestToollbar.profilDetailsToolbarBackPage.setOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.followRequestToollbar.profilDetailseToolbarSuccess.setOnClickListener {
            requireActivity().onBackPressed()
        }

        viewModel.followRequest.observe(viewLifecycleOwner) {
            if (viewModel.followRequest.value?.isEmpty() == true) {
                binding.followRequestRecycler.visibility = View.GONE
                binding.notRequestLayout.visibility = View.VISIBLE
            } else {
                val adapter = FollowRquestRecyclerAdapter(it!!, viewModel.database, viewModel.getAuth)
                binding.followRequestRecycler.layoutManager = LinearLayoutManager(requireContext())
                binding.followRequestRecycler.adapter = adapter
                binding.followRequestRecycler.visibility = View.VISIBLE
                binding.notRequestLayout.visibility = View.GONE
            }
        }
    }
}