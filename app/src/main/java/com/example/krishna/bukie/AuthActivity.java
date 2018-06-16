package com.example.krishna.bukie;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AuthActivity extends AppCompatActivity {
private int height,width;
TextView hello, bye;
boolean helloInMiddle;
int big,small,hsm,hl,bsm,bl,loginsize, GREY;
LinearLayout loginflow, signflow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

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

        hello.setTranslationX(width/2 - 106);
        bye.setTranslationX(width*3/4);

        signflow.setTranslationX(width*2);
    }

    public void translate(View view) {

        if(view.getId()==R.id.hello)
        {
            if(helloInMiddle) return;

            hsm = hello.getWidth();
            bl = bye.getWidth();
            //bring hello to middle
            //bring bye to 3/4th of screen

            ObjectAnimator animator = ObjectAnimator.ofFloat(hello, "translationX", width/2 - hl/2);
            ObjectAnimator anim2 = ObjectAnimator.ofFloat(bye, "translationX", width*3/4 - bsm/2);

            ObjectAnimator anim3 = ObjectAnimator.ofFloat(hello, "textSize", small,big);
            ObjectAnimator anim4 = ObjectAnimator.ofFloat(bye, "textSize", big,small);

            ObjectAnimator anim5 = ObjectAnimator.ofFloat(loginflow, "translationX", width/2-loginflow.getWidth());
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
        }
        else {
            if(!helloInMiddle) return;

            hl = hello.getWidth();
            bsm = bye.getWidth();
            //bring bye to middle
            //bring hello to 1/4th of screen
            ObjectAnimator animator = ObjectAnimator.ofFloat(hello, "translationX", width/4 - hello.getWidth()/2);
            ObjectAnimator anim2 = ObjectAnimator.ofFloat(bye, "translationX", width/2 - bsm);


            ObjectAnimator anim3 = ObjectAnimator.ofFloat(hello, "textSize", big, small);
            ObjectAnimator anim4 = ObjectAnimator.ofFloat(bye, "textSize", small, big);


            ObjectAnimator anim5 = ObjectAnimator.ofFloat(loginflow, "translationX", -loginflow.getWidth()*2);
            ObjectAnimator anim6 = ObjectAnimator.ofFloat(signflow, "translationX", width/2 - signflow.getWidth());

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
}
