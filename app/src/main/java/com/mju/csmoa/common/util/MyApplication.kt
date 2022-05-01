package com.mju.csmoa.common.util

import android.app.Activity
import android.app.Application
import android.graphics.Color
import androidx.core.content.res.ResourcesCompat
import com.kakao.sdk.common.KakaoSdk
import com.mju.csmoa.R
import com.mju.csmoa.common.util.datastore.JwtTokenInfoProtoManager
import com.mju.csmoa.common.util.room.database.LocalRoomDatabase
import com.mju.csmoa.common.util.room.repository.SearchHistoryRepository
import com.mju.csmoa.common.util.secret.JwtService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

class MyApplication : Application() {

    // No need to cancel this scope as it'll be torn down with the process
    private val applicationScope = CoroutineScope(SupervisorJob())

    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts
    val database by lazy { LocalRoomDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { SearchHistoryRepository(database.searchHistoryDao()) }
    val jwtTokenInfoProtoManager by lazy { JwtTokenInfoProtoManager(this) }
    val jwtService by lazy { JwtService() }

    companion object {
        lateinit var instance: MyApplication
            private set

        fun getCsBrandResourceId(csBrand: String): Int {
            return when (csBrand) {
                "cu" -> R.drawable.img_cs_cu
                "gs25" -> R.drawable.img_cs_gs25
                "seven" -> R.drawable.img_cs_seveneleven
                "ministop" -> R.drawable.img_cs_ministop
                "emart24" -> R.drawable.img_cs_emart24
                "기타" -> R.drawable.img_all_etc
                else -> -1
            }
        }

        fun getCsTextBrandResourceId(csBrand: String): Int {
            return when (csBrand) {
                "cu" -> R.drawable.img_cs_text_cu
                "gs25" -> R.drawable.img_cs_text_gs25
                "seven" -> R.drawable.img_cs_text_seven
                "ministop" -> R.drawable.img_cs_mini_ministop
                "emart24" -> R.drawable.img_cs_text_emart24
                "기타" -> R.drawable.img_all_etc
                else -> -1
            }
        }

        fun getCsBrandColor(csBrand: String): Int {
            val csBrandColorList =
                instance.resources.getStringArray(R.array.cs_brand_color_list)

            return when (csBrand) {
                "cu" -> Color.parseColor(csBrandColorList[0])
                "gs25" -> Color.parseColor(csBrandColorList[1])
                "seven" -> Color.parseColor(csBrandColorList[2])
                "ministop" -> Color.parseColor(csBrandColorList[3])
                "emart24" -> Color.parseColor(csBrandColorList[4])
                "기타" -> Color.parseColor("#f4b6c2") // just beautiful color
                else -> -1
            }
        }

        fun getEventTypeColor(eventType: String): Int {
            val eventTypeColorList =
                this.instance.resources.getStringArray(R.array.event_type_color_list)

            return when (eventType) {
                "1+1" -> Color.parseColor(eventTypeColorList[0])
                "2+1" -> Color.parseColor(eventTypeColorList[1])
                "3+1" -> Color.parseColor(eventTypeColorList[2])
                "4+1" -> Color.parseColor(eventTypeColorList[3])
                else -> -1
            }
        }

        fun makeToast(
            activity: Activity,
            title: String,
            content: String,
            motionToastStyle: MotionToastStyle
        ) {
            MotionToast.createColorToast(
                activity,
                title,
                content,
                motionToastStyle,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.SHORT_DURATION,
                ResourcesCompat.getFont(activity, R.font.helvetica_regular)
            )
        }

    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        // 카카오 로그인 활성화
        KakaoSdk.init(this, getString(R.string.kakao_app_key))
//        getAppKeyHash()
//        Log.d(TAG, " -onCreate() called / (kakao) keyHash = ${Utility.getKeyHash(this)}")
    }


//    @RequiresApi(Build.VERSION_CODES.P)
//    private fun getAppKeyHash() {
//        try {
//            val info = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES)
//            val signatures = info.signingInfo.apkContentsSigners
//            val md = MessageDigest.getInstance("SHA")
//            for (signature in signatures) {
//                val md: MessageDigest = MessageDigest.getInstance("SHA")
//                md.update(signature.toByteArray())
//                val key = String(Base64.encode(md.digest(), 0))
//                Log.d(TAG, "(fun) hash key = $key")
//            }
//        } catch(e: Exception) {
//            Log.e("name not found", e.toString())
//        }
//    }
}