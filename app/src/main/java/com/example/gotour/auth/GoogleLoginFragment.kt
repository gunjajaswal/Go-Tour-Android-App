package com.example.gotour.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.gotour.R
import com.example.gotour.databinding.FragmentLoginBinding


class GoogleLoginFragment : Fragment() {


    var _binding:FragmentLoginBinding? = null
    private val binding get() = _binding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_google_login, container, false)
    }

    companion object {

    }
}