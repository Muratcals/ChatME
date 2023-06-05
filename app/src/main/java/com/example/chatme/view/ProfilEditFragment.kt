package com.example.chatme.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.chatme.R
import com.example.chatme.Retrofit.RetrofitServices
import com.example.chatme.databinding.FragmentProfilEditBinding
import com.example.chatme.util.utils.downloadUrl
import com.example.chatme.util.utils.placeHolder
import com.example.chatme.viewmodel.ProfilEditViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.util.UUID
import javax.inject.Inject

@AndroidEntryPoint
class ProfilEditFragment : Fragment() {

    @Inject
    lateinit var viewModel: ProfilEditViewModel
    @Inject lateinit var auth: FirebaseAuth
    @Inject lateinit var getAuth:FirebaseUser
    @Inject lateinit var database:FirebaseFirestore
    private lateinit var binding: FragmentProfilEditBinding
    @Inject lateinit var storage:FirebaseStorage

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
                val imageUrl=result[0].get("profilImage") as String
                if (imageUrl.isNotEmpty()){
                    binding.editImage.downloadUrl(imageUrl, placeHolder(requireContext()))
                }
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
        binding.editImage.setOnClickListener {
            editImage()
        }
    }

    fun editImage(){
        if (ActivityCompat.checkSelfPermission(requireContext(),Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireContext(),Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED){
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),1)
        }else{
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent,100)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode==1){
            editImage()
        }
    }

    fun buttonEnabled(state:Boolean){
        if (!state){
            binding.profilProgress.visibility=View.VISIBLE
            binding.nameText.isClickable=false
            binding.biographyText.isClickable=false
            binding.genderText.isClickable=false
            binding.authNameText.isClickable=false
            binding.nameText.isEnabled=false
            binding.biographyText.isEnabled=false
            binding.genderText.isEnabled=false
            binding.authNameText.isEnabled=false
        }else{
            binding.profilProgress.visibility=View.INVISIBLE
            binding.nameText.isClickable=true
            binding.biographyText.isClickable=true
            binding.genderText.isClickable=true
            binding.authNameText.isClickable=true
            binding.nameText.isEnabled=true
            binding.biographyText.isEnabled=true
            binding.genderText.isEnabled=true
            binding.authNameText.isEnabled=true
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode==100){
            if (data!=null){
                buttonEnabled(false)
                val image =data.data
                val source =ImageDecoder.createSource(requireContext().contentResolver,image!!)
                val bitmap =ImageDecoder.decodeBitmap(source)
                val file=File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),"image.png")
                val outputStream=file.outputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG,85,outputStream)
                val compressUri=Uri.fromFile(file)
                val uuid=UUID.randomUUID().toString()
                val reference=storage.getReference(uuid)
                reference.putFile(compressUri).addOnSuccessListener { imageTask->
                    imageTask.storage.downloadUrl.addOnSuccessListener { uri->
                        database.collection("User Information").document(getAuth.email.toString()).update("profilImage",uri.toString()).addOnSuccessListener {
                            Toast.makeText(requireContext(), "Profil resmi güncellendi.", Toast.LENGTH_SHORT).show()
                            requireActivity().onBackPressed()
                            buttonEnabled(true)
                        }.addOnFailureListener {
                            reference.delete().addOnSuccessListener {
                                buttonEnabled(true)
                            }.addOnFailureListener {
                                println(it.localizedMessage)
                            }
                        }
                    }.addOnFailureListener {
                        buttonEnabled(true)
                        println(it.localizedMessage)
                    }
                }.addOnFailureListener {
                    buttonEnabled(true)
                    println(it.localizedMessage)
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun update(url:String){

    }
}