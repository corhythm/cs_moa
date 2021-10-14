package com.mju.csmoa

import android.content.Context
import com.google.android.material.bottomsheet.BottomSheetDialog
import android.content.DialogInterface
import android.util.Log
import com.mju.csmoa.DialogButtonDelegate
import java.util.Arrays
import java.io.IOException
import android.view.View
import com.mju.csmoa.databinding.DialogBottomSheetBinding
import lombok.Builder

class CustomBottomSheetDialog : BottomSheetDialog {
    private var binding: DialogBottomSheetBinding? = null

    constructor(context: Context) : super(context) {}
    constructor(context: Context, theme: Int) : super(context, theme) {}
    protected constructor(
        context: Context,
        cancelable: Boolean,
        cancelListener: DialogInterface.OnCancelListener?
    ) : super(context, cancelable, cancelListener) {
    }

    @Builder
    constructor(
        context: Context, theme: Int, title: String?,
        message: String?, lottieName: String?, dialogButtonDelegate: DialogButtonDelegate?
    ) : super(context, theme) {
        binding = DialogBottomSheetBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        binding!!.textViewLottieAnimationViewTitle.text = title
        binding!!.textViewLayoutBottomSheetMessage.text = message

        // (dirty code) check if lottie file exists.
        try {
            if (Arrays.asList(*context.assets.list("")).contains(lottieName)) {
                binding!!.lottieAnimationViewLottieAnimationViewLottie.setAnimation(lottieName)
            } else {
                binding!!.lottieAnimationViewLottieAnimationViewLottie.setAnimation("question_mark.json")
            }
        } catch (exception: IOException) {
            Log.d("로그", "CustomBottomSheetDialog: " + exception.message)
        }
    }

    // dirty code
    fun setOnClickListener(dialogButtonDelegate: DialogButtonDelegate) {
        binding!!.buttonLayoutBottomSheetYes.setOnClickListener { v: View? ->
            dialogButtonDelegate.setOnYesClickedListener(
                this
            )
        }
        binding!!.buttonLayoutBottomSheetNo.setOnClickListener { v: View? ->
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