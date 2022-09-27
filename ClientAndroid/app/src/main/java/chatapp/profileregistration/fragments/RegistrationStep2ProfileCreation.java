package chatapp.profileregistration.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import chatapp.client.R;
import chatapp.communication.websocket.ProfileRegistrationEndpoint;
import chatapp.extras.SwissArmyKnife;
import chatapp.extras.Validator;
import chatapp.profileregistration.ProfileRegistrationActivity;
import chatapp.storage.tables.Profile;
import chatapp.storage.tables.RegistrationStatus;
import chatapp.storage.tables.contentproviders.RegistrationContentProvider;


public class RegistrationStep2ProfileCreation extends Fragment {

    private final static int IMAGE_BROWSE_RESULT = 0;
    private final static int TAKE_PHOTO_RESULT = 1;
    private final static int PERFORM_CROP = 2;

    private ImageButton imageButtonProfileImage;
    private AppCompatEditText editTextProfileName;
    private AppCompatEditText editTextQuote;
    private TextView textViewCreate;

    //Profile Details
    String profileName;
    Bitmap profileImage;
    String quote;

    private ProgressDialog progressBar;


    public RegistrationStep2ProfileCreation() {
        // Required empty public constructor
    }


    public static RegistrationStep2ProfileCreation newInstance() {
        RegistrationStep2ProfileCreation fragment = new RegistrationStep2ProfileCreation();
        return fragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        saveCurrentData();
    }

    private void saveCurrentData(){

        ContentValues cv = new ContentValues();
        cv.put("ProfileName",editTextProfileName.getText().toString());
        cv.put("Quote",editTextQuote.getText().toString());
        if(profileImage!=null) {
            cv.put("ProfileImage", SwissArmyKnife.getBitmapAsByteArray(profileImage));
        }
        getActivity().getContentResolver().insert(RegistrationContentProvider.PROFILE_URI, cv);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Cursor c=getActivity().getContentResolver().query(RegistrationContentProvider.PROFILE_URI, null, null, null, null);
        if (c!=null && c.getCount()>0){
            c.moveToFirst();
            editTextProfileName.setText(c.getString(Profile.PROFILE_NAME));
            editTextQuote.setText(c.getString(Profile.QUOTE));
            byte [] imageData =c.getBlob(Profile.PROFILE_IMAGE);
            if(imageData!=null && imageData.length>0){
                imageButtonProfileImage.setImageBitmap(SwissArmyKnife.getBitmapFromByteArray(imageData));
            }

            c.close();
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_registration_step2_profile_creation, container, false);

        editTextProfileName = (AppCompatEditText)v.findViewById(R.id.editTextProfileName);
        editTextQuote = (AppCompatEditText)v.findViewById(R.id.editTextQuote);
        imageButtonProfileImage = (ImageButton) v.findViewById(R.id.imageButtonProfileImage);
        ImageButton profileMask = (ImageButton) v.findViewById(R.id.imageButtonProfileImageMask);
        textViewCreate = (TextView)v.findViewById(R.id.textViewCreate);


        //Progress Circle
        progressBar = new ProgressDialog(v.getContext());
        progressBar.setCancelable(false);
        progressBar.setMessage("Please Wait ...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.setProgress(0);
        progressBar.setMax(100);


        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.popup_profile_image_selector, null);
        final PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        FloatingActionButton fabBrowseImage = (FloatingActionButton) popupView.findViewById(R.id.fabBrowseImage);
        FloatingActionButton fabTakePhoto = (FloatingActionButton) popupView.findViewById(R.id.fabTakePhoto);

        popupWindow.setFocusable(true);


        fabBrowseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Close Popup
                popupWindow.dismiss();

                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                getActivity().startActivityForResult(intent, IMAGE_BROWSE_RESULT);
            }
        });

        fabTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Close Popup
                popupWindow.dismiss();

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                getActivity().startActivityForResult(intent, TAKE_PHOTO_RESULT);

            }
        });

        imageButtonProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.showAtLocation(getView(), Gravity.CENTER, 0, 0);
            }
        });

        profileMask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageButtonProfileImage.performClick();
            }
        });

        textViewCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Validator.validateProfileName(editTextProfileName.getText().toString())){
                    Snackbar.make(getView(),"Invalid Profile Name",Snackbar.LENGTH_SHORT).show();
                    return;
                }
                profileName = editTextProfileName.getText().toString();
                quote = editTextQuote.getText().toString();

                //Create Profile
                ((ProfileRegistrationActivity)getActivity()).createProfile(profileName,profileImage,quote);

                //Show progress circle
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

            }

            @Override
            public void profileCreatedEvent(boolean result) {
                progressBar.dismiss();

                if(result){
                    //Save Profile to database
                    ContentValues cv = new ContentValues();
                    cv.put("ProfileName",profileName);
                    if(profileImage!=null){
                        cv.put("ProfileImage",SwissArmyKnife.getBitmapAsByteArray(profileImage));
                    }
                    cv.put("Quote",quote);
                    getActivity().getContentResolver().insert(RegistrationContentProvider.PROFILE_URI, cv);
                    //Update Installation Status to completed
                    cv = new ContentValues();
                    cv.put("Completed","TRUE");
                    getActivity().getContentResolver().insert(RegistrationContentProvider.INSTALLATION_URI, cv);
                    //Close Registration Window
                    getActivity().setResult(Activity.RESULT_OK);
                    getActivity().finish();
                }else{
                    Snackbar.make(getView(),"Some thing went Wrong, Please Try Again.",Snackbar.LENGTH_LONG).show();
                }

            }

            @Override
            public void profileRecoveredEvent(boolean result, String userID, String loginID) {

            }


        });


        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {

            //If cancelled no more checking
            if (resultCode != getActivity().RESULT_OK) {
                return;
            }

            if (requestCode == IMAGE_BROWSE_RESULT && null != data) {
                Uri uri = data.getData();
                performCrop(uri);
            }

            else if (requestCode == TAKE_PHOTO_RESULT && null != data) {
                try {
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    performCrop(SwissArmyKnife.getImageUri(getContext(),bitmap));
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            else if (requestCode == PERFORM_CROP && null != data) {
                try {
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    Bitmap newBitmap = SwissArmyKnife.getScaledBitMapBaseOnScreenSize(bitmap, getActivity());
                    //Adding to Profile Image for later saving to database
                    profileImage=newBitmap;
                    imageButtonProfileImage.setImageBitmap(bitmap);

                    //Save Bitmap before loss Fragment
                    saveCurrentData();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "Something went wrong!, Please Try again", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void performCrop(Uri picUri) {
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            cropIntent.setDataAndType(picUri, "image/*");
            // set crop properties
            cropIntent.putExtra("crop", "true");
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            // indicate output X and Y
            cropIntent.putExtra("outputX", 128);
            cropIntent.putExtra("outputY", 128);
            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            getActivity().startActivityForResult(cropIntent, PERFORM_CROP);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
