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
import com.example.chatme.model.UserInformationModel
import com.example.chatme.model.followedModel
import com.example.chatme.util.CallbackRecycler
import com.example.chatme.util.utils.downloadUrl
import com.example.chatme.util.utils.placeHolder
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import de.hdodenhof.circleimageview.CircleImageView

class FollowRquestRecyclerAdapter(var requestsList:List<followedModel>,val database: FirebaseFirestore,val getAuth: FirebaseUser):RecyclerView.Adapter<FollowRquestRecyclerAdapter.FollowRecyclerVH>() {
    class FollowRecyclerVH(view:View) :RecyclerView.ViewHolder(view){
        val authImage =view.findViewById<CircleImageView>(R.id.requestImage)
        val authName=view.findViewById<TextView>(R.id.requestAuthName)
        val name=view.findViewById<TextView>(R.id.requestName)
        val successButton=view.findViewById<Button>(R.id.requestSuccessButton)
        val deleteButton=view.findViewById<Button>(R.id.requestDeleteButton)
        val progressBar =view.findViewById<ProgressBar>(R.id.requestRecyclerProgress)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowRecyclerVH {
        val view =LayoutInflater.from(parent.context).inflate(R.layout.follow_requests_view,parent,false)
        return FollowRecyclerVH(view)
    }

    override fun getItemCount(): Int {
        return if (requestsList.size<8){
            requestsList.size
        }else{
            8
        }
    }

    override fun onBindViewHolder(holder: FollowRecyclerVH, position: Int) {
        if (requestsList[position].bool==false){
            holder.authImage.downloadUrl(requestsList[position].imageUrl, placeHolder(holder.itemView.context))
            holder.name.setText(requestsList[position].name)
            holder.authName.setText(requestsList[position].authName)
            holder.successButton.setOnClickListener {
                requestSuccess(holder,requestsList[position])
            }
            holder.deleteButton.setOnClickListener {

            }
        }
    }

    fun updateData(newList:List<followedModel>){
        val diffUtilCallback =CallbackRecycler(requestsList,newList)
        val diifResult=DiffUtil.calculateDiff(diffUtilCallback)
        diifResult.dispatchUpdatesTo(this)
        requestsList=newList
    }

    fun requestSuccess(holder:FollowRecyclerVH,users:followedModel){
        holder.progressBar.visibility=View.VISIBLE
        holder.successButton.visibility=View.GONE
        val currentReference =database.collection("User Information").document(getAuth.email.toString())
        val user=database.collection("User Information").document(users.mail)
        user.collection("sendRequest").document(users.authName).delete().addOnSuccessListener {
            currentReference.collection("request").document(users.authName).update("bool",true).addOnSuccessListener {
                currentReference.collection("follow").document(users.authName).set(users).addOnSuccessListener {
                    currentReference.get().addOnSuccessListener {
                        if (it.exists()){
                            val currentUser =it.toObject(UserInformationModel::class.java)
                            val currentUserFollowed=followedModel(currentUser!!.mail,currentUser.name,currentUser.authName,currentUser.profilImage,Timestamp.now())
                            user.collection("followed").document(currentUser.mail).set(currentUserFollowed).addOnSuccessListener {
                                followController(holder,users.authName)
                            }
                        }
                    }
                }

            }
        }
    }

    fun followController(holder:FollowRecyclerVH,authName:String){
        val currentReference =database.collection("User Information").document(getAuth.email.toString())
        currentReference.collection("followed").whereEqualTo("authName",authName).get().addOnSuccessListener {
            if (it.isEmpty){
                holder.deleteButton.setText("Sende onu takip et")
            }else{
                holder.deleteButton.setText("Takip")
            }
            holder.progressBar.visibility=View.GONE
        }
    }

    fun sendNotification(){

    }
}