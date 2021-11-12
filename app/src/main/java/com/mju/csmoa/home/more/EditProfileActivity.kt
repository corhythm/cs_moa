package com.mju.csmoa.home.more

import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.mju.csmoa.DialogButtonDelegate
import com.mju.csmoa.R
import com.mju.csmoa.YesOrNoBottomSheetDialog
import com.mju.csmoa.databinding.ActivityEditProfileBinding
import com.mju.csmoa.home.more.model.UserInfo
import com.mju.csmoa.util.Constants.TAG
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle
import java.io.*
import java.util.*


class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private var originNickname: String? = null
    private lateinit var loadImageLauncher: ActivityResultLauncher<String>
    private val profileInfoViewModel: ProfileInfoViewModel by viewModels()
    private var file: File? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {

        // 파일에서 launcher 정의
        loadImageLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                uri?.let {
                    val absoluteFilePath = getFilePathFromUri(uri)

                    if (absoluteFilePath != null) {
                        binding.imageViewEditProfileProfileImg.setImageURI(uri)
                        file = File(absoluteFilePath) // 파일 절대 경로로 파일 만들기
                        profileInfoViewModel.isProfileImageChangedLiveData.value = true

                        return@registerForActivityResult
                    }

                    MotionToast.createColorToast(
                        this,
                        "이미지 가져오기",
                        "이미지 경로를 가져오는 데 실패했습니다. 갤러리로 접근해주세요",
                        MotionToastStyle.ERROR,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.SHORT_DURATION,
                        ResourcesCompat.getFont(this, R.font.helvetica_regular)
                    )
                }
            }

        // init viewModel
        val profileInfoObserver = (Observer<Boolean> { isChanged ->
            binding.buttonEditProfileSave.isEnabled = isChanged

            if (isChanged) {
                binding.buttonEditProfileSave.backgroundTintList = ContextCompat
                    .getColorStateList(this@EditProfileActivity, R.color.enabled)
            } else {
                binding.buttonEditProfileSave.backgroundTintList = ContextCompat
                    .getColorStateList(this@EditProfileActivity, R.color.disabled)
            }
        })

        profileInfoViewModel.isProfileImageChangedLiveData.observe(this, profileInfoObserver)
        profileInfoViewModel.isNicknameChangedLiveData.observe(this, profileInfoObserver)

        // init toolbar
        with(binding) {
            includeCommonToolbar.toolbarCommonToolbarToolbar.title = "프로필 수정"
            includeCommonToolbar.toolbarCommonToolbarToolbar.setNavigationIcon(R.drawable.ic_all_back)
            setSupportActionBar(includeCommonToolbar.toolbarCommonToolbarToolbar)
            includeCommonToolbar.toolbarCommonToolbarToolbar.setNavigationOnClickListener { finish() }

            // userInfo가 있으면
            if (intent.hasExtra("userInfo")) {
                val userInfo: UserInfo? = intent.getParcelableExtra("userInfo")

                if (userInfo != null) {
                    textInputEditTextEditProfileNicknameInput.setText(userInfo.nickname)
                    originNickname = userInfo.nickname

                    Glide.with(this@EditProfileActivity).load(userInfo.userProfileImageUrl)
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .fallback(R.drawable.img_all_basic_profile)
                        .placeholder(R.drawable.img_all_basic_profile)
                        .error(R.drawable.img_all_basic_profile)
                        .into(imageViewEditProfileProfileImg)
                }
            }

            // 닉네임 저장
            buttonEditProfileSave.setOnClickListener { }

            // 이미지 변경하고 싶을 때
            imageViewEditProfileProfileImg.setOnClickListener {
                loadImageLauncher.launch("image/*")
            }
        }

        initNicknameEditText()
    }


    private fun getFilePathFromUri(uri: Uri): String? {

        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.WIDTH,
            MediaStore.Images.Media.HEIGHT,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.ImageColumns.DATA // deprecated 됐지만 현재는 이것 말고는 절대 경로를 가져올 수 있는 방법이 없음
        )

        contentResolver.query(
            uri,
            projection,
            null,
            null,
            "${MediaStore.Images.Media.DISPLAY_NAME} ASC"
        ).use { cursor ->
            val idColumn = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val displayNameColumn =
                cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val bucketDisplayNameColumn =
                cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
            val dataColumn = cursor?.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA)

            if (cursor?.moveToFirst()!!) {
                val id = cursor.getLong(idColumn!!)
                val displayName = cursor.getString(displayNameColumn!!)
                val bucketDisplayName = cursor.getString(bucketDisplayNameColumn!!)
                val data = cursor.getString(dataColumn!!)
                Log.d(
                    TAG,
                    "EditProfileActivity -getFilePathFromUri() called / id = $id, displayName = $displayName, bucketDisplayName = $bucketDisplayName , data = $data"
                )

                return data
            }
        }

        return null
    }


    private fun initNicknameEditText() {

        with(binding) {
            // origin nickname (이건 나중에 서버에서 받아온 값으로 바꿔야 해)
            originNickname = textInputEditTextEditProfileNicknameInput.text.toString()

            // nickname validation
            textInputEditTextEditProfileNicknameInput.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                    val nicknameInputEditText = textInputEditTextEditProfileNicknameInput
                    val nicknameInputLayout = textInputLayoutEditProfileNicknameInputLayout

                    // 길이 제한 체크
                    if (nicknameInputEditText.text!!.length > 20) {
                        nicknameInputLayout.error = "닉네임은 20자 내외로 해주세요."
                        profileInfoViewModel.isNicknameChangedLiveData.value = false
                        return
                    }

                    // 띄어쓰기, 한글, 영문, 숫자 체크
                    val regex = Regex("^[가-힣a-zA-Z0-9]+\$")
                    if (!nicknameInputEditText.text.toString().matches(regex)) {
                        nicknameInputLayout.error = "닉네임은 띄어쓰기 없이 한글, 영문, 숫자만 가능해요"
                        profileInfoViewModel.isNicknameChangedLiveData.value = false
                        return
                    }

                    // 원래 닉네임이면
                    if (originNickname.equals(nicknameInputEditText.text.toString())) {
                        profileInfoViewModel.isNicknameChangedLiveData.value = false
                        nicknameInputLayout.error = null
                        return
                    }

                    // 모든 조건 만족하면
                    nicknameInputLayout.error = null
                    profileInfoViewModel.isNicknameChangedLiveData.value = true
                }

                override fun afterTextChanged(s: Editable?) {}
            })
        }
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

class ProfileInfoViewModel : ViewModel() {
    val isNicknameChangedLiveData = MutableLiveData<Boolean>().apply { value = false }
    val isProfileImageChangedLiveData = MutableLiveData<Boolean>().apply { value = false }
}


