package com.example.gotour.auth

import android.content.Intent
import android.os.Bundle
import android.os.Message
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.gotour.MainActivity
import com.example.gotour.R
import com.example.gotour.databinding.FragmentSignUpBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth:FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth=Firebase.auth
    }

    override fun onResume() {
        super.onResume()
        if (auth.currentUser!=null){
            startActivity(Intent(activity,MainActivity::class.java))
            activity?.finish()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            signUpbtn.setOnClickListener { signupUser() }
        }
        binding.apply {
            phoneRegister.setOnClickListener {
                findNavController().navigate(R.id.action_signUpFragment_to_phoneLoginFragment3)
            }
        }
        binding.apply {
            googleRegister.setOnClickListener { findNavController().navigate(R.id.action_signUpFragment_to_googleLoginFragment) }
        }
        binding.apply {
textViewLogin.setOnClickListener { findNavController().navigate(R.id.action_signUpFragment_to_loginFragment) }
        }
    }


    private fun signupUser() {
        disableButton()
        binding.apply {
            val email=editEmail.text.toString()
            val password=editPassword.text.toString()
            val confirmpassword=editConfirmpassword.text.toString()

            if (email.isEmpty()){
                binding.editEmail.setError("Please provide email")
                return
            }

            if (password.isEmpty()){
                binding.editPassword.setError("Please provide password")
                return
            }

            if (confirmpassword.isEmpty()){
                binding.editConfirmpassword.setError("Please Enter Confirm Password")
                return
            }
            auth.createUserWithEmailAndPassword(email, password).addOnFailureListener {
                updateUI(it.message,null)
            }.addOnSuccessListener {
                updateUI("success",it.user)
            }
        }
    }

    private fun updateUI(message: String?, user: FirebaseUser?) {
        if (user != null)
        {
            startActivity(Intent(activity,MainActivity::class.java))
            activity?.finish()
        }
        else
        {
            message?.let {
                Snackbar.make(binding.root,it,Snackbar.LENGTH_LONG).show()
            }
            enableButtons()
        }


    }

    private fun disableButton() {
        binding.signUpbtn.isEnabled=false
    }

    private fun enableButtons(){
        binding.signUpbtn.isEnabled=true
    }

    companion object{

    }
}