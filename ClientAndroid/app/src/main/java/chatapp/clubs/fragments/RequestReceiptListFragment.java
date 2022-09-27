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

public class RequestReceiptListFragment extends Fragment {


    private RecyclerView mRequestReceiptListView;
    private RequestReceiptListViewAdapter mRequestReceiptListViewAdapter;

    public RequestReceiptListFragment() {
    }



    public static RequestReceiptListFragment newInstance() {
        RequestReceiptListFragment fragment = new RequestReceiptListFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_club_manager_request_receipt_list, container, false);
        mRequestReceiptListView = (RecyclerView) v.findViewById(R.id.recyclerClubManagerFragmentContainer);
        List<ClubDetails> headerList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            ClubDetails cd = new ClubDetails();
            headerList.add(cd);

        }
        mRequestReceiptListViewAdapter = new RequestReceiptListViewAdapter(headerList);
        RecyclerView.LayoutManager rlayoutManager = new LinearLayoutManager(getContext());
        mRequestReceiptListView.setLayoutManager(rlayoutManager);
        mRequestReceiptListView.setItemAnimator(new DefaultItemAnimator());
        mRequestReceiptListView.setAdapter(mRequestReceiptListViewAdapter);

        return v;
    }


    public class RequestReceiptListViewAdapter extends RecyclerView.Adapter<RequestReceiptListViewAdapter.RequestReceiptListViewHolder> {

        List<ClubDetails> clubList = new ArrayList<>();

        public RequestReceiptListViewAdapter(List<ClubDetails> clubList) {
            this.clubList = clubList;
        }

        @Override
        public RequestReceiptListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_club_manager_request_receipt_list, parent, false);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getActivity(), ClubProfileActivity.class );
                    i.putExtra("ViewType", ClubProfileActivity.REQUEST_RECEIVER_PUBLIC);
                    startActivity(i);
                }
            });
            return new RequestReceiptListViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(RequestReceiptListViewHolder holder, int position) {

        }


        @Override
        public int getItemCount() {
            return clubList.size();
        }

        public class RequestReceiptListViewHolder extends RecyclerView.ViewHolder {
            public ImageButton profileImage;
            public TextView profileName;
            public TextView profileStatus;

            public RequestReceiptListViewHolder(View view) {
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
