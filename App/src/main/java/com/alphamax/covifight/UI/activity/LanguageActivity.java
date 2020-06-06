package com.alphamax.covifight.UI.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alphamax.covifight.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.Locale;
import java.util.prefs.Preferences;

public class LanguageActivity extends AppCompatActivity {

    private TextView english,dutch,french,spanish,german;
    private boolean engStatus,dutStatus,freStatus,spaStatus,gerStatus,choice;
    private Button proceed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);
        findViewById();
        setBool();
        onClickListener();
    }

    private void findViewById()
    {
        english=findViewById(R.id.languageEnglish);
        dutch=findViewById(R.id.languageDutch);
        french=findViewById(R.id.languageFrench);
        spanish=findViewById(R.id.languageSpanish);
        german=findViewById(R.id.languageGerman);
        proceed=findViewById(R.id.languageButton);
    }

    private void setBool()
    {
        engStatus=false;
        dutStatus=false;
        freStatus=false;
        spaStatus=false;
        gerStatus=false;
        choice=false;
    }

    private void onClickListener()
    {

        english.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(engStatus)
                {
                    english.setBackgroundResource(R.color.white);
                    engStatus=false;
                    choice=false;
                }
                else
                {
                    if(choice)
                    {
                        if(dutStatus)
                        {
                            dutch.setBackgroundResource(R.color.white);
                            dutStatus=false;
                        }
                        else if(spaStatus)
                        {
                            spanish.setBackgroundResource(R.color.white);
                            spaStatus=false;
                        }
                        else if(freStatus)
                        {
                            french.setBackgroundResource(R.color.white);
                            freStatus=false;
                        }
                        else if(gerStatus)
                        {
                            german.setBackgroundResource(R.color.white);
                            gerStatus=false;
                        }
                    }
                    else
                    {
                        choice=true;
                    }
                    english.setBackgroundResource(R.drawable.bg_language);
                    engStatus=true;
                }
            }
        });

        french.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(freStatus)
                {
                    french.setBackgroundResource(R.color.white);
                    freStatus=false;
                    choice=false;
                }
                else
                {
                    if(choice)
                    {
                        if(dutStatus)
                        {
                            dutch.setBackgroundResource(R.color.white);
                            dutStatus=false;
                        }
                        else if(spaStatus)
                        {
                            spanish.setBackgroundResource(R.color.white);
                            spaStatus=false;
                        }
                        else if(engStatus)
                        {
                            english.setBackgroundResource(R.color.white);
                            engStatus=false;
                        }
                        else if(gerStatus)
                        {
                            german.setBackgroundResource(R.color.white);
                            gerStatus=false;
                        }
                    }
                    else
                    {
                        choice=true;
                    }
                    french.setBackgroundResource(R.drawable.bg_language);
                    freStatus=true;
                }
            }
        });

        dutch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dutStatus)
                {
                    dutch.setBackgroundResource(R.color.white);
                    dutStatus=false;
                    choice=false;
                }
                else
                {
                    if(choice)
                    {
                        if(engStatus)
                        {
                            english.setBackgroundResource(R.color.white);
                            engStatus=false;
                        }
                        else if(spaStatus)
                        {
                            spanish.setBackgroundResource(R.color.white);
                            spaStatus=false;
                        }
                        else if(freStatus)
                        {
                            french.setBackgroundResource(R.color.white);
                            freStatus=false;
                        }
                        else if(gerStatus)
                        {
                            german.setBackgroundResource(R.color.white);
                            gerStatus=false;
                        }
                    }
                    else
                    {
                        choice=true;
                    }
                    dutch.setBackgroundResource(R.drawable.bg_language);
                    dutStatus=true;
                }
            }
        });

        spanish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(spaStatus)
                {
                    spanish.setBackgroundResource(R.color.white);
                    spaStatus=false;
                    choice=false;
                }
                else
                {
                    if(choice)
                    {
                        if(dutStatus)
                        {
                            dutch.setBackgroundResource(R.color.white);
                            dutStatus=false;
                        }
                        else if(engStatus)
                        {
                            english.setBackgroundResource(R.color.white);
                            engStatus=false;
                        }
                        else if(freStatus)
                        {
                            french.setBackgroundResource(R.color.white);
                            freStatus=false;
                        }
                        else if(gerStatus)
                        {
                            german.setBackgroundResource(R.color.white);
                            gerStatus=false;
                        }
                    }
                    else
                    {
                        choice=true;
                    }
                    spanish.setBackgroundResource(R.drawable.bg_language);
                    spaStatus=true;
                }
            }
        });

        german.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(gerStatus)
                {
                    german.setBackgroundResource(R.color.white);
                    gerStatus=false;
                    choice=false;
                }
                else
                {
                    if(choice)
                    {
                        if(dutStatus)
                        {
                            dutch.setBackgroundResource(R.color.white);
                            dutStatus=false;
                        }
                        else if(spaStatus)
                        {
                            spanish.setBackgroundResource(R.color.white);
                            spaStatus=false;
                        }
                        else if(freStatus)
                        {
                            french.setBackgroundResource(R.color.white);
                            freStatus=false;
                        }
                        else if(engStatus)
                        {
                            english.setBackgroundResource(R.color.white);
                            engStatus=false;
                        }
                    }
                    else
                    {
                        choice=true;
                    }
                    german.setBackgroundResource(R.drawable.bg_language);
                    gerStatus=true;
                }
            }
        });

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(choice)
                {
                    String langCode="en";
                    if(engStatus)
                    {
                        langCode="en";
                    }
                    else if(gerStatus)
                    {
                        langCode="fr";
                    }
                    else if(spaStatus)
                    {
                        langCode="es";
                    }
                    else if(dutStatus)
                    {
                        langCode="nl";
                    }
                    else if(freStatus)
                    {
                        langCode="fr";
                    }
                    Locale locale = new Locale(langCode);
                    Locale.setDefault(locale);
                    Configuration config = new Configuration();
                    config.locale = locale;
                    getBaseContext().getResources().updateConfiguration(config,getBaseContext().getResources().getDisplayMetrics());
                    Intent intent=new Intent(LanguageActivity.this,LoginActivity.class);
                    startActivity(intent);
                }
                else
                {
                    Snackbar.make(findViewById(android.R.id.content),"Please choose a language",Snackbar.LENGTH_SHORT).setActionTextColor(Color.WHITE).show();
                }
            }
        });

    }

}
