package com.example.chatme.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.chatme.R
import com.example.chatme.databinding.FragmentProfilEditBinding
import com.example.chatme.viewmodel.ProfilEditViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfilEditFragment : Fragment() {

    @Inject
    lateinit var viewModel: ProfilEditViewModel
    @Inject lateinit var auth: FirebaseAuth
    @Inject lateinit var database:FirebaseFirestore
    private lateinit var binding: FragmentProfilEditBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentProfilEditBinding.inflate(layoutInflater)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database.collection("User Information").whereEqualTo("mail",auth.currentUser!!.email.toString()).get().addOnSuccessListener {
            if (it.isEmpty){
                Toast.makeText(requireContext(), "Bir hata oluştu", Toast.LENGTH_SHORT).show()
            }else {
                val result =it.documents
                val name =result[0].get("name") as String
                val authName=result[0].get("authName") as String
                val biography=result[0].get("biography") as String
                val gender =result[0].get("gender") as String
                binding.authNameText.setText(authName)
                binding.genderText.setText(gender)
                binding.biographyText.setText(biography)
                binding.nameText.setText(name)
                binding.profilProgress.visibility=View.GONE
                binding.profilLinearLayout.visibility=View.VISIBLE
            }
        }.addOnFailureListener {
            binding.profilProgress.visibility=View.GONE
            Toast.makeText(requireContext(),it.localizedMessage , Toast.LENGTH_SHORT).show()
        }
        binding.nameLayout.setOnClickListener {
            val bundle = bundleOf("incoming" to "name")
            findNavController().navigate(R.id.action_profilEditFragment_to_profilEditDetailsFragment,bundle)
        }
        binding.authNameLayout.setOnClickListener {
            val bundle = bundleOf("incoming" to "authName")
            findNavController().navigate(R.id.action_profilEditFragment_to_profilEditDetailsFragment,bundle)
        }
        binding.biographyLayout.setOnClickListener {
            val bundle = bundleOf("incoming" to "biography")
            findNavController().navigate(R.id.action_profilEditFragment_to_profilEditDetailsFragment,bundle)
        }
        binding.genderLayout.setOnClickListener {
            val bundle = bundleOf("incoming" to "gender")
            findNavController().navigate(R.id.action_profilEditFragment_to_profilEditDetailsFragment,bundle)
        }
    }


}