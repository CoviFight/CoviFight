package com.alphamax.covifight.UI.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.alphamax.covifight.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class ProfileActivity extends AppCompatActivity {

    private EditText name,email,dob,home;
    private FirebaseFirestore database;
    private String number;
    private Button save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent=getIntent();
        number=intent.getStringExtra("number");

        database=FirebaseFirestore.getInstance();
        database.collection("Profile").document(number).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                assert document != null;
                if (document.exists())
                {
                    SharedPreferences.Editor editor = getSharedPreferences(getPackageName(), MODE_PRIVATE).edit();

                    //Encryption Keys
                    KeyPair kp = getKeyPair();
                    //Public Key
                    PublicKey publicKey = kp.getPublic();
                    byte[] publicKeyBytes = publicKey.getEncoded();
                    String publicKeyBytesBase64 = new String(Base64.encode(publicKeyBytes, Base64.DEFAULT));
                    editor.putString("PublicKey",publicKeyBytesBase64);

                    //PrivateKey
                    PrivateKey privateKey = kp.getPrivate();
                    byte[] privateKeyBytes = privateKey.getEncoded();
                    String privateKeyBytesBase64 = new String(Base64.encode(privateKeyBytes, Base64.DEFAULT));
                    editor.putString("PrivateKey",privateKeyBytesBase64);
                    editor.putString("UUID", Objects.requireNonNull(document.get("ID")).toString());
                    editor.putString("Activity",getResources().getString(R.string.activityUnknown));
                    editor.apply();

                    Intent intent1=new Intent(ProfileActivity.this,NavigationActivity.class);
                    startActivity(intent1);
                }
            }
        });

        setContentView(R.layout.activity_profile);

        findViewById();
        onClickListener();
    }

    private void findViewById()
    {
        name=findViewById(R.id.profileName);
        email=findViewById(R.id.profileEmail);
        dob=findViewById(R.id.profileDOB);
        home=findViewById(R.id.profileHomeTown);
        save=findViewById(R.id.profileButton);
    }

    private void onClickListener()
    {

        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog dpd = new DatePickerDialog(ProfileActivity.this, R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        String date = dayOfMonth + "/" + (monthOfYear+1) + "/" + year;
                        dob.setText(date);
                        dob.setSelection(dob.getText().length());
                    }
                },      Calendar.getInstance().get(Calendar.YEAR),
                        Calendar.getInstance().get(Calendar.MONTH),
                        Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
                dpd.show();

                Button ok = dpd.getButton(DatePickerDialog.BUTTON_POSITIVE);
                Button can = dpd.getButton(DatePickerDialog.BUTTON_NEGATIVE);

                ok.setTextColor(getResources().getColor(R.color.colorPrimary));
                can.setTextColor(getResources().getColor(R.color.colorPrimary));

                ok.setBackground(null);
                can.setBackground(null);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkData())
                {
                    String nm=name.getText().toString();
                    String em=email.getText().toString();
                    String db=dob.getText().toString();
                    String hm=home.getText().toString();
                    String uuid="Covid"+ UUID.randomUUID().toString()+"Safe";

                    Map<String,Object> users=new HashMap<>();
                    users.put("Name",nm);
                    users.put("Email",em);
                    users.put("DateOfBirth",db);
                    users.put("Mobile",number);
                    users.put("Home",hm);
                    users.put("Probability",0);
                    users.put("ID",uuid);

                    SharedPreferences.Editor editor = getSharedPreferences(getPackageName(), MODE_PRIVATE).edit();

                    //Encryption Keys
                    KeyPair kp = getKeyPair();
                    //Public Key
                    PublicKey publicKey = kp.getPublic();
                    byte[] publicKeyBytes = publicKey.getEncoded();
                    String publicKeyBytesBase64 = new String(Base64.encode(publicKeyBytes, Base64.DEFAULT));
                    editor.putString("PublicKey",publicKeyBytesBase64);

                    //PrivateKey
                    PrivateKey privateKey = kp.getPrivate();
                    byte[] privateKeyBytes = privateKey.getEncoded();
                    String privateKeyBytesBase64 = new String(Base64.encode(privateKeyBytes, Base64.DEFAULT));
                    editor.putString("PrivateKey",privateKeyBytesBase64);

                    //bluetoothName
                    editor.putString("UUID",uuid);
                    editor.apply();

                    database.collection("Profile").document(number).set(users);

                    Map<String,Object> ID=new HashMap<>();
                    ID.put("Number",number);
                    database.collection("Identify").document(uuid).set(ID);
                    Intent intent=new Intent(ProfileActivity.this,NavigationActivity.class);
                    startActivity(intent);
                }
                else
                {
                    Snackbar.make(findViewById(android.R.id.content),"Please Enter all data.",Snackbar.LENGTH_SHORT).setActionTextColor(Color.WHITE).show();
                }
            }
        });

    }

    private boolean checkData()
    {
        if(dob.getText().toString().isEmpty())
        {
            return false;
        }
        if(home.getText().toString().isEmpty())
        {
            return false;
        }
        if(name.getText().toString().isEmpty())
        {
            return false;
        }
        if(email.getText().toString().isEmpty())
        {
            return false;
        }
        return true;
    }

    public static KeyPair getKeyPair()
    {
        KeyPair kp = null;
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048);
            kp = kpg.generateKeyPair();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return kp;
    }

}
