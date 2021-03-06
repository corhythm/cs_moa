package com.mju.csmoa.home.review

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.mju.csmoa.R
import com.mju.csmoa.common.SelectMenuDialog
import com.mju.csmoa.databinding.ActivityWriteReviewBinding
import com.mju.csmoa.home.review.adapter.WriteReviewPhotoAdapter
import com.mju.csmoa.home.review.adapter.WriteReviewPhotoAdapter.Companion.CAMERA
import com.mju.csmoa.home.review.adapter.WriteReviewPhotoAdapter.Companion.PHOTO
import com.mju.csmoa.home.review.domain.model.Photo
import com.mju.csmoa.retrofit.RetrofitManager
import com.mju.csmoa.common.util.Constants.TAG
import com.mju.csmoa.common.util.MyApplication
import com.swein.easypermissionmanager.EasyPermissionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import www.sanju.motiontoast.MotionToastStyle
import java.io.File
import java.util.*

class WriteReviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWriteReviewBinding
    private lateinit var writeReviewPhotoAdapter: WriteReviewPhotoAdapter
    private val reviewPhotos = mutableListOf<Photo>()
    private lateinit var requestPermissionsLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var takeCameraLauncher: ActivityResultLauncher<Uri>
    private lateinit var selectGalleryLauncher: ActivityResultLauncher<String>
    private var tempImageUri: Uri? = null
    private var tempImageAbsoluteFilePath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWriteReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {
        with(binding) {
            setSupportActionBar(toolbarWriteReviewToolbar) // ?????? ?????????
            toolbarWriteReviewToolbar.setNavigationOnClickListener { onBackPressed() } // ?????? ????????? ???????????? ??? -> ?????? ??????

            // category spinner
            spinnerWriteReviewCategory.setItem(
                resources.getStringArray(R.array.item_category_list).toMutableList()
                    .apply { add("??????") })

            // ????????? ????????? ????????? ??????
            constraintLayoutWriteReviewWhereCsBuy.setOnClickListener {
                SelectCsBrandDialog(
                    context = this@WriteReviewActivity,
                    theme = R.style.BottomSheetDialogTheme,
                    onCsClicked = { csBrand: String ->
                        textViewWriteReviewWhereCsBuy.text = csBrand
                    }
                ).show()
            }

            // ?????? ?????? ?????? -> ????????? ????????? ??????
            textViewWriteReviewComplete.setOnClickListener {
                if (isAllRequirementsMeet()) {
                    try {
                        binding.progressBarWriteReviewOnGoing.visibility = View.VISIBLE

                        lifecycleScope.launch(Dispatchers.IO) {
                            val accessToken =
                                MyApplication.instance.jwtTokenInfoProtoManager.getJwtTokenInfo()?.accessToken!!

                            val reviewMultipartImages =
                                mutableListOf<MultipartBody.Part>() // multiFile list
                            reviewPhotos.forEachIndexed { index, reviewPicture ->
                                if (index > 0) {
                                    Log.d(TAG, "index = $index, reviewPicture = $reviewPicture")
                                    val file = File(reviewPicture.absoluteFilePath!!)
                                    val requestImageFile: RequestBody =
                                        file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                                    reviewMultipartImages.add(
                                        MultipartBody.Part.createFormData(
                                            "reviewImages",
                                            file.name,
                                            requestImageFile
                                        )
                                    )
                                }
                            }

                            val title = binding.editTextWriteReviewTitle.text.toString()
                                .toRequestBody(MultipartBody.FORM)
                            val price = binding.editTextWriteReviewPrice.text.toString()
                                .toRequestBody(MultipartBody.FORM)
                            val rating = binding.ratingBarWriteReviewReviewRating.rating.toString()
                                .toRequestBody(MultipartBody.FORM)
                            val category =
                                binding.spinnerWriteReviewCategory.selectedItem.toString()
                                    .toRequestBody(MultipartBody.FORM)
                            val csBrand = binding.textViewWriteReviewWhereCsBuy.text.toString()
                                .toRequestBody(MultipartBody.FORM)
                            val content = binding.editTextWriteReviewContent.text.toString()
                                .toRequestBody(MultipartBody.FORM)

                            val response = RetrofitManager.retrofitService?.postReview(
                                accessToken = accessToken,
                                reviewImages = reviewMultipartImages,
                                title = title,
                                price = price,
                                rating = rating,
                                category = category,
                                csBrand = csBrand,
                                content = content
                            )

                            Log.d(TAG, "response = $response")
                            if (response?.isSuccess != null || response?.isSuccess == true) {
                                withContext(Dispatchers.Main) {
                                    binding.progressBarWriteReviewOnGoing.visibility =
                                        View.INVISIBLE
                                    MyApplication.makeToast(
                                        this@WriteReviewActivity,
                                        "??? ?????? ??????",
                                        "????????? ??????????????? ?????????????????????",
                                        MotionToastStyle.SUCCESS
                                    )

                                    val finishWriteReviewIntent = Intent().apply {
                                        putExtra("success", response.isSuccess)
                                        putExtra("reviewId", response.result?.reviewId)
                                    }
                                    setResult(RESULT_OK, finishWriteReviewIntent)
                                    finish()
                                }
                            }
                        }
                    } catch (ex: Exception) {
                        Log.d(TAG, "(while file transfer, exception): ${ex.printStackTrace()}")
                        MyApplication.makeToast(
                            this@WriteReviewActivity,
                            "??? ?????? ??????",
                            "?????? ?????? ??? ????????? ??????????????????",
                            MotionToastStyle.ERROR
                        )
                    }
                }
                //finish()
            }

            // ?????? ??????
            requestPermissionsLauncher =
                registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions: MutableMap<String, Boolean> ->
                    Log.d(TAG, "permissions = $permissions")
                }

            // ?????? ??????
            requestPermissionsLauncher.launch(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )

            // ????????? ??????: ?????? ????????? scopedStorage??? ?????? ???????????? ???????????? ??????????????? ?????? ?????????????????? ??? ??? ??????.
            takeCameraLauncher =
                registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
                    if (success) {
                        reviewPhotos.add(
                            Photo(
                                type = PHOTO,
                                date = Date().toString(),
                                pictureUri = tempImageUri,
                                absoluteFilePath = tempImageAbsoluteFilePath
                            )
                        )
                        // notifyItemInserted??? ??? ????????? ????????? ????????? ?????? ??? ????????? ????????????.
//                        writeReviewPictureAdapter.notifyItemInserted(reviewPhotos.size - 1)
                        writeReviewPhotoAdapter.notifyItemChanged(0)
                    }
                    tempImageAbsoluteFilePath = null
                }

            // ????????? ??????
            selectGalleryLauncher =
                registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
                    uri?.let {
                        val absoluteFilePath = getFilePathFromUri(uri)
                        if (absoluteFilePath != null) {
                            reviewPhotos.add(
                                Photo(
                                    type = PHOTO,
                                    date = Date().toString(),
                                    pictureUri = uri,
                                    absoluteFilePath = absoluteFilePath
                                )
                            )
                            // notifyItemInserted??? ??? ????????? ????????? ????????? ?????? ??? ????????? ????????????.
//                        writeReviewPictureAdapter.notifyItemInserted(reviewPhotos.size - 1)
                            writeReviewPhotoAdapter.notifyItemChanged(0)
                            return@registerForActivityResult
                        }
                        MyApplication.makeToast(
                            this@WriteReviewActivity,
                            "??? ?????? ??????",
                            "????????? ???????????? ????????? ??????????????????",
                            MotionToastStyle.ERROR
                        )
                    }
                }


            // ????????? ?????? ????????? ??? -> ???????????? ????????? ?????? ?????? ?????? ????????????
            val onCameraClicked: () -> Unit = {
                if (reviewPhotos.size >= 6) {
                    MyApplication.makeToast(
                        this@WriteReviewActivity,
                        "??? ?????? ??????",
                        "?????? ???????????? ?????? 5???????????? ????????? ??? ??? ?????????!",
                        MotionToastStyle.ERROR
                    )
                } else {
                    SelectMenuDialog(
                        context = this@WriteReviewActivity,
                        theme = R.style.BottomSheetDialogTheme,
                        title = "Review Photo",
                        firstButtonText = "Take Photo",
                        secondButtonText = "From Gallery",
                        lottieName = "photo.json",
                        onFirstButtonClicked = { // gallery
                            tempImageUri = FileProvider.getUriForFile(
                                this@WriteReviewActivity,
                                "com.mju.csmoa.provider",
                                createImageFile().also { file: File ->
                                    tempImageAbsoluteFilePath =
                                        file.absoluteFile.toString()
                                })
                            Log.d(TAG, "before launch: tempImageUri = $tempImageUri")
                            // ????????? + ???????????? ?????? ??????
                            takeCameraLauncher.launch(tempImageUri)

                        },
                        onSecondButtonClicked = {
                            // ???????????? ?????? ??????
                            selectGalleryLauncher.launch("image/*")
                        }
                    ).show()
                }
            }


            // ????????? ?????? ???????????? ??????????????? ????????????
            val onCancelClicked: (position: Int) -> Unit = { position ->
                reviewPhotos.removeAt(position)
                writeReviewPhotoAdapter.notifyItemChanged(0) // 0?????? ????????? ???????????? ????????? ????????? ???????????? ???.
                writeReviewPhotoAdapter.notifyItemRemoved(position) // ????????? ?????? ???????????? ???.
            }


            // ?????? ????????? ?????????
            reviewPhotos.add(
                Photo(
                    type = CAMERA,
                    date = Date().toString(),
                    pictureUri = null,
                    absoluteFilePath = null
                )
            )
            writeReviewPhotoAdapter = WriteReviewPhotoAdapter(
                reviewPhotos = reviewPhotos,
                onCameraClicked = onCameraClicked,
                onCancelClicked = onCancelClicked
            )
            // ??????????????????
            binding.recyclerViewWriteReviewReviewImages.apply {
                adapter = writeReviewPhotoAdapter
                layoutManager = LinearLayoutManager(
                    this@WriteReviewActivity,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
            }
        }
    }

    private fun checkPermissions() {
        // ?????? ?????? ?????? ????????? ????????????
        val easyPermissionManager = EasyPermissionManager(this)
        easyPermissionManager.requestPermission(
            "permission",
            "permission are necessary",
            "setting",
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        )
    }

    private fun isAllRequirementsMeet(): Boolean {
        // ?????? ??? ??? ??????
        if (reviewPhotos.size <= 1) {
            MyApplication.makeToast(this, "??? ?????? ??????", "1??? ????????? ????????? ??????????????????.", MotionToastStyle.ERROR)
            return false
        }
        // title
        if (binding.editTextWriteReviewTitle.text.isEmpty()) {
            MyApplication.makeToast(this, "??? ?????? ??????", "????????? ????????? ????????? ??????????????????.", MotionToastStyle.ERROR)
            return false
        }
        // price
        if (binding.editTextWriteReviewPrice.text.isEmpty()) {
            MyApplication.makeToast(this, "??? ?????? ??????", "????????? ????????? ????????? ??????????????????.", MotionToastStyle.ERROR)
            return false
        }
        // ?????? ????????????
        if (binding.spinnerWriteReviewCategory.selectedItem == null) {
            MyApplication.makeToast(this, "??? ?????? ??????", "????????? ??????????????? ??????????????????.", MotionToastStyle.ERROR)
            return false
        }
        // ????????? ?????????
        if (binding.textViewWriteReviewWhereCsBuy.text.isEmpty()) {
            MyApplication.makeToast(
                this,
                "??? ?????? ??????",
                "????????? ???????????? ????????? ???????????? ??????????????????.",
                MotionToastStyle.ERROR
            )
            return false
        }
        // ??????
        if (binding.editTextWriteReviewContent.text.length < 10) {
            MyApplication.makeToast(
                this,
                "??? ?????? ??????",
                "?????? ?????? ???????????? 10??? ???????????????.",
                MotionToastStyle.ERROR
            )
            return false
        }

        return true
    }

    private fun createImageFile(): File {
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("review_image", ".jpg", storageDir)
    }

    // ???????????? ???????????? ???????????? ????????? ??? ??????.
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
//            val idColumn = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
//            val displayNameColumn =
//                cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
//            val bucketDisplayNameColumn =
//                cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
            val dataColumn = cursor?.getColumnIndexOrThrow("_data")

            if (cursor?.moveToFirst()!!) {
//                val id = cursor.getLong(idColumn!!)
//                val displayName = cursor.getString(displayNameColumn!!)
//                val bucketDisplayName = cursor.getString(bucketDisplayNameColumn!!)
                val data = cursor.getString(dataColumn!!)

                return data
            }
        }

        return null
    }

}