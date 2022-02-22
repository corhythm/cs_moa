package com.mju.csmoa.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mju.csmoa.databinding.FragmentNoSearchResultBinding

class NoSearchResultFragment(val searchWord: String? = null) : Fragment() {

    private var _binding: FragmentNoSearchResultBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNoSearchResultBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        binding.textViewNoSearchResultNoSearchLabel.text =
            if (searchWord == null) "검색 결과가 없습니다" else "'${searchWord}'에 대한 검색 결과가 없습니다."
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
