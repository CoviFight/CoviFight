package com.alphamax.covifight.service;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.alphamax.covifight.R;
import com.alphamax.covifight.UI.activity.StartActivity;
import com.alphamax.covifight.UI.fragment.HomeFragment;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.crypto.Cipher;

public class OreoService extends Service {

    private static final String TAG = "";
    private ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();
    private PowerManager.WakeLock wakeLock;

    //Broadcast Receiver
    private BroadcastReceiver mBroadcastReceiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, "onReceive: ACTION FOUND.");
            assert action != null;
            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (!mBTDevices.contains(device)) {
                    mBTDevices.add(device);
                }
            }
        }
    };



    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        //WakeLock to keep the service running
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        assert powerManager != null;
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK ,"MyApp::MyWakelockTag");
        wakeLock.acquire(30*60*1000L /*30 minutes*/);

        //For Notification of Service.
        String channelID= UUID.randomUUID().toString();
        NotificationManager notificationManager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel(channelID, getResources().getString(R.string.app_name), NotificationManager.IMPORTANCE_NONE);
        }
        assert notificationManager != null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(channel);
        }
        Intent notificationIntent=new Intent(getApplicationContext(), StartActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(),0,notificationIntent,0);
        Notification notification =new NotificationCompat.Builder(getApplicationContext(),channelID).setContentIntent(contentIntent).setContentTitle(getResources().getString(R.string.app_name)).setSmallIcon(R.mipmap.shield).build();
        //To keep the service alive
        startForeground(startId,notification);

        //Bluetooth Adapter
        BluetoothAdapter mBluetoothAdapter;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        SharedPreferences prefs = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        String bluetoothName=prefs.getString("UUID","");
        mBluetoothAdapter.setName(bluetoothName);
        if(!mBluetoothAdapter.isEnabled())
        {
            mBluetoothAdapter.enable();
        }
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
            Log.d(TAG, "btnDiscover: Canceling discovery.");
            mBluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
        }
        if (!mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
        }

        //For Location
        final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        assert locationManager != null;


        //For Activity Detection
        ActivityRecognitionClient activityRecognitionClient = new ActivityRecognitionClient(this);
        Intent activityDetectionIntent = new Intent(this, OreoActivityDetectionService.class);
        activityRecognitionClient.requestActivityUpdates(45000,PendingIntent.getService(this, 0, activityDetectionIntent, PendingIntent.FLAG_UPDATE_CURRENT));


        //Countdown Timer
        final CountDownTimer countDownTimer = new CountDownTimer(90000, 1000) {
            @Override
            public void onTick(long millisUntilFinished)
            {

            }
            @Override
            public void onFinish() {

                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                assert user != null;
                final String number =user.getPhoneNumber();

                //Database Reference
                FirebaseFirestore firestore= FirebaseFirestore.getInstance();
                try
                {
                    firestore.enableNetwork().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.i(TAG, "onSuccess: FirebaseDatabase");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                Map<String,Object> Data=new HashMap<>();

                //TimeStamp
                Date currentTimeobj = Calendar.getInstance().getTime();
                String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(currentTimeobj);
                String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(currentTimeobj);
                String currentDateandTime = currentDate+" at "+currentTime;
                long dateInsecs = (currentTimeobj.getTime())/1000;
                Data.put("TimeStamps",currentDateandTime);

                //Location
                double latitude=0.0;
                double longitude=0.0;
                try {
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        //TODO
                    }
                    Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if(location!=null)
                    {
                        latitude=(double)Math.round(location.getLatitude() * 1000d) / 1000d;
                        longitude=(double)Math.round(location.getLongitude()*1000d)/1000d;
                    }
                    else{
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
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
                try
                {
                    String tempLat=getResources().getString(R.string.latitudeHintHome)+" "+Double.toString(latitude);
                    HomeFragment.textLat.setText(tempLat);
                    String tempLong=getResources().getString(R.string.longitudeHintHome)+" "+Double.toString(longitude);
                    HomeFragment.textLong.setText(tempLong);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                String loc=""+latitude+","+longitude;
                Data.put("Location",TestEncryptData(loc));

                //Activity Update
                SharedPreferences prefs = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
                String activityUpdate=prefs.getString("Activity",getResources().getString(R.string.activityUnknown));
                Data.put("Activity",activityUpdate);
                try {
                    HomeFragment.textActivity.setText(activityUpdate);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

                //Disconnecting Bluetooth Adapter
                try{
                    unregisterReceiver(mBroadcastReceiver3);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

                //Pushing Data on Firebase
                try{
                    if(mBTDevices.size()!=0)
                    {
                        List<String> macAddress=new ArrayList<>();
                        for(int i=0;i<mBTDevices.size();i++)
                        {
                            String tempMacAddress=mBTDevices.get(i).getName();
                            //Toast.makeText(OreoService.this, tempMacAddress, Toast.LENGTH_SHORT).show();
                            if(tempMacAddress!=null && tempMacAddress.contains("Covid"))
                            {
                                macAddress.add(tempMacAddress);
                            }
                        }
                        Data.put("MacAddress",macAddress);
                    }
                    assert number != null;
                    firestore.collection("Profile").document(number).collection("TimeStamps").document("" + dateInsecs).set(Data).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "onSuccess: DataPushed");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                            Toast.makeText(OreoService.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

                //Restarting the service
                Intent restartService = new Intent(getApplicationContext(),OreoService.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    wakeLock.release();
                    startForegroundService(restartService);
                }
            }
        };
        Log.d(TAG, "onStartCommand: CountDownTimerStart");
        countDownTimer.start();
        return START_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        try
        {
            unregisterReceiver(mBroadcastReceiver3);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        Intent restartService = new Intent(getApplicationContext(),OreoService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            startForegroundService(restartService);
            wakeLock.release();
        }
        super.onDestroy();
    }

    public String TestEncryptData(String dataToEncrypt) {

        //Retrieve Public Key
        SharedPreferences prefs = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        String publicKey=prefs.getString("PublicKey","");

        //Encryption
        return encryptRSAToString(dataToEncrypt, publicKey);
    }

    public static String encryptRSAToString(String clearText, String publicKey) {

        String encryptedBase64 = "";
        try {
            KeyFactory keyFac = KeyFactory.getInstance("RSA");
            KeySpec keySpec = new X509EncodedKeySpec(Base64.decode(publicKey.trim().getBytes(), Base64.DEFAULT));
            PublicKey key = keyFac.generatePublic(keySpec);

            // get an RSA cipher object and print the provider
            //final Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWITHSHA-256ANDMGF1PADDING");

            final Cipher cipher = Cipher.getInstance("RSA/NONE/OAEPWithSHA-512AndMGF1Padding");
            // encrypt the plain text using the public key
            cipher.init(Cipher.ENCRYPT_MODE, key);

            byte[] encryptedBytes = cipher.doFinal(clearText.getBytes("UTF-8"));
            encryptedBase64 = new String(Base64.encode(encryptedBytes, Base64.DEFAULT));
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return encryptedBase64;//.replaceAll("(\\r|\\n)", "");
    }

}
