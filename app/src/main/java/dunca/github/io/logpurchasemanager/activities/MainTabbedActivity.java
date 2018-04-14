package dunca.github.io.logpurchasemanager.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import dunca.github.io.logpurchasemanager.R;
import dunca.github.io.logpurchasemanager.constants.MethodParameterConstants;
import dunca.github.io.logpurchasemanager.fragments.AcquisitionFragment;
import dunca.github.io.logpurchasemanager.fragments.AcquisitionItemFragment;
import dunca.github.io.logpurchasemanager.fragments.AcquisitionItemListFragment;

public class MainTabbedActivity extends AppCompatActivity {
    public static final String EXTRA_ACQUISITION_ID = "extra_acquisition_id";

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide fragments for each of
     * the sections
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents
     */
    private ViewPager mViewPager;
    private int mAcquisitionId;
    private FloatingActionButton mNewAcquisitionItemFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tabbed);

        initViews();
        setupOnClickActions();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TabLayout tabLayout = findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                toggleFabVisibility(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                toggleFabVisibility(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    private void toggleFabVisibility(int tabId) {
        int visibility = tabId == 1 && mAcquisitionId != MethodParameterConstants.INVALID_INDEX ?
                View.VISIBLE : View.INVISIBLE;

        mNewAcquisitionItemFab.setVisibility(visibility);
    }

    private void switchToAcquisitionItemTab() {
        mViewPager.setCurrentItem(2);
    }

    private void initViews() {
        /*
        create the adapter that will return a fragment for each of the four sections of the
        activity
        */
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = findViewById(R.id.fragment_container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(999);

        mNewAcquisitionItemFab = findViewById(R.id.fab);
    }

    private void setupOnClickActions() {
        mNewAcquisitionItemFab.setOnClickListener(view -> switchToAcquisitionItemTab());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.menu_main_tabbed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        // int id = item.getItemId();

        // //noinspection SimplifiableIfStatement
        // if (id == R.id.action_settings) {
        //     return true;
        // }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mAcquisitionId = getIntent().getIntExtra(EXTRA_ACQUISITION_ID,
                MethodParameterConstants.INVALID_INDEX);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the tabs
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page

            if (position == 0) {
                return AcquisitionFragment.newInstance(mAcquisitionId);
            } else if (position == 1) {
                return AcquisitionItemListFragment.newInstance();
            } else if (position == 2) {
                return AcquisitionItemFragment.newInstance(-1);
            }

            return AcquisitionFragment.newInstance(mAcquisitionId);
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return super.getPageTitle(position);
        }
    }
}
