package chatapp.dashboard.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import chatapp.client.R;
import chatapp.clubs.ClubManagerActivity;

public class ClubChatHeaderFragment extends Fragment {

    //For Chat header list
    private RecyclerView mRVChatHeaderListView;
    private ClubChatHeaderFragment.ClubChatHeaderAdapter mChatHeaderAdapter;

    //FAB
    private FloatingActionButton fabAddClubs;
    private FloatingActionButton fabClubs;
    private FloatingActionButton fabSearch;


    public ClubChatHeaderFragment() {
    }

    public static ClubChatHeaderFragment newInstance() {
        ClubChatHeaderFragment fragment = new ClubChatHeaderFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the chat header list layout for this fragment
        View v = inflater.inflate(R.layout.fragment_club_chat_header, container, false);

        fabAddClubs = (FloatingActionButton) v.findViewById(R.id.fabAddChat);
        fabClubs = (FloatingActionButton) v.findViewById(R.id.fabClubs);
        fabSearch = (FloatingActionButton) v.findViewById(R.id.fabSearch);

        mRVChatHeaderListView = (RecyclerView) v.findViewById(R.id.recyclerClubChatHeader);
        mRVChatHeaderListView.setHasFixedSize(true);

        List<ClubChatHeader> headerList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            ClubChatHeader dch = new ClubChatHeader();
            headerList.add(dch);

        }
        mChatHeaderAdapter = new ClubChatHeaderAdapter(headerList);
        final RecyclerView.LayoutManager rlayoutManager = new LinearLayoutManager(getContext());
        mRVChatHeaderListView.setLayoutManager(rlayoutManager);
        mRVChatHeaderListView.setItemAnimator(new DefaultItemAnimator());
        mRVChatHeaderListView.setAdapter(mChatHeaderAdapter);



