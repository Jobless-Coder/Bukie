package com.example.krishna.bukie.auth;

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
import com.example.krishna.bukie.home.HomePageActivity;
import com.example.krishna.bukie.MyFirebaseInstanceIDService;
import com.example.krishna.bukie.R;
import com.example.krishna.bukie.User;
import com.example.krishna.bukie.registration.RegistrationActivity;
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
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;

public class AuthActivity extends AppCompatActivity implements View.OnClickListener, ResendVerificationEmailDialogFragment.DialogListener {

    private static final String TAG = "AuthActivity";

    private static final int GOOGLE_SIGN_IN = 1;

    private View mFacebookBtn, mGoogleBtn, mSignInBtn, mSignUpBtn, mSendEmail;
    private TextView mForgotPass;
    private EditText mRegEmail, mRegPass, mRepRegPass, mSignInPass, mSignInEmail;

    private GoogleSignInClient mGoogleSignInClient;

    private int height, width;
    TextView hello, bye;
    boolean helloInMiddle;
    int big, small, hsm, hl, bsm, bl, loginsize, GREY, VIOLET, LIGHT_VIOLET;
    LinearLayout loginflow, signflow;
    EditText forgotemail;
    private ProgressDialog mProgressDialog;
    private FirebaseAuth mFirebaseAuth;
    private CallbackManager mCallbackManager;
    private FirebaseFirestore mFirebaseFirestore;
    private boolean mShowingForgotPass = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
        String userId = sharedPreferences.getString("uid", null);

        /*if (userId != null) {
            Intent intent = new Intent(getApplicationContext(), HomePageActivity.class);
            startActivity(intent);
            finish();
        }*/


        setContentView(R.layout.activity_auth);

        mFirebaseAuth = FirebaseAuth.getInstance();

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

        mGoogleBtn.setOnClickListener(this);
        mFacebookBtn.setOnClickListener(this);
        mForgotPass.setOnClickListener(this);
        mSignInBtn.setOnClickListener(this);
        mSendEmail.setOnClickListener(this);
        mSignUpBtn.setOnClickListener(this);

        mProgressDialog = new ProgressDialog(this);

        mFirebaseFirestore = FirebaseFirestore.getInstance();

