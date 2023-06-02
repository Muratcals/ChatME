package com.example.chatme.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.chatme.R
import com.example.chatme.databinding.FragmentProffilBinding
import com.example.chatme.model.UserInformationModel
import com.example.chatme.model.followedModel
import com.example.chatme.viewmodel.ProffilViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
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
        arguments.let {
            val incoming =it?.getString("incoming")
            val mail=it?.getString("mail")
            if (incoming != null) {
                viewModel.authFollowController(binding, mail!!)
            } else {
                binding.linearLayout4.visibility=View.GONE
                binding.profilEditButton.visibility=View.VISIBLE
                viewModel.authProfilInformation()

            }
            binding.profilEditButton.setOnClickListener {
                findNavController().navigate(R.id.action_proffilFragment_to_profilEditFragment)
            }
            binding.follow.setOnClickListener {
                if (binding.follow.text.equals("Takip Et")){
                    val users =viewModel.authInformation.value
                    val user = followedModel(users!!.customerId,users.authName,users.authName,users.profilImage)
                    viewModel.authFollow(user)
                    binding.follow.setBackgroundResource(R.drawable.button_background_shape)
                    binding.follow.setText("Takip")
                }else{
                    val users =viewModel.authInformation.value
                    val user = followedModel(users!!.customerId,users.authName,users.authName,users.profilImage)
                    viewModel.authDeleteFollow(user)
                    binding.follow.setBackgroundResource(R.drawable.button_backgorund_gray_shape)
                    binding.follow.setText("Takip Et")
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
    }
}