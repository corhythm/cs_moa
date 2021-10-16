package com.mju.csmoa

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mju.csmoa.databinding.FragmentMoreBinding

class MoreFragment : Fragment() {

    private var _binding: FragmentMoreBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoreBinding.inflate(layoutInflater)
        init()
        return binding.root
    }


    private fun init() {
        // change profile
        binding.buttonMoreEditProfile.setOnClickListener { editProfile() }
        binding.relativeLayoutMoreImageContainer.setOnClickListener { editProfile() }
    }


    private fun editProfile() {
        val editProfileIntent = Intent(requireActivity(), EditProfileActivity::class.java)
        startActivity(editProfileIntent)
    }
}