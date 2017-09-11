package com.josephsalas.instaaa;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.media.audiofx.BassBoost;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.PersistableBundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainMenu extends AppCompatActivity {

    //Algunas variables a utilizar

    private String APP_DIRECTORY = "myPictureApp/";
    private String MEDIA_DIRECTORY  = APP_DIRECTORY + "media";
    private String TEMPORALY_PICTURE_NAME = "instaImage.jpg";
    private final int MY_PERMISSIONS = 100;
    private final int PHOTO_CODE = 100;
    private final int SELECT_PICTURE = 200;
    private RelativeLayout rl_view;
    private ImageView ImageView;
    private Button choosePicture;
    private Button filterPicture;
    private Button savePicture;
    private String pPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Inicializamos el image view y el boton de la interfaz
        ImageView = (ImageView) findViewById(R.id.setPicture);
        choosePicture = (Button) findViewById(R.id.takeButton);
        filterPicture = (Button) findViewById(R.id.filterButton);
        savePicture = (Button) findViewById(R.id.saveButton);
        rl_view = (RelativeLayout) findViewById(R.id.rl_view);
        //Permisos de acceso escritos en el xml
        if(mayRequestStoragePermission())
        {
            choosePicture.setEnabled(true);
        }else
            {
                choosePicture.setEnabled(false);
            }

        //Metodo que abre al dar clic al boton
        choosePicture.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showOptions();

            }
        });

        filterPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFilters();
            }
        });

        savePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveImage();
            }
        });

    }

    private boolean mayRequestStoragePermission()
    {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return true;
        if((checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) &&
                (checkSelfPermission(CAMERA)== PackageManager.PERMISSION_GRANTED))
            return true;
        if((shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) || (shouldShowRequestPermissionRationale(CAMERA)))
        {
            Snackbar.make(rl_view, "Los permisos son necesarios para usar la app",
                    Snackbar.LENGTH_INDEFINITE).setAction(android.R.string.ok, new View.OnClickListener() {
                @TargetApi(Build.VERSION_CODES.M)
                @Override
                public void onClick(View view) {
                    requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,CAMERA}, MY_PERMISSIONS);
                }
            });
        }else
            {
                requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,CAMERA}, MY_PERMISSIONS);
            }
        return false;
    }

    //muestra las opciones de escoger o tomar foto en un CharSequence
    public void showOptions()
    {
        final CharSequence[] options = {"Tomar Foto", "Elegir de Galeria", "Cancelar"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainMenu.this);
        builder.setTitle("Elija una opcion");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int optionSelected)
            {
                if (options[optionSelected]== "Tomar Foto")
                {
                    //Metodo para acceder a la camara
                    openCamera();
                }else if(options[optionSelected]== "Elegir de Galeria")
                {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    //Lanzamos un pedido a la espera de una respuesta
                    startActivityForResult(intent.createChooser(intent, "Selecciona app para abrir imagen"), SELECT_PICTURE);
                }else
                    {
                        dialogInterface.dismiss();
                    }

            }
        });
        builder.show();
    }

    public void openCamera()
    {   //Ruta del almacenamiento interno
        File file = new File(Environment.getExternalStorageDirectory(),MEDIA_DIRECTORY);
        boolean isDirectoryCreated = file.exists();
        if(!isDirectoryCreated)
            isDirectoryCreated = file.mkdirs();

        if (isDirectoryCreated)
        {
            Long timeStamp = System.currentTimeMillis()/1000;
            String imageName = timeStamp.toString()+".jpg";
            pPath = Environment.getExternalStorageDirectory()+ File.separator + MEDIA_DIRECTORY + File.separator+ imageName;

            File newImageFile = new File(pPath);
            Intent intent  = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //Envio de parametros entre actividades
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(newImageFile));
            startActivityForResult(intent, PHOTO_CODE);
        }

    }

    //La variable no entra nula en el siguiente activity result
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("file_path", pPath);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        pPath = savedInstanceState.getString("file_path");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK)
        {
            switch (requestCode)
            {
                case PHOTO_CODE:
                    MediaScannerConnection.scanFile(this, new String[]{pPath},
                            null, new MediaScannerConnection.OnScanCompletedListener() {
                                @Override
                                public void onScanCompleted(String s, Uri uri) {
                                    Log.i("ExternalStorage", "Scanned"+ s + ":");
                                    Log.i("ExternalStorage", "-> Uri= "+ uri);
                                }
                            });
                    Bitmap bitmap = BitmapFactory.decodeFile(pPath);
                    ImageView.setImageBitmap(bitmap);
                    break;
                case SELECT_PICTURE:
                    Uri path = data.getData();
                    ImageView.setImageURI(path);
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == MY_PERMISSIONS)
        {
            if(grantResults.length ==2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1]== PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "Permisos Aceptados", Toast.LENGTH_SHORT).show();
                choosePicture.setEnabled(true);

            }
        }else
            {
                showExplanation();
            }
    }

    private void showExplanation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainMenu.this);
        builder.setTitle("Permisos denegados");
        builder.setMessage("Porfavor acepte los permismos");
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);

            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                finish();
            }
        });
        builder.show();
    }

    public void showFilters()
    {
        final GrayScale grayScale = new GrayScale();
        final Convolution convolution = new Convolution();
        final CharSequence[] options = {"Averaging", "Desaturation", "Maxposition", "Minposition", "Gaussian"
                , "NameFilter", "Cancelar"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainMenu.this);
        builder.setTitle("Elija una opcion");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int optionSelected)
            {
                if (options[optionSelected]== "Averaging")
                {
                    grayScale.averagingFiler(ImageView);
                }else if(options[optionSelected]== "Desaturation")
                {
                    grayScale.desaturation(ImageView);

                }else if(options[optionSelected] == "Maxposition")
                {
                    grayScale.maxposition(ImageView);

                }else if (options[optionSelected] == "Minposition")
                {
                    grayScale.minposition(ImageView);

                }else if(options[optionSelected] == "Gaussian")
                {

                }else if (options[optionSelected] == "NameFilter")
                {

                }
                else
                {
                    dialogInterface.dismiss();
                }

            }
        });
        builder.show();
    }

    public void saveImage()
    {
        FileOutputStream fileOutputStream = null;
        File pFile = getDisc();
        if(!pFile.exists() && !pFile.mkdirs())
        {
            Toast.makeText(this, "No se puede salvar la imagen", Toast.LENGTH_SHORT).show();
            return;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyymmsshhmmss");
        String date = simpleDateFormat.format(new Date());
        String name = "Img" + date + ".png";
        String fileName = pFile.getAbsolutePath()+"/"+name;
        File newFile = new File(fileName);
         Bitmap originalBitmap;
         Drawable originalImage;
        try {
            fileOutputStream = new FileOutputStream(newFile);
            originalImage = ImageView.getDrawable();
            originalBitmap = ((BitmapDrawable)originalImage).getBitmap();
            Bitmap bitmap = Bitmap.createBitmap(originalBitmap.getWidth(),originalBitmap.getHeight()
                    ,originalBitmap.getConfig());
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            Toast.makeText(this , "Saved Image", Toast.LENGTH_SHORT).show();
            fileOutputStream.flush();
            fileOutputStream.close();

            Intent pIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            pIntent.setData(Uri.fromFile(newFile));




        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public File getDisc()
    {
        File pFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        return new File(pFile, "Image Demo");
    }
}
