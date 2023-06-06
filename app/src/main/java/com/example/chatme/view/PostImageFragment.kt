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
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.chatme.R
import com.example.chatme.databinding.FragmentPostImageBinding
import com.example.chatme.viewmodel.PostImageViewModel
import java.io.File

class PostImageFragment : Fragment() {

    private lateinit var viewModel: PostImageViewModel
    private lateinit var binding: FragmentPostImageBinding
    val  PICK_IMAGE_REQUEST_CODE=100

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentPostImageBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),PICK_IMAGE_REQUEST_CODE)
        }else{
            val intent=Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode==PICK_IMAGE_REQUEST_CODE){
            if (ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),PICK_IMAGE_REQUEST_CODE)
            }else{
                val intent=Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE)
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
            if (data!=null){
                val image =data.data
                val imageSource = ImageDecoder.createSource(requireContext().contentResolver,image!!)
                val bitmap = ImageDecoder.decodeBitmap(imageSource)
                val bitmapScale =Bitmap.createScaledBitmap(bitmap,bitmap.width/2,bitmap.height/2,false)
                val file=File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),"image.png")
                val outputStream=file.outputStream()
                bitmapScale.compress(Bitmap.CompressFormat.PNG,85,outputStream)
                val compressUri=Uri.fromFile(file)
                val newImageSource = ImageDecoder.createSource(requireContext().contentResolver,compressUri)
                val newBitmap = ImageDecoder.decodeBitmap(newImageSource)
                binding.postImageView.setImageBitmap(newBitmap)
                binding.postImageView.visibility=View.VISIBLE
                binding.postImageToolbar.postImageNextButton.setOnClickListener {
                    if (compressUri!=null){
                        val bundle = bundleOf("imageUrl" to compressUri)
                        findNavController().navigate(R.id.action_postImageFragment_to_imageSuccessragment,bundle)
                    }
                }
            }else{
                requireActivity().finish()
            }
    }
}