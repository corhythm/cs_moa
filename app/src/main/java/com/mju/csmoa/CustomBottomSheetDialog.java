package com.mju.csmoa;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.mju.csmoa.databinding.LayoutBottomSheetBinding;

import java.io.IOException;
import java.util.Arrays;

import lombok.Builder;

public class CustomBottomSheetDialog extends BottomSheetDialog {

    private LayoutBottomSheetBinding binding;

    public CustomBottomSheetDialog(@NonNull Context context) {
        super(context);
    }

    public CustomBottomSheetDialog(@NonNull Context context, int theme) {
        super(context, theme);
    }

    protected CustomBottomSheetDialog(@NonNull Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Builder
    public CustomBottomSheetDialog(@NonNull Context context, int theme, String title,
                                   String message, String lottieName, DialogButtonDelegate dialogButtonDelegate) {
        super(context, theme);
        binding = LayoutBottomSheetBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.textViewLottieAnimationViewTitle.setText(title);
        binding.textViewLayoutBottomSheetMessage.setText(message);

        // (dirty code) check if lottie file exists.
        try {
            if (Arrays.asList(context.getAssets().list("")).contains(lottieName)) {
                binding.lottieAnimationViewLottieAnimationViewLottie.setAnimation(lottieName);
            } else {
                binding.lottieAnimationViewLottieAnimationViewLottie.setAnimation("question_mark.json");
            }
        } catch(java.io.IOException exception) {
            Log.d("로그", "CustomBottomSheetDialog: " + exception.getMessage());
        }
    }

    // dirty code
    public void setOnClickListener(DialogButtonDelegate dialogButtonDelegate) {
        binding.buttonLayoutBottomSheetYes.setOnClickListener(v -> dialogButtonDelegate.setOnYesClickedListener(this));
        binding.buttonLayoutBottomSheetNo.setOnClickListener(v -> dialogButtonDelegate.setOnNoClickedListener(this));
    }
}

interface DialogButtonDelegate {
    void setOnYesClickedListener(DialogInterface dialogInterface);
    void setOnNoClickedListener(DialogInterface dialogInterface);
}

