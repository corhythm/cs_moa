package com.mju.csmoa.home.more

import android.content.Intent
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
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.mju.csmoa.R
import com.mju.csmoa.common.EitherAOrBDialog
import com.mju.csmoa.databinding.ActivityEditProfileBinding
import com.mju.csmoa.home.more.model.UserInfo
import com.mju.csmoa.retrofit.RetrofitManager
import com.mju.csmoa.common.util.Constants.TAG
import com.mju.csmoa.common.util.MyApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle
import java.io.*

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private var originNickname: String? = null
    private lateinit var loadImageLauncher: ActivityResultLauncher<String>
    private val profileInfoViewModel: ProfileInfoViewModel by viewModels()
    private var profileImageFile: File? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {

        // ???????????? launcher ??????
        loadImageLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                uri?.let {
                    val absoluteFilePath = getFilePathFromUri(uri)

                    if (absoluteFilePath != null) {
                        Log.d(TAG, "absoluteFilePath = $absoluteFilePath")

                        Glide.with(this@EditProfileActivity).load(uri)
                            .skipMemoryCache(true)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .fallback(R.drawable.img_all_basic_profile) // load??? url??? null??? ?????? ??? ???????????? ??? ????????? ?????????
                            .placeholder(R.drawable.img_all_basic_profile) // ????????? ?????? ??? ????????? ?????????
                            .error(R.drawable.img_all_basic_profile) // ???????????? ??????????????? ????????? ???????????? ??? ???????????? ?????????
                            .into(binding.imageViewEditProfileProfileImg)

                        profileImageFile = File(absoluteFilePath) // ?????? ?????? ????????? ?????? ?????????
                        profileInfoViewModel.isProfileImageChangedLiveData.value = true

                        return@registerForActivityResult
                    }

                    MotionToast.createColorToast(
                        this,
                        "????????? ????????????",
                        "????????? ????????? ???????????? ??? ??????????????????. ???????????? ??????????????????",
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
            includeCommonToolbar.toolbarCommonToolbarToolbar.title = "????????? ??????"
            includeCommonToolbar.toolbarCommonToolbarToolbar.setNavigationIcon(R.drawable.ic_all_back)
            setSupportActionBar(includeCommonToolbar.toolbarCommonToolbarToolbar)
            includeCommonToolbar.toolbarCommonToolbarToolbar.setNavigationOnClickListener { finish() }

            // userInfo??? ?????????
            if (intent.hasExtra("userInfo")) {
                val userInfo: UserInfo? = intent.getParcelableExtra("userInfo")
                Log.d(TAG, "EditProfileActivity userInfo is not null, userInfo = $userInfo")

                if (userInfo != null) {
                    textInputEditTextEditProfileNicknameInput.setText(userInfo.nickname)
                    originNickname = userInfo.nickname

                    Glide.with(this@EditProfileActivity).load(userInfo.userProfileImageUrl)
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .fallback(R.drawable.img_all_basic_profile) // load??? url??? null??? ?????? ??? ???????????? ??? ????????? ?????????
                        .placeholder(R.drawable.img_all_basic_profile) // ????????? ?????? ??? ????????? ?????????
                        .error(R.drawable.img_all_basic_profile) // ???????????? ??????????????? ????????? ???????????? ??? ???????????? ?????????
                        .into(imageViewEditProfileProfileImg)
                }
            }

            // ????????? ??????
            buttonEditProfileSave.setOnClickListener {

                var multipartImageFile: MultipartBody.Part? = null
                var nicknameRequestBody: RequestBody? = null

                // ????????? ????????? ???????????????
                if (profileInfoViewModel.isProfileImageChangedLiveData.value == true) {
                    val requestImageFile: RequestBody =
                        profileImageFile!!.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    multipartImageFile = MultipartBody.Part.createFormData(
                        "profileImageFile",
                        profileImageFile!!.name,
                        requestImageFile
                    )
                }

//                var nicknameRequestBody:
                // ????????? ????????? ??????
                if (originNickname != binding.textInputEditTextEditProfileNicknameInput.text.toString()) {
                    nicknameRequestBody =
                        binding.textInputEditTextEditProfileNicknameInput.text.toString()
                            .toRequestBody(MultipartBody.FORM)
                }


                // ????????? ??????
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val jwtTokenInfo =
                            MyApplication.instance.jwtTokenInfoProtoManager.getJwtTokenInfo()

                        val patchUserInfoRes = RetrofitManager.retrofitService?.patchUserInfo(
                            accessToken = jwtTokenInfo!!.accessToken,
                            profileImageFile = multipartImageFile, nickname = nicknameRequestBody
                        )

                        if (patchUserInfoRes != null) {
                            Log.d(TAG, "code = ${patchUserInfoRes.code}")
                            when (patchUserInfoRes.code) {
                                100 -> { // ?????? ??????
                                    withContext(Dispatchers.Main) {
                                        makeToast(
                                            "????????? ?????? ?????? ??????",
                                            "????????? ?????? ????????? ??????????????????.",
                                            MotionToastStyle.SUCCESS
                                        )

                                        val updateUserProfileInfoIntent = Intent().apply {
                                            putExtra("patchUserInfoRes", patchUserInfoRes)
                                        }
                                        setResult(RESULT_OK, updateUserProfileInfoIntent)
                                        finish()
                                    }
                                }
                                210 -> withContext(Dispatchers.Main) {
                                    makeToast(
                                        "???????????? ?????????",
                                        "?????? ???????????? ??????????????????. :(",
                                        MotionToastStyle.ERROR
                                    )
                                }
                                215 -> withContext(Dispatchers.Main) {
                                    makeToast(
                                        "????????? ????????? ?????? ??????",
                                        "????????? ???????????? ???????????? ??? ??????????????????. :(",
                                        MotionToastStyle.ERROR
                                    )
                                }
                                else -> withContext(Dispatchers.Main) {
                                    makeToast(
                                        "????????? ?????? ?????? ??????",
                                        "????????? ?????? ????????? ??? ??? ????????? :(",
                                        MotionToastStyle.ERROR
                                    )
                                }
                            }
                        }
                    } catch (ex: Exception) {
                        Log.d(TAG, ex.message.toString())
                    }
                }
            }

            // ????????? ???????????? ?????? ???
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
            "_data" // deprecated ????????? ????????? ?????? ????????? ?????? ????????? ????????? ??? ?????? ????????? ??????
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
            val dataColumn = cursor?.getColumnIndexOrThrow("_data")

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
            // origin nickname (?????? ????????? ???????????? ????????? ????????? ????????? ???)
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

                    // ?????? ?????? ??????
                    if (nicknameInputEditText.text!!.length > 20) {
                        nicknameInputLayout.error = "???????????? 20??? ????????? ????????????."
                        profileInfoViewModel.isNicknameChangedLiveData.value = false
                        return
                    }

                    // ????????????, ??????, ??????, ?????? ??????
                    val regex = Regex("^[???-???a-zA-Z0-9]+\$")
                    if (!nicknameInputEditText.text.toString().matches(regex)) {
                        nicknameInputLayout.error = "???????????? ???????????? ?????? ??????, ??????, ????????? ????????????"
                        profileInfoViewModel.isNicknameChangedLiveData.value = false
                        return
                    }

                    // ?????? ???????????????
                    if (originNickname.equals(nicknameInputEditText.text.toString())) {
                        profileInfoViewModel.isNicknameChangedLiveData.value = false
                        nicknameInputLayout.error = null
                        return
                    }

                    // ?????? ?????? ????????????
                    nicknameInputLayout.error = null
                    profileInfoViewModel.isNicknameChangedLiveData.value = true
                }

                override fun afterTextChanged(s: Editable?) {}
            })
        }
    }

    override fun onBackPressed() {
        EitherAOrBDialog(
            context = this,
            theme = R.style.BottomSheetDialogTheme,
            lottieName = "man_question2.json",
            title = "Exit?",
            message = "???????????? ????????? ???????????? ??????????\n????????? ???????????? ????????? ????????? ???????????? ????????? :(",
            buttonAText = "No",
            buttonBText = "Yes",
            onButtonAClicked = { },
            ouButtonBClicked = { super@EditProfileActivity.onBackPressed() }
        ).show()
    }

    private fun makeToast(title: String, content: String, motionToastStyle: MotionToastStyle) {
        MotionToast.createColorToast(
            this,
            title,
            content,
            motionToastStyle,
            MotionToast.GRAVITY_BOTTOM,
            MotionToast.SHORT_DURATION,
            ResourcesCompat.getFont(this, R.font.helvetica_regular)
        )
    }
}

class ProfileInfoViewModel : ViewModel() {
    val isNicknameChangedLiveData = MutableLiveData<Boolean>().apply { value = false }
    val isProfileImageChangedLiveData = MutableLiveData<Boolean>().apply { value = false }
}




