package com.mju.csmoa.common

import android.content.Context
import android.util.Log
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mju.csmoa.databinding.DialogSelectMenuBinding
import com.mju.csmoa.common.util.Constants.TAG
import java.io.IOException

class SelectMenuDialog// (dirty code) check if lottie file exists.
    (
    context: Context,
    theme: Int,
    title: String?,
    firstButtonText: String?,
    secondButtonText: String,
    lottieName: String?,
    onFirstButtonClicked: () -> Unit,
    onSecondButtonClicked: () -> Unit
) : BottomSheetDialog(context, theme) {

    private val binding: DialogSelectMenuBinding =
        DialogSelectMenuBinding.inflate(layoutInflater)

    init {
        with(binding) {
            setContentView(root)
            textViewDialogSelectMenuTitle.text = title
            buttonDialogSelectMenuFirstButton.text = firstButtonText
            buttonDialogSelectMenuSecondButton.text = secondButtonText

            buttonDialogSelectMenuFirstButton.setOnClickListener {
                Log.d(TAG, "SelectMenuDialog button1-() called")
                onFirstButtonClicked.invoke()
                dismiss()
            }

            buttonDialogSelectMenuSecondButton.setOnClickListener {
                Log.d(TAG, "SelectMenuDialog button2-() called")
                onSecondButtonClicked.invoke()
                dismiss()
            }

            // (dirty code) check if lottie file exists.
            try {
                if (context.assets.list("")!!.contains(lottieName)) {
                    binding.lottieAnimationViewDialogSelectMenuLottie.setAnimation(lottieName)
                } else {
                    binding.lottieAnimationViewDialogSelectMenuLottie.setAnimation("question_mark.json")
                }

            } catch (exception: IOException) {
                Log.d(
                    TAG,
                    "SelectMenuDialog -() called (exception) / ${exception.printStackTrace()}"
                )
            }
        }
    }
}