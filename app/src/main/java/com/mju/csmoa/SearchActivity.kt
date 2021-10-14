package com.mju.csmoa

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView.OnEditorActionListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.mju.csmoa.databinding.ActivitySearchBinding
import com.mju.csmoa.util.room.database.LocalRoomDatabase
import com.mju.csmoa.util.room.entity.SearchHistory
import java.text.SimpleDateFormat
import java.util.*

class SearchActivity : AppCompatActivity() {
    private var binding: ActivitySearchBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        init()
    }

    // menu inflate
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        binding!!.toolbarSearchToolbar.inflateMenu(R.menu.searchbar)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.searchMenu_searchBar_search) { // 검색이 클릭됐을 때
            saveSearchHistory()
        }
        return super.onOptionsItemSelected(item)
    }

    // save search history
    private fun saveSearchHistory() {
        val searchWord = binding!!.editTextSearchSearchbar.text.toString()
        val currentDate = SimpleDateFormat("yy.MM.dd", Locale.getDefault()).format(Date())
        val database = LocalRoomDatabase.getDatabase(this)
        LocalRoomDatabase.getDatabaseWriteExecutor().execute {
            database.searchHistoryDao.insertSearchHistory(
                    SearchHistory
                            .builder()
                            .searchWord(searchWord)
                            .createdAt(currentDate)
                            .type(0)
                            .build())
        }
        binding!!.editTextSearchSearchbar.setText("")
    }

    private fun init() {

        // connect toolBar with actionBar
        setSupportActionBar(binding!!.toolbarSearchToolbar)

        // when navigationIcon clicked
        binding!!.toolbarSearchToolbar.setNavigationOnClickListener { v: View? -> finish() }

        // focus searchWindow
        binding!!.editTextSearchSearchbar.requestFocus()

        // forced to raise keyboard up
        val imm = this.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding!!.editTextSearchSearchbar.windowToken as View, 0)

        // set default fragment
        supportFragmentManager.beginTransaction()
                .replace(R.id.frameLayout_search_container, SearchHistoryFragment())
                .commit()

        // when text is input in searchbar
        binding!!.editTextSearchSearchbar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (binding!!.editTextSearchSearchbar.text.toString().isNotEmpty()) {
                    binding!!.editTextSearchSearchbar.setCompoundDrawablesWithIntrinsicBounds(
                            null, null, ContextCompat.getDrawable(this@SearchActivity, R.drawable.ic_all_delete2), null)
                } else {
                    binding!!.editTextSearchSearchbar.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })

        // when search icon is clicked in soft keyboard.
        binding!!.editTextSearchSearchbar.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                saveSearchHistory()
                return@OnEditorActionListener true
            }
            false
        })
    }
}