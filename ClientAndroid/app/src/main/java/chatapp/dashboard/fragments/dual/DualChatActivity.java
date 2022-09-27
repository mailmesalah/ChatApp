package chatapp.dashboard.fragments.dual;

import android.app.Dialog;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import chatapp.client.R;
import chatapp.extras.SwissArmyKnife;
import chatapp.services.UndergroundService;
import chatapp.storage.tables.Contact;
import chatapp.storage.tables.DualChat;
import chatapp.storage.tables.Profile;
import chatapp.storage.tables.Register;
import chatapp.storage.tables.contentproviders.ContactManagerContentProvider;
import chatapp.storage.tables.contentproviders.DualChatContentProvider;
import chatapp.storage.tables.contentproviders.RegistrationContentProvider;

public class DualChatActivity extends AppCompatActivity {

    private RecyclerView mDualChatMessageListView;
    private DualChatMessageAdapter mDualChatMessageListAdapter;

    private String mUserID;
    private String mProfileUserID;
    private ImageButton mImageButtonProfileImage;
    private TextView mTextViewProfileName;
    private FloatingActionButton mFabMoreOptions;
    private FloatingActionButton mFabSmiley;
    private TextInputEditText mEditTextChatText;
    private ImageButton mImageButtonSend;
    private LinearLayout mbottomToolbar;
    private Toolbar mProfileHolder;

    //Underground Service Connection Handler
    private boolean mUndergroundServiceConnected = false;
    private static Messenger mUndergroundMessenger;

