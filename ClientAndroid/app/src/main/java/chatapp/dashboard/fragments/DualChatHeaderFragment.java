package chatapp.dashboard.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import chatapp.client.R;
import chatapp.contacts.ContactManagerActivity;
import chatapp.dashboard.fragments.dual.DualChatActivity;
import chatapp.extras.SwissArmyKnife;
import chatapp.storage.tables.Contact;
import chatapp.storage.tables.contentproviders.ContactManagerContentProvider;
import chatapp.storage.tables.contentproviders.DualChatContentProvider;

public class DualChatHeaderFragment extends Fragment {

    //For Chat header list
    private RecyclerView mChatHeaderListView;
    private DualChatHeaderFragment.DualChatHeaderAdapter mChatHeaderAdapter;

    //FAB
    private FloatingActionButton fabAddContacts;
    private FloatingActionButton fabContacts;
    private FloatingActionButton fabSearch;


    public DualChatHeaderFragment() {
    }

    public static DualChatHeaderFragment newInstance() {
        DualChatHeaderFragment fragment = new DualChatHeaderFragment();
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Initialise Loader
        getLoaderManager().initLoader(0, null, new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                return new CursorLoader(getContext(), DualChatContentProvider.DUAL_CHAT_HEADER_URI, null, null, null, null);
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                mChatHeaderAdapter.swapCursor(data);
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {
                mChatHeaderAdapter.swapCursor(null);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the chat header list layout for this fragment
        View v = inflater.inflate(R.layout.fragment_dual_chat_header, container, false);

        fabAddContacts = (FloatingActionButton) v.findViewById(R.id.fabAddChat);
        fabContacts = (FloatingActionButton) v.findViewById(R.id.fabContacts);
        fabSearch = (FloatingActionButton) v.findViewById(R.id.fabSearch);

        mChatHeaderListView = (RecyclerView) v.findViewById(R.id.recyclerDualChatHeader);
        mChatHeaderListView.setHasFixedSize(true);

        mChatHeaderAdapter = new DualChatHeaderAdapter(getActivity(),null);
        RecyclerView.LayoutManager rlayoutManager = new LinearLayoutManager(getContext());
        mChatHeaderListView.setLayoutManager(rlayoutManager);
        mChatHeaderListView.setItemAnimator(new DefaultItemAnimator());
        mChatHeaderListView.setAdapter(mChatHeaderAdapter);


        fabAddContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetContactList bottomSheetDialogFragment = new BottomSheetContactList();
                bottomSheetDialogFragment.show(getFragmentManager(), bottomSheetDialogFragment.getTag());
            }
        });

        fabSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetSearchDualChat bottomSheetDialogFragment = new BottomSheetSearchDualChat();
                bottomSheetDialogFragment.show(getFragmentManager(), bottomSheetDialogFragment.getTag());
            }
        });

        fabContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), ContactManagerActivity.class);
                startActivity(i);
            }
        });
        return v;
    }

    public static class BottomSheetSearchDualChat extends BottomSheetDialogFragment {

        private RecyclerView mRVSearchChatResult;
        private SearchChatViewAdapter mSearchChatViewAdapter;

        @Override
        public void setupDialog(final Dialog dialog, int style) {
            super.setupDialog(dialog, style);
            View contentView = View.inflate(getContext(), R.layout.bottom_sheet_search_dual_chat, null);
            //Inflate Search Chat bottom sheet
            mRVSearchChatResult = (RecyclerView) contentView.findViewById(R.id.recyclerBottomSheetSearchResultDualChat);
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

            List<ChatSearchResult> contactList = new ArrayList<>();

            public SearchChatViewAdapter(List<ChatSearchResult> contactList) {
                this.contactList = contactList;
            }

            @Override
            public SearchChatViewAdapter.SearchChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_bottom_sheet_search_dual_chat, parent, false);
                return new SearchChatViewHolder(itemView);
            }

            @Override
            public void onBindViewHolder(SearchChatViewHolder holder, int position) {

            }


            @Override
            public int getItemCount() {
                return contactList.size();
            }

            public class SearchChatViewHolder extends RecyclerView.ViewHolder {
                public TextView profileName;
                public TextView chatDateTime;
                public TextView description;

                public SearchChatViewHolder(View view) {
                    super(view);

                    profileName = (TextView) view.findViewById(R.id.textViewBottomSheetDualChatProfileName);
                    chatDateTime = (TextView) view.findViewById(R.id.textViewBottomSheetDualChatDateTime);
                    description = (TextView) view.findViewById(R.id.textViewBottomSheetDualChatDescription);

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

    public static class BottomSheetContactList extends BottomSheetDialogFragment {


        private RecyclerView mContactListView;
        private ContactListViewAdapter mContactListViewAdapter;
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            //Initialise Loader
            getLoaderManager().initLoader(0, null, new LoaderManager.LoaderCallbacks<Cursor>() {
                @Override
                public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                    return new CursorLoader(getContext(), ContactManagerContentProvider.CONTACT_URI, null, null, null, "ProfileName");
                }

                @Override
                public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                    mContactListViewAdapter.swapCursor(data);
                }

                @Override
                public void onLoaderReset(Loader<Cursor> loader) {
                    mContactListViewAdapter.swapCursor(null);
                }
            });

        }

        @Override
        public void setupDialog(final Dialog dialog, int style) {
            super.setupDialog(dialog, style);
            View contentView = View.inflate(getContext(), R.layout.bottom_sheet_contact_list, null);
            //Inflate bottom sheet for contact list
            mContactListView = (RecyclerView) contentView.findViewById(R.id.recyclerBottomSheetContactList);
            mContactListView.setHasFixedSize(true);

            mContactListViewAdapter = new ContactListViewAdapter(getActivity(),null);
            RecyclerView.LayoutManager rlayoutManager = new LinearLayoutManager(getContext());
            mContactListView.setLayoutManager(rlayoutManager);
            mContactListView.setItemAnimator(new DefaultItemAnimator());
            mContactListView.setAdapter(mContactListViewAdapter);
            dialog.setContentView(contentView);
        }

        public class ContactListViewAdapter extends RecyclerView.Adapter<ContactListViewAdapter.ViewHolder> {

            Cursor dataCursor;
            Context context;

            public ContactListViewAdapter(Context context, Cursor dataCursor) {
                this.dataCursor = dataCursor;
                this.context = context;
            }

            public class ViewHolder extends RecyclerView.ViewHolder {
                public String userID;
                public ImageButton profileImage;
                public TextView profileName;
                public TextView quote;

                public ViewHolder(View view) {
                    super(view);

                    userID="";
                    profileImage= (ImageButton) view.findViewById(R.id.imageButtonBottomSheetProfileImage);
                    profileName = (TextView) view.findViewById(R.id.textViewBottomSheetProfileName);
                    quote = (TextView) view.findViewById(R.id.textViewBottomSheetProfileStatus);

                    itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(getActivity(), DualChatActivity.class );
                            i.putExtra("UserID", userID);
                            startActivity(i);
                        }
                    });

                }
            }


            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_bottom_sheet_contact, parent, false);

                return new ViewHolder(itemView);
            }

            @Override
            public void onBindViewHolder(ViewHolder holder, int position) {
                dataCursor.moveToPosition(position);

                holder.userID= dataCursor.getString(Contact.USER_ID);
                holder.profileName.setText(dataCursor.getString(Contact.PROFILE_NAME));
                holder.quote.setText(dataCursor.getString(Contact.QUOTE));
                byte[] bProfileImage = dataCursor.getBlob(Contact.PROFILE_IMAGE);
                if(bProfileImage!=null && bProfileImage.length>0){
                    holder.profileImage.setImageBitmap(SwissArmyKnife.getBitmapFromByteArray(bProfileImage));
                }
            }


            @Override
            public int getItemCount() {
                return (dataCursor == null) ? 0 : dataCursor.getCount();
            }

            public Cursor swapCursor(Cursor cursor) {
                if (dataCursor == cursor) {
                    return null;
                }
                Cursor oldCursor = dataCursor;
                this.dataCursor = cursor;
                if (cursor != null) {
                    this.notifyDataSetChanged();
                }
                return oldCursor;
            }
        }
    }

    public class DualChatHeaderAdapter extends RecyclerView.Adapter<DualChatHeaderAdapter.ViewHolder> {

        Cursor dataCursor;
        Context context;

        public DualChatHeaderAdapter(Context context, Cursor dataCursor) {
            this.dataCursor = dataCursor;
            this.context = context;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public String userID;
            public ImageButton profileImage;
            public TextView profileName;
            public TextView lastUpdateDateTime;
            public TextView unseenMessages;

            public ViewHolder(View view) {
                super(view);
                userID="";
                profileImage = (ImageButton) view.findViewById(R.id.imageButtonDualChatHeaderProfile);
                profileName = (TextView) view.findViewById(R.id.textViewDualChatHeaderProfileName);
                lastUpdateDateTime = (TextView) view.findViewById(R.id.textViewDualChatHeaderLastUpdate);
                unseenMessages = (TextView) view.findViewById(R.id.textViewDualChatHeaderUnseenMessages);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getActivity(), DualChatActivity.class);
                        i.putExtra("UserID", userID);
                        startActivity(i);
                    }
                });
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_dual_chat_header, parent, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            dataCursor.moveToPosition(position);

            holder.userID=dataCursor.getString(dataCursor.getColumnIndex("UserID"));
            holder.profileName.setText(dataCursor.getString(dataCursor.getColumnIndex("ProfileName")));
            String sDateTime=dataCursor.getString(dataCursor.getColumnIndex("MessageTime"));
            holder.unseenMessages.setText(dataCursor.getString(dataCursor.getColumnIndex("MessageCount")));
            byte[] bProfileImage = dataCursor.getBlob(dataCursor.getColumnIndex("ProfileImage"));
            if(bProfileImage!=null && bProfileImage.length>0){
                holder.profileImage.setImageBitmap(SwissArmyKnife.getBitmapFromByteArray(bProfileImage));
            }
            SimpleDateFormat dateFormat =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            try {
                holder.lastUpdateDateTime.setText(DateFormat.getDateTimeInstance().format(dateFormat.parse(sDateTime)));
            }catch (Exception e){
                Log.d("Invalid Date","Date Error");
                holder.lastUpdateDateTime.setText(sDateTime);
            }

        }

        @Override
        public int getItemCount() {
            return (dataCursor == null) ? 0 : dataCursor.getCount();
        }

        public Cursor swapCursor(Cursor cursor) {
            if (dataCursor == cursor) {
                return null;
            }
            Cursor oldCursor = dataCursor;
            this.dataCursor = cursor;
            if (cursor != null) {
                this.notifyDataSetChanged();
            }
            return oldCursor;
        }

    }

}