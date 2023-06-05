package com.example.chatme.view

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatme.adapter.SearchRecyclerAdapter
import com.example.chatme.databinding.FragmentSearchBinding
import com.example.chatme.model.UserInformationModel
import com.example.chatme.model.followedModel
import com.example.chatme.repository.repo
import com.example.chatme.viewmodel.SearchViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SearchFragment : Fragment() {

    @Inject lateinit var repository:repo
    @Inject lateinit var viewModel: SearchViewModel
    @Inject lateinit var getAuth: FirebaseUser
    private lateinit var binding:FragmentSearchBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentSearchBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        repository.getAllUser()
        val adapter =SearchRecyclerAdapter(arrayListOf(),getAuth)
        binding.searchRecycler.adapter=adapter
        binding.searchRecycler.layoutManager=LinearLayoutManager(requireContext())
        repository.authList.observe(viewLifecycleOwner){ list->
            binding.searcEdittext.addTextChangedListener {
                val adapterList=ArrayList<UserInformationModel>()
                for (item in list){
                    if (item.authName.toUpperCase().contains(it.toString().toUpperCase())){
                        adapterList.add(item)
                    }
                }
                adapter.updateData(adapterList)
            }
        }
    }


}