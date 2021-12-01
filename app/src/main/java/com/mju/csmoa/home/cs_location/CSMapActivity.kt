package com.mju.csmoa.home.cs_location

import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.circularreveal.cardview.CircularRevealCardView
import com.mju.csmoa.R
import com.mju.csmoa.databinding.ActivityCsmapBinding
import com.mju.csmoa.home.cs_location.domain.GetSearchKeyWordRes
import com.mju.csmoa.home.cs_location.domain.Place
import com.mju.csmoa.retrofit.RetrofitService
import com.mju.csmoa.util.Constants.TAG
import kotlinx.coroutines.*
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.min

class CSMapActivity : AppCompatActivity(), MapView.CurrentLocationEventListener {

    private lateinit var binding: ActivityCsmapBinding
    private var mapPointGeo: MapPoint.GeoCoordinate? = null
    private val baseUrl = "https://dapi.kakao.com/"
    private val appKey = "KakaoAK c187f949b6390e5c1c441435609ac8e9"
    private var longitude = ""
    private var latitude = ""
    private var passed = false

    private val rotateOpen: Animation by lazy {
        AnimationUtils.loadAnimation(this, R.anim.rotate_open)
    }
    private val rotateClose: Animation by lazy {
        AnimationUtils.loadAnimation(this, R.anim.rotate_close)
    }
    private val fromBottom: Animation by lazy {
        AnimationUtils.loadAnimation(this, R.anim.from_bottom)
    }
    private val toBottom: Animation by lazy {
        AnimationUtils.loadAnimation(this, R.anim.to_bottom)
    }

    // 클릭되었는지 체크
    private var cuClicked = false
    private var gs25Clicked = false
    private var sevenClicked = false
    private var ministopClicked = false
    private var emart24Clicked = false
    private var filterClicked = false

    private var cuColor = -1
    private var gs25Color = -1
    private var sevenColor = -1
    private var ministopColor = -1
    private var emart24Color = -1

    private val cuMapPositionItems = mutableListOf<MapPOIItem>()
    private val gs25MapPositionItems = mutableListOf<MapPOIItem>()
    private val sevenMapPositionItems = mutableListOf<MapPOIItem>()
    private val ministopMapPositionItems = mutableListOf<MapPOIItem>()
    private val emart24MapPositionItems = mutableListOf<MapPOIItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCsmapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {

        if (intent.hasExtra("csBrand")) {
            // 특정 편의점만 검색해야 한다면
            val csBrand = intent.getStringExtra("csBrand")
            when (csBrand) {
                "cu" -> cuClicked = true
                "gs25" -> gs25Clicked = true
                "seven" -> sevenClicked = true
                "ministop" -> ministopClicked = true
                "emart24" -> emart24Clicked = true
            }
        } else {
            cuClicked = true
            gs25Clicked = true
            sevenClicked = true
            ministopClicked = true
            emart24Clicked = true
        }

        with(binding) {
            mapViewCsMapMap.currentLocationTrackingMode =
                MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading // HeadingMode 사용 안 함.
            mapViewCsMapMap.setCurrentLocationEventListener(this@CSMapActivity)

            // 각 편의점 cardView strokeColor 가져오기
            cuColor = cardViewCsMapCuContainer.strokeColor
            gs25Color = cardViewCsMapGs25Container.strokeColor
            sevenColor = cardViewCsMapSevenContainer.strokeColor
            ministopColor = cardViewCsMapMinistopContainer.strokeColor
            emart24Color = cardViewCsMapEmart24Container.strokeColor

            // 시작할 때 default는 전부 다 클릭되어 있는 상태 false이면 처음에 검색 안 함
            // 나중에 다른 액티비티에서 시작할 때 여기 값을 조작하면 될 듯


            // when filter button is clicked
            cardViewCsMapFilterContainer.setOnClickListener {
                setVisibility(filterClicked)
                setAnimation(filterClicked)
                setClickable(filterClicked)
                filterClicked = !filterClicked
            }
            cardViewCsMapCuContainer.setOnClickListener {
                cuClicked = !cuClicked
                onCsClicked(
                    cuClicked,
                    cardViewCsMapCuContainer,
                    imageViewCsMapCu,
                    cuColor
                )
                if (!cuClicked) binding.mapViewCsMapMap.removePOIItems(cuMapPositionItems.toTypedArray())
                else binding.mapViewCsMapMap.addPOIItems(cuMapPositionItems.toTypedArray())
            }
            cardViewCsMapGs25Container.setOnClickListener {
                gs25Clicked = !gs25Clicked
                onCsClicked(
                    gs25Clicked,
                    cardViewCsMapGs25Container,
                    imageViewCsMapGs25,
                    gs25Color
                )
                if (!gs25Clicked) binding.mapViewCsMapMap.removePOIItems(gs25MapPositionItems.toTypedArray())
                else binding.mapViewCsMapMap.addPOIItems(gs25MapPositionItems.toTypedArray())
            }
            cardViewCsMapSevenContainer.setOnClickListener {
                sevenClicked = !sevenClicked
                onCsClicked(
                    sevenClicked,
                    cardViewCsMapSevenContainer,
                    imageViewCsMapSeven,
                    sevenColor
                )
                if (!sevenClicked) binding.mapViewCsMapMap.removePOIItems(sevenMapPositionItems.toTypedArray())
                else binding.mapViewCsMapMap.addPOIItems(sevenMapPositionItems.toTypedArray())
            }
            cardViewCsMapMinistopContainer.setOnClickListener {
                ministopClicked = !ministopClicked
                onCsClicked(
                    ministopClicked,
                    cardViewCsMapMinistopContainer,
                    imageViewCsMapMinistop,
                    ministopColor
                )
                if (!ministopClicked) binding.mapViewCsMapMap.removePOIItems(
                    ministopMapPositionItems.toTypedArray()
                )
                else binding.mapViewCsMapMap.addPOIItems(ministopMapPositionItems.toTypedArray())
            }
            cardViewCsMapEmart24Container.setOnClickListener {
                emart24Clicked = !emart24Clicked
                onCsClicked(
                    emart24Clicked,
                    cardViewCsMapEmart24Container,
                    imageViewCsMapEmart24,
                    emart24Color
                )
                if (!emart24Clicked) binding.mapViewCsMapMap.removePOIItems(emart24MapPositionItems.toTypedArray())
                else binding.mapViewCsMapMap.addPOIItems(emart24MapPositionItems.toTypedArray())
            }
        }
    }

