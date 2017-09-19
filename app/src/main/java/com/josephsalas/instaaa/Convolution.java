package com.josephsalas.instaaa;

import android.graphics.Color;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

/**
 * Created by Joseph Salas on 08/09/2017.
 */

public class Convolution {

    private Bitmap originalBitmap;
    private Drawable originalImage;
    private int colorPixel;
    private double[][] kernelJA = new double[][]{{0.111,0.111,0.111},{0.111,0.111,0.111},{0.111,0.111,0.111}};

//Filtro Gaussiano***********************************************************************************************

    public  Bitmap convolutionGauss(ImageView original)
    {
         int[] colorPixel;
        originalImage = original.getDrawable();
        originalBitmap = ((BitmapDrawable)originalImage).getBitmap();
        Bitmap finalImage = Bitmap.createBitmap(originalBitmap.getWidth(),originalBitmap.getHeight()
                ,originalBitmap.getConfig());
        int width = originalBitmap.getWidth();
        int height = originalBitmap.getHeight();

        for(int i = 0; i < width; i++)
        {
            for (int j = 0; j < height; j++)
            {
                colorPixel = getNewPixel(i, j, width, height);

                finalImage.setPixel(i,j,Color.rgb(colorPixel[0], colorPixel[1], colorPixel[2]));
            }

        }
        return  finalImage;
    }



    public int[] getNewPixel(int original_i, int original_j, int range_i, int range_j){
        int[] kernel = new int[]{1,2,1,2,4,2,1,2,1};
        int result[];
        int red = 0;
        int green = 0;
        int blue = 0;
        int new_i = original_i - 1;
        int new_j = original_j - 1;
        int kernel_count = 0;

        for(new_i = new_i; new_i < original_i+1; new_i++){

            for (new_j = new_j; new_j < original_j+1; new_j++){

                if(indexValidation(new_i, range_i) && indexValidation(new_j, range_j)){

                    int tempColor = originalBitmap.getPixel(new_i, new_j);
                    red += Color.red(tempColor) * kernel[kernel_count];
                    green += Color.green(tempColor) * kernel[kernel_count];
                    blue += Color.blue(tempColor) * kernel[kernel_count];

                }else{
                    red += 0;
                    green += 0;
                    blue += 0;
                }
                kernel_count++;
            }
        }

        red = red/16;
        blue = blue/16;
        green = green/16;
        result = new int[]{red,green,blue};
        return result;
    }




//Filtro Propio*********************************************************************************************

    public  Bitmap kernelJA(ImageView original)
    {
        originalImage = original.getDrawable();
        originalBitmap = ((BitmapDrawable)originalImage).getBitmap();
        Bitmap finalImage = Bitmap.createBitmap(originalBitmap.getWidth(),originalBitmap.getHeight()
                ,originalBitmap.getConfig());
        int width = originalBitmap.getWidth();
        int height = originalBitmap.getHeight();

        for(int i = 0; i < width; i++)
        {
            for (int j = 0; j < height; j++)
            {
                colorPixel = originalBitmap.getPixel(i,j);

                colorPixel = getNewPixelJA(i, j, width, height);

                finalImage.setPixel(i,j,colorPixel);
            }

        }
        return  finalImage;
    }



    public int getNewPixelJA(int original_i, int original_j, int range_i, int range_j){
        int result = 0;
        int new_i = original_i - 1;
        int new_j = original_j - 1;
        int kernel_i = 0;
        int kernel_j = 0;

        for(new_i = new_i; new_i < original_i+1; new_i++){

            for (new_j = new_j; new_j < original_j+1; new_j++){

                if(indexValidation(new_i, range_i) && indexValidation(new_j, range_j)){
                    result += kernelJA[kernel_i][kernel_j] * originalBitmap.getPixel(new_i, new_j);
                }else{}

                kernel_j++;
            }
            kernel_i++;
        }

        result = Math.round(result / (kernelJA.length * kernelJA[0].length));
        return result;
    }

//Filtro Propio*********************************************************************************************



//Funcion de validacion**************************************************************************************

    public boolean indexValidation(int index, int range){
        if (index >= 0 && index < range){
            return true;
        }else{
            return false;
        }
    }
}
