package chatapp.profileregistration.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import chatapp.client.R;
import chatapp.communication.websocket.ProfileRegistrationEndpoint;
import chatapp.extras.SwissArmyKnife;
import chatapp.extras.Validator;
import chatapp.profileregistration.ProfileRegistrationActivity;
import chatapp.storage.tables.contentproviders.RegistrationContentProvider;


public class RegistrationStep1RecoverByPhoneNumberFragment extends Fragment {

    private ProgressDialog progressBar;
    private TextInputEditText phoneNumberText;
    private TextInputEditText passwordText;

    public RegistrationStep1RecoverByPhoneNumberFragment() {
        // Required empty public constructor
    }


    public static RegistrationStep1RecoverByPhoneNumberFragment newInstance() {
        RegistrationStep1RecoverByPhoneNumberFragment fragment = new RegistrationStep1RecoverByPhoneNumberFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.fragment_registration_step1_recover_by_phone_number, container, false);

        phoneNumberText=(TextInputEditText)v.findViewById(R.id.inputEditTextPhoneNumber);
        passwordText =(TextInputEditText)v.findViewById(R.id.inputEditTextPassword);

        //Progress Circle
        progressBar = new ProgressDialog(v.getContext());
        progressBar.setCancelable(false);
        progressBar.setMessage("Please Wait ...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.setProgress(0);
        progressBar.setMax(100);

        v.findViewById(R.id.textViewCreate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ProfileRegistrationActivity)getActivity()).nextFragment(ProfileRegistrationActivity.RECOVER_BY_PHONE_NUMBER,"CREATE");
            }
        });

        v.findViewById(R.id.textViewRecover).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String phoneNumber = phoneNumberText.getText().toString();

                if(!Validator.validatePhoneNumber(phoneNumber)){
                    Snackbar.make(getView(),"Invalid Phone Number",Snackbar.LENGTH_SHORT).show();
                    return;
                }

                String password = passwordText.getText().toString();

                if(!Validator.validatePassword(password)){
                    Snackbar.make(getView(),"Invalid Password",Snackbar.LENGTH_SHORT).show();
                    return;
                }
                //Recover By Phone Number
                ((ProfileRegistrationActivity)getActivity()).recoverByPhoneNumber(phoneNumber, password);

                progressBar.show();
            }
        });

        ((ProfileRegistrationActivity)getActivity()).getService().getWebsocket().addListener(new ProfileRegistrationEndpoint.IProfileRegistrationListener() {
            @Override
            public void userIDCheckedEvent(boolean found, String userID) {

            }

            @Override
            public void phoneNumberCheckedEvent(boolean found, String phoneNumber) {

            }

            @Override
            public void registerUserEvent(boolean result, String userID, String loginID) {

            }

            @Override
            public void contactUploadEvent(boolean result, String userID) {
                progressBar.dismiss();

                if(result){
                    //Update Installation Status to completed
                    ContentValues cv = new ContentValues();
                    cv.put("Completed","TRUE");
                    getActivity().getContentResolver().insert(RegistrationContentProvider.INSTALLATION_URI, cv);

                    //Close Registration Window
                    getActivity().setResult(Activity.RESULT_OK);
                    getActivity().finish();
                }else{
                    Snackbar.make(getView(),"Something went wrong, Please try again",Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void profileCreatedEvent(boolean result) {

            }

            @Override
            public void profileRecoveredEvent(boolean result, String userID, String loginID) {
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

                    progressBar.show();

                }else{
                    Snackbar.make(getView(),"Phone Number and Password doesnt match.",Snackbar.LENGTH_LONG).show();
                }
            }
        });

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
