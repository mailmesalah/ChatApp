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

public class ClubProfileDetailsFragment extends Fragment {


    private RecyclerView mClubDetailsListView;
    private ClubDetailsListViewAdapter mClubDetailsListViewAdapter;

    public ClubProfileDetailsFragment() {
    }



    public static ClubProfileDetailsFragment newInstance() {
        ClubProfileDetailsFragment fragment = new ClubProfileDetailsFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_club_profile_details_container, container, false);
        mClubDetailsListView = (RecyclerView) v.findViewById(R.id.recyclerClubProfileFragmentContainer);
        List<ClubDetails> headerList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            ClubDetails cd = new ClubDetails();
            headerList.add(cd);
        }
        mClubDetailsListViewAdapter = new ClubDetailsListViewAdapter(headerList);
        RecyclerView.LayoutManager rlayoutManager = new LinearLayoutManager(getContext());
        mClubDetailsListView.setLayoutManager(rlayoutManager);
        mClubDetailsListView.setItemAnimator(new DefaultItemAnimator());
        mClubDetailsListView.setAdapter(mClubDetailsListViewAdapter);

        return v;
    }


    public class ClubDetailsListViewAdapter extends RecyclerView.Adapter<ClubDetailsListViewAdapter.ClubDetailsListViewHolder> {

        List<ClubDetails> clubList = new ArrayList<>();

        public ClubDetailsListViewAdapter(List<ClubDetails> clubList) {
            this.clubList = clubList;
        }

        @Override
        public ClubDetailsListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_club_profile_details_viewer_public, parent, false);

            return new ClubDetailsListViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ClubDetailsListViewHolder holder, int position) {

        }


        @Override
        public int getItemCount() {
            return 1;
        }

        public class ClubDetailsListViewHolder extends RecyclerView.ViewHolder {
            public ImageButton profileImage;
            public TextView profileName;
            public TextView profileStatus;

            public ClubDetailsListViewHolder(View view) {
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
