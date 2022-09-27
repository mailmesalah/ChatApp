package chatapp.contacts;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import chatapp.client.R;
import chatapp.services.UndergroundService;
import chatapp.storage.tables.contentproviders.ContactManagerContentProvider;

public class ContactProfileActivity extends AppCompatActivity {


    private RecyclerView mContactProfileView;
    private ContactProfileViewAdapter mContactProfileViewAdapter;

    private boolean mUndergroundServiceConnected=false;
    private Messenger mUndergroundMessenger;
    //Underground Service Connection Handler
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
            unbindService(mUndergroundServiceConnection);
            mUndergroundServiceConnected = false;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_profile);

        //Inflate contact profile details (Container)
        mContactProfileView = (RecyclerView) findViewById(R.id.contactProfileContainer);

        mContactProfileViewAdapter = new ContactProfileViewAdapter(this,null);
        RecyclerView.LayoutManager rlayoutManager = new LinearLayoutManager(this);
        mContactProfileView.setLayoutManager(rlayoutManager);
        mContactProfileView.setItemAnimator(new DefaultItemAnimator());
        mContactProfileView.setAdapter(mContactProfileViewAdapter);



        //Initialise Loader
        getSupportLoaderManager().initLoader(0, null, new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                return new CursorLoader(getApplicationContext(), ContactManagerContentProvider.CONTACT_PROFILE_URI, null, null, null, null);
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                mContactProfileViewAdapter.swapCursor(data);
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {
                mContactProfileViewAdapter.swapCursor(null);
            }
        });
    }


    public class ContactProfileViewAdapter extends RecyclerView.Adapter<ContactProfileViewAdapter.ViewHolder> {
        Cursor dataCursor;
        Context context;

        public  static final int CONTACT=0,REQUEST_SENT=1,REQUEST_RECEIVED=2,BLOCKED=3,OTHER=4;

        private int viewType;

        public ContactProfileViewAdapter(Context context, Cursor dataCursor) {
            this.dataCursor = dataCursor;
            this.context = context;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public String userID;
            public ImageButton profileImage;
            public TextView profileName;
            public TextView quote;
            public Switch notification;

            public ViewHolder(View view) {
                super(view);

                userID="";
                //profileImage= (ImageButton) view.findViewById(R.id.imageButtonContactProfileImage);
                //profileName = (TextView) view.findViewById(R.id.textViewContactProfileName);
                //quote = (TextView) view.findViewById(R.id.textViewContactProfileStatus);

            }
        }

        @Override
        public int getItemViewType(int position) {
            return viewType;
        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView;
            switch(viewType){
                case CONTACT:
                    itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.container_contact_profile_details_contact, parent, false);
                    break;
                case REQUEST_SENT:
                    itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.container_contact_profile_details_request_sent, parent, false);
                    break;
                case REQUEST_RECEIVED:
                    itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.container_contact_profile_details_request_received, parent, false);
                    break;
                case BLOCKED:
                    itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.container_contact_profile_details_block, parent, false);
                    break;
                case OTHER:
                    itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.container_contact_profile_details_other, parent, false);
                    break;
                default:
                    itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.container_contact_profile_details_other, parent, false);
            }

            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            dataCursor.moveToPosition(position);

            //holder.userID= dataCursor.getString(Contact.USER_ID);
            //holder.profileName.setText(dataCursor.getString(Contact.PROFILE_NAME));
            //holder.quote.setText(dataCursor.getString(Contact.QUOTE));
            //byte[] bProfileImage = dataCursor.getBlob(Contact.PROFILE_IMAGE);
            //if(bProfileImage!=null && bProfileImage.length>0){
            //    holder.profileImage.setImageBitmap(SwissArmyKnife.getBitmapFromByteArray(bProfileImage));
            //}

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
