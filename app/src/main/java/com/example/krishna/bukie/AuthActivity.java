package com.example.krishna.bukie;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.wifi.hotspot2.pps.Credential;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;

public class AuthActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int RC_SIGN_IN =1 ;
    private static final String TAG = "Googleex";
    private int height, width;
    TextView hello, bye, forgotpass;
    boolean helloInMiddle;
    int big, small, hsm, hl, bsm, bl, loginsize, GREY;
    LinearLayout loginflow, signflow,verifyemail,verifyemail2;
    View facebookbtn, googlebtn, signinbtn, signupbtn,sendemail;
    EditText regemail, regpass, repregpass, loginpass, loginemail,forgotemail;
    String email, password,reppassword,signinmethod;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private CallbackManager mCallbackManager;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignIn account;
    FirebaseUser currentUser;
    TextView verifybtn;
    private FirebaseFirestore firebaseFirestore;
    LoginManager loginManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences=getSharedPreferences("UserInfo",MODE_PRIVATE);
        String userid=sharedPreferences.getString("uid",null);


     //   Toast.makeText(this, ""+uid, Toast.LENGTH_SHORT).show();
        if(userid!=null){
            Intent intent = new Intent(getApplicationContext(), HomePageActivity.class);
            startActivity(intent);
            finish();
        }

        //Toast.makeText(this, "hello123", Toast.LENGTH_SHORT).show();
        firebaseAuth=FirebaseAuth.getInstance();
        FirebaseUser user=firebaseAuth.getCurrentUser();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        loginManager=LoginManager.getInstance();



        setContentView(R.layout.activity_auth);
        regemail = findViewById(R.id.regemail);
        regpass = findViewById(R.id.regpass);
        repregpass = findViewById(R.id.repregpass);
        loginpass = findViewById(R.id.loginpass);
        loginemail = findViewById(R.id.loginemail);
        forgotpass = findViewById(R.id.forgotpass);
        facebookbtn = findViewById(R.id.facebookbtn);
        googlebtn = findViewById(R.id.googlebtn);
        signinbtn=findViewById(R.id.signinbtn);
        signupbtn=findViewById(R.id.signupbtn);
        googlebtn.setOnClickListener(this);
        facebookbtn.setOnClickListener(this);
        forgotpass.setOnClickListener(this);
        signinbtn.setOnClickListener(this);
        sendemail=findViewById(R.id.sendemail);
        sendemail.setOnClickListener(this);
        signupbtn.setOnClickListener(this);
        verifyemail=findViewById(R.id.verifyemail);
        verifybtn=findViewById(R.id.verifybtn);
        verifyemail.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);
        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();

        //google sign in



        //facebooksign in
        signOut();
        mCallbackManager = CallbackManager.Factory.create();
        loginManager.registerCallback(
                mCallbackManager,
                new FacebookCallback < LoginResult > () {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // Handle success
                        handleFacebookAccessToken(loginResult.getAccessToken());


                    }

                    @Override
                    public void onCancel() {
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        progressDialog.dismiss();
                    }
                }
        );



        //animations
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;

        helloInMiddle = true;
        big = 30;
        small = 15;
        GREY = 0xffeaeaea;

        hello = findViewById(R.id.hello);
        bye = findViewById(R.id.bye);


        loginflow = findViewById(R.id.loginflow);
        signflow = findViewById(R.id.signup);

        hello.setTextSize(big);
        bye.setTextSize(small);

        hello.setTranslationX(width / 2 - 106);
        bye.setTranslationX(width * 3 / 4);

        signflow.setTranslationX(width * 2);
    }
