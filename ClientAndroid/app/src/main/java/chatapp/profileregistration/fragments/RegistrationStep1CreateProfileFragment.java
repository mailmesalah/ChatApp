package chatapp.profileregistration.fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import chatapp.client.R;
import chatapp.communication.websocket.ProfileRegistrationEndpoint;
import chatapp.extras.Validator;
import chatapp.profileregistration.ProfileRegistrationActivity;
import chatapp.storage.tables.Installation;
import chatapp.storage.tables.RegistrationStatus;
import chatapp.storage.tables.contentproviders.RegistrationContentProvider;


public class RegistrationStep1CreateProfileFragment extends Fragment {

    TextInputEditText userIDText;
    TextInputEditText phoneNumberText;
    ImageView userIDStatus;
    ImageView phoneNumberStatus;
    TextInputEditText passwordText;
    TextInputEditText confirmPText;
    TextView createText;
    TextView recoverText;


    boolean validUserID=false;
    boolean validPhoneNumber=false;

    private final static int INITIAL=0;
    private final static int REGISTERED=1;

    int currentState=INITIAL;
    private ProgressDialog progressBar;


    public RegistrationStep1CreateProfileFragment() {
        // Required empty public constructor
    }


    public static RegistrationStep1CreateProfileFragment newInstance() {
        RegistrationStep1CreateProfileFragment fragment = new RegistrationStep1CreateProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.fragment_registration_step1_create_profile, container, false);

        userIDText =(TextInputEditText)v.findViewById(R.id.inputEditTextUserID);
        phoneNumberText=(TextInputEditText)v.findViewById(R.id.inputEditTextPhoneNumber);
        userIDStatus = (ImageView)v.findViewById(R.id.statusUserID);
        phoneNumberStatus = (ImageView)v.findViewById(R.id.statusPhoneNumber);
        passwordText =(TextInputEditText)v.findViewById(R.id.inputEditTextPassword);
        confirmPText=(TextInputEditText)v.findViewById(R.id.inputEditTextConfirmPassword);
        createText = (TextView) v.findViewById(R.id.textViewCreate);
        recoverText = (TextView) v.findViewById(R.id.textViewRecover);

