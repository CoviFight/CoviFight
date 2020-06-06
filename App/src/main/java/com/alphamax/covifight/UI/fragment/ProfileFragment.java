package com.alphamax.covifight.UI.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.alphamax.covifight.R;
import com.alphamax.covifight.UI.activity.NavigationActivity;
import com.alphamax.covifight.UI.activity.ProfileActivity;
import com.alphamax.covifight.UI.activity.StartActivity;
import com.alphamax.covifight.helper.QRCodeHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;


public class ProfileFragment extends Fragment {

    private TextView name,place,dob,email;
    private static final String TAG = "ProfileFragment";
    private FirebaseFirestore db;
    private ImageView barcode;
    private DocumentSnapshot document;
    private Map<String,Object> users=new HashMap<>();
    private String docName,docDob,docEmail,docHome;
    private ImageButton language;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_profile,container,false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        NavigationActivity.navigationHeading.setText(getResources().getString(R.string.navigationProfile));

        name=view.findViewById(R.id.nameProfile);
        place=view.findViewById(R.id.placeProfile);
        dob=view.findViewById(R.id.dobProfile);
        email=view.findViewById(R.id.emailProfile);
        barcode=view.findViewById(R.id.barcodeProfile);
        language=view.findViewById(R.id.languageProfile);
        final Button PrivateKey=view.findViewById(R.id.keyProfile);

        db=FirebaseFirestore.getInstance();


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        final String number=user.getPhoneNumber();


        DocumentReference docRef = db.collection("Profile").document(number);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    document = task.getResult();
                    assert document != null;
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        docName= Objects.requireNonNull(document.get("Name")).toString();
                        docDob= Objects.requireNonNull(document.get("DateOfBirth")).toString();
                        docEmail= Objects.requireNonNull(document.get("Email")).toString();
                        docHome= Objects.requireNonNull(document.get("Home")).toString();
                        name.setText(docName);
                        dob.setText(docDob);
                        email.setText(docEmail);
                        place.setText(docHome);
                        users.put("Name",document.get("Name"));
                        users.put("Email",document.get("Email"));
                        users.put("DateOfBirth",document.get("DateOfBirth"));
                        users.put("Mobile",document.get("Mobile"));
                        users.put("Home",document.get("Home"));
                        users.put("Probability",document.get("Probability"));
                        users.put("ID",document.get("ID"));
                        if(document.get("PrivateKey")!=null)
                        {
                            PrivateKey.setClickable(false);
                            PrivateKey.setBackground(getResources().getDrawable(R.drawable.bg_button_block));
                        }
                    }
                    else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        String serializeString = new Gson().toJson(number);
        Bitmap bitmap = QRCodeHelper.newInstance(getContext()).setContent(serializeString)
                .setErrorCorrectionLevel(ErrorCorrectionLevel.Q)
                .setMargin(2).getQRCOde();
        barcode.setImageBitmap(bitmap);

        PrivateKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = requireActivity().getSharedPreferences(requireActivity().getPackageName(), Context.MODE_PRIVATE);
                String privateKey=prefs.getString("PrivateKey","");
                users.put("PrivateKey",privateKey);
                db.collection("Profile").document(number).set(users);
                PrivateKey.setBackground(getResources().getDrawable(R.drawable.bg_button_block));
                PrivateKey.setClickable(false);
            }
        });

        Button logout=view.findViewById(R.id.logoutProfile);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent=new Intent(getContext(), StartActivity.class);
                startActivity(intent);
            }
        });

        final String[] languages={getResources().getString(R.string.lang_english),
                getResources().getString(R.string.lang_dutch),
                getResources().getString(R.string.lang_french),
                getResources().getString(R.string.lang_german),
                getResources().getString(R.string.lang_spanish)};

        language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
                mBuilder.setTitle("Language");
                mBuilder.setSingleChoiceItems(languages, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which==0)
                        {
                            String langCode="en";
                            Locale locale = new Locale(langCode);
                            Locale.setDefault(locale);
                            Configuration config = new Configuration();
                            config.locale = locale;
                            requireActivity().getBaseContext().getResources().updateConfiguration(config,requireActivity().getBaseContext().getResources().getDisplayMetrics());
                        }
                        else if(which==1)
                        {
                            String langCode="nl";
                            Locale locale = new Locale(langCode);
                            Locale.setDefault(locale);
                            Configuration config = new Configuration();
                            config.locale = locale;
                            requireActivity().getBaseContext().getResources().updateConfiguration(config,requireActivity().getBaseContext().getResources().getDisplayMetrics());
                        }
                        else if(which==2)
                        {
                            String langCode="fr";
                            Locale locale = new Locale(langCode);
                            Locale.setDefault(locale);
                            Configuration config = new Configuration();
                            config.locale = locale;
                            requireActivity().getBaseContext().getResources().updateConfiguration(config,requireActivity().getBaseContext().getResources().getDisplayMetrics());
                        }
                        else if(which==3)
                        {
                            String langCode="de";
                            Locale locale = new Locale(langCode);
                            Locale.setDefault(locale);
                            Configuration config = new Configuration();
                            config.locale = locale;
                            requireActivity().getBaseContext().getResources().updateConfiguration(config,requireActivity().getBaseContext().getResources().getDisplayMetrics());
                        }
                        else if(which==4)
                        {
                            String langCode="es";
                            Locale locale = new Locale(langCode);
                            Locale.setDefault(locale);
                            Configuration config = new Configuration();
                            config.locale = locale;
                            requireActivity().getBaseContext().getResources().updateConfiguration(config,requireActivity().getBaseContext().getResources().getDisplayMetrics());
                        }
                        dialog.dismiss();
                        requireActivity().recreate();
                    }
                });
                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });
    }
}