    private fun onCsClicked(
        isClicked: Boolean,
        cardViewContainer: CircularRevealCardView,
        imageView: ImageView,
        csColor: Int
    ) {
        if (!isClicked) {
            cardViewContainer.strokeColor = Color.GRAY
            imageView.colorFilter =
                ColorMatrixColorFilter(ColorMatrix().apply { setSaturation(0F) })
        } else {
            cardViewContainer.strokeColor = csColor
            imageView.colorFilter =
                ColorMatrixColorFilter(ColorMatrix().apply { setSaturation(1F) })
        }
    }

    private fun setVisibility(filterClicked: Boolean) {
        if (!filterClicked) {
            binding.cardViewCsMapCuContainer.visibility = View.VISIBLE
            binding.cardViewCsMapGs25Container.visibility = View.VISIBLE
            binding.cardViewCsMapSevenContainer.visibility = View.VISIBLE
            binding.cardViewCsMapMinistopContainer.visibility = View.VISIBLE
            binding.cardViewCsMapEmart24Container.visibility = View.VISIBLE
        } else {
            binding.cardViewCsMapCuContainer.visibility = View.INVISIBLE
            binding.cardViewCsMapGs25Container.visibility = View.INVISIBLE
            binding.cardViewCsMapSevenContainer.visibility = View.INVISIBLE
            binding.cardViewCsMapMinistopContainer.visibility = View.INVISIBLE
            binding.cardViewCsMapEmart24Container.visibility = View.INVISIBLE
        }
    }

    private fun setAnimation(filterClicked: Boolean) {
        if (!filterClicked) {
            binding.cardViewCsMapCuContainer.startAnimation(fromBottom)
            binding.cardViewCsMapGs25Container.startAnimation(fromBottom)
            binding.cardViewCsMapSevenContainer.startAnimation(fromBottom)
            binding.cardViewCsMapMinistopContainer.startAnimation(fromBottom)
            binding.cardViewCsMapEmart24Container.startAnimation(fromBottom)
            binding.cardViewCsMapFilterContainer.startAnimation(rotateOpen)
        } else {
            binding.cardViewCsMapCuContainer.startAnimation(toBottom)
            binding.cardViewCsMapGs25Container.startAnimation(toBottom)
            binding.cardViewCsMapSevenContainer.startAnimation(toBottom)
            binding.cardViewCsMapMinistopContainer.startAnimation(toBottom)
            binding.cardViewCsMapEmart24Container.startAnimation(toBottom)
            binding.cardViewCsMapFilterContainer.startAnimation(rotateClose)
        }
    }

    private fun setClickable(filterClicked: Boolean) {
        if (!filterClicked) {
            binding.cardViewCsMapCuContainer.isClickable = true
            binding.cardViewCsMapGs25Container.isClickable = true
            binding.cardViewCsMapSevenContainer.isClickable = true
            binding.cardViewCsMapMinistopContainer.isClickable = true
            binding.cardViewCsMapEmart24Container.isClickable = true
        } else {
            binding.cardViewCsMapCuContainer.isClickable = false
            binding.cardViewCsMapGs25Container.isClickable = false
            binding.cardViewCsMapSevenContainer.isClickable = false
            binding.cardViewCsMapMinistopContainer.isClickable = false
            binding.cardViewCsMapEmart24Container.isClickable = false
        }
    }

