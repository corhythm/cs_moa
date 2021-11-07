package com.mju.csmoa.home

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.mju.csmoa.*
import com.mju.csmoa.databinding.ActivityHomeBinding
import com.mju.csmoa.home.event_item.EventItemsFragment
import com.mju.csmoa.home.more.MoreFragment
import com.mju.csmoa.home.search.SearchResultActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.util.*

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private var isSearchbarState = false
    private lateinit var nowFragment: Fragment
    private val TAG = "로그"
    private var backKeyPressedTime: Long = 0 // 마지막으로 back key를 눌렀던 시간

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initBottomNavigationView()
        initMainState()
    }

    private fun initBottomNavigationView() {

        binding.bottomNavViewHomeBottomMenu.setOnItemSelectedListener { menuItem ->
            val toolbar = binding.includeHomeMainToolBar.toolbarMainToolbarToolbar
            val searchMenu =
                binding.includeHomeMainToolBar.toolbarMainToolbarToolbar.menu.findItem(R.id.searchMenu_searchBar_search)

            when (menuItem.itemId) {
                R.id.bottomNavMenu_home_itemReview -> {
                    toolbar.title = "제품 리뷰"
                    searchMenu.isVisible = true
                    initMainState()
                }
                R.id.bottomNavMenu_home_itemEvent -> {
                    toolbar.title = "행사 상품"
                    searchMenu.isVisible = true
                    nowFragment = if (nowFragment !is EventItemsFragment) EventItemsFragment() else nowFragment
                    replaceFragment(nowFragment)
                }
                R.id.bottomNavMenu_home_map -> {
                    toolbar.title = "주변 편의점"
                    searchMenu.isVisible = false
                }
                R.id.bottomNavMenu_home_recipe -> {
                    toolbar.title = "꿀조합 레시피"
                    searchMenu.isVisible = true
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
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        // menu inflate
        // onCreateOptionsMenu(Menu menu) is called, when setSupportActionBar() is called
        if (isSearchbarState) { // searchToolbar
            binding.includeHomeSearchToolbar.toolbarSearchToolbarToolbar.inflateMenu(R.menu.toolbar_search_menu)
        } else { // mainToolbar
            binding.includeHomeMainToolBar.toolbarMainToolbarToolbar.inflateMenu(R.menu.toolbar_search_menu)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.searchMenu_searchBar_search -> {
                if (isSearchbarState) { // 검색창 상태일 때
                    val searchWord =
                        binding.includeHomeSearchToolbar.editTextSearchToolbarSearchbar.text.toString()
                            .trim()
                    goToSearchResult(searchWord)
                } else { // 그냥 일반상태 일 때 -> 프래그먼트 교체
                    //                Toast.makeText(this, "test toast", Toast.LENGTH_SHORT).show();
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
        nowFragment = ReviewMainFragment()
        replaceFragment(nowFragment)

        // MainToolbar visible
        binding.includeHomeMainToolBar.root.visibility = View.VISIBLE // SearchToolbar invisible
        binding.includeHomeSearchToolbar.root.visibility =
            View.INVISIBLE // BottomNavigation menu visible
        binding.bottomNavViewHomeBottomMenu.visibility = View.VISIBLE
    }

    // 검색창상태 때
    private fun initSearchState() {

        // init toolbar
        isSearchbarState = true
        setSupportActionBar(binding.includeHomeSearchToolbar.toolbarSearchToolbarToolbar)

        // change fragment
        nowFragment = SearchHistoryFragment()
        replaceFragment(nowFragment as SearchHistoryFragment)

        // MainToolbar invisible
        binding.includeHomeMainToolBar.root.visibility = View.INVISIBLE // SearchToolbar visible
        binding.includeHomeSearchToolbar.root.visibility =
            View.VISIBLE // BottomNavigation menu invisible
        binding.bottomNavViewHomeBottomMenu.visibility = View.INVISIBLE

        // when navigationIcon clicked in searchState
        binding.includeHomeSearchToolbar.toolbarSearchToolbarToolbar.setNavigationOnClickListener { onBackPressed() }

        // focus searchWindow
        binding.includeHomeSearchToolbar.editTextSearchToolbarSearchbar.requestFocus()

        // forced to raise keyboard up (exception)
        //        InputMethodManager imm = (InputMethodManager) this.getSystemService(Service.INPUT_METHOD_SERVICE);
        //        imm.showSoftInput((View) binding.editTextHomeSearchbar.getWindowToken(), 0);
        //        val imm = getSystemService(Service.INPUT_METHOD_SERVICE) as InputMethodManager
        //        imm.showSoftInput(binding.includeHomeSearchToolbar.editTextSearchToolbarSearchbar.windowToken as View, 0)

        // when text is input in searchbar
        val searchBarTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (binding.includeHomeSearchToolbar.editTextSearchToolbarSearchbar.text.toString()
                        .isNotEmpty()
                ) {
                    binding.includeHomeSearchToolbar.editTextSearchToolbarSearchbar.setCompoundDrawablesWithIntrinsicBounds(
                        null,
                        null,
                        ContextCompat.getDrawable(this@HomeActivity, R.drawable.ic_all_delete2),
                        null
                    )
                } else {
                    binding.includeHomeSearchToolbar.editTextSearchToolbarSearchbar.setCompoundDrawablesWithIntrinsicBounds(
                        null, null, null, null
                    )
                }
            }

            override fun afterTextChanged(s: Editable) {}
        }

        binding.includeHomeSearchToolbar.editTextSearchToolbarSearchbar.addTextChangedListener(
            searchBarTextWatcher
        )

        // when search icon is clicked in soft keyboard.
        binding.includeHomeSearchToolbar.editTextSearchToolbarSearchbar.setOnEditorActionListener { _: TextView?, actionId: Int, _: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val searchWord =
                    binding.includeHomeSearchToolbar.editTextSearchToolbarSearchbar.text.toString()
                        .trim()
                goToSearchResult(searchWord)

                return@setOnEditorActionListener true
            }
            false
        }
    }

    fun goToSearchResult(searchWord: String) {

        if (searchWord.trim().isEmpty()) {
            Toast.makeText(baseContext, "검색어를 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        val searchIntent = Intent(this@HomeActivity, SearchResultActivity::class.java).apply {
            putExtra("searchWord", searchWord)
        }
        startActivity(searchIntent)
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.frameLayoutHomeContainer.id, fragment).commit()
    }

    override fun onRestart() {
        if (isSearchbarState) {
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