        MyFirebaseInstanceIDService myFirebaseInstanceIDService = new MyFirebaseInstanceIDService();
        myFirebaseInstanceIDService.onTokenRefresh();
        // FirebaseInstanceId.getInstance().getInstanceId();
        // Google
        GoogleSignInOptions gso =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Facebook
        if (BuildConfig.DEBUG) {
            FacebookSdk.setIsDebugEnabled(true);
            FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
        }
        mCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance()
                .registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        handleFacebookSignIn(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        mProgressDialog.dismiss();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        mProgressDialog.dismiss();
                        Log.d(TAG, "Facebook sign in failed with exception: " + exception);
                        Toast.makeText(AuthActivity.this, R.string.sign_in_failed_retry, Toast.LENGTH_SHORT).show();
                    }
                });


        signOut();

        //animations
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;

        helloInMiddle = true;
        big = 30;
        small = 15;
        GREY = 0xffeaeaea;
        VIOLET = Color.parseColor("#673ab7");
        LIGHT_VIOLET = Color.parseColor("#B39DDB");

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

            Log.e("dimensions", "width: " + width + " login w: " + loginflow.getWidth() + " position " + loginflow.getX() + " left: " + loginflow.getLeft());
            ObjectAnimator anim5 = ObjectAnimator.ofFloat(loginflow, "X", (width - loginflow.getWidth()) / 2);
            ObjectAnimator anim6 = ObjectAnimator.ofFloat(signflow, "translationX", width * 2);

            ObjectAnimator anim7 = ObjectAnimator.ofObject(bye, "textColor", new ArgbEvaluator(), VIOLET, LIGHT_VIOLET);
            ObjectAnimator anim8 = ObjectAnimator.ofObject(hello, "textColor", new ArgbEvaluator(), LIGHT_VIOLET, VIOLET);

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
            ObjectAnimator anim6 = ObjectAnimator.ofFloat(signflow, "X", width / 2 - signflow.getWidth() / 2);

            ObjectAnimator anim7 = ObjectAnimator.ofObject(hello, "textColor", new ArgbEvaluator(), VIOLET, LIGHT_VIOLET);
            ObjectAnimator anim8 = ObjectAnimator.ofObject(bye, "textColor", new ArgbEvaluator(), LIGHT_VIOLET, VIOLET);
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
            case R.id.signupbtn:
                signUpWithEmailPass();
                break;
            case R.id.signinbtn:
                signInWithEmailPass();
                break;
            case R.id.googlebtn:
                initGoogleSignIn();
                break;
            case R.id.facebookbtn:
                initFacebookSignIn();
                break;
            case R.id.forgotpass:
                forgotPassword();
                break;
            case R.id.sendemail:
                sendEmailForForgotPassword();
                break;
        }
    }


    /**
     * Method override required for Google and Facebook sign in.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case GOOGLE_SIGN_IN:
                handleGoogleSignIn(data);
                break;
            default:
                // Documentation instructs always forwarding to mCallbackManager regardless of
                // requestCode. Not sure why, not doing that for now.
                mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }


    /**
     * Sign up user to Firebase with email and password. Displays appropriate toasts if either is
     * empty or if passwords do not match.
     */
    private void signUpWithEmailPass() {
        String email = mRegEmail.getText().toString();
        String pass = mRegPass.getText().toString();
        String repPass = mRepRegPass.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, R.string.email_empty, Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(pass)) {
            Toast.makeText(this, R.string.password_empty, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!TextUtils.equals(pass, repPass)) {
            Toast.makeText(this, R.string.passwords_dont_match, Toast.LENGTH_SHORT).show();
            mRegPass.setText("");
            mRepRegPass.setText("");
            return;
        }

        mProgressDialog.setMessage(getString(R.string.signing_up));
        mProgressDialog.show();
        Log.d(TAG, "Signing up user with email/pass: " + email + "/" + pass);

        mFirebaseAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        mProgressDialog.dismiss();
                        if (task.isSuccessful()) {
                            FirebaseUser user = mFirebaseAuth.getCurrentUser();
                            // Shift to sign in screen.
                            translate(hello);
                            mSignInEmail.setText(user.getEmail());
                            sendVerificationEmail(user);
                        } else {
                            Log.d(TAG, "Create user with email/pass failed: " + task.getException());
                            Toast.makeText(AuthActivity.this, R.string.sign_up_failed_retry, Toast.LENGTH_SHORT).show();
                        }
                        mRegEmail.setText("");
                        mRegPass.setText("");
                        mRepRegPass.setText("");
                    }
                })
                .addOnCanceledListener(new OnCanceledListener() {
                    @Override
                    public void onCanceled() {
                        mProgressDialog.dismiss();
                    }
                });
    }


    /**
     * Sends a verification email to the email registered with this user.
     *
     * @param user the Firebase user object.
     */
    private void sendVerificationEmail(final FirebaseUser user) {
        Log.d(TAG, "Sending verification email to: " + user.getEmail());
        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(AuthActivity.this, R.string.send_verification_email_success, Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d(TAG, "Send verification email failed: " + task.getException());
                            Toast.makeText(AuthActivity.this, R.string.send_verification_email_failed_retry, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    /**
     * Signs in user into Firebase with email and password. Displays appropriate toast if either
     * is empty.
     **/
    private void signInWithEmailPass() {
        String email = mSignInEmail.getText().toString();
        String pass = mSignInPass.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, R.string.email_empty, Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(pass)) {
            Toast.makeText(this, R.string.password_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        mProgressDialog.setMessage(getString(R.string.signing_in));
        mProgressDialog.show();
        Log.d(TAG, "Signing in user with email/pass: " + email + "/" + pass);

        mFirebaseAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            handleSuccesfulSignIn(mFirebaseAuth.getCurrentUser(), "email");
                        } else {
                            mProgressDialog.dismiss();
                            Log.d(TAG, "Sign in failed: " + task.getException());
                            Toast.makeText(AuthActivity.this, R.string.sign_in_failed_retry, Toast.LENGTH_SHORT).show();
                        }
                        mSignInPass.setText("");
                    }
                })
                .addOnCanceledListener(new OnCanceledListener() {
                    @Override
                    public void onCanceled() {
                        mProgressDialog.dismiss();
                    }
                });
    }


    /**
     * Launches Google sign in fragment.
     */
    private void initGoogleSignIn() {
        mProgressDialog.setMessage(getString(R.string.please_wait));
        mProgressDialog.show();
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
    }


    /**
     * Handles Google sign in by generating an {@link AuthCredential} and passing it to
     * {@link #firebaseAuthWithCredential}.
     *
     * @param data the data received from {@link #onActivityResult}.
     */
    private void handleGoogleSignIn(Intent data) {
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            Log.d(TAG, "handleGoogleSignIn:" + account.getId());
            AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
            firebaseAuthWithCredential(credential, "google");
        } catch (ApiException e) {
            mProgressDialog.dismiss();
            Log.d(TAG, "Google sign in failed, status code: " + e.getStatusCode());
            if (e.getStatusCode() != GoogleSignInStatusCodes.SIGN_IN_CANCELLED) {
                // Not cancelled by user.
                Toast.makeText(AuthActivity.this, R.string.sign_in_failed_retry, Toast.LENGTH_SHORT).show();
            }
        }
    }


    /**
     * Begins Facebook sign in.
     */
    private void initFacebookSignIn() {
        mProgressDialog.setMessage(getString(R.string.please_wait));
        mProgressDialog.show();
        LoginManager.getInstance().logInWithReadPermissions(AuthActivity.this, Arrays.asList("email", "public_profile"));
    }


    /**
     * Handles Facebook sign in by generating an {@link AuthCredential} and passing it to
     * {@link #firebaseAuthWithCredential}.
     *
     * @param token the {@link AccessToken} received from {@link FacebookCallback#onSuccess}.
     */
    private void handleFacebookSignIn(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuthWithCredential(credential, "facebook");
    }


    /**
     * Sign in to Firebase with provided credential.
     *
     * @param credential   the credential to sign in with.
     * @param signInMethod a String representing the sign in method.
     */
    private void firebaseAuthWithCredential(AuthCredential credential, final String signInMethod) {
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mFirebaseAuth.getCurrentUser();
                            Log.d(TAG, "Successfully sign in user with uid: " + user.getUid());
                            if (task.getResult().getAdditionalUserInfo().isNewUser()) {
                                // Saves one call to Firestore to check if user exists.
                                handleUnregisteredUser(user, signInMethod);
                            } else {
                                handleSuccesfulSignIn(user, signInMethod);
                            }
                        } else {
                            mProgressDialog.dismiss();
                            Toast.makeText(AuthActivity.this, R.string.sign_in_failed_retry, Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Firebase auth failed with exception: " + task.getException());
                        }
                    }
                })
                .addOnCanceledListener(new OnCanceledListener() {
                    @Override
                    public void onCanceled() {
                        mProgressDialog.dismiss();
                    }
                });
    }


    /**
     * Handles successful Firebase sign in by fetching user document from Firestore.
     *
     * @param user         the Firebase user object.
     * @param signInMethod a String representing the sign in method.
     */
    private void handleSuccesfulSignIn(final FirebaseUser user, final String signInMethod) {
        Log.d(TAG, "Successfully signed in user with uid: " + user.getUid());
        mFirebaseFirestore.collection("users")
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
                                    // User is not registered in database.
                                    reloadThenHandleUnregisteredUser(user, signInMethod);
                                    break;
                                case 1:
                                    DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                                    User user = document.toObject(User.class);
                                    handleRegisteredUser(user);
                                    break;
                                default:
                                    throw new IllegalStateException("Cannot have more than one user with same uid");
                            }
                        } else {
                            mProgressDialog.dismiss();
                            Log.d(TAG, "Error getting documents: ", task.getException());
                            Toast.makeText(AuthActivity.this, R.string.sign_in_failed_retry, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    /**
     * Reloads the Firebase user object if email is not verified. Calls
     * {@link #handleUnregisteredUser} directly if reload is not required or on reload.
     *
     * @param user         the Firebase user object.
     * @param signInMethod a String representing the sign in method.
     */
    private void reloadThenHandleUnregisteredUser(final FirebaseUser user, final String signInMethod) {
        if (!requiresEmailVerification(user)) {
            handleUnregisteredUser(user, signInMethod);
            return;
        }
        user.reload()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {
                            Log.d(TAG, "FirebaseUser reload failed: " + task.getException());
                        }
                        handleUnregisteredUser(user, signInMethod);
                    }
                });
    }


    /**
     * Checks whether the user requires email verification.
     *
     * @param user the Firebase user object
     * @return whether the user requires verification
     */
    private boolean requiresEmailVerification(FirebaseUser user) {
        for (UserInfo info : user.getProviderData()) {
            Log.d(TAG, "Provider: " + info.getProviderId());
            if (info.getProviderId().equals(EmailAuthProvider.PROVIDER_ID)) {
                return !user.isEmailVerified();
            }
        }
        return false;
    }


    /**
     * Handles an unregistered user. If the user does not have a verified email, displays a dialog
     * allowing the user to resend the verification email, otherwise launches a
     * {@link RegistrationActivity}.
     *
     * @param user         the Firebase user object.
     * @param signInMethod a String representing the sign in method.
     */
    private void handleUnregisteredUser(FirebaseUser user, String signInMethod) {
        mProgressDialog.dismiss();
        Log.d(TAG, "Unregistered user with uid: " + user.getUid());
        if (requiresEmailVerification(user)) {
            Log.d(TAG, "User has unverified email, launching dialog");
            showResendVerificationEmailDialog(user);
            return;
        }

        Toast.makeText(AuthActivity.this, R.string.sign_in_success, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(AuthActivity.this, RegistrationActivity.class);
        intent.putExtra("signinmethod", signInMethod);
        startActivity(intent);
        finish();
    }


    /**
     * Displays a dialog which allows the user to choose to receive a verification email.
     *
     * @param user the Firebase user object to be passed to the dialog fragment.
     */
    private void showResendVerificationEmailDialog(FirebaseUser user) {
        ResendVerificationEmailDialogFragment dialog = new ResendVerificationEmailDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("user", user);
        dialog.setArguments(bundle);
        dialog.show(getSupportFragmentManager(), "email_verification");
    }


    /**
     * Overrides {@link ResendVerificationEmailDialogFragment.DialogListener#onClickDialogYes}.
     *
     * @param user the Firebase user object to send the email to.
     */
    @Override
    public void onClickDialogYes(FirebaseUser user) {
        sendVerificationEmail(user);
    }


    /**
     * Handles a registered user. Saves user details to shared preferences and launches
     * {@link HomePageActivity}.
     *
     * @param user the user object
     */
    private void handleRegisteredUser(User user) {
        mProgressDialog.dismiss();
        Toast.makeText(AuthActivity.this, R.string.sign_in_success, Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Fetched user details on sign in: " + user);

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("UserInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("uid", user.getUid());
        editor.putString("fullname", user.getFullname());
        editor.putString("profilepic", user.getProfilepic());
        editor.commit();
        Intent intent = new Intent(AuthActivity.this, HomePageActivity.class);
        startActivity(intent);
        finish();
    }


    private void sendEmailForForgotPassword() {
        forgotemail = this.findViewById(R.id.forgotemail);
        final String email = forgotemail.getText().toString();
        mProgressDialog.setMessage("Sending password change email ...");
        mProgressDialog.show();
        mFirebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mProgressDialog.dismiss();
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Successfully sent password reset email to: " + email);
                            Toast.makeText(AuthActivity.this, "Password reset email sent to your email address", Toast.LENGTH_SHORT).show();
                            forgotemail.setText("");
                        } else {
                            Log.d(TAG, "Failed to send password reset email to: " + email);
                            Toast.makeText(AuthActivity.this, "Could not send email, please retry", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {
                mProgressDialog.dismiss();
            }
        });
    }


    private void forgotPassword() {
        mShowingForgotPass = true;
        this.findViewById(R.id.loginscreen).setVisibility(View.GONE);
        this.findViewById(R.id.forgotpasslayout).setVisibility(View.VISIBLE);
    }

    private void signOut() {
        mFirebaseAuth.signOut();
        mGoogleSignInClient.signOut();
        // loginManager.logOut();
    }

    @Override
    public void onBackPressed() {
        if (mShowingForgotPass) {
            mShowingForgotPass = false;
            this.findViewById(R.id.forgotpasslayout).setVisibility(View.GONE);
            this.findViewById(R.id.loginscreen).setVisibility(View.VISIBLE);
        } else {
            super.onBackPressed();
        }
    }
}
