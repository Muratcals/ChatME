package com.example.chatme.adapter

import android.app.AlertDialog
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

class FollowRquestRecyclerAdapter(var requestsList:List<followNotificationModel>,val database: FirebaseFirestore,val getAuth: FirebaseUser):RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val VIEW_TYPE_FOLLOW = 0
    private val VIEW_TYPE_REQUEST = 1

    class requestRecyclerVH(view:View) :RecyclerView.ViewHolder(view){

        val requestButtonLayout=view.findViewById<LinearLayout>(R.id.requestButtonLayout)
        val requestFollowButton=view.findViewById<Button>(R.id.requestFollowButton)
        val requestFollowButtonLayout=view.findViewById<LinearLayout>(R.id.requestFollowButtonLayout)
        val authName=view.findViewById<TextView>(R.id.requestAuthName)
        val name=view.findViewById<TextView>(R.id.requestName)
        val authImage =view.findViewById<CircleImageView>(R.id.requestImage)
        val successButton=view.findViewById<Button>(R.id.requestSuccessButton)
        val deleteButton=view.findViewById<Button>(R.id.requestDeleteButton)
        val progressBar =view.findViewById<ProgressBar>(R.id.requestRecyclerProgress)
    }

    class notificationRecyclerVH(view:View) :RecyclerView.ViewHolder(view){
        val followAuthName =view.findViewById<TextView>(R.id.followAuthName)
        val followImage =view.findViewById<CircleImageView>(R.id.followImage)
        val notificationFollowProgressBar=view.findViewById<ProgressBar>(R.id.notificationFollowProgressBar)
        val followButton =view.findViewById<Button>(R.id.followButton)
    }

    override fun getItemViewType(position: Int): Int {
        val item = requestsList[position]
        return if (item.categoryName.equals("follow")) {
            VIEW_TYPE_FOLLOW
        } else {
            VIEW_TYPE_REQUEST
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType==VIEW_TYPE_REQUEST){
            val view =LayoutInflater.from(parent.context).inflate(R.layout.follow_requests_view,parent,false)
            requestRecyclerVH(view)
        }else{
            val view =LayoutInflater.from(parent.context).inflate(R.layout.notification_follow_view,parent,false)
            notificationRecyclerVH(view)
        }
    }
    override fun getItemCount(): Int {
        return if (requestsList.size<8){
            requestsList.size
        }else{
            8
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType==VIEW_TYPE_REQUEST){
            val newHolder=holder as requestRecyclerVH
            if (requestsList[position].imageUrl.isEmpty()){
                newHolder.authImage.setImageResource(R.drawable.profil_icon)
            }else{
                newHolder.authImage.downloadUrl(requestsList[position].imageUrl, placeHolder(newHolder.itemView.context))
            }
            newHolder.name.setText(requestsList[position].name)
            newHolder.authName.setText(requestsList[position].authName)
            newHolder.successButton.setOnClickListener {
                requestSuccess(holder,requestsList[position])
            }
            holder.deleteButton.setOnClickListener {

            }
        }else{
            val newHolder =holder as notificationRecyclerVH
            val userReference =database.collection("User Information").document(requestsList[position].mail)
            val currentReference =database.collection("User Information").document(getAuth.email.toString())
            if (requestsList[position].imageUrl.isEmpty()){
                newHolder.followImage.setImageResource(R.drawable.profil_icon)
            }else{
                newHolder.followImage.downloadUrl(requestsList[position].imageUrl, placeHolder(newHolder.itemView.context))
            }
            newHolder.followAuthName.setText("${requestsList[position].authName} seni takip etmeye başladı")
            currentReference.collection("followed").whereEqualTo("authName",requestsList[position].authName).get().addOnSuccessListener {
                if (it.isEmpty){
                    currentReference.collection("sendRequest").whereEqualTo("authName",requestsList[position].authName).get().addOnSuccessListener {
                        if (!it.isEmpty){
                            newHolder.followButton.setText("Bekleniyor")
                            newHolder.followButton.setBackgroundResource(R.drawable.button_backgorund_gray_shape)
                        }else{
                            newHolder.followButton.setText("Takip et")
                            newHolder.followButton.setBackgroundResource(R.drawable.button_background_shape)
                        }
                    }
                }else{
                    newHolder.followButton.setText("Takip")
                    newHolder.followButton.setBackgroundResource(R.drawable.button_backgorund_gray_shape)
                }
            }
            newHolder.followButton.setOnClickListener {
                newHolder.followButton.visibility=View.INVISIBLE
                newHolder.notificationFollowProgressBar.visibility=View.VISIBLE
                if (newHolder.followButton.text.equals("Takip")){
                    val dialogBuilder =AlertDialog.Builder(newHolder.itemView.context)
                    dialogBuilder.setMessage("Kullanıcı takipten çıkmak istediğinize emin misiniz")
                    dialogBuilder.setTitle("Takipten çık")
                    val alert =dialogBuilder.create()
                    dialogBuilder.setPositiveButton("Çıkar",DialogInterface.OnClickListener { dialog, which ->
                        unfollow(newHolder,requestsList[position])
                        newHolder.followButton.setBackgroundResource(R.drawable.button_backgorund_gray_shape)
                        newHolder.followButton.setText("Takip Et")
                    })
                    dialogBuilder.setNegativeButton("İptal et",DialogInterface.OnClickListener { dialog, which ->
                        alert.cancel()
                    })
                    dialogBuilder.show()
                }else if (newHolder.followButton.text.equals("Takip et")){
                    currentReference.get().addOnSuccessListener {
                        if (it.exists()){
                            val currentUsers=it.toObject(UserInformationModel::class.java)
                            authFollow(newHolder,requestsList[position],currentUsers!!)
                            newHolder.followButton.setBackgroundResource(R.drawable.button_backgorund_gray_shape)
                            newHolder.followButton.setText("Bekleniyor")
                        }
                    }
                }else if (newHolder.followButton.text.equals("Bekleniyor")){
                    currentReference.get().addOnSuccessListener {
                        if (it.exists()){
                            val currentUsers=it.toObject(UserInformationModel::class.java)
                            requestDeleteFollow(newHolder,requestsList[position],currentUsers!!)
                            newHolder.followButton.setBackgroundResource(R.drawable.button_background_shape)
                            newHolder.followButton.setText("Takip Et")
                        }
                    }
                }
            }
        }
    }

    fun authFollow(
        holder: notificationRecyclerVH,
        users: followNotificationModel,
        currentUsers: UserInformationModel
    ) {
        val currentUser = followedModel(
            currentUsers.mail,
            currentUsers.authName,
            currentUsers.authName,
            currentUsers.profilImage,
            Timestamp.now(),
            bool = false
        )
        val userReference = database.collection("User Information").document(users.mail)
        val currentUserReference =
            database.collection("User Information").document(getAuth.email.toString())
        userReference.collection("request").document(currentUsers.authName).set(currentUser)
            .addOnSuccessListener {
                val notificationModel = followNotificationModel(
                    "request",
                    currentUser.mail,
                    currentUser.imageUrl,
                    currentUser.authName,
                    currentUsers.name,
                    state = false,
                    Timestamp.now()
                )
                userReference.collection("notification")
                    .document("${currentUser.authName} request").set(notificationModel)
                    .addOnSuccessListener {
                        val followAuth = followedModel(
                            users.mail,
                            users.name,
                            users.authName,
                            users.imageUrl,
                            Timestamp.now()
                        )
                        currentUserReference.collection("sendRequest").document(users.authName)
                            .set(followAuth).addOnSuccessListener {
                                Toast.makeText(holder.itemView.context, "İstek gönderildi.", Toast.LENGTH_SHORT)
                                    .show()
                                holder.followButton.visibility=View.VISIBLE
                                holder.notificationFollowProgressBar.visibility=View.INVISIBLE
                            }
                    }
            }
    }

    fun unfollow(holder: notificationRecyclerVH,list:followNotificationModel){
        val currentUser =database.collection("User Information").document(getAuth.email.toString())
        val user =database.collection("User Information").document(list.mail)
        currentUser.collection("followed").document(list.authName).delete().addOnSuccessListener {
            currentUser.get().addOnSuccessListener { current->
                val data =current.toObject(UserInformationModel::class.java)
                user.collection("follow").document(data!!.authName).delete().addOnSuccessListener {
                    Toast.makeText(holder.itemView.context, "Takipten çıkıldı", Toast.LENGTH_SHORT).show()
                    holder.followButton.visibility=View.VISIBLE
                    holder.notificationFollowProgressBar.visibility=View.INVISIBLE
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

    fun requestSuccess(holder:requestRecyclerVH, users:followNotificationModel){
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
                                        val item =followNotificationModel("follow",result!!.mail,result.imageUrl,result.authName,result.name, time = Timestamp.now())
                                        currentReference.collection("notification").document("${users.authName} follow").set(item).addOnSuccessListener {
                                            currentReference.collection("notification").document("${users.authName} request").delete().addOnSuccessListener {
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

    fun followController(holder:requestRecyclerVH, authName:String){
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

    fun requestDeleteFollow(holder: notificationRecyclerVH,users: followNotificationModel, currentUsers: UserInformationModel) {
        val userReference = database.collection("User Information").document(users.mail)
        val currentUserReference =
            database.collection("User Information").document(getAuth.email.toString())
        userReference.collection("request").document(currentUsers.authName).delete()
            .addOnSuccessListener {
                currentUserReference.collection("sendRequest").document(users.authName).delete()
                    .addOnSuccessListener {
                        userReference.collection("notification")
                            .document("${currentUsers.authName} request").delete()
                            .addOnSuccessListener {
                                holder.followButton.visibility=View.VISIBLE
                                holder.notificationFollowProgressBar.visibility=View.INVISIBLE
                            }
                    }
            }
    }
    fun sendNotification(){

    }
}