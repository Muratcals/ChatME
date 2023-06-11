package com.example.chatme.adapter

import android.app.AlertDialog
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.chatme.R
import com.example.chatme.databinding.FollowRequestsViewBinding
import com.example.chatme.databinding.NotificationCommentViewBinding
import com.example.chatme.databinding.NotificationFollowViewBinding
import com.example.chatme.databinding.NotificationLikeButtonBinding
import com.example.chatme.model.NatificationModel.CommentsModel
import com.example.chatme.model.NatificationModel.FollowModel
import com.example.chatme.model.NatificationModel.LikeModel
import com.example.chatme.model.NatificationModel.RequestModel
import com.example.chatme.model.UserInformationModel
import com.example.chatme.model.followNotificationModel
import com.example.chatme.model.followedModel
import com.example.chatme.util.CallbackRecycler
import com.example.chatme.util.utils.downloadUrl
import com.example.chatme.util.utils.placeHolder
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class FollowRquestRecyclerAdapter(var requestsList:List<Any>,val database: FirebaseFirestore,val getAuth: FirebaseUser):RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val VIEW_TYPE_FOLLOW = 0
    private val VIEW_TYPE_REQUEST = 1
    private val VIEW_TYPE_COMMENT = 2
    private val VIEW_TYPE_LIKE = 3

    class requestRecyclerVH(val binding: FollowRequestsViewBinding) :RecyclerView.ViewHolder(binding.root){

    }

    class followRecyclerVH(val binding: NotificationFollowViewBinding) :RecyclerView.ViewHolder(binding.root){

    }

    class commentRecyclerView(val binding :NotificationCommentViewBinding):RecyclerView.ViewHolder(binding.root){

    }
    class likeRecyclerView(val binding :NotificationLikeButtonBinding):RecyclerView.ViewHolder(binding.root){

    }

    override fun getItemViewType(position: Int): Int {
        val item = requestsList[position]
        when(item){
            is  RequestModel->{
                return VIEW_TYPE_REQUEST
            }
            is FollowModel->{
                return  VIEW_TYPE_FOLLOW
            }
            is CommentsModel ->{
                return VIEW_TYPE_COMMENT
            }
            is LikeModel ->{
                return VIEW_TYPE_LIKE
            }
            }
        return 5
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view =LayoutInflater.from(parent.context)
         if (viewType==VIEW_TYPE_REQUEST){
            val binding=FollowRequestsViewBinding.inflate(view)
            return requestRecyclerVH(binding)
        }else if (viewType==VIEW_TYPE_FOLLOW){
           val binding =NotificationFollowViewBinding.inflate(view)
            return followRecyclerVH(binding)
        }else if (viewType==VIEW_TYPE_COMMENT){
            val binding=NotificationCommentViewBinding.inflate(view)
             return commentRecyclerView(binding)
         }else if (viewType==VIEW_TYPE_LIKE){
             val binding=NotificationLikeButtonBinding.inflate(view)
             return likeRecyclerView(binding)
         }
        return followRecyclerVH(NotificationFollowViewBinding.inflate(view))
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
            val list =requestsList[position] as RequestModel
            if (list.authImage.isEmpty()){
                newHolder.binding.requestImage.setImageResource(R.drawable.profil_icon)
            }else{
                newHolder.binding.requestImage.downloadUrl(list.authImage, placeHolder(newHolder.binding.root.context))
            }
            newHolder.binding.requestName.setText(list.name)
            newHolder.binding.requestAuthName.setText(list.authName)
            newHolder.binding.requestSuccessButton.setOnClickListener {
                requestSuccess(holder,list)
            }
            holder.binding.requestDeleteButton.setOnClickListener {

            }
        }
        else if (holder.itemViewType==VIEW_TYPE_FOLLOW){
            val newHolder =holder as followRecyclerVH
            val lists =requestsList[position] as FollowModel
            val currentReference =database.collection("User Information").document(getAuth.email.toString())
            if (lists.authImage.isEmpty()){
                newHolder.binding.followImage.setImageResource(R.drawable.profil_icon)
            }else{
                newHolder.binding.followImage.downloadUrl(lists.authImage, placeHolder(newHolder.binding.root.context))
            }
            newHolder.binding.followAuthName.setText("${lists.authName} seni takip etmeye başladı")
            currentReference.collection("followed").whereEqualTo("authName",lists.authName).get().addOnSuccessListener {
                if (it.isEmpty){
                    currentReference.collection("sendRequest").whereEqualTo("authName",lists.authName).get().addOnSuccessListener {
                        if (!it.isEmpty){
                            newHolder.binding.followButton.setText("Bekleniyor")
                            newHolder.binding.followButton.setBackgroundResource(R.drawable.button_backgorund_gray_shape)
                        }else{
                            newHolder.binding.followButton.setText("Takip et")
                            newHolder.binding.followButton.setBackgroundResource(R.drawable.button_background_shape)
                        }
                    }
                }else{
                    newHolder.binding.followButton.setText("Takip")
                    newHolder.binding.followButton.setBackgroundResource(R.drawable.button_backgorund_gray_shape)
                }
            }
            newHolder.binding.followButton.setOnClickListener {
                newHolder.binding.followButton.visibility=View.INVISIBLE
                newHolder.binding.notificationFollowProgressBar.visibility=View.VISIBLE
                if (newHolder.binding.followButton.text.equals("Takip")){
                    val dialogBuilder =AlertDialog.Builder(newHolder.binding.root.context)
                    dialogBuilder.setMessage("Kullanıcı takipten çıkmak istediğinize emin misiniz")
                    dialogBuilder.setTitle("Takipten çık")
                    val alert =dialogBuilder.create()
                    dialogBuilder.setPositiveButton("Çıkar",DialogInterface.OnClickListener { dialog, which ->
                        unfollow(newHolder,lists)
                        newHolder.binding.followButton.setBackgroundResource(R.drawable.button_backgorund_gray_shape)
                        newHolder.binding.followButton.setText("Takip Et")
                    })
                    dialogBuilder.setNegativeButton("İptal et",DialogInterface.OnClickListener { dialog, which ->
                        alert.cancel()
                    })
                    dialogBuilder.show()
                }else if (newHolder.binding.followButton.text.equals("Takip et")){
                    currentReference.get().addOnSuccessListener {
                        if (it.exists()){
                            val currentUsers=it.toObject(UserInformationModel::class.java)
                            authFollow(newHolder,lists,currentUsers!!)
                            newHolder.binding.followButton.setBackgroundResource(R.drawable.button_backgorund_gray_shape)
                            newHolder.binding.followButton.setText("Bekleniyor")
                        }
                    }
                }else if (newHolder.binding.followButton.text.equals("Bekleniyor")){
                    currentReference.get().addOnSuccessListener {
                        if (it.exists()){
                            val currentUsers=it.toObject(UserInformationModel::class.java)
                            requestDeleteFollow(newHolder,lists,currentUsers!!)
                            newHolder.binding.followButton.setBackgroundResource(R.drawable.button_background_shape)
                            newHolder.binding.followButton.setText("Takip Et")
                        }
                    }
                }
            }
        }
        else if (holder.itemViewType==VIEW_TYPE_COMMENT){
            val newHolder =holder as commentRecyclerView
            val list =requestsList[position] as CommentsModel
            newHolder.binding.notificationCommentAuthName.setText("${list.authName} fotoğrafına yorum yaptı:${list.commentText}")
            database.collection("Posts").document(list.postId).get().addOnSuccessListener {
                if (it.exists()){
                    newHolder.binding.notificationCommentAuthImage.downloadUrl(list.authImage,
                    placeHolder(holder.itemView.context))
                    newHolder.binding.notificationCommentImage.downloadUrl(it.get("imageUrl") as String,
                        placeHolder(holder.itemView.context))
                }
            }
            newHolder.binding.notificationCommentLayout.setOnClickListener {
                val bundle = bundleOf("postId" to list.postId)
                holder.itemView.findNavController().navigate(R.id.action_notificationFragment_to_postDetailsFragmentFragment2,bundle)
            }
        }
        else if (holder.itemViewType==VIEW_TYPE_LIKE){
            val newHolder =holder as likeRecyclerView
            val list =requestsList[position] as LikeModel
            newHolder.binding.notificationLikeAuthName.setText("${list.authName} fotoğrafını beğendi")
            database.collection("Posts").document(list.postId).get().addOnSuccessListener {
                if (it.exists()){
                    newHolder.binding.notificationLikeAuthImage.downloadUrl(list.authImage,
                        placeHolder(holder.itemView.context))
                    newHolder.binding.notificationLikeImage.downloadUrl(it.get("imageUrl") as String,
                        placeHolder(holder.itemView.context))
                }
            }
            newHolder.binding.notificationLikeLayout.setOnClickListener {
                val bundle = bundleOf("postId" to list.postId)
                holder.itemView.findNavController().navigate(R.id.action_notificationFragment_to_postCommentFragment22,bundle)
            }
        }
    }

    fun authFollow(
        holder: followRecyclerVH,
        users: FollowModel,
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
                            users.authImage,
                            Timestamp.now()
                        )
                        currentUserReference.collection("sendRequest").document(users.authName)
                            .set(followAuth).addOnSuccessListener {
                                Toast.makeText(holder.itemView.context, "İstek gönderildi.", Toast.LENGTH_SHORT)
                                    .show()
                                holder.binding.followButton.visibility=View.VISIBLE
                                holder.binding.notificationFollowProgressBar.visibility=View.INVISIBLE
                            }
                    }
            }
    }

    fun unfollow(holder: followRecyclerVH, list:FollowModel){
        val currentUser =database.collection("User Information").document(getAuth.email.toString())
        val user =database.collection("User Information").document(list.mail)
        currentUser.collection("followed").document(list.authName).delete().addOnSuccessListener {
            currentUser.get().addOnSuccessListener { current->
                val data =current.toObject(UserInformationModel::class.java)
                user.collection("follow").document(data!!.authName).delete().addOnSuccessListener {
                    user.collection("notification").document("${data.authName} follow").delete().addOnSuccessListener {
                        Toast.makeText(holder.itemView.context, "Takipten çıkıldı", Toast.LENGTH_SHORT).show()
                        holder.binding.followButton.visibility=View.VISIBLE
                        holder.binding.notificationFollowProgressBar.visibility=View.INVISIBLE
                    }
                }
            }
        }
    }

    fun updateData(newList:List<Any>){
        val diffUtilCallback =CallbackRecycler(requestsList,newList)
        val diifResult=DiffUtil.calculateDiff(diffUtilCallback)
        diifResult.dispatchUpdatesTo(this)
        requestsList=newList
    }

    fun requestSuccess(holder:requestRecyclerVH, users:RequestModel){
        holder.binding.requestRecyclerProgress.visibility=View.VISIBLE
        holder.binding.requestSuccessButton.visibility=View.INVISIBLE
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
                                        val result =it.toObject(RequestModel::class.java)
                                        val item =FollowModel(result!!.authName,result.name,result.authImage,"follow",result.mail)
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
        holder.binding.requestFollowButtonLayout.visibility=View.VISIBLE
        holder.binding.requestButtonLayout.visibility=View.GONE
        val currentReference =database.collection("User Information").document(getAuth.email.toString())
        currentReference.collection("followed").whereEqualTo("authName",authName).get().addOnSuccessListener {
            if (it.isEmpty){
                holder.binding.requestFollowButton.setText("Sende onu takip et")
            }else{
                holder.binding.requestFollowButton.setText("Takip")
                holder.binding.requestFollowButton.setBackgroundResource(R.drawable.button_backgorund_gray_shape)
            }
            holder.binding.requestRecyclerProgress.visibility=View.GONE
        }
    }

    fun requestDeleteFollow(holder: followRecyclerVH, users: FollowModel, currentUsers: UserInformationModel) {
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
                                holder.binding.followButton.visibility=View.VISIBLE
                                holder.binding.notificationFollowProgressBar.visibility=View.INVISIBLE
                            }
                    }
            }
    }
}