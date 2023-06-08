package com.example.chatme.view

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.chatme.R
import com.example.chatme.adapter.PostRecyclerAdapter
import com.example.chatme.databinding.FragmentPostDetailsBinding
import com.example.chatme.model.PostModel
import com.example.chatme.util.utils.downloadUrl
import com.example.chatme.util.utils.placeHolder
import com.google.firebase.Timestamp
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PostDetailsFragmentFragment : Fragment() {

    @Inject lateinit var viewModel: PostDetailsViewModel
    private lateinit var binding:FragmentPostDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentPostDetailsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.profilPostDetailsLayout.mainPageFragmentToolbar.root.visibility=View.GONE
        arguments?.let {
            val postId=it.getString("postId")
            viewModel.database.collection("Posts").document(postId!!).get().addOnSuccessListener {
                if (it.exists()){
                    val postDetails =it.toObject(PostModel::class.java)
                    timeUpdate(postDetails!!.time)
                    binding.profilPostDetailsLayout.anaMenuPostAuthName.setText(postDetails.userWhoShared)
                    binding.profilPostDetailsLayout.anaMenuPostAuthImage.downloadUrl(postDetails.userWhoSharedImage,
                        placeHolder(requireContext())
                    )
                    binding.profilPostDetailsLayout.anaMenuPostImage.downloadUrl(postDetails.imageUrl,
                        placeHolder(requireContext())
                    )
                    if (postDetails.explanation.isEmpty()){
                        binding.profilPostDetailsLayout.anaMenuPostExplanationLayout.visibility=View.GONE
                    }else{
                        binding.profilPostDetailsLayout.anaMenuPostExplanationAuthName.setText(postDetails.userWhoShared)
                        binding.profilPostDetailsLayout.anaMenuPostExplanationText.setText(postDetails.explanation)
                        binding.profilPostDetailsLayout.anaMenuPostNumberOfLikes.setText("${postDetails.numberOfLikes} beğenme")
                    }
                    it.reference.collection("comments").get().addOnSuccessListener {
                        if (!it.isEmpty){
                            binding.profilPostDetailsLayout.commentIsViewButton.setText("${it.size()} yorumun tümünü gör")
                        }else{
                            binding.profilPostDetailsLayout.commentIsViewButton.visibility=View.GONE
                        }
                    }
                    viewModel.database.collection("User Information").document(viewModel.getAuth.email.toString()).get().addOnSuccessListener {
                        it.reference.collection("userWhoLikes").document(it.get("authName") as String).get().addOnSuccessListener {
                            if (it.exists()){
                                binding.profilPostDetailsLayout.anaMenuPostLikeAnimationButton.progress=1F
                            }
                        }
                    }
                }
            }
        }
    }

    fun timeUpdate(time: Timestamp){
        val timeSecond = Timestamp.now().seconds-time.seconds
        val minute =timeSecond/60
        val hour =minute/60
        val day =hour/24
        if (timeSecond<=60){
            binding.profilPostDetailsLayout.createPostTime.setText("${minute.toInt()} saniye önce")
        }else if (minute<=60){
            binding.profilPostDetailsLayout.createPostTime.setText("${minute.toInt()} dakika önce")
        }else if (hour<=24){
            binding.profilPostDetailsLayout.createPostTime.setText("${hour.toInt()} saat önce")
        }else if (hour>24){
            binding.profilPostDetailsLayout.createPostTime.setText("${day.toInt()} gün önce")
        }
    }

}