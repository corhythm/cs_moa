package com.mju.csmoa.home.review

import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.mju.csmoa.R
import com.mju.csmoa.common.SelectMenuDialog
import com.mju.csmoa.databinding.ActivityWriteReviewBinding
import com.mju.csmoa.home.review.adapter.WriteReviewPictureAdapter
import com.mju.csmoa.home.review.adapter.WriteReviewPictureAdapter.Companion.CAMERA
import com.mju.csmoa.home.review.adapter.WriteReviewPictureAdapter.Companion.PICTURE
import com.mju.csmoa.home.review.domain.ReviewPicture
import com.mju.csmoa.util.Constants.TAG
import com.swein.easypermissionmanager.EasyPermissionManager
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle
import java.io.File
import java.util.*
import kotlin.math.log

class WriteReviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWriteReviewBinding
    private lateinit var writeReviewPictureAdapter: WriteReviewPictureAdapter
    private val reviewPictures = mutableListOf<ReviewPicture>()
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

            toolbarWriteReviewToolbar.setNavigationOnClickListener { onBackPressed() } // 네비 아이콘 클릭했을 때 -> 뒤로 가기

            // 제품 리뷰 완료 -> 서버로 데이터 전송
            textViewWriteReviewComplete.setOnClickListener {
                finish()
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
                        reviewPictures.add(
                            ReviewPicture(
                                type = PICTURE,
                                date = Date().toString(),
                                pictureUri = tempImageUri,
                                absoluteFilePath = tempImageAbsoluteFilePath
                            )
                        )
                        // notifyItemInserted를 안 해줘도 반영이 되는데 이건 왜 그런지 모르겠다.
//                        writeReviewPictureAdapter.notifyItemInserted(reviewPictures.size - 1)
                        writeReviewPictureAdapter.notifyItemChanged(0)
                    }
                    tempImageAbsoluteFilePath = null
                }

            // 갤러리 런처
            selectGalleryLauncher =
                registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
                    uri?.let {
                        Log.d(TAG, "WriteReviewActivity -init() called / uri = $uri")
                        reviewPictures.add(
                            ReviewPicture(
                                type = PICTURE,
                                date = Date().toString(),
                                pictureUri = uri,
                                absoluteFilePath = getFilePathFromUri(uri)
                            )
                        )
                        // notifyItemInserted를 안 해줘도 반영이 되는데 이건 왜 그런지 모르겠다.
//                        writeReviewPictureAdapter.notifyItemInserted(reviewPictures.size - 1)
                        writeReviewPictureAdapter.notifyItemChanged(0)
                    }
                }


            // 카메라 버튼 눌렸을 때 -> 갤러리나 카메라 가서 리뷰 사진 가져오기
            val onCameraClicked: () -> Unit = {
                if (reviewPictures.size >= 6) {
                    makeToast(
                        "리뷰 이미지 추가",
                        "리뷰 이미지는 최대 5장까지만 업로드 할 수 있어요!",
                        MotionToastStyle.ERROR
                    )
                } else {
                    val selectMenuDialog = SelectMenuDialog(
                        context = this@WriteReviewActivity,
                        theme = R.style.BottomSheetDialogTheme,
                        title = "Review Photo",
                        firstButtonText = "Take Photo",
                        secondButtonText = "Gallery",
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
                reviewPictures.removeAt(position)
                writeReviewPictureAdapter.notifyItemChanged(0) // 0번은 개수를 표시하기 때문에 무조건 알려줘야 함.
                writeReviewPictureAdapter.notifyItemRemoved(position) // 삭제된 거도 알려줘야 함.
            }


            // 처음 카메라 이미지
            reviewPictures.add(
                ReviewPicture(
                    type = CAMERA,
                    date = Date().toString(),
                    pictureUri = null,
                    absoluteFilePath = null
                )
            )
            writeReviewPictureAdapter = WriteReviewPictureAdapter(
                reviewPictures = reviewPictures,
                onCameraClicked = onCameraClicked,
                onCancelClicked = onCancelClicked
            )
            // 리사이클러뷰
            binding.recyclerViewWriteReviewReviewImages.apply {
                adapter = writeReviewPictureAdapter
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