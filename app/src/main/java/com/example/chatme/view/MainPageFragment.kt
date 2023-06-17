package com.example.chatme.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.asFlow
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatme.adapter.PostRecyclerAdapter
import com.example.chatme.databinding.FragmentMainPageBinding
import com.example.chatme.model.UserInformationModel
import com.example.chatme.viewmodel.MainPageViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class MainPageFragment : Fragment() {

    @Inject
    lateinit var viewModel: MainPageViewModel
    private lateinit var binding: FragmentMainPageBinding
    private lateinit var adapter: PostRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainPageBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getPostList()
        view.let {
            viewModel.database.collection("User Information")
                .document(viewModel.getAuth.email.toString()).get().addOnSuccessListener {
                if (it.exists()) {
                    val userInformation = it.toObject(UserInformationModel::class.java)
                    adapter = PostRecyclerAdapter(
                        viewModel.database,
                        userInformation!!,
                        arrayListOf(),
                        viewModel.storage
                    )
                    binding.recyclerView.adapter = adapter
                    binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
                    viewModel.postList.observe(viewLifecycleOwner) { list ->
                        if (list.isNotEmpty()) {
                            adapter.updateData(list)
                        }
                    }
                }
            }
        }
        binding.mainSwiperefreshLayout.setOnRefreshListener {
            viewModel.getPostList()
            binding.mainSwiperefreshLayout.isRefreshing = false
        }
        viewModel.progress.observe(viewLifecycleOwner) {
            if (!it) {
                binding.mainPageProgress.visibility = View.INVISIBLE
                binding.recyclerView.visibility = View.VISIBLE
            }
        }
    }
}