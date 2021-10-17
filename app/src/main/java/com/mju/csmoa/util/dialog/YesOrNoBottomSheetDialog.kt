package com.mju.csmoa

import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mju.csmoa.databinding.DialogYesOrNoBottomSheetBinding
import java.io.IOException

class YesOrNoBottomSheetDialog : BottomSheetDialog {
    private var binding: DialogYesOrNoBottomSheetBinding = DialogYesOrNoBottomSheetBinding.inflate(layoutInflater)

    constructor(context: Context) : super(context) {}
    constructor(context: Context, theme: Int) : super(context, theme) {}
    constructor(
        context: Context,
        cancelable: Boolean,
        cancelListener: DialogInterface.OnCancelListener?
    ) : super(context, cancelable, cancelListener) {
    }

    constructor(
        context: Context, theme: Int, title: String?,
        message: String?, lottieName: String?
    ) : super(context, theme) {
        setContentView(binding.root)
        binding.textViewLottieAnimationViewTitle.text = title
        binding.textViewLayoutBottomSheetMessage.text = message

        // (dirty code) check if lottie file exists.
        try {
            if (context.assets.list("")!!.contains(lottieName)) {
                binding.lottieAnimationViewLottieAnimationViewLottie.setAnimation(lottieName)
            } else {
                binding.lottieAnimationViewLottieAnimationViewLottie.setAnimation("question_mark.json")
            }

        } catch (exception: IOException) {
            Log.d("로그", "CustomBottomSheetDialog: " + exception.message)
        }
    }

    // dirty code
    fun setOnClickListener(dialogButtonDelegate: DialogButtonDelegate) {
        binding.buttonLayoutBottomSheetYes.setOnClickListener {
            dialogButtonDelegate.setOnYesClickedListener(
                this
            )
        }
        binding.buttonLayoutBottomSheetNo.setOnClickListener {
            dialogButtonDelegate.setOnNoClickedListener(
                this
            )
        }
    }
}

interface DialogButtonDelegate {
    fun setOnYesClickedListener(dialogInterface: DialogInterface?)
    fun setOnNoClickedListener(dialogInterface: DialogInterface?)
}