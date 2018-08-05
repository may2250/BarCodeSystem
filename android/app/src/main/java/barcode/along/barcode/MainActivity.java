package barcode.along.barcode;

import android.support.annotation.NonNull;
import android.support.design.bottomnavigation.LabelVisibilityMode;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import barcode.along.barcode.Utils.ActivityUtils;
import barcode.along.barcode.Utils.ViewAdapter;
import barcode.along.barcode.fragment.AboutFragment;
import barcode.along.barcode.fragment.ScannerFragment;
import barcode.along.barcode.fragment.SearchFragment;

public class MainActivity extends BaseActivity {
    private List<Fragment> mFragmentList;
    private Fragment mCurrentFragment;
    //private ViewPager viewPager;
    //private Toolbar mToolbar;
   // private List<View> mViews;
    @Override
    public int initContentView() {
        return R.layout.activity_main;
    }

    @Override
    protected void initUIAndListener() {
        //mToolbar = (Toolbar) findViewById(R.id.toolbar);

    }

    @Override
    protected void initData() {
        //setSupportActionBar(mToolbar);
        //mToolbar.setTitle(getString(R.string.title_scanner));
        mFragmentList = new ArrayList<>();
        mFragmentList.add(ScannerFragment.newInstance());
        mFragmentList.add(SearchFragment.newInstance());
        mFragmentList.add(AboutFragment.newInstance());

       // mViews = new ArrayList<>();
       // LayoutInflater lf = getLayoutInflater().from(this);
       // View searchView = lf.inflate(R.layout.activity_search, null);
       // View scannerView = lf.inflate(R.layout.activity_scanner, null);
       // mViews.add(scannerView);
       // mViews.add(searchView);
        ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), mFragmentList.get(0), R.id.fragment);
        mCurrentFragment = mFragmentList.get(0);
        //viewPager = (ViewPager) findViewById(R.id.viewpager);
        //PagerAdapter adapter=new ViewAdapter(mViews);
       // viewPager.setAdapter(adapter);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
        //BottomNavigationViewHelper.disableShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_scanner:
                    switchFragment(mCurrentFragment, mFragmentList.get(0), item.getTitle());
                    //viewPager.setCurrentItem(0);
                    return true;
                case R.id.navigation_search:
                    switchFragment(mCurrentFragment, mFragmentList.get(1), item.getTitle());
                    //viewPager.setCurrentItem(1);
                    return true;
                case R.id.navigation_about:
                    switchFragment(mCurrentFragment, mFragmentList.get(2), item.getTitle());
                    //viewPager.setCurrentItem(2);
                    return true;
            }
            return false;
        }

    };

   public void switchFragment(Fragment from, Fragment to, CharSequence title) {
        if (mCurrentFragment != to) {
            mCurrentFragment = to;
            FragmentTransaction transaction = getSupportFragmentManager().
                    beginTransaction();
            //mToolbar.setTitle(title);
            if (!to.isAdded()) {
                transaction.hide(from).add(R.id.fragment, to).commit();
            } else {
                transaction.hide(from).show(to).commit();
            }
        }
   }

}
