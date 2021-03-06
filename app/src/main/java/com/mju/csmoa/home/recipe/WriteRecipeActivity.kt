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

        // content edittext??? ????????? ?????? -> ????????? ??? ????????? ??????
//        binding.editTextWriteRecipeContent.setOnFocusChangeListener { v, hasFocus ->
//            if (hasFocus) {
//                Log.d(TAG, "hasFocus = $hasFocus")
//                binding.root.smoothScrollTo(0, binding.root.bottom)
//                binding.editTextWriteRecipeContent.requestFocus()
//            }
//        }

        // ????????? ?????? ?????? -> ????????? ????????? ??????
        binding.textViewWriteRecipeComplete.setOnClickListener {
            Log.d(TAG, "recipePhotos = $recipePhotos")
            if (isAllRequirementMeet()) { // ???????????? ?????? ????????????
                binding.progressBarWriteRecipeOnGoing.visibility = View.VISIBLE
                lifecycleScope.launch(Dispatchers.IO) {

                    try {
                        val accessToken =
                            MyApplication.instance.jwtTokenInfoProtoManager.getJwtTokenInfo()?.accessToken!!

                        val multipartRecipeImages =
                            mutableListOf<MultipartBody.Part>() // multipart file list
                        recipePhotos.forEachIndexed { index, photo ->
                            if (index > 0) { // ?????? ????????? ?????? ?????? ??????????????? ??????
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
                                makeToast("???????????? ??????????????? ?????? ??????????????????.", MotionToastStyle.SUCCESS)
                                val finishWriteRecipeIntent = Intent().apply {
                                    putExtra("postRecipeRes", response.result)
                                }
                                setResult(RESULT_OK, finishWriteRecipeIntent)
                                finish()

                            } else {
                                binding.progressBarWriteRecipeOnGoing.visibility = View.INVISIBLE
                                makeToast("????????? ????????? ???????????????.", MotionToastStyle.ERROR)
                            }
                        }
                    } catch (ex: Exception) {
                        withContext(Dispatchers.Main) {
                            Log.d(
                                TAG,
                                "????????? ?????? ??? ??????: printStackTrace() = ${ex.printStackTrace()} / message = ${ex.message}"
                            )
                            makeToast("????????? ?????? ??? ????????? ??????????????????", MotionToastStyle.ERROR)
                        }
                    }
                }

            }
        }
    }

    private fun initIngredient() {
        // dialog?????? ?????? ?????? ?????? ?????? ????????? ???
        val onCompleteClicked: (ingredientName: String, ingredientPrice: String, csBrand: String) -> Unit =
            { ingredientName, ingredientPrice, csBrand ->
                ingredients.add(Ingredient(ingredientName, ingredientPrice, csBrand))
                ingredientAdapter.notifyItemChanged(0)
            }

        // ?????? ?????? ?????? ????????? ???
        val onAddClicked: () -> Unit = {
            AddIngredientDialog(this, onCompleteClicked).show()
        }
        // ?????? ?????? ?????? ????????? ???
        val onDeleteClicked: (position: Int) -> Unit = {
            ingredients.removeAt(it)
            ingredientAdapter.notifyItemChanged(it)
        }

        ingredients.add(Ingredient("", "", "")) // ????????? ??? ??? ??????
        ingredientAdapter = AddIngredientAdapter(ingredients, onAddClicked, onDeleteClicked)
        binding.recyclerViewWriteRecipeIngredients.apply {
            adapter = ingredientAdapter
            layoutManager =
                LinearLayoutManager(this@WriteRecipeActivity, LinearLayoutManager.VERTICAL, false)
        }

    }

    private fun initPhoto() {
        // ????????? ?????? ????????? ??? -> ???????????? ????????? ?????? ?????? ?????? ????????????
        val onCameraClicked: () -> Unit = {
            if (recipePhotos.size >= 6) {
                makeToast(
                    "?????? ???????????? ?????? 5???????????? ????????? ??? ??? ?????????!",
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
                        // ????????? + ???????????? ?????? ??????
                        takeCameraLauncher.launch(tempImageUri)
                    },
                    // ???????????? ?????? ??????
                    onSecondButtonClicked = { selectGalleryLauncher.launch("image/*") }
                ).show()
            }
        }


        // ????????? ?????? ???????????? ??????????????? ????????????
        val onCancelClicked: (position: Int) -> Unit = { position ->
            recipePhotos.removeAt(position)
            writeRecipePhotoAdapter.notifyItemChanged(0) // 0?????? ????????? ???????????? ????????? ???????????? ???.
            writeRecipePhotoAdapter.notifyItemRemoved(position) // ????????? ?????? ???????????? ???.
        }

        // ?????? ????????? ?????????
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
        // ??????????????????
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
        // ?????? ??? ??? ??????
        if (recipePhotos.size <= 1) {
            makeToast("1??? ????????? ????????? ??????????????????.", MotionToastStyle.ERROR)
            return false
        }
        // title
        if (binding.editTextWriteRecipeTitle.text.isEmpty()) {
            makeToast("????????? ????????? ??????????????????.", MotionToastStyle.ERROR)
            return false
        }
        if (ingredients.size <= 1) {
            makeToast("????????? ????????? ??????????????????.", MotionToastStyle.ERROR)
            return false
        }
        // content
        if (binding.editTextWriteRecipeContent.text.isEmpty()) {
            makeToast("????????? ????????? ??????????????????.", MotionToastStyle.ERROR)
            return false
        }

        return true
    }

    private fun initLauncher() {
        // ????????? ??? ????????? ?????? ?????? ??????
        val requestPermissionsLauncher =
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
                    recipePhotos.add(
                        Photo(
                            type = PHOTO,
                            date = Date().toString(),
                            pictureUri = tempImageUri,
                            absoluteFilePath = tempImageAbsoluteFilePath
                        )
                    )
                    // notifyItemInserted??? ??? ????????? ????????? ????????? ?????? ??? ????????? ????????????.
//                        writeReviewPictureAdapter.notifyItemInserted(reviewPhotos.size - 1)
                    writeRecipePhotoAdapter.notifyItemChanged(0)
                }
                tempImageAbsoluteFilePath = null
            }

        // ????????? ??????
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
                        // notifyItemInserted??? ??? ????????? ????????? ????????? ?????? ??? ????????? ????????????.
//                        writeReviewPictureAdapter.notifyItemInserted(reviewPhotos.size - 1)
                        writeRecipePhotoAdapter.notifyItemChanged(0)
                        return@registerForActivityResult
                    }
                    makeToast("????????? ???????????? ????????? ??????????????????", MotionToastStyle.ERROR)
                }
            }
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

                return cursor.getString(dataColumn!!)
            }
        }

        return null
    }

    private fun makeToast(content: String, motionToastStyle: MotionToastStyle) {
        MotionToast.createColorToast(
            this,
            "??? ????????? ?????????",
            content,
            motionToastStyle,
            MotionToast.GRAVITY_BOTTOM,
            MotionToast.SHORT_DURATION,
            ResourcesCompat.getFont(this, R.font.helvetica_regular)
        )
    }

}