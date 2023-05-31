package com.example.chatme.view

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.chatme.R
import com.example.chatme.databinding.FragmentProfilEditDetailsBinding
import com.example.chatme.viewmodel.ProfilEditDetailsViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfilEditDetailsFragment : Fragment() {

    @Inject
    lateinit var viewModel: ProfilEditDetailsViewModel
    @Inject
    lateinit var database: FirebaseFirestore
    @Inject
    lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentProfilEditDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfilEditDetailsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = (requireActivity() as AppCompatActivity)
        activity.supportActionBar!!.hide()
        arguments?.let {
            val incoming = it.getString("incoming")
            binding.profilDetailsToolbar.profilDetailsToolbarBackPage.setOnClickListener {
                requireActivity().onBackPressed()
            }
            binding.profilDetailsClearText.setOnClickListener {
                binding.profilDetailsTitleContent.setText("")
            }
            database.collection("User Information")
                .whereEqualTo("mail", auth.currentUser!!.email.toString()).get()
                .addOnSuccessListener { result->
                    if (result.isEmpty){
                        Toast.makeText(requireContext(), "Bir hata oluştu", Toast.LENGTH_SHORT).show()
                    }else{
                        val results =result.documents
                        val id =results[0].id
                        val authName=results[0].get("authName") as String
                        val gender =results[0].get("gender") as String
                        val name =results[0].get("name") as String
                        val biography=results[0].get("biography") as String
                        when (incoming) {
                            "name"->{
                                binding.profilDetailsTitleContent.setText(name)
                                binding.profilDetailsToolbar.profilDetailseToolbarSuccess.setOnClickListener {
                                    viewModel.nameUpdate(requireActivity(),id,binding.profilDetailsTitleContent.text.toString())
                                }
                                binding.profilDetailsTitleContent.addTextChangedListener {
                                    successControl(it.toString(),name)
                                }
                            }
                            "authName" -> {
                                binding.profilDetailsTitle.setText("Kullanıcı adı")
                                binding.profilDetailsToolbar.profilDetailseToolbarTitle.setText("Kullanıcı adı")
                                binding.profilDetailsTitleContent.setText(authName)
                                binding.profilDetailsToolbar.profilDetailseToolbarSuccess.setOnClickListener {
                                    viewModel.authNameUpdate(requireActivity(),id,binding.profilDetailsTitleContent.text.toString())
                                }
                                binding.profilDetailsTitleContent.addTextChangedListener {
                                   successControl(it.toString(),authName)
                                }
                            }
                            "gender" -> {
                                binding.profilDetailsTitle.setText("Bu, bilgi profilinde gözükmeyecek.")
                                binding.profilDetailsToolbar.profilDetailseToolbarTitle.setText("Cinsiyet")
                                binding.profilDetailsLayout.visibility = View.GONE
                                binding.genderRadioGroup.visibility = View.VISIBLE
                                binding.profilDetailsTitleContent.setText(authName)
                                if (gender.equals("Erkek")) binding.male.isChecked=true
                                else if (gender.equals("Kadın")) binding.female.isChecked=true
                                else if (gender.equals("none")) binding.none.isChecked=true
                                binding.genderRadioGroup.setOnCheckedChangeListener { radioGroup, i ->
                                    if (radioGroup.checkedRadioButtonId==binding.male.id){
                                        viewModel.genderUpdate(requireActivity(),id,"Erkek")
                                    }else if (radioGroup.checkedRadioButtonId==binding.female.id){
                                        viewModel.genderUpdate(requireActivity(),id,"Kadın")
                                    }else if (radioGroup.checkedRadioButtonId==binding.none.id){
                                        viewModel.genderUpdate(requireActivity(),id,"none")
                                    }
                                }
                            }
                            "biography" -> {
                                binding.profilDetailsTitle.setText("Biyografi")
                                binding.profilDetailsToolbar.profilDetailseToolbarTitle.setText("Biyografi")
                                binding.profilDetailsTitleContent.setText(biography)
                                binding.profilDetailsTitleContent.addTextChangedListener {
                                    successControl(it.toString(),biography)
                                }
                                binding.profilDetailsToolbar.profilDetailseToolbarSuccess.setOnClickListener {
                                    viewModel.biographyUpdate(requireActivity(),id,binding.profilDetailsTitleContent.text.toString())
                                }
                            }
                            else -> {

                            }
                        }
                    }
                }
        }
    }

    fun successControl(newItems:String,oldItems:String){
        if (newItems.equals(oldItems)){
            binding.profilDetailsToolbar.profilDetailseToolbarSuccess.setTextColor(resources.getColor(R.color.gray))
            binding.profilDetailsToolbar.profilDetailseToolbarSuccess.isClickable=false
        }else{
            binding.profilDetailsToolbar.profilDetailseToolbarSuccess.setTextColor(resources.getColor(R.color.buttonColor))
            binding.profilDetailsToolbar.profilDetailseToolbarSuccess.isClickable=true
        }
    }
}