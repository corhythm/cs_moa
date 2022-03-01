package com.mju.csmoa.home.recipe

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mju.csmoa.R
import com.mju.csmoa.databinding.DialogAddIngredientBinding
import com.mju.csmoa.databinding.ItemIngredientCsbrandBinding
import com.mju.csmoa.common.util.Constants.TAG
import com.mju.csmoa.common.util.MyApplication
import com.mju.csmoa.common.util.RecyclerViewDecoration
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

class AddIngredientDialog(
    private val mContext: Context,
    private val onCompletedClick: (ingredientName: String, ingredientPrice: String, csBrand: String) -> Unit
) : Dialog(mContext) {

    private lateinit var binding: DialogAddIngredientBinding
    private lateinit var ingredientCsBrandAdapter: IngredientCsBrandAdapter
    private val csBrandResources = mutableListOf<CsBrandResource>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogAddIngredientBinding.inflate(layoutInflater)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // 모서리 튀어나온 부분 제거
        window?.requestFeature(Window.FEATURE_NO_TITLE)
        setContentView(binding.root)

        binding.buttonAddIngredientAdd.setOnClickListener {
            val csBrand = checkValid()
            if (csBrand != null) {
                onCompletedClick(
                    binding.editTextAddIngredientIngredientName.text.toString(),
                    binding.editTextAddIngredientIngredientPrice.text.toString(),
                    csBrand
                )
                dismiss()
            }
        }

        binding.buttonAddIngredientCancel.setOnClickListener { dismiss() }

        // init csBrand recyclerView
        val csBrands = context.resources.getStringArray(R.array.cs_brand_list)
        csBrands.forEach {
            csBrandResources.add(CsBrandResource(it, false))
        }
        csBrandResources.add(CsBrandResource("기타", false)) // 마지막 기타 하나 추가

        // csBrand 클릭됐을 때
        val onItemClicked: (position: Int) -> Unit = { it ->
            val nowClicked = !csBrandResources[it].isClicked
            csBrandResources.forEach { csBrandResource ->
                csBrandResource.isClicked = false
            } // 하나만 클릭 가능하게 하기 위해
            csBrandResources[it].isClicked = nowClicked
            ingredientCsBrandAdapter.notifyItemRangeChanged(0, csBrandResources.size)
        }

        ingredientCsBrandAdapter = IngredientCsBrandAdapter(csBrandResources, onItemClicked)
        binding.recyclerViewAddIngredientCsBrand.apply {
            adapter = ingredientCsBrandAdapter
            addItemDecoration(RecyclerViewDecoration(0, 0, 10, 10))
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun checkValid(): String? {
        if (binding.editTextAddIngredientIngredientName.text.toString().trim().isEmpty()) {
            // 재료명 기입했는지
            makeToast("재료 이름을 입력해주세요.")
            return null
        }
        // 가격이 숫자로만 되어 있는지
        val price = binding.editTextAddIngredientIngredientPrice.text.toString().toIntOrNull()
        Log.d(TAG, "price = $price")
        if (price == null || price < 0) {
            // 가격을 기입했는지
            makeToast("재료 가격은 0 이상의 숫자만 입력가능합니다.")
            return null
        }

        csBrandResources.forEach { if (it.isClicked) return it.csBrand }
        // forEach 다 돌아도 클릭된 거 하나도 없으면
        makeToast("재료를 구매할 수 있는 편의점을 선택해주세요.")
        return null
    }

    private fun makeToast(content: String) {
        MotionToast.createColorToast(
            mContext as WriteRecipeActivity,
            "재료 추가",
            content,
            MotionToastStyle.ERROR,
            MotionToast.GRAVITY_BOTTOM,
            MotionToast.SHORT_DURATION,
            ResourcesCompat.getFont(context, R.font.helvetica_regular)
        )
    }
}


class IngredientCsBrandAdapter(
    private val csBrandResourceIds: List<CsBrandResource>,
    private val onItemClicked: (position: Int) -> Unit
) : RecyclerView.Adapter<IngredientCsBrandViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        IngredientCsBrandViewHolder(parent, onItemClicked)

    override fun onBindViewHolder(holder: IngredientCsBrandViewHolder, position: Int) {
        holder.bind(csBrandResourceIds[position])
    }

    override fun getItemCount() = csBrandResourceIds.size
}

class IngredientCsBrandViewHolder(parent: ViewGroup, onItemClicked: (position: Int) -> Unit) :
    RecyclerView.ViewHolder(
        ItemIngredientCsbrandBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ).root
    ) {
    private val binding = ItemIngredientCsbrandBinding.bind(itemView)

    init {
        binding.cardViewIngredientCsBrandContainer.setOnClickListener {
            onItemClicked.invoke(absoluteAdapterPosition)
        }
    }

    fun bind(csBrandResource: CsBrandResource) {

        val resourceId = MyApplication.getCsBrandResourceId(csBrandResource.csBrand)
        val csColor = MyApplication.getCsBrandColor(csBrandResource.csBrand)

        if (!csBrandResource.isClicked) {
            binding.imageViewIngrdientCsBrandCsBrand.colorFilter =
                ColorMatrixColorFilter(ColorMatrix().apply { setSaturation(0F) })
            binding.cardViewIngredientCsBrandContainer.strokeColor = Color.GRAY
        } else {
            binding.imageViewIngrdientCsBrandCsBrand.colorFilter =
                ColorMatrixColorFilter(ColorMatrix().apply { setSaturation(1F) })
            binding.cardViewIngredientCsBrandContainer.strokeColor = csColor
        }

        binding.imageViewIngrdientCsBrandCsBrand.setImageResource(resourceId)
    }
}

data class CsBrandResource(val csBrand: String, var isClicked: Boolean)