package com.mju.csmoa;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.mju.csmoa.databinding.ActivityHomeBinding;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;
    private Fragment nowFragment;
//    private final Map<Integer, Fragment> fragmentMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
    }

    private void init() {

        nowFragment = new ReviewMainFragment();

        // toolbar 설정
//        setSupportActionBar(binding.toolbarHomeToolbar);
        replaceFragment(nowFragment);

//        binding.bottomNavViewHomeBottomMenu.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                return false;
//            }
//        });

        binding.bottomNavViewHomeBottomMenu.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            // switch-case 사용 지양 (구글 권고사항)
            if (itemId == R.id.bottomNavMenu_home_itemReview) { // 제품 리뷰
                if (!(nowFragment instanceof ReviewMainFragment)) {
                    nowFragment = new ReviewMainFragment();
                    replaceFragment(nowFragment);
                }
            } else if (itemId == R.id.recyclerView_itemReview_eventCategory) { // 행사 상품

            } else if (itemId == R.id.bottomNavMenu_home_map) { // 지도


            } else if (itemId == R.id.bottomNavMenu_home_recipe) { // 우리들의 레시피
                if (!(nowFragment instanceof RecipeFragment)) {
                    nowFragment = new RecipeFragment();
                    replaceFragment(nowFragment);
                }
            } else if (itemId == R.id.bottomNavMenu_home_more) { // 더보기
                Log.d("로그", "call more");
                startActivity(new Intent(this, MoreActivity.class));
//                overridePendingTransition(R.anim.activity_slide_in, R.anim.activity_slide_out);
            }

            return true;
        });
    }

    // 프래그먼트 교체
    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayout_home_container, fragment)
                .commit();
    }
}