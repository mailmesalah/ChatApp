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

public class ClubListFragment extends Fragment {


    private RecyclerView mClubListView;
    private ClubListViewAdapter mClubListViewAdapter;

    public ClubListFragment() {
    }


    public static ClubListFragment newInstance() {
        ClubListFragment fragment = new ClubListFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_club_manager_club_list, container, false);
        mClubListView = (RecyclerView) v.findViewById(R.id.recyclerClubManagerFragmentContainer);
        List<ClubDetails> headerList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            ClubDetails cd = new ClubDetails();
            headerList.add(cd);

        }
        mClubListViewAdapter = new ClubListViewAdapter(headerList);
        RecyclerView.LayoutManager rlayoutManager = new LinearLayoutManager(getContext());
        mClubListView.setLayoutManager(rlayoutManager);
        mClubListView.setItemAnimator(new DefaultItemAnimator());
        mClubListView.setAdapter(mClubListViewAdapter);

        return v;
    }


    public class ClubListViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private final static int CONTACT_LIST=0, SUGGEST_LIST=1;
        List<ClubDetails> clubList = new ArrayList<>();

        public ClubListViewAdapter(List<ClubDetails> clubList) {
            this.clubList = clubList;
        }

        @Override
        public int getItemViewType(int position) {
            return position<10?CONTACT_LIST:SUGGEST_LIST;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if(viewType==CONTACT_LIST){
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_club_manager_club_list, parent, false);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getActivity(), ClubProfileActivity.class );
                        i.putExtra("ViewType", ClubProfileActivity.MEMBER_PUBLIC_PRIVATE_SECRET);
                        startActivity(i);
                    }
                });
                return new ClubListViewHolder(itemView);
            }else {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_club_manager_club_admin_list, parent, false);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getActivity(), ClubProfileActivity.class );
                        i.putExtra("ViewType", ClubProfileActivity.ADMIN_PUBLIC_PRIVATE_SECRET);
                        startActivity(i);
                    }
                });
                return new SuggestListViewHolder(itemView);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            //holder.getItemViewType();
        }


        @Override
        public int getItemCount() {
            return clubList.size();
        }

        public class ClubListViewHolder extends RecyclerView.ViewHolder {
            public ImageButton profileImage;
            public TextView profileName;
            public TextView profileStatus;

            public ClubListViewHolder(View view) {
                super(view);

                //profileImage= (ImageButton) view.findViewById(R.id.imageButtonClubProfileImage);
                //profileName = (TextView) view.findViewById(R.id.textViewClubProfileName);
                //quote = (TextView) view.findViewById(R.id.textViewClubProfileStatus);

            }
        }

        public class SuggestListViewHolder extends RecyclerView.ViewHolder {
            public ImageButton profileImage;
            public TextView profileName;
            public TextView profileStatus;

            public SuggestListViewHolder(View view) {
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
