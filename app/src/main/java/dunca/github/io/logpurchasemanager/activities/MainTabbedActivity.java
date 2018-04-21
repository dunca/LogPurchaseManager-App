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
import android.view.View;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import dunca.github.io.logpurchasemanager.R;
import dunca.github.io.logpurchasemanager.constants.MethodParameterConstants;
import dunca.github.io.logpurchasemanager.fragments.AcquisitionFragment;
import dunca.github.io.logpurchasemanager.fragments.AcquisitionItemFragment;
import dunca.github.io.logpurchasemanager.fragments.AcquisitionItemListFragment;
import dunca.github.io.logpurchasemanager.fragments.AcquisitionLogPriceListFragment;
import dunca.github.io.logpurchasemanager.fragments.events.AcquisitionIdEvent;
import io.github.dunca.logpurchasemanager.shared.model.Acquisition;

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

        EventBus.getDefault().register(this);

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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
    }

    private void toggleFabVisibility(int tabId) {
        int visibility = tabId == 1 && mAcquisitionId != MethodParameterConstants.INVALID_INDEX ?
                View.VISIBLE : View.INVISIBLE;

        mNewAcquisitionItemFab.setVisibility(visibility);
    }

    private void initViews() {
        /*
        create the adapter that will return a fragment for each of the four sections of the
        activity
        */
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = findViewById(R.id.fragment_container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // keep up to 999 fragments alive. If we don't set this, fragments are destroyed and
        // recreated (eg.: switching to tab 1 from tab 3, kills the 3rd fragment, but inits the
        // first and the second
        mViewPager.setOffscreenPageLimit(999);

        mNewAcquisitionItemFab = findViewById(R.id.fab);
    }

    private void setupOnClickActions() {
        mNewAcquisitionItemFab.setOnClickListener(view -> mViewPager.setCurrentItem(2));
    }

    @Override
    protected void onResume() {
        super.onResume();

        mAcquisitionId = getIntent().getIntExtra(EXTRA_ACQUISITION_ID, MethodParameterConstants.INVALID_INDEX);
    }

    /**
     * We subscribe to an {@link AcquisitionIdEvent}. This type of event is sent by
     * the {@link AcquisitionFragment} fragment when a new {@link Acquisition} instance is saved
     *
     * @param event an {@link AcquisitionIdEvent} instance corresponding to the currently selected
     *              {@link Acquisition} object
     */
    @Subscribe
    public void onAcquisitionIdEvent(AcquisitionIdEvent event) {
        mAcquisitionId = event.getAcquisitionId();
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
                return AcquisitionItemFragment.newInstance();
            } else if (position == 3) {
                return AcquisitionLogPriceListFragment.newInstance();
            }

            throw new IllegalStateException("Invalid tab id");
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
