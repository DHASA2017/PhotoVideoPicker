package com.dl.mediapicker.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;


import com.dl.mediapicker.model.Photo;
import com.dl.mediapicker.util.GalleryFinal;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        /**
         * 使用此方法调用
         * 1、this----context
         * 2、GalleryFinal.TYPE_ALL 图片和视频；TYPE_IMAGE图片；TYPE_VIDEO视频
         * 3、规定选择数量
         * 4、
         */
        GalleryFinal.selectMedias(this, GalleryFinal.TYPE_ALL,10, new GalleryFinal.OnSelectMediaListener() {
            @Override
            public void onSelected(ArrayList<Photo> photoArrayList) {

            }
        });

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void sendMedia(ArrayList<Photo> photoList) {
        Log.e("多媒体", photoList.toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        System.gc();
    }


}
