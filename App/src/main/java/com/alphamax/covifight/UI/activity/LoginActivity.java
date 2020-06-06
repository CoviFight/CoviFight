package com.alphamax.covifight.UI.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alphamax.covifight.R;

import com.google.android.material.snackbar.Snackbar;


public class LoginActivity extends AppCompatActivity {

    private Button nextButton;
    private TextView areaCodeText;
    private EditText phoneNumber;
    private String areaCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViewById();
        areaCode="+"+getCountryZipCode();
        areaCodeText.setText(areaCode);
        onClickListener();
    }

    private void findViewById()
    {
        nextButton=findViewById(R.id.loginButton);
        areaCodeText=findViewById(R.id.loginNumberCode);
        phoneNumber=findViewById(R.id.loginNumber);
    }

    private String getCountryZipCode()
    {
        String CountryID="";
        String CountryZipCode="";
        TelephonyManager manager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        //getNetworkCountryIso
        assert manager != null;
        CountryID= manager.getSimCountryIso().toUpperCase();
        String[] rl=this.getResources().getStringArray(R.array.CountryCodes);
        for (String s : rl)
        {
            String[] g = s.split(",");
            if (g[1].trim().equals(CountryID.trim()))
            {
                CountryZipCode = g[0];
                break;
            }
        }
        return CountryZipCode;
    }

    private void onClickListener()
    {
        nextButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if(phoneNumber.getText().toString().isEmpty())
                {
                    Snackbar.make(findViewById(android.R.id.content),getResources().getString(R.string.loginSnackbarMobile),Snackbar.LENGTH_SHORT).setActionTextColor(Color.WHITE).show();
                }
                else
                {
                    Intent intent=new Intent(LoginActivity.this,VerificationActivity.class);
                    String number=areaCode+phoneNumber.getText().toString().trim();
                    intent.putExtra("number",number);
                    startActivity(intent);
                }
            }
        });
    }

}
