package com.alphamax.covifight.UI.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.alphamax.covifight.R;
import com.alphamax.covifight.service.OreoService;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class NavigationActivity extends AppCompatActivity {

    public static TextView navigationHeading;
    private int backButtonCount = 0;
    public static List<String> listLocations=new ArrayList<>();
    public static List<LatLng> heatMapList=new ArrayList<com.google.android.gms.maps.model.LatLng>();
    public static Integer probabilityHome;
    private static final String TAG = "";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        NavController navController = Navigation.findNavController(NavigationActivity.this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navView, navController);
        navigationHeading=findViewById(R.id.navigationHeading);
        navigationHeading=findViewById(R.id.navigationHeading);
        probabilityHome=0;
        getHotSpot();
        requestLocationPermission();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            Intent intent=new Intent(NavigationActivity.this, OreoService.class);
            startForegroundService(intent);
        }

    }

    public void getHotSpot()
    {
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        db.collection("HighRiskPlaces").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult()))
                    {
                        listLocations.add(document.getId());
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void requestLocationPermission()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Location Permission")
                        .setMessage("Please kindly select the permission for always")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                ActivityCompat.requestPermissions(NavigationActivity.this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001);
                            }
                        }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.create().show();
            }
            else
            {
                requestSMSPermission();
            }
        }
        else
        {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(NavigationActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001);
            }
            else
            {
                requestSMSPermission();
            }
        }
    }

    private void requestSMSPermission()
    {
        if (checkSelfPermission(Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(NavigationActivity.this,new String[]{Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS}, 1002);
        }
        else
        {
            requestStoragePermission();
        }
    }

    private void requestStoragePermission()
    {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(NavigationActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1003);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case 1001:if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED))
            {
                requestLocationPermission();
            }
            else if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                requestSMSPermission();
            }
                break;
            case 1002:if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED))
            {
                requestSMSPermission();
            }
            else if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                requestStoragePermission();
            }
                break;
            case 1003:if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED))
            {
                requestStoragePermission();
            }
                break;
        }
    }

    @Override
    public void onBackPressed()
    {
        if (backButtonCount >= 1) {
            backButtonCount = 0;
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            Snackbar.make(findViewById(android.R.id.content),getResources().getString(R.string.navigationSnackbar),Snackbar.LENGTH_LONG).setActionTextColor(Color.WHITE).show();
            backButtonCount++;
        }
    }

}
