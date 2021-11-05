package com.mju.csmoa.home.more

import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.mju.csmoa.DialogButtonDelegate
import com.mju.csmoa.R
import com.mju.csmoa.YesOrNoBottomSheetDialog
import com.mju.csmoa.databinding.ActivityEditProfileBinding

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private var originNickname: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {

        // init toolbar
        binding.includeCommonToolbar.toolbarCommonToolbarToolbar.title = "프로필 수정"
        binding.includeCommonToolbar.toolbarCommonToolbarToolbar.setNavigationIcon(R.drawable.ic_all_back)
        setSupportActionBar(binding.includeCommonToolbar.toolbarCommonToolbarToolbar)
        binding.includeCommonToolbar.toolbarCommonToolbarToolbar.setNavigationOnClickListener { finish() }

        // origin nickname (이건 나중에 서버에서 받아온 값으로 바꿔야 해)
        originNickname = binding.textInputEditTextEditProfileNicknameInput.text.toString()

        // nickname validation
        val nicknameTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                val nicknameInputEditText = binding.textInputEditTextEditProfileNicknameInput
                val nicknameInputLayout = binding.textInputLayoutEditProfileNicknameInputLayout

                // 길이 제한 체크
                if (nicknameInputEditText.text!!.length > 20) {
                    nicknameInputLayout.error = "닉네임은 20자 내외로 해주세요."
                    binding.buttonEditProfileSave.isEnabled = false
                    binding.buttonEditProfileSave.backgroundTintList = ContextCompat
                        .getColorStateList(this@EditProfileActivity, R.color.azure)
                    return
                }

                // TODO: 닉네임 중복 여부는 나중에 Debounce 사용해서 구현

                // 띄어쓰기, 한글, 영문, 숫자 체크
                val regex = Regex("^[가-힣ㄱ-ㅎa-zA-Z0-9]+\$")
                if (!nicknameInputEditText.text.toString().matches(regex)) {
                    nicknameInputLayout.error = "닉네임은 띄어쓰기 없이 한글, 영문, 숫자만 가능해요"
                    binding.buttonEditProfileSave.isEnabled = false
                    binding.buttonEditProfileSave.backgroundTintList = ContextCompat
                        .getColorStateList(this@EditProfileActivity, R.color.disabled)
                    return
                }

                // 원래 닉네임이 아니면
                if (originNickname.equals(nicknameInputEditText.text.toString())) {
                    binding.buttonEditProfileSave.isEnabled = false
                    binding.buttonEditProfileSave.backgroundTintList = ContextCompat
                        .getColorStateList(this@EditProfileActivity, R.color.disabled)
                    return
                }

                // 모든 조건 만족하면
                nicknameInputLayout.error = null
                binding.buttonEditProfileSave.isEnabled = true
                binding.buttonEditProfileSave.backgroundTintList = ContextCompat
                    .getColorStateList(this@EditProfileActivity, R.color.enabled)

            }

            override fun afterTextChanged(s: Editable?) {}
        }

        binding.textInputEditTextEditProfileNicknameInput.addTextChangedListener(nicknameTextWatcher)
    }

    override fun onBackPressed() {
        val bottomSheetDialog = YesOrNoBottomSheetDialog(
            context = this,
            theme = R.style.BottomSheetDialogTheme,
            lottieName = "man_question2.json",
            title = "Exit?",
            message = "회원가입을 중단하실 건가요?\n중간에 나가시면 수정한 정보는 저장되지 않아요 :("
        )

        // dirty code (builder에서 전달해서 처리하고 싶은데, super.onBackPressed()가 안 먹는다)
        bottomSheetDialog.setOnClickListener(object : DialogButtonDelegate {
            override fun setOnYesClickedListener(dialogInterface: DialogInterface?) {
                dialogInterface?.dismiss()
                super@EditProfileActivity.onBackPressed()
            }

            override fun setOnNoClickedListener(dialogInterface: DialogInterface?) {
                dialogInterface?.dismiss()
            }
        })

        bottomSheetDialog.show()
    }
}