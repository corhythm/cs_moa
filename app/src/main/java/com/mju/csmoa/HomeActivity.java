package com.mju.csmoa;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.mju.csmoa.databinding.ActivityHomeBinding;
import com.mju.csmoa.util.room.database.LocalRoomDatabase;
import com.mju.csmoa.util.room.entity.SearchHistory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;
    private boolean isSearchbarState = false;
    private Fragment nowFragment = null;
    private final String TAG = "로그";
    private long backKeyPressedTime = 0; // 마지막으로 back key를 눌렀던 시간

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initMainState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // menu inflate
        // onCreateOptionsMenu(Menu menu) is called, when setSupportActionBar() is called
        if (isSearchbarState) {
            // searchToolbar
            binding.includeHomeSearchToolbar.toolbarSearchToolbarToolbar.inflateMenu(R.menu.toolbar_search_menu);
        } else {
            // mainToolbar
            binding.includeHomeMainToolBar.toolbarMainToolbarToolbar.inflateMenu(R.menu.toolbar_search_menu);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.searchMenu_searchBar_search) { // 검색이 클릭됐을 때
            if (isSearchbarState) { // 검색창 상태일 때
                saveSearchHistory();
            } else { // 그냥 일반상태 일 때 -> 프래그먼트 교체
//                Toast.makeText(this, "test toast", Toast.LENGTH_SHORT).show();
                initSearchState();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    // 일반상태일 떄
    private void initMainState() {

        isSearchbarState = false;
        setSupportActionBar(binding.includeHomeMainToolBar.toolbarMainToolbarToolbar);


        // MainToolbar visible
        binding.includeHomeMainToolBar.getRoot().setVisibility(View.VISIBLE);
        // SearchToolbar invisible
        binding.includeHomeSearchToolbar.getRoot().setVisibility(View.INVISIBLE);
        // BottomNavigation menu visible
        binding.bottomNavViewHomeBottomMenu.setVisibility(View.VISIBLE);
    }


    // 검색창상태 때
    private void initSearchState() {

        Log.d(TAG, "initSearchState: ");
        isSearchbarState = true;
        setSupportActionBar(binding.includeHomeSearchToolbar.toolbarSearchToolbarToolbar);

        nowFragment = new SearchHistoryFragment();
        replaceFragment(nowFragment);

        // MainToolbar invisible
        binding.includeHomeMainToolBar.getRoot().setVisibility(View.INVISIBLE);
        // SearchToolbar visible
        binding.includeHomeSearchToolbar.getRoot().setVisibility(View.VISIBLE);
        // BottomNavigation menu invisible
        binding.bottomNavViewHomeBottomMenu.setVisibility(View.INVISIBLE);

        // when navigationIcon clicked in searchState
        binding.includeHomeSearchToolbar.toolbarSearchToolbarToolbar.setNavigationOnClickListener(v -> {
            finish();
        });

        // focus searchWindow
        binding.includeHomeSearchToolbar.editTextSearchToolbarSearchbar.requestFocus();

        // forced to raise keyboard up (exception)
//        InputMethodManager imm = (InputMethodManager) this.getSystemService(Service.INPUT_METHOD_SERVICE);
//        imm.showSoftInput((View) binding.editTextHomeSearchbar.getWindowToken(), 0);


        // when text is input in searchbar
        binding.includeHomeSearchToolbar.editTextSearchToolbarSearchbar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (binding.includeHomeSearchToolbar.editTextSearchToolbarSearchbar.getText().toString().length() >= 1) {
                    binding.includeHomeSearchToolbar.editTextSearchToolbarSearchbar.setCompoundDrawablesWithIntrinsicBounds(
                            null, null, ContextCompat.getDrawable(HomeActivity.this, R.drawable.ic_all_delete2), null);
                } else {
                    binding.includeHomeSearchToolbar.editTextSearchToolbarSearchbar.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // when search icon is clicked in soft keyboard.
        binding.includeHomeSearchToolbar.editTextSearchToolbarSearchbar.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                saveSearchHistory();
                return true;
            }
            return false;
        });
    }

    // save search history
    private void saveSearchHistory() {

        if (binding.includeHomeSearchToolbar.editTextSearchToolbarSearchbar.getText().length() < 1) {
            Toast.makeText(getBaseContext(), "검색어를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        String searchWord = binding.includeHomeSearchToolbar.editTextSearchToolbarSearchbar.getText().toString().trim();
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
//        binding.includeHomeSearchToolbar.editTextSearchToolbarSearchbar.setText("");

        // 검색 결과 없음 프래그먼트로 이동
        nowFragment = new NoSearchResultFragment();
        replaceFragment(nowFragment);
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayout_home_container, fragment)
                .commit();
    }

    @Override
    public void onBackPressed() {

        // dirty code
        if (isSearchbarState) { // back previous fragment
//            initMainState();
            if (nowFragment instanceof NoSearchResultFragment) {
              initSearchState();
            }
            if (nowFragment instanceof SearchHistoryFragment) {
                initMainState();
            }

        } else { // Application end
            if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
                backKeyPressedTime = System.currentTimeMillis();
                Toast.makeText(this, "'뒤로' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
                finish();
            }
        }
    }
}