package com.mju.csmoa.login

import android.content.Context
import android.util.Log
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mju.csmoa.common.util.Constants
import com.mju.csmoa.common.util.Constants.API_BASE_URL
import com.mju.csmoa.common.util.Constants.TAG
import com.mju.csmoa.common.util.MyApplication
import com.mju.csmoa.databinding.DialogSetServerAddressBinding
import com.mju.csmoa.retrofit.RetrofitClient
import www.sanju.motiontoast.MotionToastStyle

class SetServerAddressDialog(context: Context, theme: Int) : BottomSheetDialog(context, theme) {

    private val binding: DialogSetServerAddressBinding =
        DialogSetServerAddressBinding.inflate(layoutInflater)

    init {
        with(binding) {
            setContentView(root)
            textInputEditTextDialogSelectSetServerAddressAddressInput.setText(API_BASE_URL)

            buttonDialogSelectSetServerAddressSetServerAddress.setOnClickListener {
                val serverAddress =
                    textInputEditTextDialogSelectSetServerAddressAddressInput.text.toString()

                if (serverAddress.isNotBlank()) {
                    API_BASE_URL = serverAddress
                    if (RetrofitClient.updateRetrofitClient(API_BASE_URL, context)) { // retrofitClient 객체 교체를 성공하면
                        dismiss()
                    }
                } else {
                    MyApplication.makeToast(
                        activity = context as SignInActivity,
                        "Invalid Server Address",
                        "서버 주소를 입력해주세요",
                        MotionToastStyle.WARNING
                    )
                }
            }
        }
    }
}