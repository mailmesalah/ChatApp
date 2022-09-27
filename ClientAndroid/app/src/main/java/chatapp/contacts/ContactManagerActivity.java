package chatapp.contacts;

import android.app.Dialog;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import chatapp.client.R;
import chatapp.contacts.fragments.BlockListFragment;
import chatapp.contacts.fragments.ContactListFragment;
import chatapp.contacts.fragments.RequestReceiptListFragment;
import chatapp.contacts.fragments.RequestSentListFragment;
import chatapp.extras.SwissArmyKnife;
import chatapp.extras.Validator;
import chatapp.profileregistration.ProfileRegistrationActivity;
import chatapp.services.UndergroundService;
import chatapp.storage.tables.Contact;
import chatapp.storage.tables.contentproviders.ContactManagerContentProvider;

public class ContactManagerActivity extends AppCompatActivity {

    //Tab Index
    private final static int CONTACT_LIST=0;
    private final static int RECEIVED_LIST=1;
    private final static int SENT_LIST=2;
    private final static int BLOCK_LIST=3;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private FloatingActionButton fabSearch;

    //Underground Service Connection Handler
    private static boolean mUndergroundServiceConnected = false;
    private static Messenger mUndergroundMessenger;

    private ServiceConnection mUndergroundServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mUndergroundMessenger = new Messenger(service);
            mUndergroundServiceConnected = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mUndergroundMessenger = null;
            mUndergroundServiceConnected = false;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();

        // Bind to the service
        bindService(new Intent(this, UndergroundService.class), mUndergroundServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();


        // Unbind from the service
        if (mUndergroundServiceConnected) {
            //Notify Service
            sendTabToService(chatapp.notifications.NotificationManager.NONE);

            unbindService(mUndergroundServiceConnection);
            mUndergroundServiceConnected = false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_manager);

        mViewPager = (ViewPager) findViewById(R.id.contactManagerContainer);
        createTabs(mViewPager);

        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewPager);

        fabSearch = (FloatingActionButton) findViewById(R.id.fabSearch);
        //setupTabIcons(mTabLayout);

        fabSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetSearchContactManager bottomSheetDialogFragment = new BottomSheetSearchContactManager();
                bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
            }
        });

        onNewIntent(getIntent());

        //Cancel all current notifications
        NotificationManager notifManager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notifManager.cancelAll();
    }

    @Override
    public void onNewIntent(Intent intent){
        Bundle extras = intent.getExtras();
        if(extras != null){
            if(extras.containsKey("OPEN"))
            {
                String msg = extras.getString("OPEN");
                switch(msg){
                    case "CONTACT_LIST":{
                        mViewPager.setCurrentItem(CONTACT_LIST,false);
                        //Notify Service
                        sendTabToService(chatapp.notifications.NotificationManager.CONTACT_TAB);
                        break;
                    }
                    case "REQUEST_RECEIVED_LIST":{
                        mViewPager.setCurrentItem(RECEIVED_LIST,false);
                        //Notify Service
                        sendTabToService(chatapp.notifications.NotificationManager.REQUEST_RECEIVED_TAB);
                        break;
                    }
                    default:
                        mViewPager.setCurrentItem(CONTACT_LIST,false);
                        //Notify Service
                        sendTabToService(chatapp.notifications.NotificationManager.CONTACT_TAB);
                }

            }
        }
    }

    private void setupTabIcons(TabLayout tabLayout) {
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_map);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_dual_header);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_club_header);
    }

    private void createTabs(ViewPager viewPager) {
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mSectionsPagerAdapter.addFragment(ContactListFragment.newInstance(), "Contact List");
        mSectionsPagerAdapter.addFragment(RequestReceiptListFragment.newInstance(), "Request Received");
        mSectionsPagerAdapter.addFragment(RequestSentListFragment.newInstance(), "Request Sent");
        mSectionsPagerAdapter.addFragment(BlockListFragment.newInstance(), "Block List");
        viewPager.setAdapter(mSectionsPagerAdapter);
    }


    public static class BottomSheetSearchContactManager extends BottomSheetDialogFragment {

        private static int START = 0;
        private static int LENGTH = 30;

        RecyclerView mContactListView;
        TextInputEditText mInputTextSearch;
        BottomSheetSearchContactManager.ContactListViewAdapter mContactListViewAdapter;


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            //Initialise Loader
            getLoaderManager().initLoader(0, null, new LoaderManager.LoaderCallbacks<Cursor>() {
                @Override
                public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                    return new CursorLoader(getContext(), ContactManagerContentProvider.SEARCH_PROFILE_URI, null, null, null, "ProfileName");
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
            View contentView = View.inflate(getContext(), R.layout.bottom_sheet_contact_manager_search, null);

            //Inflate Search Contact bottom sheet
            mContactListView = (RecyclerView) contentView.findViewById(R.id.recyclerContactManagerBottomSheetSearchResult);
            mInputTextSearch = (TextInputEditText) contentView.findViewById(R.id.inputEditTextSearch);

            mContactListViewAdapter = new ContactListViewAdapter(getActivity(), null);
            RecyclerView.LayoutManager rlayoutManager = new LinearLayoutManager(getContext());
            mContactListView.setLayoutManager(rlayoutManager);
            mContactListView.setItemAnimator(new DefaultItemAnimator());
            mContactListView.setAdapter(mContactListViewAdapter);

            //Listen for text change
            mInputTextSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() != 0) {
                        //Delete Previous Data
                        getActivity().getContentResolver().delete(ContactManagerContentProvider.SEARCH_PROFILE_URI, null, null);
                        searchProfiles(s.toString(), START, LENGTH);
                    }
                }
            });

            dialog.setContentView(contentView);

            BottomSheetBehavior.from((View) contentView.getParent()).setState(BottomSheetBehavior.STATE_EXPANDED);
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

                    profileImage = (ImageButton) view.findViewById(R.id.imageButtonContactProfileImage);
                    profileName = (TextView) view.findViewById(R.id.textViewContactProfileName);
                    quote = (TextView) view.findViewById(R.id.textViewContactProfileStatus);

                    ((FloatingActionButton) view.findViewById(R.id.fabMiniAdd)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            addRequest(userID);
                        }
                    });

                    ((FloatingActionButton) view.findViewById(R.id.fabMiniBlock)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            blockOther(userID);
                        }
                    });

                }
            }

            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View itemView;


                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_contact_manager_suggest_list, parent, false);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getContext(), ContactProfileActivity.class);
                        startActivity(i);
                    }
                });


                return new ViewHolder(itemView);
            }

            @Override
            public void onBindViewHolder(ViewHolder holder, int position) {
                dataCursor.moveToPosition(position);

                holder.userID = dataCursor.getString(Contact.USER_ID);
                holder.profileName.setText(dataCursor.getString(Contact.PROFILE_NAME));
                holder.quote.setText(dataCursor.getString(Contact.QUOTE));
                byte[] bProfileImage = dataCursor.getBlob(Contact.PROFILE_IMAGE);
                Log.d("worksss", "111s");
                if (bProfileImage != null && bProfileImage.length > 0) {
                    holder.profileImage.setImageBitmap(SwissArmyKnife.getBitmapFromByteArray(bProfileImage));
                }
                Log.d("worksss", "1112");

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

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        private void addFragment(Fragment fragment, String title) {
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

    /*
    * Service Methods start
    * */
    private static void getContacts() {

        Message msg = Message.obtain(null,
                UndergroundService.GET_CONTACT_LIST);
        try {
            mUndergroundMessenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    private static void getRequestsReceived() {
        Message msg = Message.obtain(null,
                UndergroundService.GET_REQUEST_RECEIVED_LIST);
        try {
            mUndergroundMessenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private static void getRequestsSent() {
        Message msg = Message.obtain(null,
                UndergroundService.GET_REQUEST_SENT_LIST);
        try {
            mUndergroundMessenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private static void getBlockedContacts() {
        Message msg = Message.obtain(null,
                UndergroundService.GET_BLOCK_LIST);
        try {
            mUndergroundMessenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private static void searchProfiles(String searchText, int start, int length) {
        Message msg = Message.obtain(null,
                UndergroundService.SEARCH_PROFILES);

        Bundle data = new Bundle();
        data.putString("SEARCH_TEXT", searchText);
        data.putInt("START", start);
        data.putInt("LENGTH", length);

        msg.setData(data);
        try {
            mUndergroundMessenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static void addRequest(String contactID) {

        Message msg = Message.obtain(null,
                UndergroundService.ADD_REQUEST);

        Bundle data = new Bundle();
        data.putString("CONTACT_ID", contactID);

        msg.setData(data);
        try {
            mUndergroundMessenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    public static void acceptRequest(String contactID) {

        Message msg = Message.obtain(null,
                UndergroundService.ACCEPT_REQUEST);

        Bundle data = new Bundle();
        data.putString("CONTACT_ID", contactID);

        msg.setData(data);
        try {
            mUndergroundMessenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }
    
    public static void blockContact(String contactID) {

        Message msg = Message.obtain(null,
                UndergroundService.BLOCK_CONTACT);

        Bundle data = new Bundle();
        data.putString("CONTACT_ID", contactID);

        msg.setData(data);
        try {
            mUndergroundMessenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    public static void blockRequestReceived(String contactID) {

        Message msg = Message.obtain(null,
                UndergroundService.BLOCK_REQUEST_RECEIVED);

        Bundle data = new Bundle();
        data.putString("CONTACT_ID", contactID);

        msg.setData(data);
        try {
            mUndergroundMessenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    public static void blockRequestSent(String contactID) {

        Message msg = Message.obtain(null,
                UndergroundService.BLOCK_REQUEST_SENT);

        Bundle data = new Bundle();
        data.putString("CONTACT_ID", contactID);

        msg.setData(data);
        try {
            mUndergroundMessenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    public static void blockOther(String contactID) {

        Message msg = Message.obtain(null,
                UndergroundService.BLOCK_OTHER);

        Bundle data = new Bundle();
        data.putString("CONTACT_ID", contactID);

        msg.setData(data);
        try {
            mUndergroundMessenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    public static void removeContact(String contactID) {

        Message msg = Message.obtain(null,
                UndergroundService.REMOVE_CONTACT);

        Bundle data = new Bundle();
        data.putString("CONTACT_ID", contactID);

        msg.setData(data);
        try {
            mUndergroundMessenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    public static void removeRequestReceived(String contactID) {

        Message msg = Message.obtain(null,
                UndergroundService.REMOVE_REQUEST_RECEIVED);

        Bundle data = new Bundle();
        data.putString("CONTACT_ID", contactID);

        msg.setData(data);
        try {
            mUndergroundMessenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    public static void removeRequestSent(String contactID) {

        Message msg = Message.obtain(null,
                UndergroundService.REMOVE_REQUEST_SENT);

        Bundle data = new Bundle();
        data.putString("CONTACT_ID", contactID);

        msg.setData(data);
        try {
            mUndergroundMessenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    public static void removeBlock(String contactID) {

        Message msg = Message.obtain(null,
                UndergroundService.REMOVE_BLOCK);

        Bundle data = new Bundle();
        data.putString("CONTACT_ID", contactID);

        msg.setData(data);
        try {
            mUndergroundMessenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    public static void sendTabToService(int tab) {

        if(mUndergroundServiceConnected) {
            Message msg = Message.obtain(null,
                    UndergroundService.CONTACT_MANAGER_TAB);

            Bundle data = new Bundle();
            data.putInt("TAB", tab);

            msg.setData(data);
            try {
                mUndergroundMessenger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

    }
    /*
    * Service Methods end
    * */


}
