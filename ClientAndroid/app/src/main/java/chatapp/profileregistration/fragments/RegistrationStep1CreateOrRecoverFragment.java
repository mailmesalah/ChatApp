package chatapp.profileregistration.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import chatapp.client.R;
import chatapp.profileregistration.ProfileRegistrationActivity;


public class RegistrationStep1CreateOrRecoverFragment extends Fragment {


    public RegistrationStep1CreateOrRecoverFragment() {
        // Required empty public constructor
    }


    public static RegistrationStep1CreateOrRecoverFragment newInstance() {
        RegistrationStep1CreateOrRecoverFragment fragment = new RegistrationStep1CreateOrRecoverFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.fragment_registration_step1_create_or_recover, container, false);

        v.findViewById(R.id.textViewCreate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ProfileRegistrationActivity)getActivity()).nextFragment(ProfileRegistrationActivity.CREATE_OR_RECOVER,"CREATE");
            }
        });

        v.findViewById(R.id.textViewRecover).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ProfileRegistrationActivity)getActivity()).nextFragment(ProfileRegistrationActivity.CREATE_OR_RECOVER,"RECOVER");

            }
        });

        return v;
    }


    private void showRecoveryAlert(){
        new AlertDialog.Builder(getContext())
                .setTitle("Old Profile Found!")
                .setMessage("Do you want to recover your old profile")
                .setPositiveButton("Recover", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setCancelable(false)
                .show();
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
