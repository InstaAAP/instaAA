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
    private int colorPixel;
    private int[][] kernel = new int[][]{{1,1,1},{1,1,1},{1,1,1}};


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

                colorPixel = getNewPixel(i, j, height, width);

                finalImage.setPixel(i,j,colorPixel);
            }

        }
        return  finalImage;
    }

    public int getNewPixel(int original_i, int original_j, int range_i, int range_j){
        int result = 0;

        int new_i = original_i - 1;
        int new_j = original_j - 1;
        int kernel_i = 0;
        int kernel_j = 0;

        for(new_i = new_i; new_i < original_i + 1; new_i++){

            for (new_j = new_j; new_j < original_j + 1; new_j++){

                if(indexValidation(new_i, range_i) && indexValidation(new_j, range_j)){
                    result += kernel[kernel_i][kernel_j] * originalBitmap.getPixel(new_i, new_j);
                }else{}

                kernel_j++;
            }
            kernel_i++;
        }

        result = Math.round(result / (kernel.length * kernel[0].length));
        return result;
    }

    /**
     * Function to validate the index we want to access
     * @param index Index to be checked
     * @param range Valid range for the index to be in
     * @return True if the index is in range, False if the index is out of range
     */
    public boolean indexValidation(int index, int range){
        if (index >= 0 && index < range){
            return true;
        }else{
            return false;
        }
    }
}
