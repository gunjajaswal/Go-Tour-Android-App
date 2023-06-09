package com.example.gotour.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Message
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import com.example.gotour.MainActivity
import com.example.gotour.R
import com.example.gotour.databinding.FragmentPhoneLoginBinding
import com.google.android.material.internal.ViewUtils.hideKeyboard
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit


class PhoneLoginFragment : Fragment() {

    private var _binding:FragmentPhoneLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private var storeverificationId:String?=""
    private lateinit var resendToken:PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks:PhoneAuthProvider.OnVerificationStateChangedCallbacks

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        auth.setLanguageCode("en")

    }

    override fun onResume() {
        super.onResume()
        if(auth.currentUser!=null){
            startActivity(Intent(activity, MainActivity::class.java))
            activity?.finish()
        }
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       _binding = FragmentPhoneLoginBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
            override fun onVerificationCompleted(credential: PhoneAuthCredential){
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                if (e is FirebaseAuthInvalidCredentialsException) {
                    Snackbar.make(binding.root, "Invalid Phone Number", Snackbar.LENGTH_SHORT).show()
                } else if (e is FirebaseTooManyRequestsException){
                    Snackbar.make(binding.root,"Quota Exceeded", Snackbar.LENGTH_SHORT).show()
                }
                }
            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken)
            {
                storeverificationId = verificationId
                resendToken = token
                showOtpLayout()
            }

        }
        binding.btnGetOtp.setOnClickListener { getOtp() }
    }

    private fun getOtp() {
        hideKeyboard()
        val  phoneNumber = binding.editPhone.text.toString()
        val option=PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(option)
    }

    private fun hideKeyboard() {
        val imm=activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken,0)
    }

    private fun showOtpLayout() {
        binding.editOtpWrapper.visibility = View.VISIBLE
        binding.btnVerifyOtp.visibility = View.VISIBLE
        binding.btnGetOtp.visibility = View.GONE
        binding.editPhone.isEnabled = false

        binding.btnVerifyOtp.setOnClickListener { verifyOtp() }


    }

    private fun verifyOtp() {
        val code = binding.editOtp.text.toString()
        if (code.isEmpty()){
            binding.editOtp.error="Please provide OTP"
            return
        }
        val credential =PhoneAuthProvider.getCredential(storeverificationId!!,code)
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()){ task ->
                if (task.isSuccessful){
                    val user = task.result?.user
                    updateUI("Success",user)
                }
                else
                {
                    if (task.exception is FirebaseAuthInvalidCredentialsException){

                        // The verification code entered was invalid
                    }
                    updateUI(task.exception?.message?:"some error occurred",null)

                }

            }

    }

    private fun updateUI(message: String?, user: FirebaseUser?) {
        if (user != null){
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

    private fun enableButtons() {
        binding.btnGetOtp.isEnabled = true
    }

    companion object {

    }
}