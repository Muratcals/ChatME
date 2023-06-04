package com.example.chatme.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.chatme.R
import com.example.chatme.model.UserInformationModel
import com.example.chatme.model.followNotificationModel
import com.example.chatme.model.followedModel
import com.example.chatme.util.CallbackRecycler
import com.example.chatme.util.utils.downloadUrl
import com.example.chatme.util.utils.placeHolder
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import de.hdodenhof.circleimageview.CircleImageView

class FollowRquestRecyclerAdapter(var requestsList:List<followNotificationModel>,val database: FirebaseFirestore,val getAuth: FirebaseUser):RecyclerView.Adapter<FollowRquestRecyclerAdapter.FollowRecyclerVH>() {
    class FollowRecyclerVH(view:View) :RecyclerView.ViewHolder(view){
        val followAuthName =view.findViewById<TextView>(R.id.followAuthName)
        val requestButtonLayout=view.findViewById<LinearLayout>(R.id.requestButtonLayout)
        val requestFollowButton=view.findViewById<Button>(R.id.requestFollowButton)
        val requestFollowButtonLayout=view.findViewById<LinearLayout>(R.id.requestFollowButtonLayout)
        val followImage =view.findViewById<CircleImageView>(R.id.followImage)
        val followButton =view.findViewById<Button>(R.id.followButton)
        val authImage =view.findViewById<CircleImageView>(R.id.requestImage)
        val authName=view.findViewById<TextView>(R.id.requestAuthName)
        val name=view.findViewById<TextView>(R.id.requestName)
        val successButton=view.findViewById<Button>(R.id.requestSuccessButton)
        val deleteButton=view.findViewById<Button>(R.id.requestDeleteButton)
        val progressBar =view.findViewById<ProgressBar>(R.id.requestRecyclerProgress)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowRecyclerVH {
        if (requestsList[viewType].categoryName.equals("request")){
            val view =LayoutInflater.from(parent.context).inflate(R.layout.follow_requests_view,parent,false)
            return FollowRecyclerVH(view)
        }else if (requestsList[viewType].categoryName.equals("follow")){
            val view =LayoutInflater.from(parent.context).inflate(R.layout.notification_follow_view,parent,false)
            return FollowRecyclerVH(view)
        }else{
            val view =LayoutInflater.from(parent.context).inflate(R.layout.follow_requests_view,parent,false)
            return FollowRecyclerVH(view)
        }
    }

    override fun getItemCount(): Int {
        return if (requestsList.size<8){
            requestsList.size
        }else{
            8
        }
    }

    override fun onBindViewHolder(holder: FollowRecyclerVH, position: Int) {
        if (requestsList[position].categoryName.equals("request")){
            holder.authImage.downloadUrl(requestsList[position].imageUrl, placeHolder(holder.itemView.context))
            holder.name.setText(requestsList[position].name)
            holder.authName.setText(requestsList[position].authName)
            holder.successButton.setOnClickListener {
                requestSuccess(holder,requestsList[position])
            }
            holder.deleteButton.setOnClickListener {

            }
        }else if (requestsList[position].categoryName.equals("follow")){
            holder.followImage.downloadUrl(requestsList[position].imageUrl, placeHolder(holder.itemView.context))
            holder.followAuthName.setText("${requestsList[position].authName} seni takip etmeye başladı")
            holder.followButton.setOnClickListener {
                val dialogBuilder =AlertDialog.Builder(holder.itemView.context)
                dialogBuilder.setMessage("Kullanıcı takipten çıkmak istediğinize emin misiniz")
                dialogBuilder.setTitle("Takipten çık")
                dialogBuilder.show()
                val alert =dialogBuilder.create()
                dialogBuilder.setPositiveButton("Çıkar",DialogInterface.OnClickListener { dialog, which ->
                    unfollow(holder.itemView.context,requestsList[position])
                })
                dialogBuilder.setNegativeButton("İptal et",DialogInterface.OnClickListener { dialog, which ->
                    alert.cancel()
                })
            }
        }
    }

    fun unfollow(context: Context,list:followNotificationModel){
        val currentUser =database.collection("User Information").document(getAuth.email.toString())
        val user =database.collection("User Information").document(list.mail)
        currentUser.collection("followed").document(list.authName).delete().addOnSuccessListener {
            currentUser.get().addOnSuccessListener { current->
                val data =current.toObject(UserInformationModel::class.java)
                user.collection("follow").document(data!!.authName).delete().addOnSuccessListener {
                    Toast.makeText(context, "Takipten çıkıldı", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun updateData(newList:List<followNotificationModel>){
        val diffUtilCallback =CallbackRecycler(requestsList,newList)
        val diifResult=DiffUtil.calculateDiff(diffUtilCallback)
        diifResult.dispatchUpdatesTo(this)
        requestsList=newList
    }

    fun requestSuccess(holder:FollowRecyclerVH,users:followNotificationModel){
        holder.progressBar.visibility=View.VISIBLE
        holder.successButton.visibility=View.INVISIBLE
        val currentReference =database.collection("User Information").document(getAuth.email.toString())
        val user=database.collection("User Information").document(users.mail)
        user.collection("sendRequest").document(users.authName).delete().addOnSuccessListener {
            currentReference.collection("request").document(users.authName).update("bool",true).addOnSuccessListener {
                currentReference.collection("follow").document(users.authName).set(users).addOnSuccessListener {
                    currentReference.get().addOnSuccessListener {
                        if (it.exists()){
                            val currentUser =it.toObject(UserInformationModel::class.java)
                            val currentUserFollowed=followedModel(currentUser!!.mail,currentUser.name,currentUser.authName,currentUser.profilImage,Timestamp.now())
                            user.collection("followed").document(currentUser.authName).set(currentUserFollowed).addOnSuccessListener {
                                currentReference.collection("notification").document("${users.authName} request").get().addOnSuccessListener {
                                    if (it.exists()){
                                        val result =it.toObject(followNotificationModel::class.java)
                                        val item =followNotificationModel("follow",result!!.mail,result.imageUrl,result.authName,result.name)
                                        currentReference.collection("notification").document("${users.authName} request").delete().addOnSuccessListener {
                                            currentReference.collection("notification").document("${users.authName} follow").set(item).addOnSuccessListener {
                                                user.collection("sendRequest").document(currentUser.authName).delete().addOnSuccessListener {
                                                    followController(holder,users.authName)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

            }
        }
    }

    fun followController(holder:FollowRecyclerVH,authName:String){
        holder.requestFollowButtonLayout.visibility=View.VISIBLE
        holder.requestButtonLayout.visibility=View.GONE
        val currentReference =database.collection("User Information").document(getAuth.email.toString())
        currentReference.collection("followed").whereEqualTo("authName",authName).get().addOnSuccessListener {
            if (it.isEmpty){
                holder.requestFollowButton.setText("Sende onu takip et")
            }else{
                holder.requestFollowButton.setText("Takip")
                holder.requestFollowButton.setBackgroundResource(R.drawable.button_backgorund_gray_shape)
            }
            holder.progressBar.visibility=View.GONE
        }
    }

    fun sendNotification(){

    }
}