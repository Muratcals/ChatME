package com.example.chatme.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.chatme.R
import com.example.chatme.adapter.PostRecyclerView
import com.example.chatme.databinding.FragmentProffilBinding
import com.example.chatme.model.UserInformationModel
import com.example.chatme.model.followedModel
import com.example.chatme.util.utils.downloadUrl
import com.example.chatme.util.utils.placeHolder
import com.example.chatme.viewmodel.ProffilViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.type.DateTime
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime
import javax.inject.Inject

@AndroidEntryPoint
class ProffilFragment : Fragment() {

    @Inject
    lateinit var viewModel: ProffilViewModel
    @Inject
    lateinit var auth: FirebaseAuth
    @Inject
    lateinit var database: FirebaseFirestore
    private lateinit var binding: FragmentProffilBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProffilBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observerItem()
        viewModel.authProfilInformation()
        viewModel.getPost()
        val adapter =PostRecyclerView(arrayListOf())
        binding.userImagesRecycler.adapter=adapter
        binding.userImagesRecycler.layoutManager=GridLayoutManager(requireContext(),3)
        viewModel.postList.observe(viewLifecycleOwner){
            adapter.updateData(it)
        }
        binding.profilEditButton.setOnClickListener {
            findNavController().navigate(R.id.action_proffilFragment_to_profilEditFragment)
        }
        binding.followLayout.setOnClickListener {
            val bundle = bundleOf("authName" to viewModel.authInformation.value!!.authName,"incoming" to "follow")
            findNavController().navigate(R.id.action_proffilFragment_to_followAndFollowedFragment,bundle)
        }
        binding.followedLayout.setOnClickListener {
            val bundle = bundleOf("authName" to viewModel.authInformation.value!!.authName,"incoming" to "followed")
            findNavController().navigate(R.id.action_proffilFragment_to_followAndFollowedFragment,bundle)
        }
    }

    fun observerItem(){
        viewModel.authInformation.observe(viewLifecycleOwner) {
            if (it != null) {
                updateUI(it)
            }
        }
        viewModel.follow.observe(viewLifecycleOwner){
            binding.followCount.setText(it.size.toString())
        }
        viewModel.followed.observe(viewLifecycleOwner){
            binding.followedCount.setText(it.size.toString())
        }
        viewModel.progress.observe(viewLifecycleOwner) {
            if (it) {
                binding.profilProgressBar.visibility = View.VISIBLE
                binding.profilLayout.visibility = View.GONE
            } else {
                binding.profilProgressBar.visibility = View.GONE
                binding.profilLayout.visibility = View.VISIBLE
            }
        }

    }

    fun updateUI(user: UserInformationModel) {
        binding.profilUserName.setText(user.name)
        binding.profilToolbar.toolbarUserAuthName.setText(user.authName)
        binding.profilBiography.setText(user.biography)
        binding.profilProgressBar.visibility = View.GONE
        binding.profilLayout.visibility = View.VISIBLE
        if (user.profilImage.isNotEmpty()){
            binding.profilImage.downloadUrl(user.profilImage, placeHolder(requireContext()))
        }
    }
}