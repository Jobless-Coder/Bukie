package com.example.krishna.bukie;

import android.app.Notification;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

public class ReportActivity extends AppCompatActivity {
    private RadioGroup radioGroup;
    private EditText editText;
    private View reportbtn;
    private int checkedRadioButtonId;
    private String reason,additionalinfo,timestamp,uid,fullname,adid;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        Bundle bundle=getIntent().getExtras();
        adid=bundle.getString("adid");
        progressDialog=new ProgressDialog(this);
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Report an ad");
        SharedPreferences sharedPreferences=getSharedPreferences("UserInfo",MODE_PRIVATE);
        uid=sharedPreferences.getString("uid",null);
        fullname=sharedPreferences.getString("fullname",null);
        radioGroup=findViewById(R.id.radiogrp);
        editText=findViewById(R.id.additional);
        reportbtn=findViewById(R.id.reportbtn);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                checkedRadioButtonId=checkedId;
                if(checkedId==R.id.rb5)
                    editText.setVisibility(View.VISIBLE);

                else {
                    editText.setText("");
                    editText.setVisibility(View.GONE);
                }

            }
        });
        reportbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(ReportActivity.this, "jjjj", Toast.LENGTH_SHORT).show();
                RadioButton radioBtn = (RadioButton) findViewById(checkedRadioButtonId);
                reason=radioBtn.getText().toString();
                additionalinfo=editText.getText().toString();
                Date date=new Date();
                timestamp=date.getTime()+"";
                Report report=new Report(uid,fullname,reason,additionalinfo,timestamp);
                        progressDialog.setMessage("Reporting ad .....");
                        progressDialog.show();
                FirebaseDatabase.getInstance().getReference().child("reports").child(adid).push().setValue(report).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                progressDialog.dismiss();
                                Toast.makeText(ReportActivity.this, "Ad reported successfully", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            else {

                                progressDialog.dismiss();
                                Toast.makeText(ReportActivity.this, "Failure reporting ad", Toast.LENGTH_SHORT).show();


                            }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(ReportActivity.this, "Failure reporting ad", Toast.LENGTH_SHORT).show();


                    }
                });


            }
        });


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}