        //Progress Circle
        progressBar = new ProgressDialog(v.getContext());
        progressBar.setCancelable(false);
        progressBar.setMessage("Please Wait ...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.setProgress(0);
        progressBar.setMax(100);


        createText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!validUserID){
                    Snackbar.make(getView(),"Invalid User ID",Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if(!validPhoneNumber){
                    Snackbar.make(getView(),"Invalid Phone Number",Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if(!Validator.validatePassword(passwordText.getText().toString())){
                    Snackbar.make(getView(),"Invalid Password",Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if(!passwordText.getText().toString().equals(confirmPText.getText().toString())){
                    Snackbar.make(getView(),"Passwords don't match",Snackbar.LENGTH_SHORT).show();
                    return;
                }

                ((ProfileRegistrationActivity)getActivity()).registerUser(userIDText.getText().toString(), phoneNumberText.getText().toString(), passwordText.getText().toString());
                //Show progress bar so that user wont click any other control after this
                progressBar.show();
            }
        });

        recoverText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ProfileRegistrationActivity)getActivity()).nextFragment(ProfileRegistrationActivity.CREATE_PROFILE,"RECOVER");
            }
        });

        //User id check
        userIDText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() != 0){
                    if(Validator.validateUserID(s.toString())){
                        ((ProfileRegistrationActivity)getActivity()).checkUserIDUsed(s.toString());
                    }else{
                        userIDStatus.setVisibility(View.GONE);
                        validUserID=false;
                    }

                }
            }
        });

        userIDText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    if(!Validator.validateUserID(userIDText.getText().toString())){
                        //Show message for invalid user id
                        Toast.makeText(getContext(),"Not a valid User ID",Toast.LENGTH_LONG).show();
                        validUserID = false;
                    }
                }
            }
        });

        //phone number check
        phoneNumberText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() != 0){
                    if(Validator.validatePhoneNumber(s.toString())){
                        ((ProfileRegistrationActivity)getActivity()).checkPhoneNumberUsed(s.toString());
                    }else{
                        phoneNumberStatus.setVisibility(View.GONE);
                        validPhoneNumber=false;
                    }

                }
            }
        });

        phoneNumberText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    if(!Validator.validatePhoneNumber(phoneNumberText.getText().toString())){
                        //Show message for invalid user id
                        Toast.makeText(getContext(),"Not a valid Phone Number",Toast.LENGTH_LONG).show();
                        validPhoneNumber=false;
                    }
                }
            }
        });

        ((ProfileRegistrationActivity)getActivity()).getService().getWebsocket().addListener(new ProfileRegistrationEndpoint.IProfileRegistrationListener() {
            @Override
            public void userIDCheckedEvent(boolean found, String userID) {

                if(userIDText.getText().toString().equals(userID)){

                    if(!found){
                        userIDStatus.setImageResource(R.drawable.ic_add);
                        validUserID=true;
                        userIDStatus.setVisibility(View.VISIBLE);
                    }else{
                        userIDStatus.setImageResource(R.drawable.ic_block);
                        validUserID=false;
                        userIDStatus.setVisibility(View.VISIBLE);
                    }

                }else{
                    validUserID=false;
                    userIDStatus.setVisibility(View.GONE);
                }
            }

            @Override
            public void phoneNumberCheckedEvent(boolean found, String phoneNumber) {

                if(phoneNumberText.getText().toString().equals(phoneNumber)){
                    if(!found){
                        phoneNumberStatus.setImageResource(R.drawable.ic_add);
                        validPhoneNumber=true;
                        phoneNumberStatus.setVisibility(View.VISIBLE);
                    }else{
                        phoneNumberStatus.setImageResource(R.drawable.ic_block);
                        validPhoneNumber=false;
                        phoneNumberStatus.setVisibility(View.VISIBLE);
                    }

                }else{
                    validPhoneNumber=false;
                    phoneNumberStatus.setVisibility(View.GONE);
                }
            }

            @Override
            public void registerUserEvent(boolean result, String userID, String loginID) {
                progressBar.dismiss();

                if(result) {
                    ContentValues cv = new ContentValues();
                    cv.put("UserID", userID);
                    cv.put("LoginID", loginID);
                    getActivity().getContentResolver().insert(RegistrationContentProvider.REGISTER_URI, cv);

                    //Set as Registration Completed
                    cv = new ContentValues();
                    cv.put("Status","Registered");
                    getActivity().getContentResolver().insert(RegistrationContentProvider.STATUS_URI, cv);

                    //Upload Contacts
                    ((ProfileRegistrationActivity)getActivity()).uploadContacts();

                }else{
                    Snackbar.make(getView(),"User ID or Phone Number Already Used",Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void contactUploadEvent(boolean result, String userID) {
                progressBar.dismiss();

                if(result){
                    Log.d("Contact Upload","Success");
                    ContentValues cv = new ContentValues();
                    cv.put("Status","ContactUploaded");
                    getActivity().getContentResolver().insert(RegistrationContentProvider.STATUS_URI, cv);

                    //Move to Profile Creation
                    ((ProfileRegistrationActivity)getActivity()).nextFragment(ProfileRegistrationActivity.CREATE_PROFILE,"PROFILE");
                }else{
                    Snackbar.make(getView(),"Something went wrong, Please try again",Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void profileCreatedEvent(boolean result) {

            }

            @Override
            public void profileRecoveredEvent(boolean result, String userID, String loginID) {

            }
        });


        Cursor c=getActivity().getContentResolver().query(RegistrationContentProvider.STATUS_URI, null, null, null, null);
        if (c!=null && c.getCount()>0){
            c.moveToFirst();
            if(c.getString(RegistrationStatus.STATUS).equalsIgnoreCase("Registered")){
                // always close the cursor
                c.close();
                //Upload Contacts
                ((ProfileRegistrationActivity)getActivity()).uploadContacts();
                progressBar.show();
            }
        }


        return v;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
