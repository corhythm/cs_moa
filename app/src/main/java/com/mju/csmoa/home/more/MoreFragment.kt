package com.mju.csmoa.home.more

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mju.csmoa.R
import com.mju.csmoa.databinding.FragmentMoreBinding
import com.mju.csmoa.databinding.ItemDividerBinding
import com.mju.csmoa.databinding.ItemMoreBinding

class MoreFragment : Fragment() {

    private var _binding: FragmentMoreBinding? = null
    private val binding get() = _binding!!
    private val moreMenuRecyclerAdapter = MoreMenuRecyclerAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoreBinding.inflate(layoutInflater)
        init()
        return binding.root
    }


    private fun init() {

        // TODO: 서버로부터 자기 프로필 정보 받아와서 화면에 뿌려줘야 함

        val moreItemNameList = requireContext().resources.getStringArray(R.array.more_item_list)
        val moreItemImageDrawableList =
            requireContext().resources.obtainTypedArray(R.array.more_item_image_list)
        val itemMoreMenuList = ArrayList<ItemMoreMenu>()

        for (i in moreItemNameList.indices) {

            if (moreItemNameList[i].equals("divider")) {
                itemMoreMenuList.add(ItemMoreMenu())
                continue
            }

            itemMoreMenuList.add(
                ItemMoreMenu(
                menuImageResourceId = moreItemImageDrawableList.getResourceId(i, 0),
                menuName = moreItemNameList[i]
            )
            )

        }

        // init recyclerView
        binding.recyclerViewMoreMenuList.apply {
            adapter = moreMenuRecyclerAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            moreMenuRecyclerAdapter.submitList(itemMoreMenuList)
        }

        // change profile
        binding.buttonMoreEditProfile.setOnClickListener { editProfile() }
        binding.relativeLayoutMoreImageContainer.setOnClickListener { editProfile() }
    }


    private fun editProfile() {
        val editProfileIntent = Intent(requireActivity(), EditProfileActivity::class.java)
        startActivity(editProfileIntent)
    }
}

class MoreMenuRecyclerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var itemMoreList: List<ItemMoreMenu>

    fun submitList(itemMoreList: List<ItemMoreMenu>) {
        this.itemMoreList = itemMoreList
    }

    override fun getItemViewType(position: Int): Int {
        return when (itemMoreList[position].menuName) {
            "divider" -> 1
            else -> 0
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 0) MoreMenuViewHolder(
            ItemMoreBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
        else DivideViewHolder(
            ItemDividerBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MoreMenuViewHolder) {
            holder.bind(itemMoreList[position])
        }
    }

    override fun getItemCount(): Int = this.itemMoreList.size

}

class MoreMenuViewHolder(private val itemMoreBinding: ItemMoreBinding) :
    RecyclerView.ViewHolder(itemMoreBinding.root) {

    fun bind(itemMoreMenu: ItemMoreMenu) {
        itemMoreBinding.imageViewItemMoreMenuImg.setImageResource(itemMoreMenu.menuImageResourceId)
        itemMoreBinding.textViewItemMoreMenuName.text = itemMoreMenu.menuName

        // 메뉴 중 하나 눌렸을 때
        itemMoreBinding.root.setOnClickListener {

        }
    }
}

// Divider
class DivideViewHolder(private val itemDividerBinding: ItemDividerBinding) :
    RecyclerView.ViewHolder(itemDividerBinding.root) {}

data class ItemMoreMenu(val menuImageResourceId: Int = -1, val menuName: String = "divider")