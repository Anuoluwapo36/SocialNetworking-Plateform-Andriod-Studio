package com.example.ziniraproject.until;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

public class Heart {
    private static final String TAG = "Heart";

    public ImageView heartWhite, heartRed;

    public Heart(ImageView heartRed, ImageView heartWhite) {
        this.heartRed = heartRed;
        this.heartWhite = heartWhite;
    }

    public void toggleLike() {
        Log.d(TAG, "toggleLike: toggling heart.");
        AnimatorSet animationSet = new AnimatorSet();

        if (heartRed.getVisibility() == View.VISIBLE) {
            // Animate heartRed to disappear
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(heartRed, "scaleX", 1f, 0f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(heartRed, "scaleY", 1f, 0f);
            animationSet.playTogether(scaleX, scaleY);
            animationSet.setInterpolator(new DecelerateInterpolator());
            animationSet.setDuration(300);

            animationSet.addListener(new Animator.AnimatorListener() {
                @Override public void onAnimationStart(Animator animator) {}
                @Override public void onAnimationEnd(Animator animator) {
                    heartRed.setVisibility(View.GONE);
                    heartWhite.setVisibility(View.VISIBLE);
                }
                @Override public void onAnimationCancel(Animator animator) {}
                @Override public void onAnimationRepeat(Animator animator) {}
            });

        } else {
            // Animate heartRed to appear
            heartRed.setScaleX(0.1f);
            heartRed.setScaleY(0.1f);
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(heartRed, "scaleX", 0.1f, 1f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(heartRed, "scaleY", 0.1f, 1f);
            animationSet.playTogether(scaleX, scaleY);
            animationSet.setInterpolator(new DecelerateInterpolator());
            animationSet.setDuration(300);

            heartRed.setVisibility(View.VISIBLE);
            heartWhite.setVisibility(View.GONE);
        }

        animationSet.start();
    }



}
