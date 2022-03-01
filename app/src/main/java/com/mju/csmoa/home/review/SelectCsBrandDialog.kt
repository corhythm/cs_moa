package com.mju.csmoa.home.review

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mju.csmoa.R
import com.mju.csmoa.databinding.DialogSelectCsBrandBinding
import com.mju.csmoa.databinding.ItemCsBrandBinding
import com.mju.csmoa.databinding.ItemCsBrandEtcBinding
import com.mju.csmoa.home.review.SelectCsBrandDialog.Companion.ETC
import com.mju.csmoa.common.util.RecyclerViewDecoration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SelectCsBrandDialog(context: Context, theme: Int, onCsClicked: (String) -> Unit) :
    BottomSheetDialog(context, theme) {

    private val binding: DialogSelectCsBrandBinding =
        DialogSelectCsBrandBinding.inflate(layoutInflater)
    private lateinit var convenienceStoreAdapter: ConvenienceStoreAdapter

    companion object {
        const val ETC = 0
        const val CS = 1
    }

    init {
        with(binding) {
            setContentView(root)

            val csBrands = context.resources.getStringArray(R.array.cs_brand_list)
            val csBrandImages = context.resources.obtainTypedArray(R.array.cs_brand_logo_list)
            val csBrandColors = context.resources.getStringArray(R.array.cs_brand_color_list)

            val convenienceStores = mutableListOf<ConvenienceStore>()
            csBrands.forEachIndexed { index, _ ->
                convenienceStores.add(
                    ConvenienceStore(
                        type = CS,
                        csBrand = csBrands[index],
                        csBrandImageId = csBrandImages.getResourceId(index, -1),
                        csBrandColor = csBrandColors[index],
                        isClicked = false
                    )
                )
            }

            // 기타 편의점
            convenienceStores.add(
                ConvenienceStore(
                    type = ETC,
                    csBrand = "기타",
                    csBrandImageId = null,
                    csBrandColor = null,
                    isClicked = false
                )
            )

            val onItemClick: (position: Int) -> Unit = { position ->
                convenienceStores[position].isClicked = !convenienceStores[position].isClicked
                convenienceStoreAdapter.notifyItemChanged(position)
                CoroutineScope(Dispatchers.Main).launch {
                    delay(400L)
                    onCsClicked.invoke(convenienceStores[position].csBrand!!)
                    dismiss()
                }
            }


            recyclerViewDialogSelectCsBrandCsBrands.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                convenienceStoreAdapter = ConvenienceStoreAdapter(convenienceStores, onItemClick)
                adapter = convenienceStoreAdapter
                addItemDecoration(RecyclerViewDecoration(0, 0, 5, 10))
            }
        }
    }

}

class ConvenienceStoreAdapter(
    private val convenienceStores: List<ConvenienceStore>,
    private val onItemClicked: (position: Int) -> Unit
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int) = convenienceStores[position].type

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ETC)
            ConvenienceStoreEtcViewHolder(parent, onItemClicked)
        else
            ConvenienceStoreViewHolder(parent, onItemClicked)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ConvenienceStoreViewHolder) {
            holder.bind(convenienceStores[position])
        } else {
            (holder as ConvenienceStoreEtcViewHolder).bind(convenienceStores[position])
        }
    }

    override fun getItemCount() = convenienceStores.size
}

class ConvenienceStoreEtcViewHolder(
    parent: ViewGroup,
    private val onItemClicked: (position: Int) -> Unit
) : RecyclerView.ViewHolder(
    ItemCsBrandEtcBinding.inflate(
        LayoutInflater.from(parent.context), parent, false
    ).root
) {
    private val binding = ItemCsBrandEtcBinding.bind(itemView)

    init {
        binding.root.setOnClickListener { onItemClicked.invoke(absoluteAdapterPosition) }
    }

    fun bind(convenienceStore: ConvenienceStore) {
        if (convenienceStore.isClicked)
            binding.imageViewCsBrandEtcCheck.visibility = View.VISIBLE
        else
            binding.imageViewCsBrandEtcCheck.visibility = View.INVISIBLE
    }
}


class ConvenienceStoreViewHolder(
    parent: ViewGroup,
    private val onItemClicked: (position: Int) -> Unit
) : RecyclerView.ViewHolder(
    ItemCsBrandBinding.inflate(
        LayoutInflater.from(parent.context), parent, false
    ).root
) {
    private val binding = ItemCsBrandBinding.bind(itemView)

    init {
        binding.root.setOnClickListener { onItemClicked.invoke(absoluteAdapterPosition) }
    }

    fun bind(convenienceStore: ConvenienceStore) {

        if (convenienceStore.isClicked)
            binding.imageViewCsBrandCheck.visibility = View.VISIBLE
        else
            binding.imageViewCsBrandCheck.visibility = View.INVISIBLE

        binding.imageViewCsBrandSelectBrand.setImageResource(convenienceStore.csBrandImageId!!)
        binding.cardViewCsBrandContainer.strokeColor =
            Color.parseColor(convenienceStore.csBrandColor)
    }
}


data class ConvenienceStore(
    val type: Int,
    val csBrand: String?,
    val csBrandImageId: Int?,
    val csBrandColor: String?,
    var isClicked: Boolean
)