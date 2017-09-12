package com.josephsalas.instaaa;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

/**
 * Created by Joseph Salas on 08/09/2017.
 */

public class Convolution {

    private Color pixelColor;
    private Bitmap originalBitmap;
    private Drawable originalImage;
    private int A,R,G,B;
    private int colorPixel;


    /***
     * Funcion para convolucionar una imagen con un filtro gausianno
     * @param original
     * @return
     */
    public Bitmap convolutionGaus(ImageView original)
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