        fabAddClubs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetClubList bottomSheetDialogFragment = new BottomSheetClubList();
                bottomSheetDialogFragment.show(getFragmentManager(), bottomSheetDialogFragment.getTag());
            }
        });

        fabSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetSearchClubChat bottomSheetDialogFragment = new BottomSheetSearchClubChat();
                bottomSheetDialogFragment.show(getFragmentManager(), bottomSheetDialogFragment.getTag());
            }
        });

        fabClubs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), ClubManagerActivity.class);
                startActivity(i);
            }
        });
        return v;
    }

    public static class BottomSheetSearchClubChat extends BottomSheetDialogFragment {

        private RecyclerView mRVSearchChatResult;
        private SearchChatViewAdapter mSearchChatViewAdapter;

        @Override
        public void setupDialog(final Dialog dialog, int style) {
            super.setupDialog(dialog, style);
            View contentView = View.inflate(getContext(), R.layout.bottom_sheet_search_club_chat, null);

            //Inflate Search Chat bottom sheet
            mRVSearchChatResult = (RecyclerView) contentView.findViewById(R.id.recyclerBottomSheetSearchResultClubChat);
            List<ChatSearchResult> searchResultList = new ArrayList<>();
            for (int i = 0; i < 20; i++) {
                ChatSearchResult searchResult = new ChatSearchResult();
                searchResultList.add(searchResult);

            }
            mSearchChatViewAdapter = new SearchChatViewAdapter(searchResultList);
            mRVSearchChatResult.setLayoutManager(new LinearLayoutManager(getContext()));
            mRVSearchChatResult.setItemAnimator(new DefaultItemAnimator());
            mRVSearchChatResult.setAdapter(mSearchChatViewAdapter);

            dialog.setContentView(contentView);
        }

        public class SearchChatViewAdapter extends RecyclerView.Adapter<SearchChatViewAdapter.SearchChatViewHolder> {

            List<ChatSearchResult> ClubList = new ArrayList<>();

            public SearchChatViewAdapter(List<ChatSearchResult> ClubList) {
                this.ClubList = ClubList;
            }

            @Override
            public SearchChatViewAdapter.SearchChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_bottom_sheet_search_club_chat, parent, false);
                return new SearchChatViewHolder(itemView);
            }

            @Override
            public void onBindViewHolder(SearchChatViewHolder holder, int position) {

            }


            @Override
            public int getItemCount() {
                return ClubList.size();
            }

            public class SearchChatViewHolder extends RecyclerView.ViewHolder {
                public TextView profileName;
                public TextView chatDateTime;
                public TextView description;

                public SearchChatViewHolder(View view) {
                    super(view);

                    profileName = (TextView) view.findViewById(R.id.textViewBottomSheetClubChatProfileName);
                    chatDateTime = (TextView) view.findViewById(R.id.textViewBottomSheetClubChatDateTime);
                    description = (TextView) view.findViewById(R.id.textViewBottomSheetClubChatDescription);

                }
            }
        }

        public class ChatSearchResult {
            private String profileName;
            private String chatDateTime;
            private String description;

            public ChatSearchResult() {
            }

            public ChatSearchResult(String profileName, String chatDateTime, String description) {
                this.profileName = profileName;
                this.chatDateTime = chatDateTime;
                this.description = description;
            }

            public String getProfileName() {
                return profileName;
            }

            public void setProfileName(String profileName) {
                this.profileName = profileName;
            }

            public String getChatDateTime() {
                return chatDateTime;
            }

            public void setChatDateTime(String chatDateTime) {
                this.chatDateTime = chatDateTime;
            }

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }
        }
    }

    public static class BottomSheetClubList extends BottomSheetDialogFragment {

        private RecyclerView mRVAddClubsListView;
        private ClubListViewAdapter mClubListViewAdapter;

        @Override
        public void setupDialog(final Dialog dialog, int style) {
            super.setupDialog(dialog, style);
            View contentView = View.inflate(getContext(), R.layout.bottom_sheet_club_list, null);

            //Inflate bottom sheet for Club list
            mRVAddClubsListView = (RecyclerView) contentView.findViewById(R.id.recyclerBottomSheetClubList);
            mRVAddClubsListView.setHasFixedSize(true);

            List<BottomSheetClub> ClubList = new ArrayList<>();
            for (int i = 0; i < 20; i++) {
                BottomSheetClub Club = new BottomSheetClub();
                ClubList.add(Club);

            }
            mClubListViewAdapter = new ClubListViewAdapter(ClubList);
            mRVAddClubsListView.setLayoutManager(new LinearLayoutManager(getContext()));
            mRVAddClubsListView.setItemAnimator(new DefaultItemAnimator());
            mRVAddClubsListView.setAdapter(mClubListViewAdapter);

            dialog.setContentView(contentView);
        }

        public class ClubListViewAdapter extends RecyclerView.Adapter<ClubListViewAdapter.ClubListViewHolder> {

            List<BottomSheetClub> ClubList = new ArrayList<>();

            public ClubListViewAdapter(List<BottomSheetClub> ClubList) {
                this.ClubList = ClubList;
            }

            @Override
            public ClubListViewAdapter.ClubListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_bottom_sheet_club, parent, false);
                return new ClubListViewHolder(itemView);
            }

            @Override
            public void onBindViewHolder(ClubListViewHolder holder, int position) {

            }


            @Override
            public int getItemCount() {
                return ClubList.size();
            }

            public class ClubListViewHolder extends RecyclerView.ViewHolder {
                public ImageButton profileImage;
                public TextView profileName;
                public TextView lastUpdateDateTime;

                public ClubListViewHolder(View view) {
                    super(view);
                    profileImage = (ImageButton) view.findViewById(R.id.imageButtonBottomSheetProfile);
                    profileName = (TextView) view.findViewById(R.id.textViewBottomSheetProfileName);
                    lastUpdateDateTime = (TextView) view.findViewById(R.id.textViewBottomSheetProfileStatus);
                }
            }
        }

        public class BottomSheetClub {
            private Image profileImage;
            private String profileName;
            private String description;

            public BottomSheetClub() {
            }

            public BottomSheetClub(Image profileImage, String profileName, String description) {
                this.profileImage = profileImage;
                this.profileName = profileName;
                this.description = description;
            }

            public Image getProfileImage() {
                return profileImage;
            }

            public void setProfileImage(Image profileImage) {
                this.profileImage = profileImage;
            }

            public String getProfileName() {
                return profileName;
            }

            public void setProfileName(String profileName) {
                this.profileName = profileName;
            }

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }
        }
    }

    public class ClubChatHeaderAdapter extends RecyclerView.Adapter<ClubChatHeaderAdapter.ClubChatHeaderViewHolder> {

        private List<ClubChatHeader> chatHeaderList;

        public ClubChatHeaderAdapter(List<ClubChatHeader> chatHeaderList) {
            this.chatHeaderList = chatHeaderList;
        }

        @Override
        public ClubChatHeaderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_club_chat_header, parent, false);
            return new ClubChatHeaderViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ClubChatHeaderViewHolder holder, int position) {
            ClubChatHeader chatHead = chatHeaderList.get(position);

        }

        @Override
        public int getItemCount() {
            return chatHeaderList.size();
        }

        public class ClubChatHeaderViewHolder extends RecyclerView.ViewHolder {
            public ImageButton profileImage;
            public TextView profileName;
            public TextView lastUpdateDateTime;
            public TextView unseenMessages;

            public ClubChatHeaderViewHolder(View view) {
                super(view);
                //profileImage = (ImageButton) view.findViewById(R.id.imageButtonClubChatHeaderProfileImage);
                //profileName = (TextView) view.findViewById(R.id.textViewClubChatHeaderProfileName);
                //lastUpdateDateTime = (TextView) view.findViewById(R.id.textViewClubChatHeaderLastUpdate);
                //unseenMessages = (TextView) view.findViewById(R.id.textViewClubChatHeaderUnseenMessages);
            }
        }

    }

    public class ClubChatHeader {
        private Image profileImage;
        private String profileName;
        private Date lastUpdateDateTime;
        private int unseenMessages;

        public ClubChatHeader() {
        }

        public ClubChatHeader(Image profileImage, String profileName, Date lastUpdateDateTime, int unseenMessages) {
            this.profileImage = profileImage;
            this.profileName = profileName;
            this.lastUpdateDateTime = lastUpdateDateTime;
            this.unseenMessages = unseenMessages;
        }

        public Image getProfileImage() {
            return profileImage;
        }

        public void setProfileImage(Image profileImage) {
            this.profileImage = profileImage;
        }

        public String getProfileName() {
            return profileName;
        }

        public void setProfileName(String profileName) {
            this.profileName = profileName;
        }

        public Date getLastUpdateDateTime() {
            return lastUpdateDateTime;
        }

        public void setLastUpdateDateTime(Date lastUpdateDateTime) {
            this.lastUpdateDateTime = lastUpdateDateTime;
        }

        public int getUnseenMessages() {
            return unseenMessages;
        }

        public void setUnseenMessages(int unseenMessages) {
            this.unseenMessages = unseenMessages;
        }
    }


}