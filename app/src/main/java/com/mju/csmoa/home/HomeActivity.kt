package com.mju.csmoa.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.mju.csmoa.*
import com.mju.csmoa.databinding.ActivityHomeBinding
import com.mju.csmoa.home.event_item.EventItemsFragment
import com.mju.csmoa.home.more.MoreFragment
import com.mju.csmoa.home.recipe.RecipeSearchResultActivity
import com.mju.csmoa.home.recipe.RecipesFragment
import com.mju.csmoa.home.review.ReviewSearchResultActivity
import com.mju.csmoa.home.review.ReviewsFragment
import com.mju.csmoa.common.util.MyApplication
import www.sanju.motiontoast.MotionToastStyle


class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private var isSearchbarState = false
    private var nowFragment: Fragment = ReviewsFragment() // 제일 처음은 reviews로 시작
    private var backKeyPressedTime: Long = 0 // 마지막으로 back key를 눌렀던 시간

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        initMainState()
    }

    private fun init() {

        binding.bottomNavViewHomeBottomMenu.setOnItemSelectedListener { menuItem ->
            val toolbar = binding.includeHomeMainToolBar.toolbarMainToolbarToolbar
            val searchMenu =
                binding.includeHomeMainToolBar.toolbarMainToolbarToolbar.menu.findItem(R.id.searchMenu_searchBar_search)

            when (menuItem.itemId) {
                R.id.bottomNavMenu_home_itemReview -> {
                    toolbar.title = "제품 리뷰"
                    searchMenu.isVisible = true // 제품 리뷰는 검색 지원
                    nowFragment =
                        if (nowFragment !is ReviewsFragment) ReviewsFragment() else nowFragment
                    replaceFragment(nowFragment)
                }
                R.id.bottomNavMenu_home_itemEvent -> {
                    toolbar.title = "행사 상품"
                    searchMenu.isVisible = false // 행사상품은 따로 검색을 지원하지 않음
                    nowFragment =
                        if (nowFragment !is EventItemsFragment) EventItemsFragment() else nowFragment
                    replaceFragment(nowFragment)
                }
                R.id.bottomNavMenu_home_recipe -> {
                    toolbar.title = "꿀조합 레시피"
                    searchMenu.isVisible = true // 레시피 역시 검색 지원
                    nowFragment =
                        if (nowFragment !is RecipesFragment) RecipesFragment() else nowFragment
                    replaceFragment(nowFragment)
                }
                R.id.bottomNavMenu_home_more -> {
                    toolbar.title = "더보기"
                    searchMenu.isVisible = false
                    nowFragment = if (nowFragment !is MoreFragment) MoreFragment() else nowFragment
                    replaceFragment(nowFragment)
                }
            }
            return@setOnItemSelectedListener true
        }

        // 텍스트 일력할 시, X 버튼 노출 -> 이것도 X 버튼 누르면 editText empty되게 수정해야 함(근데 하려고 하면 custom editText 만들어야 함)
        val searchBarTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (binding.includeHomeSearchToolbar.editTextSearchToolbarSearchbar
                        .text.toString().isNotEmpty()
                ) {
                    binding.includeHomeSearchToolbar.editTextSearchToolbarSearchbar.setCompoundDrawablesWithIntrinsicBounds(
                        null,
                        null,
                        ContextCompat.getDrawable(this@HomeActivity, R.drawable.ic_all_delete2),
                        null
                    )
                } else {
                    binding.includeHomeSearchToolbar.editTextSearchToolbarSearchbar
                        .setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
                }
            }

            override fun afterTextChanged(s: Editable) {}
        }
        binding.includeHomeSearchToolbar.editTextSearchToolbarSearchbar
            .addTextChangedListener(searchBarTextWatcher)

        // 키보드에 있는 검색 버튼 클릭했을 때
        binding.includeHomeSearchToolbar.editTextSearchToolbarSearchbar
            .setOnEditorActionListener { _: TextView?, actionId: Int, _: KeyEvent? ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    val searchWord =
                        binding.includeHomeSearchToolbar.editTextSearchToolbarSearchbar.text.toString()
                            .trim()
                    goToSearchResults(searchWord)

                    return@setOnEditorActionListener true
                }
                false
            }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // menu inflate
        if (isSearchbarState) { // 최근 검색어가 띄어진 상태
            binding.includeHomeSearchToolbar.toolbarSearchToolbarToolbar.inflateMenu(R.menu.toolbar_search_menu)
        } else { // 검색 버튼과 함께 그냥 리뷰, 행사 상품 정보, 레시피 정보가 띄워진 상태
            binding.includeHomeMainToolBar.toolbarMainToolbarToolbar.inflateMenu(R.menu.toolbar_search_menu)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.searchMenu_searchBar_search -> {
                if (isSearchbarState) { // 검색창 상태일 때
                    val searchWord =
                        binding.includeHomeSearchToolbar.editTextSearchToolbarSearchbar
                            .text.toString().trim()
                    goToSearchResults(searchWord) // 검색 결과 페이지로 이동
                } else { // 그냥 일반상태 일 때 -> 검색창 프래그먼트로 바꾸기
                    initSearchState()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // 일반상태일 떄
    private fun initMainState() {
        // init toolbar
        isSearchbarState = false
        setSupportActionBar(binding.includeHomeMainToolBar.toolbarMainToolbarToolbar)

        // change fragment
        replaceFragment(nowFragment)

        // MainToolbar visible
        binding.includeHomeMainToolBar.root.visibility = View.VISIBLE // SearchToolbar invisible
        binding.includeHomeSearchToolbar.root.visibility =
            View.INVISIBLE // BottomNavigation menu visible
        binding.bottomNavViewHomeBottomMenu.visibility = View.VISIBLE
    }

    // 검색창 상태일 때
    private fun initSearchState() {
        // init toolbar
        isSearchbarState = true
        setSupportActionBar(binding.includeHomeSearchToolbar.toolbarSearchToolbarToolbar) // 검색창 툴바로 변경

        // change fragment (이건 replace가 아니라 add를 해줘야 함, replace하면 review를 다시 가져와야 하므로)
        supportFragmentManager.beginTransaction()
            .add(binding.frameLayoutHomeContainer.id, SearchHistoryFragment(nowFragment)).commit()

        with(binding) {
            // MainToolbar invisible
            includeHomeMainToolBar.root.visibility = View.INVISIBLE
            includeHomeSearchToolbar.root.visibility = View.VISIBLE
            bottomNavViewHomeBottomMenu.visibility = View.INVISIBLE

            // when navigationIcon clicked in searchState
            includeHomeSearchToolbar.toolbarSearchToolbarToolbar.setNavigationOnClickListener { onBackPressed() }

            // focus searchWindow
            if (includeHomeSearchToolbar.editTextSearchToolbarSearchbar.requestFocus()) {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(
                    includeHomeSearchToolbar.editTextSearchToolbarSearchbar,
                    InputMethodManager.SHOW_IMPLICIT
                )
            }
        }
    }

    // 검색 결과로 이동
    fun goToSearchResults(searchWord: String) {
        if (searchWord.trim().isEmpty()) {
            MyApplication.makeToast(this, "검색", "검색어를 입력해주세요", MotionToastStyle.WARNING)
            return
        }

        val searchResultIntent: Intent =
            if (nowFragment is ReviewsFragment) { // 리뷰에서 검색하면 -> ReviewSearchResultActivity
                Intent(this@HomeActivity, ReviewSearchResultActivity::class.java).apply {
                    putExtra("searchWord", searchWord)
                }
            } else { // 레시피에서 검색하면 -> RecipeSearchResultActivity
                Intent(this@HomeActivity, RecipeSearchResultActivity::class.java).apply {
                    putExtra("searchWord", searchWord)
                }
            }
        startActivity(searchResultIntent)
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.frameLayoutHomeContainer.id, fragment).commit()
    }

    // 검색 결과에서 다시 검색창 모드로 돌아오기
    override fun onRestart() {
        if (isSearchbarState) { // 검색창 상태이면, 검색하고 다시 되돌아왔을 때, 검색 텍스트 지우기
            binding.includeHomeSearchToolbar.editTextSearchToolbarSearchbar.setText("")
        }
        super.onRestart()
    }

    override fun onBackPressed() {
        if (isSearchbarState) { // back previous fragment
            initMainState()
            return
        }
        // Application end
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis()
            Toast.makeText(this, "'뒤로' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show()
            return
        }

        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            finish()
        }
    }


}