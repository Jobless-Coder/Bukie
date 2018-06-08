package com.example.krishna.bukie;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText username,password,email;
    private Button login;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth=FirebaseAuth.getInstance();
       /*FirebaseUser user to get email */

        username=(EditText)findViewById(R.id.username);
        email=(EditText)findViewById(R.id.email);
        password=(EditText)findViewById(R.id.password);
        login=(Button)findViewById(R.id.login);
        login.setOnClickListener(this);
        progressDialog=new ProgressDialog(this);
        if(firebaseAuth.getCurrentUser()!=null) {
            firebaseAuth.signOut();
            Toast.makeText(this, "already signed in", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        loginUser();
    }

    private void loginUser() {
        String emailid=email.getText().toString().trim();
        //String userid=username.getText().toString().trim();
        String passwordid=password.getText().toString().trim();

        //Toast.makeText(this, emailid+passwordid, Toast.LENGTH_SHORT).show();
        if(TextUtils.isEmpty(emailid)){
            Toast.makeText(this, "Email cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(passwordid)){
            Toast.makeText(this, "Password cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Logging in...");
        progressDialog.show();
        firebaseAuth.signInWithEmailAndPassword(emailid,passwordid)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()){
                            Toast.makeText(LoginActivity.this, "Success logging in", Toast.LENGTH_SHORT).show();
                        }
                        else
                            Toast.makeText(LoginActivity.this, "Failure", Toast.LENGTH_SHORT).show();
                    }
                });
        password.setText("");
        email.setText("");
    }
}
