package com.example.administrator.opencvdemo.module;

import android.content.Context;
import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * @Description : MainContract
 * @class : MainContract
 */


public interface MainContract {

    interface View{
        void updateUI(String s);
    }

    interface Presenter{
        void getAccessToken();
        void getRecognitionResultByImage(Bitmap bitmap);
    }

}
