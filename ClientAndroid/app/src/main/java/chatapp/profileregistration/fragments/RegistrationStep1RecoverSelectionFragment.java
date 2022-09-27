package chatapp.profileregistration.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import chatapp.client.R;
import chatapp.profileregistration.ProfileRegistrationActivity;


public class RegistrationStep1RecoverSelectionFragment extends Fragment {

    public RegistrationStep1RecoverSelectionFragment() {
        // Required empty public constructor
    }


    public static RegistrationStep1RecoverSelectionFragment newInstance() {
        RegistrationStep1RecoverSelectionFragment fragment = new RegistrationStep1RecoverSelectionFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.fragment_registration_step1_recover_by_selection, container, false);


        v.findViewById(R.id.textViewUserID).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ProfileRegistrationActivity)getActivity()).nextFragment(ProfileRegistrationActivity.RECOVER_BY_SELECTION,"USER_ID");
            }
        });

        v.findViewById(R.id.textViewPhoneNumber).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ProfileRegistrationActivity)getActivity()).nextFragment(ProfileRegistrationActivity.RECOVER_BY_SELECTION,"PHONE_NUMBER");
            }
        });

        v.findViewById(R.id.textViewCreate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ProfileRegistrationActivity)getActivity()).nextFragment(ProfileRegistrationActivity.RECOVER_BY_SELECTION,"CREATE_NEW");
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
