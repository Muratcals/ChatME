package com.example.chatme.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.chatme.R
import com.example.chatme.model.PostModel
import com.example.chatme.util.CallbackRecyclerPost
import com.example.chatme.util.utils.downloadUrl
import com.example.chatme.util.utils.placeHolder

class PostRecyclerView (var postList: List<PostModel>):RecyclerView.Adapter<PostRecyclerView.PostVH>(){
    class PostVH(view:View):RecyclerView.ViewHolder(view) {
        val image =view.findViewById<ImageView>(R.id.profilPostImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostVH {
        val view =LayoutInflater.from(parent.context).inflate(R.layout.post_list_view,parent,false)
        return PostVH(view)
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    override fun onBindViewHolder(holder: PostVH, position: Int) {
        holder.image.downloadUrl(postList[position].imageUrl, placeHolder(holder.itemView.context))
        holder.image.setOnClickListener {
            val bundle = bundleOf("postId" to postList[position].id)
            holder.itemView.findNavController().navigate(R.id.action_proffilFragment_to_postDetailsFragmentFragment,bundle)
        }
    }
    fun updateData(list:List<PostModel>){
        val diffUtil = CallbackRecyclerPost(postList,list)
        val calculate = DiffUtil.calculateDiff(diffUtil)
        postList=list
        calculate.dispatchUpdatesTo(this)
    }
}