package chatapp.contacts.fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import chatapp.client.R;
import chatapp.contacts.ContactManagerActivity;
import chatapp.contacts.ContactProfileActivity;
import chatapp.extras.SwissArmyKnife;
import chatapp.notifications.NotificationManager;
import chatapp.storage.tables.Contact;
import chatapp.storage.tables.contentproviders.ContactManagerContentProvider;

public class BlockListFragment extends Fragment {


    private RecyclerView mContactListView;
    private ContactListViewAdapter mContactListViewAdapter;

    public BlockListFragment() {
    }



    public static BlockListFragment newInstance() {
        BlockListFragment fragment = new BlockListFragment();
        return fragment;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            ((ContactManagerActivity)getActivity()).sendTabToService(NotificationManager.NONE);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Initialise Loader
        getLoaderManager().initLoader(0, null, new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                return new CursorLoader(getContext(), ContactManagerContentProvider.BLOCKED_CONTACT_URI, null, null, null, "ProfileName");
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_contact_manager_block_list, container, false);
        mContactListView = (RecyclerView) v.findViewById(R.id.recyclerContactManagerFragmentContainer);

        mContactListViewAdapter = new ContactListViewAdapter(getActivity(),null);
        RecyclerView.LayoutManager rlayoutManager = new LinearLayoutManager(getContext());
        mContactListView.setLayoutManager(rlayoutManager);
        mContactListView.setItemAnimator(new DefaultItemAnimator());
        mContactListView.setAdapter(mContactListViewAdapter);

        return v;
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
            public TextView profileName;

            public ViewHolder(View view) {
                super(view);

                profileName = (TextView) view.findViewById(R.id.textViewContactProfileName);

                ((FloatingActionButton) view.findViewById(R.id.fabMiniRemove)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((ContactManagerActivity)getActivity()).removeBlock(userID);
                    }
                });
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_contact_manager_block_list, parent, false);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getActivity(), ContactProfileActivity.class );
                    i.putExtra("ViewType", ContactProfileActivity.ContactProfileViewAdapter.CONTACT);
                    startActivity(i);
                }
            });
            return new ViewHolder(itemView);

        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            dataCursor.moveToPosition(position);

            holder.userID= dataCursor.getString(Contact.USER_ID);
            holder.profileName.setText(dataCursor.getString(Contact.PROFILE_NAME));

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