//animtions
    public void translate(View view) {

        if (view.getId() == R.id.hello) {
            if (helloInMiddle) return;

            hsm = hello.getWidth();
            bl = bye.getWidth();
            //bring hello to middle
            //bring bye to 3/4th of screen

            ObjectAnimator animator = ObjectAnimator.ofFloat(hello, "translationX", width / 2 - hl / 2);
            ObjectAnimator anim2 = ObjectAnimator.ofFloat(bye, "translationX", width * 3 / 4 - bsm / 2);

            ObjectAnimator anim3 = ObjectAnimator.ofFloat(hello, "textSize", small, big);
            ObjectAnimator anim4 = ObjectAnimator.ofFloat(bye, "textSize", big, small);

            ObjectAnimator anim5 = ObjectAnimator.ofFloat(loginflow, "translationX", width / 2 - loginflow.getWidth());
            ObjectAnimator anim6 = ObjectAnimator.ofFloat(signflow, "translationX", width + signflow.getWidth());

            ObjectAnimator anim7 = ObjectAnimator.ofObject(bye, "textColor", new ArgbEvaluator(), Color.WHITE, GREY);
            ObjectAnimator anim8 = ObjectAnimator.ofObject(hello, "textColor", new ArgbEvaluator(), GREY, Color.WHITE);

            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            anim2.setInterpolator(new AccelerateDecelerateInterpolator());
            AnimatorSet set = new AnimatorSet();
            set.play(animator).with(anim2).with(anim3).with(anim4).with(anim5).with(anim6).with(anim7).with(anim8);

            //findViewById(R.id.loginparent).setVisibility(View.VISIBLE);
            set.start();
            helloInMiddle = true;
        } else {
            if (!helloInMiddle) return;

            hl = hello.getWidth();
            bsm = bye.getWidth();
            //bring bye to middle
            //bring hello to 1/4th of screen
            ObjectAnimator animator = ObjectAnimator.ofFloat(hello, "translationX", width / 4 - hello.getWidth() / 2);
            ObjectAnimator anim2 = ObjectAnimator.ofFloat(bye, "translationX", width / 2 - bsm);


            ObjectAnimator anim3 = ObjectAnimator.ofFloat(hello, "textSize", big, small);
            ObjectAnimator anim4 = ObjectAnimator.ofFloat(bye, "textSize", small, big);


            ObjectAnimator anim5 = ObjectAnimator.ofFloat(loginflow, "translationX", -loginflow.getWidth() * 2);
            ObjectAnimator anim6 = ObjectAnimator.ofFloat(signflow, "translationX", width / 2 - signflow.getWidth());

            ObjectAnimator anim7 = ObjectAnimator.ofObject(hello, "textColor", new ArgbEvaluator(), Color.WHITE, GREY);
            ObjectAnimator anim8 = ObjectAnimator.ofObject(bye, "textColor", new ArgbEvaluator(), GREY, Color.WHITE);
            loginsize = loginflow.getWidth();

            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            anim2.setInterpolator(new AccelerateDecelerateInterpolator());

            AnimatorSet set = new AnimatorSet();
            set.play(animator).with(anim2).with(anim3).with(anim4).with(anim5).with(anim6).with(anim7).with(anim8);

            set.start();

            //findViewById(R.id.loginparent).setVisibility(View.GONE);
            helloInMiddle = false;
        }
    }
    //google sign in
    private void signIn() {
        progressDialog.setMessage("Signing in ...");
        progressDialog.show();
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if(requestCode!= RC_SIGN_IN)
            mCallbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);



       firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Toast.makeText(AuthActivity.this, "Successfully signed in", Toast.LENGTH_SHORT).show();
                            boolean isNew=task.getResult().getAdditionalUserInfo().isNewUser();
                            if(isNew)
                            {
                                progressDialog.dismiss();
                                Intent intent=new Intent(AuthActivity.this,RegistrationActivity.class);
                                intent.putExtra("signinmethod","google");
                                startActivity(intent);
                                finish();
                            }
                            else{
                                firebaseFirestore.collection("users")
                                        .whereEqualTo("uid", firebaseAuth.getCurrentUser().getUid()+"")
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        String UID=document.getData().get("uid").toString();

                                                        SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences("UserInfo",MODE_PRIVATE);
                                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                                        editor.putString("uid",document.getData().get("uid").toString());
                                                        editor.putString("fullname",document.getData().get("fullname").toString());
                                                        editor.putString("profilepic",document.getData().get("profilepic").toString());
                                                        editor.commit();
                                                        progressDialog.dismiss();
                                                        Intent intent=new Intent(AuthActivity.this,HomePageActivity.class);
                                                        startActivity(intent);
                                                        finish();

                                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                                    }
                                                } else {
                                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                                }
                                            }
                                        });

                            }



                        } else {
                            progressDialog.dismiss();
                            /*Toast.makeText(AuthActivity.this, "Sign in failure , please try again"+task.getResult()+task.getException(),
                                    Toast.LENGTH_SHORT).show();*/
                            Log.e("jkl",task.getResult()+"except"+task.getException());

                        }


                    }
                }).addOnCanceledListener(new OnCanceledListener() {
           @Override
           public void onCanceled() {
               progressDialog.dismiss();
           }
       });
    }
    @Override
    public void onStart() {
        super.onStart();
        currentUser = firebaseAuth.getCurrentUser();
        // updateUI(currentUser);
    }
    //sign up email
    private void registerUser() {

            email = regemail.getText().toString();
            password = regpass.getText().toString();
            reppassword = repregpass.getText().toString();

        //Toast.makeText(this, email+password, Toast.LENGTH_SHORT).show();
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Email cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Password cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.compareTo(reppassword) == 0) {
            progressDialog.setMessage("Signing up ...");
            progressDialog.show();
            firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progressDialog.dismiss();
                    if (task.isSuccessful()){

                        Toast.makeText(AuthActivity.this, "Signed in with email address ", Toast.LENGTH_SHORT).show();
                        //loginflow.setVisibility();
                        //verifyemail.setVisibility(View.VISIBLE);
                        regpass.setText("");
                        repregpass.setText("");
                        regemail.setText("");
                        hello();
                       return;


                    }
                    else
                    {
                        Toast.makeText(AuthActivity.this, "Couldnot register pls try again", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnCanceledListener(new OnCanceledListener() {
                @Override
                public void onCanceled() {
                    progressDialog.dismiss();
                }
            });


        } else
            Toast.makeText(this, "Passwords doesn't match", Toast.LENGTH_SHORT).show();

        regpass.setText("");
        repregpass.setText("");
        regemail.setText("");




    }

    public void hello() {
        this.findViewById(R.id.loginscreen).setVisibility(View.GONE);
        verifyemail2=findViewById(R.id.verifyemail);
        verifyemail2.setVisibility(View.VISIBLE);
        final int[] k = {0};
        verifyemail2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.i("nibbaaa","jkl");
              //  Toast.makeText(getApplicationContext(), "hello", Toast.LENGTH_SHORT).show();

                if (!firebaseAuth.getCurrentUser().isEmailVerified()){
                    verifyEmail(k[0]);
                 //   Log.i("nibbaaa","hellol");

                }



            }
        });
        if (!firebaseAuth.getCurrentUser().isEmailVerified()){
            verifyEmail( k[0]++);
        }
        else {
            Intent intent=new Intent(AuthActivity.this,HomePageActivity.class);
            startActivity(intent);
            finish();
        }

    }
    public void verifyEmail(int k){
        if(k!=0){
            TextView btn=this.findViewById(R.id.verifybtn);
            btn.setText("Resend Email");
            Log.i("nibbaa","k="+k);
        }


        FirebaseUser user=firebaseAuth.getCurrentUser();

        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(AuthActivity.this, "Email sent to user, please verify!", Toast.LENGTH_SHORT).show();
                           // Log.d("nibbaa1", "Email sent.");

                        }
                        else {
                          //  Log.d("nibbaa1", "Email not sent.");
                        }
                    }
                });

    }

    //sign in with email
    private void loginUser() {
        email = loginemail.getText().toString();
        password = loginpass.getText().toString();

        //Toast.makeText(this, emailid+passwordid, Toast.LENGTH_SHORT).show();
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Email cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Password cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Signing in ...");
        progressDialog.show();
        firebaseAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()){
                            Toast.makeText(AuthActivity.this, "Success logging in", Toast.LENGTH_SHORT).show();
                            firebaseFirestore.collection("users")
                                    .whereEqualTo("uid", firebaseAuth.getCurrentUser().getUid()+"")
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    String UID=document.getData().get("uid").toString();
                                                    Toast.makeText(AuthActivity.this, ""+UID, Toast.LENGTH_SHORT).show();
                                                    SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences("UserInfo",MODE_PRIVATE);
                                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                                    editor.putString("uid",document.getData().get("uid").toString());
                                                    editor.putString("fullname",document.getData().get("fullname").toString());
                                                    editor.putString("profilepic",document.getData().get("profilepic").toString());
                                                    editor.commit();
                                                    Intent intent=new Intent(AuthActivity.this,HomePageActivity.class);
                                                    //intent.putExtra("signinmethod","google");
                                                    startActivity(intent);
                                                    finish();

                                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                                }
                                            } else {
                                                Log.d(TAG, "Error getting documents: ", task.getException());
                                            }
                                        }
                                    });

                        }
                        else
                            Toast.makeText(AuthActivity.this, "Failure", Toast.LENGTH_SHORT).show();
                    }
                });
        loginemail.setText("");
        loginpass.setText("");
    }
    //facebook sign in
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());


        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Toast.makeText(AuthActivity.this, "Successfully signed in", Toast.LENGTH_SHORT).show();

                            boolean isNew=task.getResult().getAdditionalUserInfo().isNewUser();
                            if(isNew)
                            {
                                progressDialog.dismiss();
                                Intent intent=new Intent(AuthActivity.this,RegistrationActivity.class);
                                intent.putExtra("signinmethod","facebook");
                                startActivity(intent);
                                finish();
                            }
                            else{
                                firebaseFirestore.collection("users")
                                        .whereEqualTo("uid", firebaseAuth.getCurrentUser().getUid()+"")
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        String UID=document.getData().get("uid").toString();
                                                        Toast.makeText(AuthActivity.this, ""+UID, Toast.LENGTH_SHORT).show();
                                                        SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences("UserInfo",MODE_PRIVATE);
                                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                                        editor.putString("uid",document.getData().get("uid").toString());
                                                        editor.putString("fullname",document.getData().get("fullname").toString());
                                                        editor.putString("profilepic",document.getData().get("profilepic").toString());
                                                        editor.commit();
                                                        progressDialog.dismiss();
                                                        Intent intent=new Intent(AuthActivity.this,HomePageActivity.class);
                                                        startActivity(intent);
                                                        finish();

                                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                                    }
                                                } else {
                                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                                }
                                            }
                                        });

                            }

                        } else {
                            progressDialog.dismiss();

                            Toast.makeText(AuthActivity.this, "Sign in failure,please try again",
                                    Toast.LENGTH_SHORT).show();

                        }


                    }
                }).addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {
                progressDialog.dismiss();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signinbtn:

                loginUser();
                break;
            case R.id.signupbtn:
                registerUser();
                break;
            case R.id.googlebtn:
                signIn();
                break;
            case R.id.facebookbtn:
                progressDialog.setMessage("Signing in ...");
                progressDialog.show();
                LoginManager.getInstance().logInWithReadPermissions(
                        this,
                        Arrays.asList("email", "public_profile")
                );
                break;
            case R.id.forgotpass:
                forgotPassword();
                break;
            case R.id.sendemail:
                sendEmailForForgotPassword();
                break;
            /*case R.id.verifyemail2:
                Toast.makeText(this, "hello", Toast.LENGTH_SHORT).show();
                int k=0;
                while (!firebaseAuth.getCurrentUser().isEmailVerified()){
                    verifyEmail(k);
                    k++;
                }

                Intent intent=new Intent(AuthActivity.this,RegistrationActivity.class);
                intent.putExtra("signinmethod","email");
                startActivity(intent);
                finish();

                break;*/
            default:
                break;
        }
    }

    private void sendEmailForForgotPassword() {
        forgotemail=this.findViewById(R.id.forgotemail);
        email=forgotemail.getText().toString();
        progressDialog.setMessage("Sending password change email ...");
        progressDialog.show();
        firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {

                            Toast.makeText(AuthActivity.this, "Password reset email sent to your email address", Toast.LENGTH_SHORT).show();

                            // Log.d(TAG, "Email sent.");
                        }
                        else{
                            Toast.makeText(AuthActivity.this, "Cannot send email,retry!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {
                progressDialog.dismiss();
            }
        });
        forgotemail.setText("");

    }


    private void forgotPassword() {
        this.findViewById(R.id.loginscreen).setVisibility(View.GONE);
        this.findViewById(R.id.forgotpasslayout).setVisibility(View.VISIBLE);

    }
    private void signOut() {
        // Firebase sign out

        firebaseAuth.signOut();
        mGoogleSignInClient.signOut();
        loginManager.logOut();


        // Google sign out

    }

    /*public void onVerifyButtonClick(View view) {
        int k=0;
        Toast.makeText(this, "kkkk", Toast.LENGTH_SHORT).show();
        while (!firebaseAuth.getCurrentUser().isEmailVerified()){
            verifyEmail(k);
            k++;
        }

        Intent intent=new Intent(AuthActivity.this,RegistrationActivity.class);
        intent.putExtra("signinmethod","email");
        startActivity(intent);
        finish();
    }*/
}
