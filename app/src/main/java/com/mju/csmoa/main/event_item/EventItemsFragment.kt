package com.mju.csmoa.main.event_item

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mju.csmoa.databinding.FragmentEventItemsBinding
import com.mju.csmoa.main.event_item.filter.FilteringBottomSheetDialog

class EventItemsFragment : Fragment() {

    private var _binding: FragmentEventItemsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            FragmentEventItemsBinding.inflate(layoutInflater)
        init()
        return binding.root
    }

    private fun init() {
        val buttonSheetDialog = FilteringBottomSheetDialog(requireContext()).show()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}