    private fun searchKeyword(
        keyword: String,
        page: Int, // 검색할 페이지, 기본값은 1, size 값에 따라 1~45 가능
        code: String = "CS2", // 카카오맵의 편의점 코드
        x: String = longitude,
        y: String = latitude,
        rad: Int = 10000 // 반경(radius)
    ) {
        // 편의점 위치 데이터 가져오고
        lifecycleScope.launch(Dispatchers.IO) {
            val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val api = retrofit.create(RetrofitService::class.java)
            val response = api.getSearchKeyword(
                key = appKey, // Authorization: KakaoAK {your_kakao_appkey}
                query = keyword,
                page = page,
                category = code,
                x = x,
                y = y,
                rad = rad
            )

            // 마커에 입력
            withContext(Dispatchers.Main) { addItemsAndMarkers(response, keyword) }
        }
    }

    // 마커 추가
    private fun addItemsAndMarkers(getSearchKeyWordRes: GetSearchKeyWordRes?, keyword: String) {
        if (!getSearchKeyWordRes?.documents.isNullOrEmpty()) {
            // 검색 결과 있으면
            for (document in getSearchKeyWordRes!!.documents) {
                addCSMapPoIItem(keyword, document) // 지도에 마커 추가
            }

            // 데이터 모두 가져왔으면 trackingMode 해제
            binding.mapViewCsMapMap.currentLocationTrackingMode =
                MapView.CurrentLocationTrackingMode.TrackingModeOff
        }
    }

    private fun addCSMapPoIItem(keyword: String, place: Place) {
        var csImageResourceId: Int = -1
        val point = MapPOIItem() // 리스트에 넣기 위해서 미리 선언

        when (keyword) {
            "cu" -> {
                csImageResourceId = R.drawable.img_cs_mini_cu
                cuMapPositionItems.add(point)
            }
            "gs25" -> {
                csImageResourceId = R.drawable.img_cs_mini_gs25
                gs25MapPositionItems.add(point)
            }
            "세븐일레븐" -> {
                csImageResourceId = R.drawable.img_cs_mini_seven
                sevenMapPositionItems.add(point)
            }
            "ministop" -> {
                csImageResourceId = R.drawable.img_cs_mini_ministop
                ministopMapPositionItems.add(point)
            }
            "emart24" -> {
                csImageResourceId = R.drawable.img_cs_mini_emart24
                emart24MapPositionItems.add(point)
            }
        }

        point.apply {
            itemName = place.placeName
            mapPoint = MapPoint.mapPointWithGeoCoord(
                place.y.toDouble(),
                place.x.toDouble()
            )
            markerType = MapPOIItem.MarkerType.CustomImage          // 마커 모양 (커스텀)
            customImageResourceId = csImageResourceId // 커스텀 마커 이미지
            selectedMarkerType = MapPOIItem.MarkerType.CustomImage  // 클릭 시 마커 모양 (커스텀)
            customSelectedImageResourceId = csImageResourceId // 클릭 시 커스텀 마커 이미지
            isCustomImageAutoscale = false      // 커스텀 마커 이미지 크기 자동 조정
            setCustomImageAnchor(0.5f, 1.0f)    // 마커 이미지 기준점
        }

        binding.mapViewCsMapMap.addPOIItem(point)
    }

    // 단말의 현위치 좌표값을 통보받을 수 있다.
    override fun onCurrentLocationUpdate(p0: MapView?, p1: MapPoint?, p2: Float) {
        Log.d(TAG, "CSMapActivity -onCurrentLocationUpdate() called")
        if (!passed) {
            mapPointGeo = p1?.mapPointGeoCoord

            longitude = mapPointGeo?.longitude.toString()
            latitude = mapPointGeo?.latitude.toString()

            if (cuClicked) searchKeyword("cu", 1) // 여기있는 page가 뭘 의미하지?
            if (gs25Clicked) searchKeyword("gs25", 1)
            if (sevenClicked) searchKeyword("세븐일레븐", 1)
            if (ministopClicked) searchKeyword("ministop", 1)
            if (emart24Clicked) searchKeyword("emart24", 1)
            passed = true
            Log.d(TAG, "passed = $passed")
        }
    }

    // 단말의 방향(Heading) 각도 값을 통보받을 수 있다.
    override fun onCurrentLocationDeviceHeadingUpdate(p0: MapView?, p1: Float) {
//        TODO("Not yet implemented") // TODO가 살아있으면 TrackingModeOnWithHeading이 작동하지 않는다.
    }

    // 현 위치 갱신 작업에 실패한 경우 호출
    override fun onCurrentLocationUpdateFailed(p0: MapView?) {
        TODO("Not yet implemented")
    }

    // 현위치 트래킹 기능이 사용자에 의해 취소된 겨웅에 호출.
    // 처음 현위치를 찾는 동안에 현위치를 찾는 중이라는 Alert Dialog 인터페이스가 사용자에게 노출된다.
    // 첫 현위를 찾기 전에 사용자가 취소 버튼을 누른 경우에 호출된다.
    override fun onCurrentLocationUpdateCancelled(p0: MapView?) {
        TODO("Not yet implemented")
    }
}