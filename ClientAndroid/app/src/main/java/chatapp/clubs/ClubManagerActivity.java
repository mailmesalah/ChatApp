package chatapp.clubs;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import chatapp.client.R;
import chatapp.clubs.fragments.BlockListFragment;
import chatapp.clubs.fragments.ClubListFragment;
import chatapp.clubs.fragments.RequestReceiptListFragment;
import chatapp.clubs.fragments.RequestSentListFragment;

public class ClubManagerActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private FloatingActionButton fabSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_manager);

        mViewPager = (ViewPager) findViewById(R.id.clubManagerContainer);
        createTabs(mViewPager);

        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewPager);

        fabSearch = (FloatingActionButton) findViewById(R.id.fabSearch);
        //setupTabIcons(mTabLayout);


        fabSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetSearchClubManager bottomSheetDialogFragment = new BottomSheetSearchClubManager();
                bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
            }
        });

}

    private void setupTabIcons(TabLayout tabLayout){
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_map);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_dual_header);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_club_header);
    }

    private void createTabs(ViewPager viewPager){
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mSectionsPagerAdapter.addFragment(ClubListFragment.newInstance(),"Club List");
        mSectionsPagerAdapter.addFragment(RequestReceiptListFragment.newInstance(),"Request Received");
        mSectionsPagerAdapter.addFragment(RequestSentListFragment.newInstance(),"Request Sent");
        mSectionsPagerAdapter.addFragment(BlockListFragment.newInstance(),"Block List");
        viewPager.setAdapter(mSectionsPagerAdapter);
    }

    public static class BottomSheetSearchClubManager extends BottomSheetDialogFragment {

        private RecyclerView mRVSearchResult;
        private SearchClubViewAdapter mSearchClubViewAdapter;

        @Override
        public void setupDialog(final Dialog dialog, int style) {
            super.setupDialog(dialog, style);
            View contentView = View.inflate(getContext(), R.layout.bottom_sheet_club_manager_search, null);

            //Inflate Search Club bottom sheet
            mRVSearchResult = (RecyclerView) contentView.findViewById(R.id.recyclerClubManagerBottomSheetSearchResult);

            mSearchClubViewAdapter = new SearchClubViewAdapter();
            mRVSearchResult.setLayoutManager(new LinearLayoutManager(getActivity()));
            mRVSearchResult.setItemAnimator(new DefaultItemAnimator());
            mRVSearchResult.setAdapter(mSearchClubViewAdapter);
            dialog.setContentView(contentView);
        }

        public class SearchClubViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

            private  final int CONTACT=0,REQUEST_SENT=1,REQUEST_RECEIVED=2,BLOCKED=3,ADMIN=4;

            public SearchClubViewAdapter() {
            }

            @Override
            public int getItemViewType(int position) {
                if(position<5){
                    return CONTACT;
                }else if(position<10){
                    return REQUEST_RECEIVED;
                }else if(position<13){
                    return REQUEST_SENT;
                }else if(position<15){
                    return BLOCKED;
                }else{
                    return ADMIN;
                }
            }

            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View itemView;
                switch(viewType){
                    case CONTACT:
                        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_club_manager_club_list, parent, false);
                        itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i = new Intent(getContext(), ClubProfileActivity.class );
                                i.putExtra("ViewType",CONTACT);
                                startActivity(i);
                            }
                        });
                        break;
                    case REQUEST_SENT:
                        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_club_manager_request_sent_list, parent, false);
                        itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i = new Intent(getContext(), ClubProfileActivity.class );
                                i.putExtra("ViewType",REQUEST_SENT);
                                startActivity(i);
                            }
                        });
                        break;
                    case REQUEST_RECEIVED:
                        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_club_manager_request_receipt_list, parent, false);
                        itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i = new Intent(getContext(), ClubProfileActivity.class );
                                i.putExtra("ViewType",REQUEST_RECEIVED);
                                startActivity(i);
                            }
                        });
                        break;
                    case BLOCKED:
                        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_club_manager_block_list, parent, false);
                        itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i = new Intent(getContext(), ClubProfileActivity.class );
                                i.putExtra("ViewType",BLOCKED);
                                startActivity(i);
                            }
                        });
                        break;
                    case ADMIN:
                        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_club_manager_club_admin_list, parent, false);
                        itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i = new Intent(getContext(), ClubProfileActivity.class );
                                i.putExtra("ViewType",ADMIN);
                                startActivity(i);
                            }
                        });
                        break;
                    default:
                        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_club_manager_club_list, parent, false);
                        itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i = new Intent(getContext(), ClubProfileActivity.class );
                                i.putExtra("ViewType",CONTACT);
                                startActivity(i);
                            }
                        });
                }

                return new SearchClubViewHolder(itemView);
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            }


            @Override
            public int getItemCount() {
                return 20;
            }

            public class SearchClubViewHolder extends RecyclerView.ViewHolder {
                public TextView profileName;
                public TextView chatDateTime;
                public TextView description;

                public SearchClubViewHolder(View view) {
                    super(view);

                    profileName = (TextView) view.findViewById(R.id.textViewBottomSheetDualChatProfileName);
                    chatDateTime = (TextView) view.findViewById(R.id.textViewBottomSheetDualChatDateTime);
                    description = (TextView) view.findViewById(R.id.textViewBottomSheetDualChatDescription);

                }
            }
        }
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
