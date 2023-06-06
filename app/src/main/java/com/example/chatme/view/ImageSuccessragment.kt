package com.example.chatme.view

import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.chatme.databinding.FragmentImageSuccessragmentBinding
import com.example.chatme.viewmodel.ImageSuccessragmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ImageSuccessragment : Fragment() {

    @Inject lateinit var viewModel: ImageSuccessragmentViewModel
    private lateinit var binding:FragmentImageSuccessragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentImageSuccessragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            val imageUrl =it.getParcelable<Uri>("imageUrl")
            binding.successProgress.visibility=View.INVISIBLE
            val source =ImageDecoder.createSource(requireContext().contentResolver,imageUrl!!)
            val bitmap =ImageDecoder.decodeBitmap(source)
            binding.imageSuccessImageView.setImageBitmap(bitmap)
            binding.imageSuccessImageView.visibility=View.VISIBLE
            binding.imageSuccessragmentToolbar.postImageNextButton.setText("Paylaş")
            binding.imageSuccessragmentToolbar.postImageBack.setOnClickListener {
                requireActivity().onBackPressed()
            }
            binding.imageSuccessragmentToolbar.postImageNextButton.setOnClickListener {
                viewModel.postImage(requireActivity(),imageUrl,binding.imageInformation.text.toString())
            }
            viewModel.progress.observe(viewLifecycleOwner){
                if (it) binding.successProgress.visibility=View.VISIBLE
                else binding.successProgress.visibility=View.INVISIBLE
            }
        }
    }

}