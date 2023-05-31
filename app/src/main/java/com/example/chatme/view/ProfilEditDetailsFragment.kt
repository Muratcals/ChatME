package com.example.chatme.view

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.chatme.R
import com.example.chatme.databinding.FragmentProfilEditDetailsBinding
import com.example.chatme.viewmodel.ProfilEditDetailsViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfilEditDetailsFragment : Fragment() {

    @Inject
    lateinit var viewModel: ProfilEditDetailsViewModel
    private lateinit var binding:FragmentProfilEditDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentProfilEditDetailsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity =(requireActivity() as AppCompatActivity)
        activity.supportActionBar!!.hide()
        arguments?.let {
            val incoming =it.getString("incoming")
            when(incoming){
                "authName"->{
                    binding.profilDetailsTitle.setText("Kullanıcı Adı")
                }
            }
        }
    }
}