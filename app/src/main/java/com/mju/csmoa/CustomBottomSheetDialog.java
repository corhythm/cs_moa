package com.mju.csmoa;

import android.content.Context;
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
                                   String message, String lottieName) throws IOException {
        super(context, theme);
        binding = LayoutBottomSheetBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.textViewLottieAnimationViewTitle.setText(title);
        binding.textViewLayoutBottomSheetMessage.setText(message);

        // check if lottie file exists.
        if (Arrays.asList(context.getAssets().list("")).contains(lottieName)) {
            binding.layoutBottomSheetLottieAnimationViewLottie.setAnimation(lottieName);
        } else {
            binding.layoutBottomSheetLottieAnimationViewLottie.setAnimation("question_mark.json");
        }

    }

    public LayoutBottomSheetBinding getBinding() { return binding; }
}
