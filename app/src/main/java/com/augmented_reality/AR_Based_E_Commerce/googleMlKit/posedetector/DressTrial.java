package com.augmented_reality.AR_Based_E_Commerce.googleMlKit.posedetector;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;

import com.augmented_reality.AR_Based_E_Commerce.R;


public class DressTrial extends View {

    Bitmap background;
    Bitmap background2;
    Bitmap background3;
    Bitmap trialRoomBG;

    Bitmap front;
    Bitmap back;

    public DressTrial(Context context) {
        super(context);
        background = BitmapFactory.decodeResource(getResources(), R.drawable.t);
        background2 = BitmapFactory.decodeResource(getResources(), R.drawable.t1);
        background3 = BitmapFactory.decodeResource(getResources(), R.drawable.body);
        trialRoomBG = BitmapFactory.decodeResource(getResources(), R.drawable.imagebackground);

        front = BitmapFactory.decodeResource(getResources(), R.drawable.front);
        back = BitmapFactory.decodeResource(getResources(), R.drawable.back);
    }

    public Bitmap getImage(int id){
        if(id==1)
            return background;
        else if(id==2)
            return background2;
        else if(id==3)
            return background3;
        else if(id==4)
            return trialRoomBG;
        else if(id==5)
            return front;
        else if(id==6)
            return back;
        else
            return  null;
    }

}
