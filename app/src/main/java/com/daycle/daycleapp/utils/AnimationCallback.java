package com.daycle.daycleapp.utils;

import android.view.animation.Animation;

import java.io.Serializable;

public interface AnimationCallback extends Serializable {
    public void AnimationEnd(Animation animation);
}
