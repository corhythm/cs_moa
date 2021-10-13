package com.mju.csmoa;

import static com.mju.csmoa.util.room.database.LocalRoomDatabase.MIGRATION_1_2;

import android.app.Service;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.room.Room;

import com.mju.csmoa.databinding.ActivitySearchBinding;
import com.mju.csmoa.util.room.database.LocalRoomDatabase;
import com.mju.csmoa.util.room.entity.SearchHistory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity {

    private ActivitySearchBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
    }

    // menu inflate
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        binding.toolbarSearchToolbar.inflateMenu(R.menu.searchbar);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.searchMenu_searchBar_search) { // 검색이 클릭됐을 때
            saveSearchHistory();
        }

        return super.onOptionsItemSelected(item);
    }

    // save search history
    private void saveSearchHistory() {

        String searchWord = binding.editTextSearchSearchbar.getText().toString();
        String currentDate = new SimpleDateFormat("yy.MM.dd", Locale.getDefault()).format(new Date());

        LocalRoomDatabase database = LocalRoomDatabase.getDatabase(this);
        LocalRoomDatabase.getDatabaseWriteExecutor().execute(() -> {
            database.getSearchHistoryDao().insertSearchHistory(
                    SearchHistory
                            .builder()
                            .searchWord(searchWord)
                            .createdAt(currentDate)
                            .type(0)
                            .build());
        });
        binding.editTextSearchSearchbar.setText("");
    }

    private void init() {

        // connect toolBar with actionBar
        setSupportActionBar(binding.toolbarSearchToolbar);

        // when navigationIcon clicked
        binding.toolbarSearchToolbar.setNavigationOnClickListener(v -> {
            finish();
        });

        // focus searchWindow
        binding.editTextSearchSearchbar.requestFocus();

        // forced to raise keyboard up
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Service.INPUT_METHOD_SERVICE);
        imm.showSoftInput((View) binding.editTextSearchSearchbar.getWindowToken(), 0);

        // set default fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayout_search_container, new SearchHistoryFragment())
                .commit();

        // when text is input in searchbar
        binding.editTextSearchSearchbar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (binding.editTextSearchSearchbar.getText().toString().length() >= 1) {
                    binding.editTextSearchSearchbar.setCompoundDrawablesWithIntrinsicBounds(
                            null, null, ContextCompat.getDrawable(SearchActivity.this, R.drawable.ic_all_delete2), null);
                } else {
                    binding.editTextSearchSearchbar.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // when search icon is clicked in soft keyboard.
        binding.editTextSearchSearchbar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    saveSearchHistory();
                    return true;
                }
                return false;
            }
        });

    }
}