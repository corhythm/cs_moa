package com.mju.csmoa.main.search

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.mju.csmoa.R
import com.mju.csmoa.databinding.ActivitySearchResultBinding
import com.mju.csmoa.util.room.database.LocalRoomDatabase
import com.mju.csmoa.util.room.entity.SearchHistory
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class SearchResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchResultBinding
    private lateinit var nowFragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {

        // toolbar init
        setSupportActionBar(binding.toolbarSearchResultToolbar)
        binding.toolbarSearchResultToolbar.setNavigationOnClickListener { super.onBackPressed() }

        // get searchWord data from HomeActivity
        val searchWord = intent.getStringExtra("searchWord")
        binding.editTextSearchResultSearchbar.setText(searchWord!!)
        saveSearchHistory(searchWord)

        // 검색 결과 없으면 NoSearchResultFragment로 이동
        nowFragment = NoSearchResultFragment()
        replaceFragment(nowFragment)
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.frameLayoutSearchResultContainer.id, fragment)
            .commit()
    }

    // save search history
    private fun saveSearchHistory(searchWord: String) {

        if (searchWord.trim().isEmpty()) {
            return
        }

        val currentDate = SimpleDateFormat("yy.MM.dd HH:mm:ss", Locale.getDefault()).format(Date())
        val database = LocalRoomDatabase.getDatabase(this)

        lifecycleScope.launch {
            database.searchHistoryDao().insertSearchHistory(
                SearchHistory(searchWord = searchWord, createdAt = currentDate, type = 0)
            )
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        binding.toolbarSearchResultToolbar.inflateMenu(R.menu.toolbar_search_menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.searchMenu_searchBar_search -> {

            }
        }

        return super.onOptionsItemSelected(item)
    }


}