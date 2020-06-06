package com.alphamax.covifight.UI.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alphamax.covifight.R;
import com.goodiebag.pinview.Pinview;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class VerificationActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    private String verificationID;
    private PhoneAuthProvider.ForceResendingToken resendtoken;
    private String number;
    private String codeEnter;
    private int minute, second;

    private Pinview pinview;
    private TextView resend;
    private Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);
        Intent intent=getIntent();
        number=intent.getStringExtra("number");
        firebaseAuth=FirebaseAuth.getInstance();
        findViewById();
        sendVerificationCode(number);
        onClickListener();
        new CountDownTimer(120000,1000)
        {

            @Override
            public void onTick(long millisUntilFinished) {
                minute = (int)millisUntilFinished/60000;
                second = (int)(millisUntilFinished/1000)%60;

                String temp=getResources().getString(R.string.resendotp)+" in "+String.format("%02d",minute) + ":" + String.format("%02d",second);
                resend.setText(temp);
            }

            @Override
            public void onFinish()
            {
                resend.setText(getResources().getString(R.string.resendotp));
            }
        }.start();
    }

    private void findViewById()
    {
        pinview=findViewById(R.id.verficationOTP);
        pinview.setTextColor(Color.parseColor("#15135B"));
        resend=findViewById(R.id.verificationResend);
        submit=findViewById(R.id.verificationButton);
    }

    private void onClickListener()
    {
        pinview.setPinViewEventListener(new Pinview.PinViewEventListener() {
            @Override
            public void onDataEntered(Pinview pinview, boolean fromUser) {
                codeEnter = pinview.getValue();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyCode(codeEnter);
            }
        });

        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(resend.getText().toString().trim().equals(getResources().getString(R.string.resendotp)))
                {
                    reSendVerificationCode(number);
                    new CountDownTimer(120000,1000)
                    {

                        @Override
                        public void onTick(long millisUntilFinished) {
                            minute = (int)millisUntilFinished/60000;
                            second = (int)(millisUntilFinished/1000)%60;
                            String temp=getResources().getString(R.string.resendotpIn)+String.format("%02d",minute) + ":" + String.format("%02d",second);
                            resend.setText(temp);
                        }

                        @Override
                        public void onFinish() {
                            resend.setText(getResources().getString(R.string.resendotp));
                        }
                    }.start();
                }
            }
        });
    }

    private void sendVerificationCode(String mobileNumber)
    {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(mobileNumber, 60, TimeUnit.SECONDS, this, callback);
    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks callback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s,PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationID = s;
            resendtoken=forceResendingToken;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            String verificationCode = phoneAuthCredential.getSmsCode();

            if(verificationCode != null)
            {
                verifyCode(verificationCode);
            }

        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Log.i("Error:",e.toString());
            Snackbar.make(findViewById(android.R.id.content),e.toString(),Snackbar.LENGTH_LONG).setActionTextColor(Color.WHITE).show();
        }
    };

    private void verifyCode(String code)
    {
        try {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationID,code);
            signInWithCredential(credential);
        }
        catch (Exception e)
        {
            Snackbar.make(findViewById(android.R.id.content), Objects.requireNonNull(e.getMessage()),Snackbar.LENGTH_SHORT).setActionTextColor(Color.WHITE).show();
            e.printStackTrace();
        }

    }

    private void signInWithCredential(PhoneAuthCredential credential)
    {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            Intent intent=new Intent(VerificationActivity.this,ProfileActivity.class);
                            intent.putExtra("number",number);
                            startActivity(intent);
                        }
                        else
                        {
                            Snackbar.make(findViewById(android.R.id.content),"OTP Verification Failed",Snackbar.LENGTH_SHORT).setActionTextColor(Color.WHITE).show();
                        }
                    }
                });
    }

    private void reSendVerificationCode(String mobileNumber)
    {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(mobileNumber, 60, TimeUnit.SECONDS, this, callback,resendtoken);
    }

}
