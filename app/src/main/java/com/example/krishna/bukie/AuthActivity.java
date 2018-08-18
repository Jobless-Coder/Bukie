package com.example.krishna.bukie;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.krishna.bukie.BuildConfig;
import com.example.krishna.bukie.HomePageActivity;
import com.example.krishna.bukie.R;
import com.example.krishna.bukie.RegistrationActivity;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.LoggingBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;

public class AuthActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "AuthActivity";
    private static final int RC_SIGN_IN = 1;

    private View mFacebookBtn, mGoogleBtn, mSignInBtn, mSignUpBtn, mSendEmail;
    private TextView mForgotPass, mVerifyBtn;
    private EditText mRegEmail, mRegPass, mRepRegPass, mSignInPass, mSignInEmail;

    private GoogleSignInClient mGoogleSignInClient;

    private int height, width;
    TextView hello, bye;
    boolean helloInMiddle;
    int big, small, hsm, hl, bsm, bl, loginsize, GREY;
    LinearLayout loginflow, signflow, mVerifyEmail,verifyemail2;
    EditText forgotemail;
    //String email, password,reppassword, signinmethod;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private CallbackManager mCallbackManager;
    FirebaseUser currentUser;
    private FirebaseFirestore firebaseFirestore;
    LoginManager loginManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
        String userId = sharedPreferences.getString("uid", null);

        if (userId != null) {
            Intent intent = new Intent(getApplicationContext(), HomePageActivity.class);
            startActivity(intent);
            finish();
        }

        firebaseAuth = FirebaseAuth.getInstance();

        // Google
        GoogleSignInOptions gso =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Facebook
        loginManager = LoginManager.getInstance();
        if (BuildConfig.DEBUG) {
            FacebookSdk.setIsDebugEnabled(true);
            FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
        }

        setContentView(R.layout.activity_auth);
        mRegEmail = findViewById(R.id.regemail);
        mRegPass = findViewById(R.id.regpass);
        mRepRegPass = findViewById(R.id.repregpass);

        mSignInEmail = findViewById(R.id.loginemail);
        mSignInPass = findViewById(R.id.loginpass);
        mForgotPass = findViewById(R.id.forgotpass);

        mFacebookBtn = findViewById(R.id.facebookbtn);
        mGoogleBtn = findViewById(R.id.googlebtn);

        mSignInBtn = findViewById(R.id.signinbtn);
        mSignUpBtn = findViewById(R.id.signupbtn);

        mSendEmail = findViewById(R.id.sendemail);
        mVerifyEmail = findViewById(R.id.verifyemail);
        mVerifyBtn = findViewById(R.id.verifybtn);

        mGoogleBtn.setOnClickListener(this);
        mFacebookBtn.setOnClickListener(this);
        mForgotPass.setOnClickListener(this);
        mSignInBtn.setOnClickListener(this);
        mSendEmail.setOnClickListener(this);
        mSignUpBtn.setOnClickListener(this);
        mVerifyEmail.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);

        firebaseFirestore = FirebaseFirestore.getInstance();

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

    //animations
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

            Log.e("dimensions","width: "+width+" login w: "+loginflow.getWidth()+" position "+loginflow.getX()+" left: "+loginflow.getLeft());
            ObjectAnimator anim5 = ObjectAnimator.ofFloat(loginflow, "X", (width-loginflow.getWidth())/2);
            ObjectAnimator anim6 = ObjectAnimator.ofFloat(signflow, "translationX", width*2);

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


            ObjectAnimator anim5 = ObjectAnimator.ofFloat(loginflow, "translationX", -loginflow.getWidth()*2);
            ObjectAnimator anim6 = ObjectAnimator.ofFloat(signflow, "X", width / 2 - signflow.getWidth()/2);

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signinbtn:
                signInWithEmailPass();
                break;
            case R.id.signupbtn:
                signUpWithEmailPass();
                break;
            case R.id.googlebtn:
                googleSignIn();
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

    /**
     * Signs in user into Firebase with email and password. Displays appropriate toast if either
     * is empty.
     **/
    private void signInWithEmailPass() {
        String email = mSignInEmail.getText().toString();
        String pass = mSignInPass.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Email cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(pass)) {
            Toast.makeText(this, "Password cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Signing in ...");
        progressDialog.show();
        Log.d(TAG, "Signing in user with email/pass = " + email + "/" + pass);

        firebaseAuth
                .signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            handleSuccesfulSignIn();
                        }
                        else {
                            Toast.makeText(AuthActivity.this, "Sign in failed", Toast.LENGTH_SHORT).show();
                        }
                        mSignInEmail.setText("");
                        mSignInPass.setText("");
                    }
                })
                .addOnCanceledListener(new OnCanceledListener() {
                    @Override
                    public void onCanceled() {
                        progressDialog.dismiss();
                    }
                });
    }


    /**
     * Handles successful Firebase sign in by fetching corresponding document from Firestore.
     */
    private void handleSuccesfulSignIn() {
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        Log.d(TAG, "Successfully signed in user with uid " + user.getUid());

        firebaseFirestore
                .collection("users")
                .whereEqualTo("uid", user.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            int userCount = querySnapshot.size();
                            switch (userCount) {
                                case 0:
                                    handleUnregisteredUser(user, "email");
                                    break;
                                case 1:
                                    DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                                    handleRegisteredUser(document);
                                    break;
                                default:
                                    throw new IllegalStateException("Cannot have more than one user with same uid");
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }


    /**
     * Handles an unregistered user. If the user does not have a verified email, displays an
     * appropriate toast, else launches {@link RegistrationActivity}.
     *
     * @param user the Firebase user object.
     * @param signInMethod a String representing the sign in method.
     */
    private void handleUnregisteredUser(FirebaseUser user, String signInMethod) {
        Log.d(TAG, "Unregistered user with uid/isEmailVerified = " + user.getUid() + "/" + user.isEmailVerified());
        if (!user.isEmailVerified()) {
            Toast.makeText(AuthActivity.this, "Please verify your email", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(AuthActivity.this, "Success signing in", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(AuthActivity.this, RegistrationActivity.class);
        intent.putExtra("signinmethod", signInMethod);
        startActivity(intent);
        finish();
    }


    /**
     * Handles a registered user. Extracts user details from provided document, saves them to
     * shared preferences and launches {@link HomePageActivity}.
     *
     * @param document a document representing user details.
     */
    private void handleRegisteredUser(DocumentSnapshot document) {
        Toast.makeText(AuthActivity.this, "Success signing in", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Fetched user document on sign in: " + document.getId() + " => " + document.getData());

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("UserInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("uid", document.getData().get("uid").toString());
        editor.putString("fullname", document.getData().get("fullname").toString());
        editor.putString("profilepic", document.getData().get("profilepic").toString());
        editor.commit();
        Intent intent = new Intent(AuthActivity.this, HomePageActivity.class);
        startActivity(intent);
        finish();
    }


    /**
     * Sign up user to Firebase with email and password. Displays appropriate toasts if either is
     * empty or if passwords do not match.
     * */
    private void signUpWithEmailPass() {
        String email = mRegEmail.getText().toString();
        String pass = mRegPass.getText().toString();
        String repPass = mRepRegPass.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Email cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(pass)) {
            Toast.makeText(this, "Password cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!TextUtils.equals(pass, repPass)) {
            Toast.makeText(this, "Passwords should match", Toast.LENGTH_SHORT).show();
            mRegPass.setText("");
            mRepRegPass.setText("");
            return;
        }

        progressDialog.setMessage("Signing up ...");
        progressDialog.show();
        Log.d(TAG, "Signing up user with email/pass = " + email + "/" + pass);

        firebaseAuth
                .createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()){
                            Toast.makeText(AuthActivity.this, "Successfully signed up", Toast.LENGTH_SHORT).show();
                            //loginflow.setVisibility();
                            //mVerifyEmail.setVisibility(View.VISIBLE);
                            handleSuccessfulSignUp();
                        } else {
                            Toast.makeText(AuthActivity.this, "Could not register, pleass try again", Toast.LENGTH_SHORT).show();
                        }
                        mRegPass.setText("");
                        mRepRegPass.setText("");
                        mRegEmail.setText("");
                    }
                })
                .addOnCanceledListener(new OnCanceledListener() {
                    @Override
                    public void onCanceled() {
                        progressDialog.dismiss();
                    }
                });
    }


    private void handleSuccessfulSignUp() {
        this.findViewById(R.id.loginscreen).setVisibility(View.GONE);
        verifyemail2=findViewById(R.id.verifyemail);
        verifyemail2.setVisibility(View.VISIBLE);
        final int[] k = {0};
        verifyemail2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.i("nibbaaa","jkl");
                //  Toast.makeText(getApplicationContext(), "hello", Toast.LENGTH_SHORT).show();
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null && user.isEmailVerified()){
                    verifyEmail(k[0]);
                    //   Log.i("nibbaaa","hellol");
                }
            }
        });
        if (!firebaseAuth.getCurrentUser().isEmailVerified()){
            verifyEmail( k[0]++);
        }
        /*else {
            Intent intent=new Intent(AuthActivity.this,HomePageActivity.class);
            startActivity(intent);
            finish();
        }*/

    }

    private void verifyEmail(int k){
        FirebaseAuth.getInstance().getCurrentUser().reload();
        //firebaseAuth=FirebaseAuth.getInstance();
        final FirebaseUser user=firebaseAuth.getCurrentUser();
        Log.i("verify",user.isEmailVerified()+"");
        if(user.isEmailVerified())
        {
            // progressDialog.dismiss();
            Intent intent=new Intent(AuthActivity.this,RegistrationActivity.class);
            intent.putExtra("signinmethod","email");
            intent.putExtra("isProfile",true);
            startActivity(intent);
            finish();
        }
        if(k!=0){
            TextView btn=this.findViewById(R.id.verifybtn);
            btn.setText("Resend Email");
            Log.i("nibbaa","k="+k);
        }

        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {


                            Toast.makeText(AuthActivity.this, "Email sent to user, please verify!", Toast.LENGTH_SHORT).show();
                            signOut();
                            // Log.d("nibbaa1", "Email sent.");

                        }
                        else {
                            //  Log.d("nibbaa1", "Email not sent.");
                        }
                    }
                });

    }

    //google sign in
    private void googleSignIn() {
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
                Log.w(TAG, "Google sign in failed, status code = " + e.getStatusCode());
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
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            Log.d(TAG, "Signed in uid " + user.getUid());
                            boolean isNew=task.getResult().getAdditionalUserInfo().isNewUser();
                            if(isNew)
                            {
                                progressDialog.dismiss();
                                Intent intent=new Intent(AuthActivity.this,RegistrationActivity.class);
                                intent.putExtra("isProfile",true);
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
                                                    Log.d(TAG, "found users " + task.getResult().size());
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

                            boolean isNew = task.getResult().getAdditionalUserInfo().isNewUser();
                            if (isNew) {
                                progressDialog.dismiss();
                                Intent intent = new Intent(AuthActivity.this, RegistrationActivity.class);
                                intent.putExtra("signinmethod", "facebook");
                                intent.putExtra("isProfile",true);
                                startActivity(intent);
                                finish();
                            } else {
                                firebaseFirestore.collection("users")
                                        .whereEqualTo("uid", firebaseAuth.getCurrentUser().getUid() + "")
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        String UID = document.getData().get("uid").toString();
                                                        Toast.makeText(AuthActivity.this, "" + UID, Toast.LENGTH_SHORT).show();
                                                        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("UserInfo", MODE_PRIVATE);
                                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                                        editor.putString("uid", document.getData().get("uid").toString());
                                                        editor.putString("fullname", document.getData().get("fullname").toString());
                                                        editor.putString("profilepic", document.getData().get("profilepic").toString());
                                                        editor.commit();
                                                        progressDialog.dismiss();
                                                        Intent intent = new Intent(AuthActivity.this, HomePageActivity.class);
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

    private void sendEmailForForgotPassword() {
        forgotemail=this.findViewById(R.id.forgotemail);
        String email=forgotemail.getText().toString();
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
        // loginManager.logOut();

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
