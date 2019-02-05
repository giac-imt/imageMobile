package imt.fr.frontimagemobile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    final int REQUEST_IMAGE_CAPTURE = 61460;

    Button btn_appareil_photo;

    String mCurrentPhotoPath;

    File photoFile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_appareil_photo = findViewById(R.id.photo);

        btn_appareil_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
                Log.d(this.getClass().getSimpleName() + "CAMERA", "Lancement de l'appareil photo effectué");
            }
        });
    }

    /**
     * Lancement de l'appareil photo avec photo en qualité high
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e(this.getClass().getSimpleName(), ex.getMessage());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "imt.fr.frontimagemobile",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    /**
     * Créer un fichier image
     * @return un fichier image
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            File file = new File(mCurrentPhotoPath);
            Uri uri = Uri.fromFile(file);

            Log.d(this.getClass().getSimpleName() + "CAMERA", "Prise de photo et retour avec le bundle sur l'activité 1");

            Intent intent = new Intent(this, PhotoActivity.class);
            intent.putExtra("image", uri);
            startActivity(intent);
        }
    }

    /**
     * Dès que l'activité se stoppe, effacer les fichiers temporaires
     */
    @Override
    protected void onStop() {
        //On efface le fichier temporaire une fois envoyé ou annulé
        super.onStop();
        if(photoFile != null){
            deleteTempFiles(photoFile);
        }
    }

    /**
     * Effacer les fichiers temporaires
     * @param file : fichier temporaire à supprimer
     * @return True si fichier supprimé
     */
    private boolean deleteTempFiles(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.isDirectory()) {
                        deleteTempFiles(f);
                    } else {
                        f.delete();
                    }
                }
            }
        }
        return file.delete();
    }
}
