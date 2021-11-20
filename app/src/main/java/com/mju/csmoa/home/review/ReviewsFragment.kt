package com.mju.csmoa.home.review

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mju.csmoa.databinding.FragmentReviewsBinding

class ReviewsFragment : Fragment() {

    private var _binding: FragmentReviewsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReviewsBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {

        with(binding) {
            // 새 리뷰 작성
            cardViewReviewsWriteReview.setOnClickListener {
                startActivity(
                    Intent(
                        requireContext(),
                        WriteReviewActivity::class.java
                    )
                )
            }
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}