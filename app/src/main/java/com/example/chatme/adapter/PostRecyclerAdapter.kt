package com.example.chatme.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.chatme.R
import com.example.chatme.model.PostModel
import com.example.chatme.model.UserInformationModel
import com.example.chatme.model.UserWhoLikesModel
import com.example.chatme.model.followedModel
import com.example.chatme.util.CallbackRecycler
import com.example.chatme.util.CallbackRecyclerPost
import com.example.chatme.util.utils.downloadUrl
import com.example.chatme.util.utils.placeHolder
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import de.hdodenhof.circleimageview.CircleImageView


class PostRecyclerAdapter(val database:FirebaseFirestore,val currentUserInformationModel: UserInformationModel, var postList:List<PostModel>):RecyclerView.Adapter<PostRecyclerAdapter.PostVH>() {
    class PostVH(view:View):RecyclerView.ViewHolder(view) {
        val authImage =view.findViewById<CircleImageView>(R.id.anaMenuPostAuthImage)
        val authName=view.findViewById<TextView>(R.id.anaMenuPostAuthName)
        val postImage=view.findViewById<ImageView>(R.id.anaMenuPostImage)
        val likeAnimation=view.findViewById<LottieAnimationView>(R.id.anaMenuPostLikeAnimationButton)
        val saveButton=view.findViewById<LottieAnimationView>(R.id.anaMenuPostSaveAnimationButton)
        val numberOfLikes =view.findViewById<TextView>(R.id.anaMenuPostNumberOfLikes)
        val explanationLayout =view.findViewById<LinearLayout>(R.id.anaMenuPostExplanationLayout)
        val explanationAuthName=view.findViewById<TextView>(R.id.anaMenuPostExplanationAuthName)
        val explanationText=view.findViewById<TextView>(R.id.anaMenuPostExplanationText)
        val commentsIsView=view.findViewById<TextView>(R.id.commentIsViewButton)
        val postCommentButton=view.findViewById<ImageView>(R.id.anaMenuPostCommentButton)
        val postTime =view.findViewById<TextView>(R.id.createPostTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostVH {
        val view =LayoutInflater.from(parent.context).inflate(R.layout.ana_menu_post_view,parent,false)
        return PostVH(view)
    }

    override fun onBindViewHolder(holder: PostVH, position: Int) {
        holder.postImage.downloadUrl(postList[position].imageUrl, placeHolder(holder.itemView.context))
        holder.authImage.downloadUrl(postList[position].userWhoSharedImage, placeHolder(holder.itemView.context))
        timeUpdate(holder,postList[position].time)
        holder.authName.setText(postList[position].userWhoShared)
        database.collection("Posts").document(postList[position].id).collection("userWhoLike").whereEqualTo("authName",currentUserInformationModel.authName).get().addOnSuccessListener {
            if (!it.isEmpty){
                holder.likeAnimation.progress=1F
                holder.likeAnimation.invalidate()
            }else{
                holder.likeAnimation.progress=0F
                holder.likeAnimation.invalidate()
            }
        }
        database.collection("Posts").document(postList[position].id).collection("comments").get().addOnSuccessListener {
            if (it.isEmpty){
                holder.commentsIsView.visibility=View.GONE
            }else{
                holder.commentsIsView.visibility=View.VISIBLE
                holder.commentsIsView.setText("${it.size()} yorumun tümünü gör")
            }
        }
        if (postList[position].numberOfLikes!=0){
            holder.numberOfLikes.visibility=View.VISIBLE
            holder.numberOfLikes.setText("${postList[position].numberOfLikes} beğenme")
        }else{
            holder.numberOfLikes.visibility=View.GONE
        }
        if (postList[position].explanation.isNotEmpty()){
            holder.explanationLayout.visibility=View.VISIBLE
            holder.explanationAuthName.setText(postList[position].userWhoShared)
            holder.explanationText.setText(postList[position].explanation)
        }else{
            holder.explanationLayout.visibility=View.GONE
        }

        holder.likeAnimation.setOnClickListener {
            likeImages(holder,postList[position].id,postList[position].numberOfLikes,)
        }
        holder.saveButton.setOnClickListener {
            holder.saveButton.playAnimation()
        }
        holder.authName.setOnClickListener {
            if (holder.authName.equals(currentUserInformationModel.authName)){
            holder.itemView.findNavController().navigate(R.id.action_mainPageFragment_to_proffilFragment3)
            }else{
                database.collection("User Information").whereEqualTo("authName",postList[position].userWhoShared).get().addOnSuccessListener {
                    if (!it.isEmpty){
                        val userInformation=it.toObjects(UserInformationModel::class.java)
                        val bundle = bundleOf("authName" to postList[position].userWhoShared,"incoming" to "share","mail" to userInformation[0].mail)
                        holder.itemView.findNavController().navigate(R.id.action_mainPageFragment_to_searchProfilFragment3,bundle)
                    }
                }
            }
        }
        holder.commentsIsView.setOnClickListener {
            val bundle = bundleOf("postId" to postList[position].id,"authName" to currentUserInformationModel.authName)
            holder.itemView.findNavController().navigate(R.id.action_mainPageFragment_to_postCommentFragment,bundle)
        }
        holder.postCommentButton.setOnClickListener {
            val bundle = bundleOf("postId" to postList[position].id,"authName" to currentUserInformationModel.authName)
            holder.itemView.findNavController().navigate(R.id.action_mainPageFragment_to_postCommentFragment,bundle)
        }

    }

    override fun getItemCount(): Int {
        return postList.size
    }

    fun likeImages(holder: PostVH,documentId:String,numberOfLikes:Int){
        database.collection("Posts").document(documentId).collection("userWhoLike").whereEqualTo("authName",currentUserInformationModel.authName).get().addOnSuccessListener {
            val datas =it.toObjects(UserWhoLikesModel::class.java)
            if (datas.isEmpty()){
                holder.likeAnimation.playAnimation()
                holder.likeAnimation.progress=1F
                val postLikeModel =UserWhoLikesModel(currentUserInformationModel.mail,currentUserInformationModel.name,currentUserInformationModel.authName,currentUserInformationModel.profilImage,Timestamp.now())
                database.collection("Posts").document(documentId).collection("userWhoLike").document(currentUserInformationModel.authName).set(postLikeModel).addOnSuccessListener {
                    database.collection("Posts").document(documentId).update("numberOfLikes",numberOfLikes+1)
                }
            }else{
                holder.likeAnimation.clearAnimation()
                database.collection("Posts").document(documentId).collection("userWhoLike").document(currentUserInformationModel.authName).delete().addOnSuccessListener {
                    database.collection("Posts").document(documentId).update("numberOfLikes",numberOfLikes-1).addOnSuccessListener {

                    }
                }
            }
        }

    }

    fun updateData(list:List<PostModel>){
        val diffUtil =CallbackRecyclerPost(postList,list)
        val calculate =DiffUtil.calculateDiff(diffUtil)
        postList=list
        calculate.dispatchUpdatesTo(this)
    }

    fun timeUpdate(holder : PostVH,time:Timestamp){
        val timeSecond =Timestamp.now().seconds-time.seconds
        val minute =timeSecond/60
        val hour =minute/60
        val day =hour/24
        if (timeSecond<=60){
            holder.postTime.setText("${minute.toInt()} saniye önce")
        }else if (minute<=60){
            holder.postTime.setText("${minute.toInt()} dakika önce")
        }else if (hour<=24){
            holder.postTime.setText("${hour.toInt()} saat önce")
        }else if (hour>24){
            holder.postTime.setText("${day.toInt()} gün önce")
        }
    }
}