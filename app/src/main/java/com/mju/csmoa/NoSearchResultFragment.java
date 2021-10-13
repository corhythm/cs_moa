package com.mju.csmoa;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mju.csmoa.databinding.FragmentNoSearchResultBinding;
import com.mju.csmoa.util.MyApplication;

public class NoSearchResultFragment extends Fragment {

    private FragmentNoSearchResultBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentNoSearchResultBinding.inflate(inflater, container, false);
        init();
        return binding.getRoot();
    }

    private void init() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
