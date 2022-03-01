package com.mju.csmoa.home.recipe

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
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.GsonBuilder
import com.mju.csmoa.R
import com.mju.csmoa.common.SelectMenuDialog
import com.mju.csmoa.databinding.ActivityWriteRecipeBinding
import com.mju.csmoa.home.recipe.adapter.AddIngredientAdapter
import com.mju.csmoa.home.recipe.adapter.WriteRecipePhotoAdapter
import com.mju.csmoa.home.recipe.domain.model.Ingredient
import com.mju.csmoa.home.review.adapter.WriteReviewPhotoAdapter.Companion.CAMERA
import com.mju.csmoa.home.review.adapter.WriteReviewPhotoAdapter.Companion.PHOTO
import com.mju.csmoa.home.review.domain.model.Photo
import com.mju.csmoa.retrofit.RetrofitManager
import com.mju.csmoa.common.util.Constants.TAG
import com.mju.csmoa.common.util.MyApplication
import com.mju.csmoa.common.util.RecyclerViewDecoration
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
import java.io.File
import java.lang.Exception
import java.util.*

class WriteRecipeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWriteRecipeBinding
    private lateinit var takeCameraLauncher: ActivityResultLauncher<Uri>
    private lateinit var selectGalleryLauncher: ActivityResultLauncher<String>

    private var tempImageUri: Uri? = null
    private var tempImageAbsoluteFilePath: String? = null

    private val recipePhotos = mutableListOf<Photo>()
    private val ingredients = mutableListOf<Ingredient>()

    private lateinit var writeRecipePhotoAdapter: WriteRecipePhotoAdapter
    private lateinit var ingredientAdapter: AddIngredientAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWriteRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        initLauncher()
        initPhoto()
        initIngredient()
    }

    private fun init() {
        setSupportActionBar(binding.toolbarWriteRecipeToolbar)
        binding.toolbarWriteRecipeToolbar.setNavigationOnClickListener { onBackPressed() }

        // content edittext에 포커싱 되면 -> 스크롤 뷰 아래로 내림
//        binding.editTextWriteRecipeContent.setOnFocusChangeListener { v, hasFocus ->
//            if (hasFocus) {
//                Log.d(TAG, "hasFocus = $hasFocus")
//                binding.root.smoothScrollTo(0, binding.root.bottom)
//                binding.editTextWriteRecipeContent.requestFocus()
//            }
//        }

        // 레시피 입력 완료 -> 서버로 데이터 전송
        binding.textViewWriteRecipeComplete.setOnClickListener {
            Log.d(TAG, "recipePhotos = $recipePhotos")
            if (isAllRequirementMeet()) { // 입력사항 모두 입력하면
                binding.progressBarWriteRecipeOnGoing.visibility = View.VISIBLE
                lifecycleScope.launch(Dispatchers.IO) {

                    try {
                        val accessToken =
                            MyApplication.instance.jwtTokenInfoProtoManager.getJwtTokenInfo()?.accessToken!!

                        val multipartRecipeImages =
                            mutableListOf<MultipartBody.Part>() // multipart file list
                        recipePhotos.forEachIndexed { index, photo ->
                            if (index > 0) { // 제일 처음은 사진 추가 용도이므로 제외
                                val file = File(photo.absoluteFilePath!!)
                                val requestImageFile: RequestBody =
                                    file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                                multipartRecipeImages.add(
                                    MultipartBody.Part.createFormData(
                                        "recipeImages",
                                        file.name,
                                        requestImageFile
                                    )
                                )
                            }
                        }

                        val name = binding.editTextWriteRecipeTitle.text.toString()
                            .toRequestBody(MultipartBody.FORM)
                        val content = binding.editTextWriteRecipeContent.text.toString()
                            .toRequestBody(MultipartBody.FORM)
                        val serializedIngredients = GsonBuilder().create()
                            .toJson(ingredients.filterIndexed { index, _ -> index > 0 })
                            .toRequestBody(MultipartBody.FORM)


                        val response = RetrofitManager.retrofitService?.postRecipe(
                            accessToken = accessToken,
                            recipeImages = multipartRecipeImages,
                            name = name,
                            ingredients = serializedIngredients,
                            content = content
                        )

                        withContext(Dispatchers.Main) {
                            Log.d(TAG, "response = $response")
                            if (response?.isSuccess != null && response.isSuccess) {
                                binding.progressBarWriteRecipeOnGoing.visibility = View.INVISIBLE
                                makeToast("레시피를 정상적으로 등록 완료했습니다.", MotionToastStyle.SUCCESS)
                                val finishWriteRecipeIntent = Intent().apply {
                                    putExtra("postRecipeRes", response.result)
                                }
                                setResult(RESULT_OK, finishWriteRecipeIntent)
                                finish()

                            } else {
                                binding.progressBarWriteRecipeOnGoing.visibility = View.INVISIBLE
                                makeToast("레시피 등록을 실패했어요.", MotionToastStyle.ERROR)
                            }
                        }
                    } catch (ex: Exception) {
                        withContext(Dispatchers.Main) {
                            Log.d(
                                TAG,
                                "레시피 저장 중 오류: printStackTrace() = ${ex.printStackTrace()} / message = ${ex.message}"
                            )
                            makeToast("레시피 등록 중 오류가 발생했습니다", MotionToastStyle.ERROR)
                        }
                    }
                }

            }
        }
    }

    private fun initIngredient() {
        // dialog에서 재료 추가 완료 버튼 눌렀을 때
        val onCompleteClicked: (ingredientName: String, ingredientPrice: String, csBrand: String) -> Unit =
            { ingredientName, ingredientPrice, csBrand ->
                ingredients.add(Ingredient(ingredientName, ingredientPrice, csBrand))
                ingredientAdapter.notifyItemChanged(0)
            }

        // 재료 추가 버튼 눌렀을 때
        val onAddClicked: () -> Unit = {
            AddIngredientDialog(this, onCompleteClicked).show()
        }
        // 재료 삭제 버튼 눌렀을 때
        val onDeleteClicked: (position: Int) -> Unit = {
            ingredients.removeAt(it)
            ingredientAdapter.notifyItemChanged(it)
        }

        ingredients.add(Ingredient("", "", "")) // 리스트 맨 위 헤더
        ingredientAdapter = AddIngredientAdapter(ingredients, onAddClicked, onDeleteClicked)
        binding.recyclerViewWriteRecipeIngredients.apply {
            adapter = ingredientAdapter
            layoutManager =
                LinearLayoutManager(this@WriteRecipeActivity, LinearLayoutManager.VERTICAL, false)
        }

    }

    private fun initPhoto() {
        // 카메라 버튼 눌렸을 때 -> 갤러리나 카메라 가서 리뷰 사진 가져오기
        val onCameraClicked: () -> Unit = {
            if (recipePhotos.size >= 6) {
                makeToast(
                    "리뷰 이미지는 최대 5장까지만 업로드 할 수 있어요!",
                    MotionToastStyle.ERROR
                )
            } else {
                SelectMenuDialog(
                    context = this,
                    theme = R.style.BottomSheetDialogTheme,
                    title = "Recipe Photo",
                    firstButtonText = "Take Photo",
                    secondButtonText = "From Gallery",
                    lottieName = "photo2.json",
                    onFirstButtonClicked = { // gallery
                        tempImageUri = FileProvider.getUriForFile(
                            this,
                            "com.mju.csmoa.provider",
                            createImageFile().also { file: File ->
                                tempImageAbsoluteFilePath =
                                    file.absoluteFile.toString()
                            })
                        // 카메라 + 저장공간 권한 확인
                        takeCameraLauncher.launch(tempImageUri)
                    },
                    // 저장공간 권한 확인
                    onSecondButtonClicked = { selectGalleryLauncher.launch("image/*") }
                ).show()
            }
        }


        // 추가된 사진 삭제하고 어댑터에게 알려주기
        val onCancelClicked: (position: Int) -> Unit = { position ->
            recipePhotos.removeAt(position)
            writeRecipePhotoAdapter.notifyItemChanged(0) // 0번은 개수를 표시하기 때문에 알려줘야 함.
            writeRecipePhotoAdapter.notifyItemRemoved(position) // 삭제된 거도 알려줘야 함.
        }

        // 처음 카메라 이미지
        recipePhotos.add(
            Photo(
                type = CAMERA,
                date = Date().toString(),
                pictureUri = null,
                absoluteFilePath = null
            )
        )
        writeRecipePhotoAdapter = WriteRecipePhotoAdapter(
            recipePhotos = recipePhotos,
            onCameraClicked = onCameraClicked,
            onCancelClicked = onCancelClicked
        )
        // 리사이클러뷰
        binding.recyclerViewWriteRecipeReviewImages.apply {
            adapter = writeRecipePhotoAdapter
            addItemDecoration(RecyclerViewDecoration(0, 0, 7, 7))
            layoutManager = LinearLayoutManager(
                this@WriteRecipeActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        }
    }

    private fun isAllRequirementMeet(): Boolean {
        // 사진 한 장 이상
        if (recipePhotos.size <= 1) {
            makeToast("1장 이상의 사진을 추가해주세요.", MotionToastStyle.ERROR)
            return false
        }
        // title
        if (binding.editTextWriteRecipeTitle.text.isEmpty()) {
            makeToast("레시피 제목을 입력해주세요.", MotionToastStyle.ERROR)
            return false
        }
        if (ingredients.size <= 1) {
            makeToast("레시피 재료를 추가해주세요.", MotionToastStyle.ERROR)
            return false
        }
        // content
        if (binding.editTextWriteRecipeContent.text.isEmpty()) {
            makeToast("레시피 내용을 입력해주세요.", MotionToastStyle.ERROR)
            return false
        }

        return true
    }

    private fun initLauncher() {
        // 카메라 및 갤러리 권한 사전 요청
        val requestPermissionsLauncher =
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
                    recipePhotos.add(
                        Photo(
                            type = PHOTO,
                            date = Date().toString(),
                            pictureUri = tempImageUri,
                            absoluteFilePath = tempImageAbsoluteFilePath
                        )
                    )
                    // notifyItemInserted를 안 해줘도 반영이 되는데 이건 왜 그런지 모르겠다.
//                        writeReviewPictureAdapter.notifyItemInserted(reviewPhotos.size - 1)
                    writeRecipePhotoAdapter.notifyItemChanged(0)
                }
                tempImageAbsoluteFilePath = null
            }

        // 갤러리 런처
        selectGalleryLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
                uri?.let {
                    val absoluteFilePath = getFilePathFromUri(uri)
                    if (absoluteFilePath != null) {
                        recipePhotos.add(
                            Photo(
                                type = PHOTO,
                                date = Date().toString(),
                                pictureUri = uri,
                                absoluteFilePath = absoluteFilePath
                            )
                        )
                        // notifyItemInserted를 안 해줘도 반영이 되는데 이건 왜 그런지 모르겠다.
//                        writeReviewPictureAdapter.notifyItemInserted(reviewPhotos.size - 1)
                        writeRecipePhotoAdapter.notifyItemChanged(0)
                        return@registerForActivityResult
                    }
                    makeToast("사진은 갤러리를 통해서 접근해주세요", MotionToastStyle.ERROR)
                }
            }
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

                return cursor.getString(dataColumn!!)
            }
        }

        return null
    }

    private fun makeToast(content: String, motionToastStyle: MotionToastStyle) {
        MotionToast.createColorToast(
            this,
            "새 레시피 만들기",
            content,
            motionToastStyle,
            MotionToast.GRAVITY_BOTTOM,
            MotionToast.SHORT_DURATION,
            ResourcesCompat.getFont(this, R.font.helvetica_regular)
        )
    }

}