package com.example.chatme.adapter

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.chatme.R
import com.example.chatme.model.CommentModel
import com.example.chatme.model.NatificationModel.CommentsModel
import com.example.chatme.model.PostModel
import com.example.chatme.model.UserInformationModel
import com.example.chatme.model.UserWhoLikesModel
import com.example.chatme.util.CallbackRecycler
import com.example.chatme.util.CallbackRecyclerPost
import com.example.chatme.util.utils.downloadUrl
import com.example.chatme.util.utils.placeHolder
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FirebaseFirestore
import de.hdodenhof.circleimageview.CircleImageView

class CommentsRecyclerAdapter(var commentList:List<CommentsModel>,val database:FirebaseFirestore,val getAuth: FirebaseUser,val documentId: String): RecyclerView.Adapter<CommentsRecyclerAdapter.CommentsVH>() {
    class CommentsVH(view: View):RecyclerView.ViewHolder(view) {
        val commentsAuthImage=view.findViewById<CircleImageView>(R.id.commentsDetailsAuthImage)
        val commentsAuthName=view.findViewById<TextView>(R.id.commentsUserWhoShared)
        val commenstExplanation =view.findViewById<TextView>(R.id.commentsDetailsExplanationText)
        val commentsTime =view.findViewById<TextView>(R.id.commentsDetailsPostTime)
        val commentEllipsis =view.findViewById<ImageView>(R.id.commentRecyclerViewEllipsis)
        val commentEllipsisDelete =view.findViewById<LinearLayout>(R.id.ellipsisDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsVH {
        val view =LayoutInflater.from(parent.context).inflate(R.layout.comments_view,parent,false)
        return CommentsVH(view)
    }

    override fun getItemCount(): Int {
        return commentList.size
    }

    override fun onBindViewHolder(holder: CommentsVH, position: Int) {
        if (getAuth.email.toString().equals(commentList[position].mail)){
            holder.commentEllipsis.visibility=View.VISIBLE
        }else{
            holder.commentEllipsis.visibility=View.GONE
        }
        holder.commentEllipsis.setOnClickListener {
            if (holder.commentEllipsisDelete.isVisible){
                holder.commentEllipsisDelete.visibility=View.GONE
            }else{
                holder.commentEllipsisDelete.visibility=View.VISIBLE
            }
        }
        holder.commentEllipsisDelete.setOnClickListener {
            val builder =AlertDialog.Builder(holder.itemView.context)
            builder.setTitle("Yorumu sil")
            builder.setMessage("Bu yorumu silmek istediğinize emin misiniz ?")
            builder.setPositiveButton("Sil"){ dialog,_->
                deleteComment(holder.itemView.context,commentList[position])
            }
            builder.setNegativeButton("İptal et"){dialog,_->
                dialog.cancel()
            }
            builder.show()
        }
        updateTime(holder,commentList[position].time)
        holder.commenstExplanation.setText(commentList[position].commentText)
        holder.commentsAuthName.setText(commentList[position].authName)
        holder.commentsAuthImage.downloadUrl(commentList[position].authImage, placeHolder(holder.itemView.context))
    }



    fun updateTime(holder: CommentsVH,time:Timestamp){
        val timeSecond =Timestamp.now().seconds-time.seconds
        val minute =timeSecond/60
        val hour =minute/60
        val day =hour/24
        if (timeSecond<=60){
            holder.commentsTime.setText("${minute.toInt()}sn")
        }else if (minute<=60){
            holder.commentsTime.setText("${minute.toInt()}d")
        }else if (hour<=24){
            holder.commentsTime.setText("${hour.toInt()}s")
        }else if (hour>24){
            holder.commentsTime.setText("${day.toInt()}g")
        }
    }

    fun updateData(list:List<CommentsModel>){
        val diffUtil = CallbackRecycler(commentList,list)
        val calculate = DiffUtil.calculateDiff(diffUtil)
        commentList=list
        calculate.dispatchUpdatesTo(this)
    }

    fun deleteComment(context: Context,comment:CommentsModel,){
        database.collection("Posts").document(documentId).collection("comments").document(comment.commentId).delete().addOnSuccessListener {
            database.collection("Posts").document(comment.postId).get().addOnSuccessListener {post->
                val postContent =post.toObject(PostModel::class.java)
                database.collection("User Information").document(comment.mail).collection("notification").document(postContent!!.userWhoShared).delete().addOnSuccessListener {
                    Toast.makeText(context, "Yorum silindi", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }
}