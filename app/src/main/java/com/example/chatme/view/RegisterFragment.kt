package com.example.chatme.view

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.chatme.R
import com.example.chatme.databinding.FragmentRegisterBinding
import com.example.chatme.model.UserInformationModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    @Inject
    lateinit var viewModel: RegisterViewModel

    private lateinit var binding: FragmentRegisterBinding
    var gender =""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentRegisterBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.progress.observe(viewLifecycleOwner){
            if (it){
                binding.registerProgress.visibility=View.VISIBLE
            }else{
                binding.registerProgress.visibility=View.GONE
            }
        }
        binding.createUserButton.setOnClickListener {
            if (binding.registerGenderMale.isChecked){
                gender=binding.registerGenderMale.text.toString()
            }else{
                gender=binding.registerGenderFemale.text.toString()
            }
            if (binding.registerMail.text?.isEmpty()==true && binding.registerPassword.text?.isEmpty()==true &&
                binding.registerName.text?.isEmpty()==true && binding.registerUserName.text?.isEmpty()==true &&
                (!binding.registerGenderFemale.isChecked || !binding.registerGenderMale.isChecked)){
                Toast.makeText(requireContext(), "Bütün bilgilieri doldur", Toast.LENGTH_SHORT).show()
            }else{
                val user =UserInformationModel(binding.registerName.text.toString(),binding.registerMail.text.toString(),binding.registerUserName.text.toString(),gender)
                viewModel.createUser(requireActivity(),user,binding.registerPassword.text.toString())
            }
        }

    }
}