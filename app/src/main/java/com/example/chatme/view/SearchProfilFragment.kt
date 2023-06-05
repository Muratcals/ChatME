package com.example.chatme.view

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.chatme.R
import com.example.chatme.databinding.FragmentSearchProfilBinding
import com.example.chatme.model.UserInformationModel
import com.example.chatme.model.followedModel
import com.example.chatme.util.utils
import com.example.chatme.util.utils.downloadUrl
import com.example.chatme.viewmodel.ProffilViewModel
import com.example.chatme.viewmodel.SearchProfilViewModel
import com.google.firebase.Timestamp
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SearchProfilFragment : Fragment() {

    @Inject lateinit var viewModel:SearchProfilViewModel
    private lateinit var binding:FragmentSearchProfilBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentSearchProfilBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            val incoming=it.getString("incoming")
            val mail=it.getString("mail")
            val authName=it.getString("authName")
            observerItem()
            viewModel.authFollowController(binding, mail!!,authName!!)
            binding.follow.setOnClickListener {
                val users =viewModel.authInformation.value
                val currentUser=viewModel.currentUserInformation.value
                if (binding.follow.text.equals("Onayla")){
                    viewModel.reFollow(binding,users!!)
                    binding.follow.setBackgroundResource(R.drawable.button_backgorund_gray_shape)
                    binding.follow.setText("Takip")
                }else if (binding.follow.text.equals("Takip Et") || binding.follow.text.equals("Sende onu takip et")){
                    viewModel.authFollow(requireContext(),users!!,currentUser!!)
                    binding.follow.setBackgroundResource(R.drawable.button_backgorund_gray_shape)
                    binding.follow.setText("Bekleniyor")
                }else if (binding.follow.text.equals("Takip")){
                    val builder=AlertDialog.Builder(requireContext())
                    builder.setTitle("Takipten çık")
                    builder.setMessage("Takipten çıkmak istediğinize emin misiniz")
                    builder.setPositiveButton("Çık") { dialog, which ->
                        val user = followedModel(
                            users!!.customerId, users.authName, users.authName, users.profilImage,
                            Timestamp.now()
                        )
                        viewModel.authDeleteFollow(binding,user,currentUser!!)
                        binding.follow.setBackgroundResource(R.drawable.button_background_shape)
                        binding.follow.setText("Takip Et")
                    }
                    builder.setNegativeButton("İptal") { dialog, which ->
                        dialog.cancel()
                    }
                    builder.show()
                }else if (binding.follow.text.equals("Bekleniyor")){
                    val users =viewModel.authInformation.value
                    viewModel.requestDeleteFollow(users!!,currentUser!!)
                    binding.follow.setBackgroundResource(R.drawable.button_background_shape)
                    binding.follow.setText("Takip Et")
                }
            }
            binding.searchFollowCount.setOnClickListener {
                val bundle = bundleOf("incoming" to "follow","authName" to authName)
                if (incoming.equals("search")){
                    findNavController().navigate(R.id.action_searchProfilFragment_to_followAndFollowedFragment2,bundle)
                }else{
                    findNavController().navigate(R.id.action_searchProfilFragment2_to_followAndFollowedFragment,bundle)
                }
            }
            binding.searchFollowedCount.setOnClickListener {
                val bundle = bundleOf("incoming" to "followed","authName" to authName)
                if (incoming.equals("search")){
                    findNavController().navigate(R.id.action_searchProfilFragment_to_followAndFollowedFragment2,bundle)
                }else{
                    findNavController().navigate(R.id.action_searchProfilFragment2_to_followAndFollowedFragment,bundle)
                }
            }
        }
    }
    fun observerItem(){
        viewModel.authInformation.observe(viewLifecycleOwner) {
            if (it != null) {
                updateUI(it)
            }
        }
        viewModel.follow.observe(viewLifecycleOwner){
            binding.searchFollowCount.setText(it.size.toString())
        }
        viewModel.followed.observe(viewLifecycleOwner){
            binding.searchFollowedCount.setText(it.size.toString())
        }
        viewModel.progress.observe(viewLifecycleOwner) {
            if (it) {
                binding.searchProgressBar.visibility = View.VISIBLE
                binding.searchLayout.visibility = View.GONE
            } else {
                binding.searchProgressBar.visibility = View.GONE
                binding.searchLayout.visibility = View.VISIBLE
            }
        }
    }

    fun updateUI(user: UserInformationModel) {
        binding.searchUserName.setText(user.name)
        binding.searchToolbar.toolbarUserAuthName.setText(user.authName)
        binding.searchBiography.setText(user.biography)
        binding.searchProgressBar.visibility = View.GONE
        binding.searchLayout.visibility = View.VISIBLE
        if (user.profilImage.isNotEmpty()){
            binding.searchImage.downloadUrl(user.profilImage, utils.placeHolder(requireContext()))
        }
    }
}