    private ServiceConnection mUndergroundServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mUndergroundMessenger = new Messenger(service);
            mUndergroundServiceConnected = true;
            //Send Current UserID(Contact)
            sendContactIDToService(mUserID);
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
            //Send Current UserID(Contact) to nothing
            sendContactIDToService("");
            unbindService(mUndergroundServiceConnection);
            mUndergroundServiceConnected = false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dual_chat);

        //For Notification
        onNewIntent(getIntent());

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mUserID = extras.getString("UserID", mUserID);
        }

        mbottomToolbar=(LinearLayout)findViewById(R.id.bottomToolbar);
        mProfileHolder = (Toolbar)findViewById(R.id.profileHolder);
        mImageButtonProfileImage = (ImageButton) findViewById(R.id.imageButtonProfileImage);
        mTextViewProfileName = (TextView) findViewById(R.id.textViewProfileName);
        mFabMoreOptions = (FloatingActionButton) findViewById(R.id.fabMore);
        mFabSmiley = (FloatingActionButton) findViewById(R.id.fabSmiley);

        mEditTextChatText = (TextInputEditText) findViewById(R.id.editTextChatText);
        mImageButtonSend = (ImageButton) findViewById(R.id.imageButtonSend);

        Log.d("mUserID", mUserID + " id create");
        //File profile name and profile image from database to view
        int _ID = 0;
        Cursor contact = getContentResolver().query(ContactManagerContentProvider.CONTACT_URI, null, "UserID=?", new String[]{mUserID}, null);
        if (contact != null && contact.getCount() > 0) {
            contact.moveToFirst();
            mTextViewProfileName.setText(contact.getString(Contact.PROFILE_NAME));
            byte[] bProfileImage = contact.getBlob(Contact.PROFILE_IMAGE);
            if (bProfileImage != null && bProfileImage.length > 0) {
                mImageButtonProfileImage.setImageBitmap(SwissArmyKnife.getBitmapFromByteArray(bProfileImage));
                _ID = contact.getInt(Contact.ID);
            }
        }
        contact.close();

        //Get Profile User ID
        Cursor profile = getContentResolver().query(RegistrationContentProvider.REGISTER_URI, null, null, null, null);
        if (profile != null && profile.getCount() > 0) {
            profile.moveToFirst();
            mProfileUserID = profile.getString(Register.USER_ID);
        }
        profile.close();


        // Inflate message list recycler view
        mDualChatMessageListView = (RecyclerView) findViewById(R.id.recyclerDualChatContainer);
        mDualChatMessageListAdapter = new DualChatMessageAdapter(this, null);
        RecyclerView.LayoutManager rlayoutManager = new LinearLayoutManager(getApplicationContext());
        mDualChatMessageListView.setLayoutManager(rlayoutManager);
        mDualChatMessageListView.setItemAnimator(new DefaultItemAnimator());
        mDualChatMessageListView.setAdapter(mDualChatMessageListAdapter);

        //Initialise Smiley BottomSheet
        final BottomSheetBehavior behavior = BottomSheetBehavior.from(findViewById(R.id.bottomSheetDualChatSmiley));
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_DRAGGING:

                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:

                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:

                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }

        });

        //Registering Actions
        mFabMoreOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetMoreOptions bottomSheetDialogFragment = new BottomSheetMoreOptions();
                bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
            }
        });

        mFabSmiley.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        mImageButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mEditTextChatText.getText().toString().length() > 0) {
                    String messageText = mEditTextChatText.getText().toString();

                    ContentValues c = new ContentValues();
                    c.put("SenderUserID", mProfileUserID);
                    c.put("ReceiverUserID", mUserID);
                    c.put("MessageText", messageText);
                    c.put("FilePath", "");
                    c.put("MessageType", DualChat.OUT_TEXT);
                    c.put("MessageTime", SwissArmyKnife.getCurrentLocalDateTime("yyyy-MM-dd HH:mm:ss"));
                    c.put("SeenBy", DualChat.SENDER);
                    //Save in database
                    Uri insert = getContentResolver().insert(DualChatContentProvider.DUAL_CHAT_URI, c);
                    long clientChatID = ContentUris.parseId(insert);

                    //Send to server
                    sendMessage(mUserID, messageText, clientChatID, DualChat.OUT_TEXT, "");

                    //Clear Text Box
                    mEditTextChatText.setText("");


                    MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.message_send);
                    mediaPlayer.start();
                }
            }
        });

        //Initialise Loader

        getSupportLoaderManager().initLoader(0, null, new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                String where = "SenderUserID=? Or ReceiverUserID=?";
                return new CursorLoader(getApplicationContext(), DualChatContentProvider.DUAL_CHAT_URI, null, where, new String[]{mUserID, mUserID}, null);
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                mDualChatMessageListAdapter.swapCursor(data);

                int count = mDualChatMessageListView.getAdapter().getItemCount();
                if (count > 0) {
                    mDualChatMessageListView.scrollToPosition(count - 1);
                }
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {
                mDualChatMessageListAdapter.swapCursor(null);
            }
        });


        //Cancel all current notifications
        NotificationManager notifManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notifManager.cancel(_ID);
    }

    @Override
    public void onNewIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (extras.containsKey("CONTACT_ID")) {
                String contactID = extras.getString("CONTACT_ID");
                mUserID = contactID;
            }
        }
    }

    public static class BottomSheetMoreOptions extends BottomSheetDialogFragment {


        @Override
        public void setupDialog(final Dialog dialog, int style) {
            super.setupDialog(dialog, style);
            View contentView = View.inflate(getContext(), R.layout.bottom_sheet_dual_chat_more_options, null);

            dialog.setContentView(contentView);
        }


    }

    public class DualChatMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        Cursor dataCursor;
        Context context;

        private SparseBooleanArray selectedItems;
        private ActionMode actionMode;

        public DualChatMessageAdapter(Context context, Cursor dataCursor) {
            this.dataCursor = dataCursor;
            this.context = context;

            selectedItems = new SparseBooleanArray();
        }

        public void toggleSelection(int pos) {
            if (selectedItems.get(pos, false)) {
                selectedItems.delete(pos);
            }
            else {
                selectedItems.put(pos, true);
            }
            //Update count
            if(actionMode!=null){
                actionMode.setTitle(getSelectedItemCount()+"");
            }
            notifyItemChanged(pos);
        }

        public void clearSelections() {
            selectedItems.clear();
            notifyDataSetChanged();
        }

        public int getSelectedItemCount() {
            return selectedItems.size();
        }

        public List<Integer> getSelectedItems() {
            List<Integer> items =
                    new ArrayList<Integer>(selectedItems.size());
            for (int i = 0; i < selectedItems.size(); i++) {
                items.add(selectedItems.keyAt(i));
            }
            return items;
        }


        public class ViewHolderText extends RecyclerView.ViewHolder {
            public long clientChatID;
            public long serverChatID;
            public TextView messageTime;
            public TextView messageText;
            //Layout
            public View layout;

            public ViewHolderText(View view) {
                super(view);

                messageTime = (TextView) view.findViewById(R.id.textViewDateTime);
                messageText = (TextView) view.findViewById(R.id.textViewTextMessage);
                layout = view.findViewById(R.id.linearLayout);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int idx = getAdapterPosition();
                        if (actionMode != null) {
                            toggleSelection(idx);
                            return;
                        }
                    }
                });

                view.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        //View view = mDualChatMessageListView.findChildViewUnder(e.getX(), e.getY());
                        if (actionMode != null) {
                            return false;
                        }
                        //Hide other controls
                        mProfileHolder.setVisibility(View.GONE);
                        mbottomToolbar.setVisibility(View.GONE);

                        actionMode = startActionMode(new ActionMode.Callback() {
                            @Override
                            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                                MenuInflater inflater = getMenuInflater();
                                inflater.inflate(R.menu.action_bar_contextual_menu_dual_chat, menu);
                                return true;
                            }

                            @Override
                            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                                return false;
                            }

                            @Override
                            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.menuForward:
                                        actionMode.finish();
                                        return true;
                                    case R.id.menuCopy:
                                        actionMode.finish();
                                        return true;
                                    case R.id.menuDelete:
                                        actionMode.finish();
                                        return true;

                                    default:
                                }
                                return false;
                            }

                            @Override
                            public void onDestroyActionMode(ActionMode mode) {
                                actionMode = null;
                                clearSelections();

                                //show other Controls
                                mProfileHolder.setVisibility(View.VISIBLE);
                                mbottomToolbar.setVisibility(View.VISIBLE);
                            }
                        });

                        int idx = getAdapterPosition();
                        toggleSelection(idx);
                        return false;
                    }
                });

                view.setLongClickable(true);
            }

        }

        public class ViewHolderImage extends RecyclerView.ViewHolder {

            public ViewHolderImage(View view) {
                super(view);
            }
        }

        public class ViewHolderImages extends RecyclerView.ViewHolder {

            public ViewHolderImages(View view) {
                super(view);
            }
        }

        public class ViewHolderCameraImage extends RecyclerView.ViewHolder {

            public ViewHolderCameraImage(View view) {
                super(view);
            }
        }

        public class ViewHolderAudio extends RecyclerView.ViewHolder {

            public ViewHolderAudio(View view) {
                super(view);
            }
        }

        public class ViewHolderAudios extends RecyclerView.ViewHolder {

            public ViewHolderAudios(View view) {
                super(view);
            }
        }

        public class ViewHolderVoice extends RecyclerView.ViewHolder {

            public ViewHolderVoice(View view) {
                super(view);
            }
        }

        public class ViewHolderVideo extends RecyclerView.ViewHolder {

            public ViewHolderVideo(View view) {
                super(view);
            }
        }

        public class ViewHolderVideos extends RecyclerView.ViewHolder {

            public ViewHolderVideos(View view) {
                super(view);
            }
        }

        public class ViewHolderCameraVideo extends RecyclerView.ViewHolder {

            public ViewHolderCameraVideo(View view) {
                super(view);
            }
        }

        public class ViewHolderVoiceCall extends RecyclerView.ViewHolder {

            public ViewHolderVoiceCall(View view) {
                super(view);
            }
        }

        public class ViewHolderVideoCall extends RecyclerView.ViewHolder {

            public ViewHolderVideoCall(View view) {
                super(view);
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (dataCursor != null) {
                dataCursor.moveToPosition(position);
                return dataCursor.getInt(dataCursor.getColumnIndex("MessageType"));
            }

            return 0;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView;

            switch (viewType) {
                case DualChat.IN_TEXT: {
                    itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_dual_chat_in_text_message, parent, false);
                    return new ViewHolderText(itemView);
                }

                case DualChat.OUT_TEXT: {
                    itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_dual_chat_out_text_message, parent, false);
                    return new ViewHolderText(itemView);
                }

                case DualChat.IN_IMAGE_TEXT: {
                    itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_dual_chat_in_text_message, parent, false);
                    return new ViewHolderText(itemView);
                }

                case DualChat.OUT_IMAGE_TEXT: {
                    itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_dual_chat_in_text_message, parent, false);
                    return new ViewHolderText(itemView);
                }

                case DualChat.IN_IMAGES_TEXT: {
                    itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_dual_chat_in_text_message, parent, false);
                    return new ViewHolderText(itemView);
                }

                case DualChat.OUT_IMAGES_TEXT: {
                    itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_dual_chat_in_text_message, parent, false);
                    return new ViewHolderText(itemView);
                }

                case DualChat.IN_AUDIO_TEXT: {
                    itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_dual_chat_in_text_message, parent, false);
                    return new ViewHolderText(itemView);
                }

                case DualChat.OUT_AUDIO_TEXT: {
                    itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_dual_chat_in_text_message, parent, false);
                    return new ViewHolderText(itemView);
                }

                case DualChat.IN_AUDIOS_TEXT: {
                    itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_dual_chat_in_text_message, parent, false);
                    return new ViewHolderText(itemView);
                }

                case DualChat.OUT_AUDIOS_TEXT: {
                    itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_dual_chat_in_text_message, parent, false);
                    return new ViewHolderText(itemView);
                }

                case DualChat.IN_CAMERA_IMAGE_TEXT: {
                    itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_dual_chat_in_text_message, parent, false);
                    return new ViewHolderText(itemView);
                }

                case DualChat.OUT_CAMERA_IMAGE_TEXT: {
                    itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_dual_chat_in_text_message, parent, false);
                    return new ViewHolderText(itemView);
                }

                case DualChat.IN_CAMERA_VIDEO_TEXT: {
                    itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_dual_chat_in_text_message, parent, false);
                    return new ViewHolderText(itemView);
                }

                case DualChat.OUT_CAMERA_VIDEO_TEXT: {
                    itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_dual_chat_in_text_message, parent, false);
                    return new ViewHolderText(itemView);
                }

                case DualChat.IN_VOICE_TEXT: {
                    itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_dual_chat_in_text_message, parent, false);
                    return new ViewHolderText(itemView);
                }

                case DualChat.OUT_VOICE_TEXT: {
                    itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_dual_chat_in_text_message, parent, false);
                    return new ViewHolderText(itemView);
                }

                case DualChat.IN_VIDEO_TEXT: {
                    itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_dual_chat_in_text_message, parent, false);
                    return new ViewHolderText(itemView);
                }

                case DualChat.OUT_VIDEO_TEXT: {
                    itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_dual_chat_in_text_message, parent, false);
                    return new ViewHolderText(itemView);
                }

                case DualChat.IN_VIDIEOS_TEXT: {
                    itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_dual_chat_in_text_message, parent, false);
                    return new ViewHolderText(itemView);
                }

                case DualChat.OUT_VIDIEOS_TEXT: {
                    itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_dual_chat_in_text_message, parent, false);
                    return new ViewHolderText(itemView);
                }

                case DualChat.IN_VOICE_CALL_SUMMERY_TEXT: {
                    itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_dual_chat_in_text_message, parent, false);
                    return new ViewHolderText(itemView);
                }

                case DualChat.OUT_VOICE_CALL_SUMMERY_TEXT: {
                    itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_dual_chat_in_text_message, parent, false);
                    return new ViewHolderText(itemView);
                }

                case DualChat.IN_VIDEO_CALL_SUMMARY_TEXT: {
                    itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_dual_chat_in_text_message, parent, false);
                    return new ViewHolderText(itemView);
                }

                case DualChat.OUT_VIDEO_CALL_SUMMARY_TEXT: {
                    itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_dual_chat_in_text_message, parent, false);
                    return new ViewHolderText(itemView);
                }

                case DualChat.IN_FILE_TEXT: {
                    itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_dual_chat_in_text_message, parent, false);
                    return new ViewHolderText(itemView);
                }

                case DualChat.OUT_FILE_TEXT: {
                    itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_dual_chat_in_text_message, parent, false);
                    return new ViewHolderText(itemView);
                }

                default:
                    itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_dual_chat_in_text_message, parent, false);
                    return new ViewHolderText(itemView);
            }

        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            dataCursor.moveToPosition(position);

            ViewHolderText textHolder = (ViewHolderText) holder;
            textHolder.clientChatID = dataCursor.getLong(DualChat.ID);
            textHolder.serverChatID = dataCursor.getLong(DualChat.SERVER_CHAT_ID);
            String sDateTime = dataCursor.getString(DualChat.MESSAGE_TIME);
            textHolder.messageText.setText(dataCursor.getString(DualChat.MESSAGE_TEXT));
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            try {
                textHolder.messageTime.setText(DateFormat.getDateTimeInstance().format(dateFormat.parse(sDateTime)));
            } catch (Exception e) {
                Log.d("Invalid Date", "Date Error");
                textHolder.messageTime.setText(sDateTime);
            }
            //Implement Seen By
            GradientDrawable shape = (GradientDrawable) textHolder.layout.getBackground();

            switch (dataCursor.getInt(DualChat.SEEN_BY)) {
                case DualChat.SENDER:
                    shape.setColor(Color.LTGRAY);
                    break;
                case DualChat.SERVER:
                    shape.setColor(ContextCompat.getColor(getApplication(),R.color.colorAccent));
                    //Check if incoming message
                    if(dataCursor.getString(DualChat.SENDER_USER_ID).equals(mUserID)){
                        ContentValues cv = new ContentValues();
                        cv.put("SeenBy", DualChat.RECEIVER);
                        getContentResolver().update(DualChatContentProvider.DUAL_CHAT_URI, cv, "_ID=?", new String[]{textHolder.clientChatID + ""});
                    }
                    break;
                case DualChat.RECEIVER:
                    shape.setColor(Color.WHITE);
                    break;
                default:
            }

            if(selectedItems.get(position,false)){
                textHolder.itemView.setBackgroundColor(ContextCompat.getColor(getApplication(),R.color.colorPrimary));
            }else{
                textHolder.itemView.setBackgroundColor(ContextCompat.getColor(getApplication(),R.color.windowBackground));
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


    /*
    Service Methods
     */

    private static void sendMessage(String receiverUserID, String messageText, long clientChatID, int messageType, String filePath) {
        Message msg = Message.obtain(null,
                UndergroundService.SEND_DUAL_CHAT);
        Bundle data = new Bundle();
        data.putString("RECEIVER_USER_ID", receiverUserID);
        data.putString("MESSAGE_TEXT", messageText);
        data.putLong("CLIENT_CHAT_ID", clientChatID);
        data.putInt("MESSAGE_TYPE", messageType);
        data.putString("FILE_PATH", filePath);

        msg.setData(data);
        try {
            mUndergroundMessenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    //Local Service messages
    private static void sendContactIDToService(String contactID) {
        Message msg = Message.obtain(null,
                UndergroundService.DUAL_CHAT_ACTIVITY);
        Bundle data = new Bundle();
        data.putString("CONTACT_ID", contactID);

        msg.setData(data);
        try {
            mUndergroundMessenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
