package com.example.chatme.view

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.chatme.R
import com.example.chatme.databinding.FragmentPostCommentBinding
import com.example.chatme.util.utils.downloadUrl
import com.example.chatme.util.utils.placeHolder
import com.google.firebase.Timestamp
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
@AndroidEntryPoint
class PostCommentFragment : Fragment() {

    @Inject lateinit var viewModel: PostCommentViewModel
    private lateinit var binding:FragmentPostCommentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentPostCommentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            val postId =it.getString("postId")
            val currentAuthName=it.getString("authName")
            viewModel.getPost(postId!!)
            viewModel.getComments(postId)
            viewModel.progress.observe(viewLifecycleOwner){
                if (it){
                    binding.commentLayout.visibility=View.GONE
                    binding.commentsProgress.visibility=View.VISIBLE
                }else{
                    binding.commentLayout.visibility=View.VISIBLE
                    binding.commentsProgress.visibility=View.GONE
                }
            }
            viewModel.post.observe(viewLifecycleOwner){
                binding.commentsAuthImage.downloadUrl(it.userWhoSharedImage, placeHolder(requireContext()))
                binding.commentsExplanationText.setText(it.explanation)
                binding.commentsUserWhoShared.setText(it.userWhoShared)
                binding.addCommentAuthImage.downloadUrl(it.userWhoSharedImage, placeHolder(requireContext()))
                binding.commentEdittext.setHint("${currentAuthName} adıyla yorum ekle")
            }
           viewModel.comments.observe(viewLifecycleOwner){
               if (it.isEmpty()){
                   binding.commentsEmptyLayout.visibility=View.VISIBLE
                   binding.commentsRecyclerView.visibility=View.GONE
               }else{
                   binding.commentsEmptyLayout.visibility=View.GONE
                   binding.commentsRecyclerView.visibility=View.VISIBLE
               }
           }
        }
    }


}