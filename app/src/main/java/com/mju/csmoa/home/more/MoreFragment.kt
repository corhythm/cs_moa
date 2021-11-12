package com.mju.csmoa.home.more

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mju.csmoa.R
import com.mju.csmoa.databinding.FragmentMoreBinding
import com.mju.csmoa.databinding.ItemDividerBinding
import com.mju.csmoa.databinding.ItemMoreBinding
import com.mju.csmoa.home.more.model.UserInfo
import com.mju.csmoa.retrofit.RetrofitManager
import com.mju.csmoa.util.Constants.TAG
import com.mju.csmoa.util.MyApplication
import kotlinx.coroutines.*
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle
import java.net.SocketTimeoutException

class MoreFragment : Fragment() {

    private var _binding: FragmentMoreBinding? = null
    private val binding get() = _binding!!
    private val moreMenuRecyclerAdapter = MoreMenuRecyclerAdapter()
    private var userInfo: UserInfo? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoreBinding.inflate(layoutInflater)
        init()
        return binding.root
    }

    private fun init() {

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

        with(binding) {
            // init recyclerView
            recyclerViewMoreMenuList.apply {
                adapter = moreMenuRecyclerAdapter
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                moreMenuRecyclerAdapter.submitList(itemMoreMenuList)
            }

            // change profile
            buttonMoreEditProfile.setOnClickListener { goToEditProfile() }
            relativeLayoutMoreImageContainer.setOnClickListener { goToEditProfile() }

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val jwtTokenInfo =
                        MyApplication.instance.jwtTokenInfoProtoManager.getJwtTokenInfo()
                    Log.d(TAG, "MoreFragment -init() called / jwtTokenInfo = $jwtTokenInfo")

                    // 사용자 정보 받아오기
                    val getUserInfoRes =
                        RetrofitManager.retrofitService?.getUserInfo(jwtTokenInfo!!.accessToken)
                    Log.d(TAG, "MoreFragment -init() called / getUserInfoRes = $getUserInfoRes")

                    // 회원 정보 받아오는 데 실패하면
                    if (getUserInfoRes == null) {
                        withContext(Dispatchers.Main) {
                            makeToast("회원 정보", "회원 정보를 받아오는데 실패했어요 :(", MotionToastStyle.ERROR)
                        }
                        return@launch
                    }

                    userInfo = getUserInfoRes.result

                    withContext(Dispatchers.Main) {
                        textViewMoreEmail.text = userInfo?.email
                        textViewMoreNickname.text = userInfo?.nickname

                        // if userProfileImageUrl == null -> init basic image
                        Glide.with(requireContext()).load(userInfo?.userProfileImageUrl)
                            .placeholder(R.drawable.img_all_basic_profile)
                            .error(R.drawable.img_all_basic_profile)
                            .into(imageViewMoreProfileImg)
                    }
                } catch (exception: SocketTimeoutException) {
                    withContext(Dispatchers.Main) {
                        makeToast("회원 정보", "회원 정보를 받아오는데 실패했어요 :(", MotionToastStyle.ERROR)
                    }
                }


            }
        }

    }


    private fun goToEditProfile() {

        if (userInfo == null) {
            makeToast("회원정보 가져오기 실패", "회원 정보를 수정할 수 없습니다.", MotionToastStyle.ERROR)
            return
        }
        val editProfileIntent = Intent(requireActivity(), EditProfileActivity::class.java).apply {
            putExtra("userInfo", userInfo)
        }
        startActivity(editProfileIntent)
    }

    private fun makeToast(title: String, content: String, motionToastStyle: MotionToastStyle) {
        MotionToast.createColorToast(
            requireActivity(),
            title,
            content,
            motionToastStyle,
            MotionToast.GRAVITY_BOTTOM,
            MotionToast.SHORT_DURATION,
            ResourcesCompat.getFont(requireContext(), R.font.helvetica_regular)
        )
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