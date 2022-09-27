package chatapp.clubs.fragments;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
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
import java.util.List;

import chatapp.client.R;
import chatapp.clubs.ClubProfileActivity;

public class ClubProfileMembersFragment extends Fragment {


    private RecyclerView mClubMembersListView;
    private ClubMembersListViewAdapter mClubMembersListViewAdapter;

    public ClubProfileMembersFragment() {
    }



    public static ClubProfileMembersFragment newInstance() {
        ClubProfileMembersFragment fragment = new ClubProfileMembersFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_club_manager_request_sent_list, container, false);
        mClubMembersListView = (RecyclerView) v.findViewById(R.id.recyclerClubManagerFragmentContainer);
        List<ClubDetails> headerList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            ClubDetails cd = new ClubDetails();
            headerList.add(cd);

        }
        mClubMembersListViewAdapter = new ClubMembersListViewAdapter(headerList);
        RecyclerView.LayoutManager rlayoutManager = new LinearLayoutManager(getContext());
        mClubMembersListView.setLayoutManager(rlayoutManager);
        mClubMembersListView.setItemAnimator(new DefaultItemAnimator());
        mClubMembersListView.setAdapter(mClubMembersListViewAdapter);

        return v;
    }


    public class ClubMembersListViewAdapter extends RecyclerView.Adapter<ClubMembersListViewAdapter.ClubMembersListViewHolder> {

        List<ClubDetails> clubList = new ArrayList<>();

        public ClubMembersListViewAdapter(List<ClubDetails> clubList) {
            this.clubList = clubList;
        }

        @Override
        public ClubMembersListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_club_manager_request_sent_list, parent, false);
            return new ClubMembersListViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ClubMembersListViewHolder holder, int position) {

        }


        @Override
        public int getItemCount() {
            return clubList.size();
        }

        public class ClubMembersListViewHolder extends RecyclerView.ViewHolder {
            public ImageButton profileImage;
            public TextView profileName;
            public TextView profileStatus;

            public ClubMembersListViewHolder(View view) {
                super(view);

                //profileImage= (ImageButton) view.findViewById(R.id.imageButtonClubProfileImage);
                //profileName = (TextView) view.findViewById(R.id.textViewClubProfileName);
                //quote = (TextView) view.findViewById(R.id.textViewClubProfileStatus);

            }
        }
    }

    public class ClubDetails {
        private Image profileImage;
        private String profileName;
        private String profileStatus;

        public ClubDetails() {
        }

        public ClubDetails(Image profileImage, String profileName, String profileStatus) {
            this.profileImage = profileImage;
            this.profileName = profileName;
            this.profileStatus = profileStatus;
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

        public String getProfileStatus() {
            return profileStatus;
        }

        public void setProfileStatus(String profileStatus) {
            this.profileStatus = profileStatus;
        }
    }
}
