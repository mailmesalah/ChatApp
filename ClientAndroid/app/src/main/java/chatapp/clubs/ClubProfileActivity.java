package chatapp.clubs;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import chatapp.client.R;
import chatapp.clubs.fragments.BlockListFragment;
import chatapp.clubs.fragments.ClubListFragment;
import chatapp.clubs.fragments.ClubProfileDetailsFragment;
import chatapp.clubs.fragments.ClubProfileMembersFragment;
import chatapp.clubs.fragments.RequestReceiptListFragment;
import chatapp.clubs.fragments.RequestSentListFragment;

public class ClubProfileActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;

    public static final int VIEWER_PUBLIC=0,VIEWER_PRIVATE=1,REQUEST_RECEIVER_PUBLIC=3,REQUEST_RECEIVER_PRIVATE=4,REQUEST_RECEIVER_SECRET=5,REQUEST_SENDER_PUBLIC=6, REQUEST_SENDER_PRIVATE=7,REQUEST_SENDER_SECRET=8,BLOCKED_PUBLIC_PRIVATE_SECRET=9,ADMIN_PUBLIC_PRIVATE_SECRET=10,MEMBER_PUBLIC_PRIVATE_SECRET=11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_profile);

        mViewPager = (ViewPager) findViewById(R.id.clubProfileContainer);
        createTabs(mViewPager);

        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewPager);

}

    private void createTabs(ViewPager viewPager){
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mSectionsPagerAdapter.addFragment(ClubProfileDetailsFragment.newInstance(),"Club");
        mSectionsPagerAdapter.addFragment(ClubProfileMembersFragment.newInstance(),"Members");
        viewPager.setAdapter(mSectionsPagerAdapter);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position)  {
            return mFragmentList.get(position);
        }

        private void addFragment(Fragment fragment, String title){
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}
