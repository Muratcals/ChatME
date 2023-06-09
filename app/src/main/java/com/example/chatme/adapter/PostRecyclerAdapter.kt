package com.example.chatme.adapter

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.chatme.R
import com.example.chatme.model.NatificationModel.LikeModel
import com.example.chatme.model.PostModel
import com.example.chatme.model.UserInformationModel
import com.example.chatme.model.UserWhoLikesModel
import com.example.chatme.util.CallbackRecyclerPost
import com.example.chatme.util.utils.downloadUrl
import com.example.chatme.util.utils.placeHolder
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


class PostRecyclerAdapter(val database:FirebaseFirestore,val currentUserInformationModel: UserInformationModel, var postList:List<PostModel>,val storage: FirebaseStorage):RecyclerView.Adapter<PostRecyclerAdapter.PostVH>() {
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
        val ellipsisIcon =view.findViewById<ImageView>(R.id.postEllipsisIcon)
        val ellipsisDelete =view.findViewById<LinearLayout>(R.id.ellipsisDelete)
        val ellipsisSave =view.findViewById<LinearLayout>(R.id.ellipsisSave)
        val ellipsisLayout =view.findViewById<LinearLayout>(R.id.mainEllipsisLayout)
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
        if (!postList[position].userWhoShared.equals(currentUserInformationModel.authName)){
            holder.ellipsisDelete.visibility=View.GONE
        }
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
        database.collection("Posts").document(postList[position].id).collection("userWhoLike").get().addOnSuccessListener {
            if (!it.isEmpty){
                holder.numberOfLikes.visibility=View.VISIBLE
                holder.numberOfLikes.setText("${it.size()} beğenme")
            }else{
                holder.numberOfLikes.visibility=View.GONE
            }
        }

        if (postList[position].explanation.isNotEmpty()){
            holder.explanationLayout.visibility=View.VISIBLE
            holder.explanationAuthName.setText(postList[position].userWhoShared)
            holder.explanationText.setText(postList[position].explanation)
        }else{
            holder.explanationLayout.visibility=View.GONE
        }

        holder.likeAnimation.setOnClickListener {
            likeImages(holder,postList[position],postList[position].numberOfLikes,postList[position].userWhoShared)
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
        holder.ellipsisIcon.setOnClickListener {
            if (holder.ellipsisLayout.isVisible){
                holder.ellipsisLayout.visibility=View.GONE
            }else{
                holder.ellipsisLayout.visibility=View.VISIBLE
            }
        }
        holder.ellipsisDelete.setOnClickListener {
            ellipsisDelete(holder,postList[position].imageUrl)
        }
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    fun ellipsisDelete(holder: PostVH,documentId:String){
        val alertBuilder=AlertDialog.Builder(holder.itemView.context)
        alertBuilder.setMessage("Gönderinizi silmek istediğinize emin misiniz")
        alertBuilder.setTitle("Gönderiyi sil")
        alertBuilder.setPositiveButton("Sil") { dialog, which ->
            val reference = database.collection("Posts").document(documentId)
            reference.get().addOnSuccessListener {
                if (it.exists()) {
                    storage.getReference(it.get("imageUrl") as String).delete()
                        .addOnSuccessListener {
                            reference.delete().addOnSuccessListener {
                                Toast.makeText(
                                    holder.itemView.context,
                                    "Gönderi başarıyla silindi",
                                    Toast.LENGTH_SHORT
                                ).show()
                                dialog.cancel()
                            }
                        }
                }
            }
        }
        alertBuilder.setNegativeButton("İptal et") { dialog, which ->
            dialog.cancel()
        }

    }

    fun likeImages(holder: PostVH, document: PostModel, numberOfLikes:Int,authName:String){
        database.collection("Posts").document(document.id).collection("userWhoLike").whereEqualTo("authName",currentUserInformationModel.authName).get().addOnSuccessListener {
            if (it.isEmpty()){
                holder.likeAnimation.playAnimation()
                holder.likeAnimation.progress=1F
                val postLikeModel =UserWhoLikesModel(currentUserInformationModel.mail,currentUserInformationModel.name,currentUserInformationModel.authName,currentUserInformationModel.profilImage,Timestamp.now())
                val notificationLikeModel =LikeModel(document.id,currentUserInformationModel.authName,currentUserInformationModel.profilImage,document.imageUrl,"like",currentUserInformationModel.mail)
                database.collection("Posts").document(document.id).collection("userWhoLike").document(currentUserInformationModel.authName).set(postLikeModel).addOnSuccessListener {
                    database.collection("Posts").document(document.id).update("numberOfLikes",numberOfLikes+1).addOnSuccessListener {
                        MainScope().launch(Dispatchers.IO) {
                            database.collection("User Information").whereEqualTo("authName",authName).get().addOnSuccessListener {userModel->
                                val userInformationModel=userModel.toObjects(UserInformationModel::class.java)
                                if (!userInformationModel[0].mail.equals(FirebaseAuth.getInstance().currentUser!!.email.toString())){
                                    database.collection("User Information").document(userInformationModel[0].mail).collection("notification").document("${currentUserInformationModel.authName} like").set(notificationLikeModel).addOnSuccessListener {

                                    }
                                }

                            }
                        }

                    }
                }
            }else{
                holder.likeAnimation.clearAnimation()
                database.collection("Posts").document(document.id).collection("userWhoLike").document(currentUserInformationModel.authName).delete().addOnSuccessListener {
                    database.collection("Posts").document(document.id).update("numberOfLikes",numberOfLikes-1).addOnSuccessListener {

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

    fun saveImage(){

    }
}