package com.example.chatme.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatme.adapter.CommentsRecyclerAdapter
import com.example.chatme.databinding.FragmentPostCommentBinding
import com.example.chatme.model.CommentModel
import com.example.chatme.model.NatificationModel.CommentsModel
import com.example.chatme.model.PostModel
import com.example.chatme.model.UserInformationModel
import com.example.chatme.util.utils.downloadUrl
import com.example.chatme.util.utils.placeHolder
import com.example.chatme.viewmodel.PostCommentViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.toObject
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID
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
            binding.commentToolbar.profilDetailseToolbarTitle.setText("Yorumlar")
            binding.commentToolbar.profilDetailsToolbarBackPage.setOnClickListener {
                requireActivity().onBackPressed()
            }
            binding.commentToolbar.profilDetailseToolbarSuccess.visibility=View.GONE
            val postId =it.getString("postId")
            val currentAuthName=it.getString("authName")
            viewModel.getPost(postId!!)
            viewModel.getComments(postId)
            val adapter =CommentsRecyclerAdapter(arrayListOf(),viewModel.database,viewModel.getAuth,postId)
            binding.commentsRecyclerView.adapter=adapter
            binding.commentsRecyclerView.layoutManager=LinearLayoutManager(requireContext())
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
                val time =Timestamp.now().seconds-it.time.seconds
                val minute =time/60
                val hour =minute/60
                val day =hour/24
                if (time<=60){
                    binding.commentsPostTime.setText("${minute.toInt()}sn")
                }else if (minute<=60){
                binding.commentsPostTime.setText("${minute.toInt()}d")
                }else if (hour<=24){
                    binding.commentsPostTime.setText("${hour.toInt()}s")
                }else if (hour>24){
                    binding.commentsPostTime.setText("${day.toInt()}g")
                }
            }
           viewModel.comments.observe(viewLifecycleOwner){
               if (it?.isEmpty()==true){
                   binding.commentsEmptyLayout.visibility=View.VISIBLE
                   binding.commentsRecyclerView.visibility=View.GONE
               }else{
                   binding.commentsEmptyLayout.visibility=View.GONE
                   binding.commentsRecyclerView.visibility=View.VISIBLE
                   adapter.updateData(it!!)
               }
           }
            binding.commentEdittext.addTextChangedListener {
                if (it?.isEmpty()==true){
                    binding.shareCommentButton.visibility=View.INVISIBLE
                }else{
                    binding.shareCommentButton.visibility=View.VISIBLE
                }
            }
            binding.shareCommentButton.setOnClickListener {
                viewModel.database.collection("Posts").document(postId).get().addOnSuccessListener {
                    if (it.exists()){
                        val sharedAuthModel =it.toObject(PostModel::class.java)
                        viewModel.postComments(view,postId,binding.commentEdittext.text.toString(),sharedAuthModel!!.userWhoShared)
                        binding.commentEdittext.text.clear()
                    }

                }

            }
        }
    }


}