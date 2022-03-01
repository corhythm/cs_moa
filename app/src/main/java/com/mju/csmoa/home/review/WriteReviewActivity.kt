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
            setSupportActionBar(toolbarWriteReviewToolbar) // 툴바 활성화
            toolbarWriteReviewToolbar.setNavigationOnClickListener { onBackPressed() } // 네비 아이콘 클릭했을 때 -> 뒤로 가기

            // category spinner
            spinnerWriteReviewCategory.setItem(
                resources.getStringArray(R.array.item_category_list).toMutableList()
                    .apply { add("기타") })

            // 구매한 편의점 브랜드 클릭
            constraintLayoutWriteReviewWhereCsBuy.setOnClickListener {
                SelectCsBrandDialog(
                    context = this@WriteReviewActivity,
                    theme = R.style.BottomSheetDialogTheme,
                    onCsClicked = { csBrand: String ->
                        textViewWriteReviewWhereCsBuy.text = csBrand
                    }
                ).show()
            }

            // 제품 리뷰 완료 -> 서버로 데이터 전송
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
                                        "새 리뷰 작성",
                                        "리뷰가 정상적으로 등록되었습니다",
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
                            "새 리뷰 작성",
                            "리뷰 저장 중 오류가 발생했습니다",
                            MotionToastStyle.ERROR
                        )
                    }
                }
                //finish()
            }

            // 권한 요청
            requestPermissionsLauncher =
                registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions: MutableMap<String, Boolean> ->
                    Log.d(TAG, "permissions = $permissions")
                }

            // 권한 요청
            requestPermissionsLauncher.launch(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )

            // 카메라 런처: 해당 파일은 scopedStorage인 내부 저장소에 저장소에 저장되므로 일반 갤러리에서는 볼 수 없다.
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
                        // notifyItemInserted를 안 해줘도 반영이 되는데 이건 왜 그런지 모르겠다.
//                        writeReviewPictureAdapter.notifyItemInserted(reviewPhotos.size - 1)
                        writeReviewPhotoAdapter.notifyItemChanged(0)
                    }
                    tempImageAbsoluteFilePath = null
                }

            // 갤러리 런처
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
                            // notifyItemInserted를 안 해줘도 반영이 되는데 이건 왜 그런지 모르겠다.
//                        writeReviewPictureAdapter.notifyItemInserted(reviewPhotos.size - 1)
                            writeReviewPhotoAdapter.notifyItemChanged(0)
                            return@registerForActivityResult
                        }
                        MyApplication.makeToast(
                            this@WriteReviewActivity,
                            "새 리뷰 작성",
                            "사진은 갤러리를 통해서 접근해주세요",
                            MotionToastStyle.ERROR
                        )
                    }
                }


            // 카메라 버튼 눌렸을 때 -> 갤러리나 카메라 가서 리뷰 사진 가져오기
            val onCameraClicked: () -> Unit = {
                if (reviewPhotos.size >= 6) {
                    MyApplication.makeToast(
                        this@WriteReviewActivity,
                        "새 리뷰 작성",
                        "리뷰 이미지는 최대 5장까지만 업로드 할 수 있어요!",
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
                            // 카메라 + 저장공간 권한 확인
                            takeCameraLauncher.launch(tempImageUri)

                        },
                        onSecondButtonClicked = {
                            // 저장공간 권한 확인
                            selectGalleryLauncher.launch("image/*")
                        }
                    ).show()
                }
            }


            // 추가된 사진 삭제하고 어댑터에게 알려주기
            val onCancelClicked: (position: Int) -> Unit = { position ->
                reviewPhotos.removeAt(position)
                writeReviewPhotoAdapter.notifyItemChanged(0) // 0번은 개수를 표시하기 때문에 무조건 알려줘야 함.
                writeReviewPhotoAdapter.notifyItemRemoved(position) // 삭제된 거도 알려줘야 함.
            }


            // 처음 카메라 이미지
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
            // 리사이클러뷰
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
        // 권한 확인 하고 없으면 실행하자
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
        // 사진 한 장 이상
        if (reviewPhotos.size <= 1) {
            MyApplication.makeToast(this, "새 리뷰 작성", "1장 이상의 사진을 추가해주세요.", MotionToastStyle.ERROR)
            return false
        }
        // title
        if (binding.editTextWriteReviewTitle.text.isEmpty()) {
            MyApplication.makeToast(this, "새 리뷰 작성", "리뷰할 제품의 제목을 입력해주세요.", MotionToastStyle.ERROR)
            return false
        }
        // price
        if (binding.editTextWriteReviewPrice.text.isEmpty()) {
            MyApplication.makeToast(this, "새 리뷰 작성", "리뷰할 제품의 가격을 입력해주세요.", MotionToastStyle.ERROR)
            return false
        }
        // 제품 카테고리
        if (binding.spinnerWriteReviewCategory.selectedItem == null) {
            MyApplication.makeToast(this, "새 리뷰 작성", "제품의 카테고리를 선택해주세요.", MotionToastStyle.ERROR)
            return false
        }
        // 편의점 브랜드
        if (binding.textViewWriteReviewWhereCsBuy.text.isEmpty()) {
            MyApplication.makeToast(
                this,
                "새 리뷰 작성",
                "제품을 구매하신 편의점 브랜드를 선택해주세요.",
                MotionToastStyle.ERROR
            )
            return false
        }
        // 내용
        if (binding.editTextWriteReviewContent.text.length < 10) {
            MyApplication.makeToast(
                this,
                "새 리뷰 작성",
                "최소 입력 문자수는 10자 이상입니다.",
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

    // 갤러리로 접근해야 절대경로 받아올 수 있음.
    private fun getFilePathFromUri(uri: Uri): String? {

        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.WIDTH,
            MediaStore.Images.Media.HEIGHT,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            "_data" // deprecated 됐지만 현재는 이것 말고는 절대 경로를 가져올 수 있는 방법이 없음
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