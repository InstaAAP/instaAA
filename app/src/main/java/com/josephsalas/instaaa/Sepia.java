package com.josephsalas.instaaa;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

/**
 * Created by Alberto Obando on 9/17/2017.
 */

public class Sepia {

    private Color pixelColor;
    private Bitmap originalBitmap;
    private Drawable originalImage;
    private int A,R,G,B;
    private int colorPixel;

    public Bitmap sepiaFiler(ImageView original)
    {
        originalImage = original.getDrawable();
        originalBitmap = ((BitmapDrawable)originalImage).getBitmap();
        Bitmap finalImage = Bitmap.createBitmap(originalBitmap.getWidth(),originalBitmap.getHeight()
                ,originalBitmap.getConfig());
        int width = originalBitmap.getWidth();
        int height = originalBitmap.getHeight();

        for(int i = 0; i < height; i++)
        {
            for (int j = 0; j < width; j++)
            {
                colorPixel = originalBitmap.getPixel(i,j);
                A = Color.alpha(colorPixel);
                R = Color.red(colorPixel);
                G = Color.green(colorPixel);
                B = Color.blue(colorPixel);

                R = (R + G + B) / 3;
                G = R;
                B = R;

                finalImage.setPixel(i,j,Color.argb(A,R,G,B));
            }

        }
        return  finalImage;
    }

}
