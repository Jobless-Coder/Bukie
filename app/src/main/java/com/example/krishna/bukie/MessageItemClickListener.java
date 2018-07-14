package com.example.krishna.bukie;

import android.view.View;

public interface MessageItemClickListener {
    public void onSaveContact(View view, int position);
    public void onLocation(View view, int position);
    public void onCameraImage(View view, int position);

}
