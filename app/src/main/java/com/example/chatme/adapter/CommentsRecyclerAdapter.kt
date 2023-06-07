package com.example.chatme.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.chatme.R
import com.example.chatme.model.CommentModel
import com.example.chatme.model.PostModel
import com.example.chatme.util.CallbackRecycler
import com.example.chatme.util.CallbackRecyclerPost
import com.example.chatme.util.utils.downloadUrl
import com.example.chatme.util.utils.placeHolder
import com.google.firebase.Timestamp
import de.hdodenhof.circleimageview.CircleImageView

class CommentsRecyclerAdapter(var commentList:List<CommentModel>): RecyclerView.Adapter<CommentsRecyclerAdapter.CommentsVH>() {
    class CommentsVH(view: View):RecyclerView.ViewHolder(view) {
        val commentsAuthImage=view.findViewById<CircleImageView>(R.id.commentsDetailsAuthImage)
        val commentsAuthName=view.findViewById<TextView>(R.id.commentsUserWhoShared)
        val commenstExplanation =view.findViewById<TextView>(R.id.commentsDetailsExplanationText)
        val commentsTime =view.findViewById<TextView>(R.id.commentsDetailsPostTime)
        val likeButton =view.findViewById<ImageView>(R.id.commentLikeButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsVH {
        val view =LayoutInflater.from(parent.context).inflate(R.layout.comments_view,parent,false)
        return CommentsVH(view)
    }

    override fun getItemCount(): Int {
        return commentList.size
    }

    override fun onBindViewHolder(holder: CommentsVH, position: Int) {
        updateTime(holder,commentList[position].commentTime)
        holder.commenstExplanation.setText(commentList[position].commentText)
        holder.commentsAuthName.setText(commentList[position].commenter)
        holder.commentsAuthImage.downloadUrl(commentList[position].commenterImage, placeHolder(holder.itemView.context))
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

    fun updateData(list:List<CommentModel>){
        val diffUtil = CallbackRecycler(commentList,list)
        val calculate = DiffUtil.calculateDiff(diffUtil)
        commentList=list
        calculate.dispatchUpdatesTo(this)
    }
}