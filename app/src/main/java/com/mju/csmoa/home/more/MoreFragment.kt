package com.mju.csmoa.home.more

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mju.csmoa.R
import com.mju.csmoa.databinding.FragmentMoreBinding
import com.mju.csmoa.databinding.ItemDividerBinding
import com.mju.csmoa.databinding.ItemMoreBinding
import com.mju.csmoa.home.cs_location.CSMapActivity
import com.mju.csmoa.home.more.model.PatchUserInfoRes
import com.mju.csmoa.home.more.model.UserInfo
import com.mju.csmoa.home.review.MyReviewsActivity
import com.mju.csmoa.retrofit.RetrofitManager
import com.mju.csmoa.common.util.Constants.TAG
import com.mju.csmoa.common.util.MyApplication
import kotlinx.coroutines.*
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle
import java.net.SocketTimeoutException

class MoreFragment : Fragment() {

    private var _binding: FragmentMoreBinding? = null
    private val binding get() = _binding!!
    private lateinit var moreMenuRecyclerAdapter: MoreMenuRecyclerAdapter
    private lateinit var updateProfileInfoLauncher: ActivityResultLauncher<Intent>
    private var userInfo: UserInfo? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoreBinding.inflate(layoutInflater)
        init()
        return binding.root
    }

    private fun init() {

        updateProfileInfoLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val patchUserInfoRes =
                        result.data?.getParcelableExtra<PatchUserInfoRes>("patchUserInfoRes")
                    Log.d(TAG, "update complete / patchUserInfoRes = $patchUserInfoRes")
                    if (patchUserInfoRes != null) {
                        with(binding) {
                            textViewMoreNickname.text = patchUserInfoRes.result.nickname
                            Glide.with(requireContext())
                                .load(patchUserInfoRes.result.userProfileImageUrl)
                                .placeholder(R.drawable.img_all_basic_profile)
                                .error(R.drawable.img_all_basic_profile)
                                .into(imageViewMoreProfileImg)

                            userInfo?.userProfileImageUrl =
                                patchUserInfoRes.result.userProfileImageUrl
                            userInfo?.nickname = patchUserInfoRes.result.nickname
                        }
                    }
                }
            }

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

        // 아이템 클릭됐을 때
        val onItemClicked: (position: Int) -> Unit = {
            when (itemMoreMenuList[it].menuName) {
                "주변 편의점 찾기" -> startActivity(Intent(requireContext(), CSMapActivity::class.java))
                "내가 작성한 리뷰" -> startActivity(
                    Intent(
                        requireContext(),
                        MyReviewsActivity::class.java
                    )
                )
            }
        }
        moreMenuRecyclerAdapter = MoreMenuRecyclerAdapter(itemMoreMenuList, onItemClicked)

        with(binding) {
            // init recyclerView
            recyclerViewMoreMenuList.apply {
                adapter = moreMenuRecyclerAdapter
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            }

            // change profile
            buttonMoreEditProfile.setOnClickListener { goToEditProfile() }
            relativeLayoutMoreImageContainer.setOnClickListener { goToEditProfile() }

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val jwtTokenInfo =
                        MyApplication.instance.jwtTokenInfoProtoManager.getJwtTokenInfo()

                    // 사용자 정보 받아오기
                    val getUserInfoRes =
                        RetrofitManager.retrofitService?.getUserInfo(jwtTokenInfo!!.accessToken)

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
        updateProfileInfoLauncher.launch(editProfileIntent)
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

class MoreMenuRecyclerAdapter(
    private val itemMoreList: List<ItemMoreMenu>,
    private val onItemClicked: (Position: Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return when (itemMoreList[position].menuName) {
            "divider" -> 1
            else -> 0
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 0) MoreMenuViewHolder(parent, onItemClicked)
        else DivideViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MoreMenuViewHolder) {
            holder.bind(itemMoreList[position])
        }
    }

    override fun getItemCount(): Int = this.itemMoreList.size

}

class MoreMenuViewHolder(parent: ViewGroup, private val onItemClicked: (Position: Int) -> Unit) :
    RecyclerView.ViewHolder(
        ItemMoreBinding.inflate(
            LayoutInflater.from(
                parent.context
            ), parent, false
        ).root
    ) {

    private val binding = ItemMoreBinding.bind(itemView)

    init {
        binding.root.setOnClickListener {
            onItemClicked.invoke(absoluteAdapterPosition)
        }
    }

    fun bind(itemMoreMenu: ItemMoreMenu) {
        binding.imageViewItemMoreMenuImg.setImageResource(itemMoreMenu.menuImageResourceId)
        binding.textViewItemMoreMenuName.text = itemMoreMenu.menuName
    }
}


// Divider
class DivideViewHolder(parent: ViewGroup) :
    RecyclerView.ViewHolder(
        ItemDividerBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ).root
    ) {}

data class ItemMoreMenu(val menuImageResourceId: Int = -1, val menuName: String = "divider")