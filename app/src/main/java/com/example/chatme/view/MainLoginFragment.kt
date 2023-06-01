package com.example.chatme.view


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.chatme.MainActivity
import com.example.chatme.databinding.FragmentMainLoginBinding
import com.example.chatme.viewmodel.MainLoginViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainLoginFragment : Fragment() {

    @Inject
    lateinit var viewModel: MainLoginViewModel
    @Inject lateinit var auth :FirebaseAuth
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
        viewModel.progress.observe(viewLifecycleOwner){
            if (it){
                binding.mainLoginProgress.visibility=View.VISIBLE
                binding.mainScrollView.alpha=0.3F
                binding.mainScrollView.isClickable=false
            }else{
                binding.mainLoginProgress.visibility=View.GONE
                binding.mainScrollView.alpha=1F
                binding.mainScrollView.isClickable=true
            }
        }
        if (auth.currentUser!=null){
            val intent = Intent(requireContext(),MainActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
        binding.loginButton.setOnClickListener {
            if (binding.loginUserName.text?.isNotEmpty()==true && binding.loginPassword.text?.isNotEmpty()==true){
                viewModel.loginUser(requireActivity(),binding.loginUserName.text.toString(), binding.loginPassword.text.toString())
            }else{
                Toast.makeText(requireContext(), "Kullanıcı adı ve şifre giriniz", Toast.LENGTH_SHORT).show()
            }

        }
    }
}