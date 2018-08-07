package com.example.krishna.bukie;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScannerDialog {


    private ZXingScannerView scannerView;
    private FrameLayout frame;
    boolean attached = false;
    String scannedCode = "";
    Activity ac;
    Dialog dialog;
    ScannerResultListener listener;
    public ScannerDialog(){
    }


    public void showDialog(Activity activity, int displayWidth, int displayHeight, ScannerResultListener listener){
        ac=activity;
        dialog = new Dialog(activity);
        //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.scanner_dialog);

        this.listener = listener;
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        int dialogWindowWidth = (int) (displayWidth * 0.9f);
        int dialogWindowHeight = (ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.width = dialogWindowWidth;
        layoutParams.height = dialogWindowHeight;
        dialog.getWindow().setAttributes(layoutParams);

        TextView dialogButton = dialog.findViewById(R.id.cancel_button);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        initiateScanner();
        scannerView.startCamera();
    }

    public void initiateScanner()
    {
        frame = dialog.findViewById(R.id.framescan);
        scannerView = dialog.findViewById(R.id.xingscan);
        //ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //scannerView.setLayoutParams(params);
        scannerView.setAspectTolerance(1f);
        scannerView.setResultHandler(new ZXingScannerView.ResultHandler() {
            @Override
            public void handleResult(Result result) {
                setContents(result.getText(), result.getBarcodeFormat().name());
                scannerView.stopCamera();
            }
        });
        //frame.addView(scannerView);

    }


    private void setContents(String text, String codeType) {

        listener.onSuccess(text);
        scannedCode = text;
        dialog.dismiss();
    }


}