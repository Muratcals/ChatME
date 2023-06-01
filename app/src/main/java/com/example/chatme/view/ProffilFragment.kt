package com.example.chatme.view

import androidx.lifecycle.ViewModelProvider
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
import com.example.chatme.util.utils.downloadUrl
import com.example.chatme.viewmodel.ProffilViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID
import javax.inject.Inject

@AndroidEntryPoint
class ProffilFragment : Fragment() {

    @Inject lateinit var viewModel: ProffilViewModel
    @Inject lateinit var auth: FirebaseAuth
    @Inject lateinit var database:FirebaseFirestore
    private lateinit var binding :FragmentProffilBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentProffilBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        database.collection("User Information").whereEqualTo("mail",auth.currentUser!!.email.toString()).get().addOnSuccessListener {
            if (it.isEmpty){
                Toast.makeText(requireContext(), "Bir hata oluştu", Toast.LENGTH_SHORT).show()
            }else{
                val result =it.toObjects(UserInformationModel::class.java)
                val name =result[0].name
                val authName=result[0].authName
                val biography =result[0].biography
                val imageUrl=result[0].profilImage
                val follow =result[0].follow
                val followed=result[0].followed
                if (imageUrl.isNotEmpty()){
                    binding.profilImage.downloadUrl(imageUrl)
                }
                binding.followCount.setText(follow.size.toString())
                binding.followedCount.setText(followed.size.toString())
                binding.profilUserName.setText(name)
                binding.profilToolbar.toolbarUserAuthName.setText(authName)
                binding.profilBiography.setText(biography)
                binding.profilProgressBar.visibility=View.GONE
                binding.profilLayout.visibility=View.VISIBLE
            }
        }.addOnFailureListener {
            Toast.makeText(requireContext(),it.localizedMessage, Toast.LENGTH_SHORT).show()
            binding.profilProgressBar.visibility=View.GONE
        }
        binding.profilEditButton.setOnClickListener {
            findNavController().navigate(R.id.action_proffilFragment_to_profilEditFragment)
        }
    }

}