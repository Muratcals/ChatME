package com.example.chatme.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.chatme.databinding.FragmentRegisterBinding
import com.example.chatme.model.UserInformationModel
import com.example.chatme.viewmodel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID
import javax.inject.Inject

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    @Inject
    lateinit var viewModel: RegisterViewModel

    private lateinit var binding: FragmentRegisterBinding
    var gender = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.progress.observe(viewLifecycleOwner) {
            if (it) {
                binding.registerProgress.visibility = View.VISIBLE
                binding.scrollView2.alpha = 0.3F
                binding.scrollView2.isClickable = false
            } else {
                binding.registerProgress.visibility = View.GONE
                binding.scrollView2.alpha = 1F
                binding.scrollView2.isClickable = true
            }
        }
        binding.createUserButton.setOnClickListener {
            if (binding.registerGenderMale.isChecked) {
                gender = binding.registerGenderMale.text.toString()
            } else {
                gender = binding.registerGenderFemale.text.toString()
            }
            if (binding.registerMail.text?.isEmpty() == true && binding.registerPassword.text?.isEmpty() == true &&
                binding.registerName.text?.isEmpty() == true && binding.registerUserName.text?.isEmpty() == true &&
                (!binding.registerGenderFemale.isChecked || !binding.registerGenderMale.isChecked)
            ) {
                Toast.makeText(requireContext(), "Bütün bilgilieri doldur", Toast.LENGTH_SHORT)
                    .show()
            } else {
                val uuid = UUID.randomUUID().toString()
                val user = UserInformationModel(
                    uuid,
                    binding.registerName.text.toString(),
                    binding.registerMail.text.toString(),
                    binding.registerUserName.text.toString(),
                    gender,
                    biography = "",
                    "",
                    arrayListOf(),
                    arrayListOf()
                )
                viewModel.createUser(
                    requireActivity(),
                    user,
                    binding.registerPassword.text.toString()
                )
            }
        }

    }
}