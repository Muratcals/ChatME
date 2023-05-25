package com.example.chatme.view


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.chatme.databinding.FragmentMainLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainLoginFragment : Fragment() {

    @Inject
    lateinit var viewModel: MainLoginViewModel

    private lateinit var binding: FragmentMainLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentMainLoginBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.loginButton.setOnClickListener {
            if (binding.loginUserName.text?.isNotEmpty()==true && binding.loginPassword.text?.isNotEmpty()==true){
                viewModel.loginUser(requireActivity(),binding.loginUserName.text.toString(), binding.loginPassword.text.toString())
            }else{
                Toast.makeText(requireContext(), "Kullanıcı adı ve şifre giriniz", Toast.LENGTH_SHORT).show()
            }

        }
    }
}