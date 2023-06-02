package com.example.chatme.view

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
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SearchFragment : Fragment() {

    @Inject lateinit var repository:repo
    @Inject lateinit var viewModel: SearchViewModel
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
        var adapter =SearchRecyclerAdapter(arrayListOf())
        binding.searchRecycler.adapter=adapter
        binding.searchRecycler.layoutManager=LinearLayoutManager(requireContext())
        repository.authList.observe(viewLifecycleOwner){ list->
            binding.searcEdittext.addTextChangedListener {
                val adapterList=ArrayList<UserInformationModel>()
                for (item in list){
                    if (item.authName.contains(it.toString())){
                        adapterList.add(item)
                    }
                }
                adapter =SearchRecyclerAdapter(adapterList)
                binding.searchRecycler.adapter=adapter
            }
        }
    }
}