package com.dev_sheep.story_of_man_and_woman.utils

import android.view.View
import android.view.animation.AlphaAnimation

class AnimationUtil {

    public fun fade_out(view: View){
       var a: AlphaAnimation =  AlphaAnimation(0.1f,1.0f);
        a.setFillAfter(true); // 에니메이션이 끝난 뒤 상태를 유지하는 설정, 설정하지 않으면 duration 이후 원래 상태로 되돌아감
        a.setDuration(650L);
        view.startAnimation(a);
    }

    public fun fade_int(view: View){
        var a: AlphaAnimation = AlphaAnimation(1.0f,0.0f)
        a.setFillAfter(true)
        a.setDuration(1000)
        view.startAnimation(a)
    }
}