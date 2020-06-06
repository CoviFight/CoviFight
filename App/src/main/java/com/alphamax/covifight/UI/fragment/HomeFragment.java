package com.alphamax.covifight.UI.fragment;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alphamax.covifight.R;
import com.alphamax.covifight.UI.activity.NavigationActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.Objects;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class HomeFragment extends Fragment {

    private double latitude;
    private double longitude;
    public static TextView textLat,textLong,textActivity;
    private double probabilityHome=0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_home,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        NavigationActivity.navigationHeading.setText(getResources().getString(R.string.navigationHome));

        //findViewById
        textLat=view.findViewById(R.id.lattitudeHome);
        textLong=view.findViewById(R.id.longitudeHome);
        textActivity=view.findViewById(R.id.activityHome);

        //Set Location in Home Fragment
        latitude=0.0;
        longitude=0.0;
        final LocationManager locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
        assert locationManager != null;
        try {
            if (ActivityCompat.checkSelfPermission(requireActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireActivity().getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //TODO
            }
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if(location!=null)
            {
                latitude=(double)Math.round(location.getLatitude() * 1000d) / 1000d;
                longitude=(double)Math.round(location.getLongitude()*1000d)/1000d;
            }
            else{
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if(location!=null)
                {
                    latitude=(double)Math.round(location.getLatitude() * 1000d) / 1000d;
                    longitude=(double)Math.round(location.getLongitude()*1000d)/1000d;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        String tempLat=getResources().getString(R.string.latitudeHintHome)+" "+Double.toString(latitude);
        textLat.setText(tempLat);
        String tempLong=getResources().getString(R.string.longitudeHintHome)+" "+Double.toString(longitude);
        textLong.setText(tempLong);
        SharedPreferences prefs = requireActivity().getSharedPreferences(requireActivity().getPackageName(), Context.MODE_PRIVATE);
        String activityUpdate=prefs.getString("Activity",getResources().getString(R.string.activityUnknown));
        textActivity.setText(activityUpdate);

        final TextView probability=view.findViewById(R.id.probabilityHome);


        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        String number = user.getPhoneNumber();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        assert number != null;
        DocumentReference docRef = db.collection("Profile").document(number);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    assert document != null;
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        probabilityHome = Double.parseDouble(Objects.requireNonNull(document.get("Probability")).toString());
                        probabilityHome*=100;
                        if (probabilityHome > 75) {
                            probability.setText("You may have caught the virus, please get yourself tested!");
                            probability.setTextColor(getResources().getColor(R.color.prob75));
                        }
                        else if (probabilityHome > 25) {
                            probability.setText("There are chance that you might be infected");
                            probability.setTextColor(getResources().getColor(R.color.prob15));
                        }
                        else
                        {
                            probability.setText("Nothing Detected yet,\nyou\'re good to go!");
                            probability.setTextColor(getResources().getColor(R.color.prob0));
                        }
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
        if (probabilityHome > 75) {
            probability.setText("You may have caught the virus, please get yourself tested!");
            probability.setTextColor(getResources().getColor(R.color.prob75));
        }
        else if (probabilityHome > 25) {
            probability.setText("There are chance that you might be infected");
            probability.setTextColor(getResources().getColor(R.color.prob15));
        }
        else
        {
            probability.setText("Nothing Detected yet,\nyou\'re good to go!");
            probability.setTextColor(getResources().getColor(R.color.prob0));
        }
    }

}
