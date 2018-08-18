package com.example.krishna.bukie.auth;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import com.example.krishna.bukie.R;
import com.google.firebase.auth.FirebaseUser;

public class ResendVerificationEmailDialogFragment extends DialogFragment {

    public interface DialogListener {
        void onClickDialogYes(FirebaseUser user);
    }

    private DialogListener mListener;
    private FirebaseUser mUser;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mListener = (DialogListener) activity;
        Bundle bundle = getArguments();
        mUser = bundle.getParcelable("user");
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.resend_verification_email)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onClickDialogYes(mUser);
                    }
                })
               .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // do nothing
                    }
                });
        return builder.create();
    }
}
