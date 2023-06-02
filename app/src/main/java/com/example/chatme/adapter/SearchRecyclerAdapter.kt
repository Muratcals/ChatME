package com.example.chatme.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.chatme.R
import com.example.chatme.model.UserInformationModel
import com.example.chatme.model.followedModel
import com.example.chatme.util.utils.downloadUrl
import de.hdodenhof.circleimageview.CircleImageView

class SearchRecyclerAdapter(
    var searchItems:List<UserInformationModel>
):RecyclerView.Adapter<SearchRecyclerAdapter.SearchVH>() {
    class SearchVH(view: View):RecyclerView.ViewHolder(view) {
        val image =view.findViewById<CircleImageView>(R.id.searchAuthImage)
        val authName=view.findViewById<TextView>(R.id.searchAuthName)
        val searchLayout=view.findViewById<ConstraintLayout>(R.id.searchLayout)
        val name =view.findViewById<TextView>(R.id.searchName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchVH {
        val view =LayoutInflater.from(parent.context).inflate(R.layout.search_recycler_view,parent,false)
        return SearchVH(view)
    }

    override fun getItemCount(): Int {
        return if (searchItems.size>5){
            5
        }else{
            searchItems.size
        }
    }

    override fun onBindViewHolder(holder: SearchVH, position: Int) {
        if (searchItems[position].profilImage.isNotEmpty()){
            holder.image.downloadUrl(searchItems[position].profilImage)
        }
        holder.searchLayout.setOnClickListener {
            val bundle = bundleOf("incoming" to "search","mail" to searchItems[position].mail)
            holder.itemView.findNavController().navigate(R.id.action_searchFragment_to_proffilFragment2,bundle)
        }
        holder.name.setText(searchItems[position].name)
        holder.authName.setText(searchItems[position].authName)
    }
}