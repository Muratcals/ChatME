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
import com.example.chatme.util.utils
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
        database.collection("User Information").document(postList[position].sharedMail).get().addOnSuccessListener {
            if (it.exists()){
                val userInformation =it.toObject(UserInformationModel::class.java)
                if (userInformation!!.profilImage.isEmpty()){
                    holder.authImage.setImageResource(R.drawable.profil_icon)
                }else{
                    holder.authImage.downloadUrl(userInformation.profilImage, placeHolder(holder.itemView.context))
                }
                if (!userInformation.authName.equals(currentUserInformationModel.authName)){
                    holder.ellipsisDelete.visibility=View.GONE
                }
                holder.authName.setText(userInformation.authName)
                if (postList[position].explanation.isNotEmpty()){
                    holder.explanationLayout.visibility=View.VISIBLE
                    holder.explanationAuthName.setText(userInformation.authName)
                    holder.explanationText.setText(postList[position].explanation)
                }else{
                    holder.explanationLayout.visibility=View.GONE
                }
            }
        }
        holder.postImage.downloadUrl(postList[position].imageUrl, placeHolder(holder.itemView.context))
        timeUpdate(holder,postList[position].time)
        database.collection("User Information").document(currentUserInformationModel.mail).collection("saveImage").document(postList[position].id).get().addOnSuccessListener {
            if (it.exists()){
                holder.saveButton.progress=1F
            }else{
                holder.saveButton.progress=0F
            }
        }
        database.collection("Posts").document(postList[position].id).collection("userWhoLike").whereEqualTo("authName",currentUserInformationModel.authName).get().addOnSuccessListener {
            if (!it.isEmpty){
                holder.likeAnimation.progress=1F
            }else {
                holder.likeAnimation.progress=0F
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
        holder.likeAnimation.setOnClickListener {
            likeImages(holder,postList[position],postList[position].sharedMail)
        }
        holder.saveButton.setOnClickListener {
            holder.saveButton.playAnimation()
        }
        holder.authName.setOnClickListener {
            if (postList[position].sharedMail.equals(currentUserInformationModel.mail)){
            holder.itemView.findNavController().navigate(R.id.action_mainPageFragment_to_proffilFragment3)
            }else{
                database.collection("User Information").document(postList[position].sharedMail).get().addOnSuccessListener {
                    if (it.exists()){
                        val userInformation=it.toObject(UserInformationModel::class.java)
                        val bundle = bundleOf("authName" to userInformation!!.authName,"incoming" to "share","mail" to userInformation.mail)
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
        holder.saveButton.setOnClickListener {
            saveImage(holder,postList[position])
        }
        holder.ellipsisSave.setOnClickListener {
            saveImage(holder,postList[position])
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
        alertBuilder.setNegativeButton("İptal et") { dialog, _ ->
            dialog.cancel()
        }

    }

    fun likeImages(holder: PostVH, document: PostModel,mail:String){
            if (holder.likeAnimation.progress==0F){
                holder.likeAnimation.playAnimation()
                if (holder.numberOfLikes.text.isEmpty()){
                    holder.numberOfLikes.setText("1 beğenme")
                    holder.numberOfLikes.visibility=View.VISIBLE
                }else{
                    val parts = holder.numberOfLikes.text.toString().split(" ")
                    val number = parts[0]
                    holder.numberOfLikes.setText("${number.toInt()+1} beğenme")
                }
                MainScope().launch(Dispatchers.IO) {
                    val postLikeModel =UserWhoLikesModel(currentUserInformationModel.mail,currentUserInformationModel.name,currentUserInformationModel.authName,currentUserInformationModel.profilImage,Timestamp.now())
                    val notificationLikeModel =LikeModel(document.id,currentUserInformationModel.authName,currentUserInformationModel.profilImage,document.imageUrl,"like",currentUserInformationModel.mail)
                    database.collection("Posts").document(document.id).collection("userWhoLike").document(currentUserInformationModel.authName).set(postLikeModel).addOnSuccessListener {
                        database.collection("Posts").document(document.id).collection("userWhoLike").get().addOnSuccessListener {
                            database.collection("Posts").document(document.id).update("numberOfLikes",it.size()).addOnSuccessListener {
                                database.collection("User Information").document(mail).get().addOnSuccessListener {userModel->
                                    if (userModel.exists()){
                                        val userInformationModel=userModel.toObject(UserInformationModel::class.java)
                                        if (!userInformationModel!!.mail.equals(FirebaseAuth.getInstance().currentUser!!.email.toString())){
                                            database.collection("User Information").document(userInformationModel.mail).collection("notification").document("${currentUserInformationModel.authName} like").set(notificationLikeModel).addOnSuccessListener {

                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }else{
                holder.likeAnimation.progress=0F
                val parts = holder.numberOfLikes.text.toString().split(" ")
                val number = parts[0]
                if (number.toInt()!=1){
                    holder.numberOfLikes.setText("${number.toInt()-1} beğenme")
                }else{
                    holder.numberOfLikes.setText("")
                    holder.numberOfLikes.visibility=View.GONE
                }
                MainScope().launch(Dispatchers.IO) {
                    database.collection("Posts").document(document.id).collection("userWhoLike").document(currentUserInformationModel.authName).delete().addOnSuccessListener {
                        database.collection("Posts").document(document.id).collection("userWhoLike").get().addOnSuccessListener {
                            database.collection("Posts").document(document.id).update("numberOfLikes",it.size()).addOnSuccessListener {

                            }
                        }
                    }
                }
            }

    }

    fun updateData(list:List<PostModel>){
        val diffUtil =CallbackRecyclerPost(postList,list)
        val calculate =DiffUtil.calculateDiff(diffUtil)
        calculate.dispatchUpdatesTo(this)
        postList=list
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

    fun saveImage(holder: PostVH,postModel: PostModel){
        val currentUserReference =database.collection("User Information").document(currentUserInformationModel.mail).collection("saveImage")
        currentUserReference.whereEqualTo("id",postModel.id).get().addOnSuccessListener {
            if (it.isEmpty){
                holder.saveButton.playAnimation()
                currentUserReference.document(postModel.id).set(postModel).addOnSuccessListener {

                }
            }else{
                holder.saveButton.progress=0F
                currentUserReference.document(postModel.id).delete().addOnSuccessListener {

                }
            }
        }

    }
}