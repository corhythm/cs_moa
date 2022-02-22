package com.mju.csmoa.common

import android.content.Context
import android.util.Log
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mju.csmoa.databinding.DialogEitherAOrBBinding
import java.io.IOException

class EitherAOrBDialog// (dirty code) check if lottie file exists.
    (
    context: Context,
    theme: Int,
    title: String?,
    message: String?,
    lottieName: String?,
    buttonAText: String?,
    buttonBText: String?,
    onButtonAClicked: () -> Unit,
    ouButtonBClicked: () -> Unit
) : BottomSheetDialog(context, theme) {

    private val binding: DialogEitherAOrBBinding =
        DialogEitherAOrBBinding.inflate(layoutInflater)

    init {
        with(binding) {
            setContentView(root)
            textViewDialogEitherAOrBTitle.text = title
            textViewDialogEitherAOrBMessage.text = message
            buttonDialogEitherAOrBButtonA.text = buttonAText
            buttonDialogEitherAOrBButtonB.text = buttonBText

            buttonDialogEitherAOrBButtonA.setOnClickListener {
                onButtonAClicked()
                dismiss()
            }
            buttonDialogEitherAOrBButtonB.setOnClickListener {
                ouButtonBClicked()
                dismiss()
            }

            try {
                if (context.assets.list("")!!.contains(lottieName)) {
                    binding.lottieAnimationViewDialogEitherAOrBLottie.setAnimation(lottieName)
                } else {
                    binding.lottieAnimationViewDialogEitherAOrBLottie.setAnimation("question_mark.json")
                }

            } catch (exception: IOException) {
                Log.d("로그", "CustomBottomSheetDialog: " + exception.message)
            }
        }
    }

}

