package com.example.chatme.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.chatme.R
import com.example.chatme.databinding.FragmentFollowAndFollowedBinding
import com.example.chatme.model.followedModel
import com.example.chatme.util.CallbackRecycler
import com.example.chatme.util.utils.downloadUrl
import com.example.chatme.util.utils.placeHolder
import com.example.chatme.viewmodel.FollowAndFollowedViewModel
import de.hdodenhof.circleimageview.CircleImageView

class FollowAndFollowedAdapter(var list:List<followedModel>, val state:String,val viewModel:FollowAndFollowedViewModel):RecyclerView.Adapter<FollowAndFollowedAdapter.FollowAndFollowVH>() {
    class FollowAndFollowVH(view: View):RecyclerView.ViewHolder(view) {
        val image =view.findViewById<CircleImageView>(R.id.followAndFollowedImage)
        val name =view.findViewById<TextView>(R.id.followAndFollowedName)
        val authName=view.findViewById<TextView>(R.id.followAndFollowedAuthName)
        val button =view.findViewById<Button>(R.id.followAndFollowedButton)
        val progress=view.findViewById<ProgressBar>(R.id.followAndFollowedProgress)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowAndFollowVH {
        val view =LayoutInflater.from(parent.context).inflate(R.layout.follow_and_followed_view,parent,false)
        return FollowAndFollowVH(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: FollowAndFollowVH, position: Int) {
        if (list[position].imageUrl.isEmpty()){
            holder.image.setImageResource(R.drawable.profil_icon)
        }else{
            holder.image.downloadUrl(list[position].imageUrl, placeHolder(holder.itemView.context))
        }
        holder.name.setText(list[position].name)
        holder.authName.setText(list[position].authName)

        val currentUserReference=viewModel.database.collection("User Information").document(viewModel.getAuth.email.toString())
        val userReference =viewModel.database.collection("User Information").document(list[position].mail)
        if (state.equals("follow")){
            currentUserReference.collection("followed").whereEqualTo("authName",list[position].authName).get().addOnSuccessListener{
                if (it.isEmpty){
                    println("gir1")
                    currentUserReference.collection("request").whereEqualTo("authName",list[position].authName).whereGreaterThan("bool",false).get().addOnSuccessListener {
                        if (it.isEmpty){
                            println("gir3")
                            holder.button.setText("Bekleniyor")
                            holder.button.setBackgroundResource(R.drawable.button_backgorund_gray_shape)
                        }else{
                            println("gir4")
                            holder.button.setText("Takip Et")
                            holder.button.setBackgroundResource(R.drawable.button_background_shape)
                        }
                    }
                }else{
                    println("gir2")
                    holder.button.setText("Takip")
                    holder.button.setBackgroundResource(R.drawable.button_background_shape)
                }
            }
        }
        holder.button.setText("Takip")
        holder.button.setBackgroundResource(R.drawable.button_backgorund_gray_shape)
        holder.button.setOnClickListener {
            holder.progress.visibility=View.VISIBLE
            holder.button.visibility=View.INVISIBLE
            if (holder.button.text.equals("Takip Et")){
                viewModel.authFollow(holder.itemView.context,list[position])
            }
        }
    }

    fun updateData(newList: List<followedModel>) {
        val callback =CallbackRecycler(list,newList)
        val diffUtil =DiffUtil.calculateDiff(callback)
        diffUtil.dispatchUpdatesTo(this)
        list=newList
    }
}