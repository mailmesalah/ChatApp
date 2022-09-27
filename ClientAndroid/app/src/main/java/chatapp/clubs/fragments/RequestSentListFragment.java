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

public class RequestSentListFragment extends Fragment {


    private RecyclerView mRequestSentListView;
    private RequestSentListViewAdapter mRequestSentListViewAdapter;

    public RequestSentListFragment() {
    }



    public static RequestSentListFragment newInstance() {
        RequestSentListFragment fragment = new RequestSentListFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_club_manager_request_sent_list, container, false);
        mRequestSentListView = (RecyclerView) v.findViewById(R.id.recyclerClubManagerFragmentContainer);
        List<ClubDetails> headerList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            ClubDetails cd = new ClubDetails();
            headerList.add(cd);

        }
        mRequestSentListViewAdapter = new RequestSentListViewAdapter(headerList);
        RecyclerView.LayoutManager rlayoutManager = new LinearLayoutManager(getContext());
        mRequestSentListView.setLayoutManager(rlayoutManager);
        mRequestSentListView.setItemAnimator(new DefaultItemAnimator());
        mRequestSentListView.setAdapter(mRequestSentListViewAdapter);

        return v;
    }


    public class RequestSentListViewAdapter extends RecyclerView.Adapter<RequestSentListViewAdapter.RequestSentListViewHolder> {

        List<ClubDetails> clubList = new ArrayList<>();

        public RequestSentListViewAdapter(List<ClubDetails> clubList) {
            this.clubList = clubList;
        }

        @Override
        public RequestSentListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_club_manager_request_sent_list, parent, false);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getActivity(), ClubProfileActivity.class );
                    i.putExtra("ViewType", ClubProfileActivity.REQUEST_SENDER_PUBLIC);
                    startActivity(i);
                }
            });
            return new RequestSentListViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(RequestSentListViewHolder holder, int position) {

        }


        @Override
        public int getItemCount() {
            return clubList.size();
        }

        public class RequestSentListViewHolder extends RecyclerView.ViewHolder {
            public ImageButton profileImage;
            public TextView profileName;
            public TextView profileStatus;

            public RequestSentListViewHolder(View view) {
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
