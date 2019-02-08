package imt.fr.frontimagemobile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    final int REQUEST_IMAGE_CAPTURE = 61460;
    final int PICK_IMAGE_REQUEST    = 12345;

    Button btn_appareil_photo;
    Button btn_photo_librairie;
    Button btn_importer_zip;
    Button btn_indexer;

    String mCurrentPhotoPath;

    File photoFile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_appareil_photo = findViewById(R.id.prendre_photo);
        btn_photo_librairie = findViewById(R.id.importer_photo);
        btn_importer_zip = findViewById(R.id.importer_zip);
        btn_indexer = findViewById(R.id.indexer);

        btn_appareil_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        btn_photo_librairie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });

        btn_importer_zip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btn_indexer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), R.string.indexationLancement, Toast.LENGTH_SHORT).show();
                indexer();
            }
        });
    }

    private void resultat(int id){
        String url = "http://192.168.1.33:8000/image/" + id;
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest jsonArrayRequest = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(this.getClass().getSimpleName() + " GET", "réponse OK");
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(this.getClass().getSimpleName() + " GET", "Réponse KO : " + error.getMessage() + error.getCause());
            }
        });
        queue.add(jsonArrayRequest);
    }

    private void indexer(){
        String url = "http://192.168.1.33:8000/index/";
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(this.getClass().getSimpleName() + " GET", "indexation OK");
                        Toast.makeText(getApplicationContext(), R.string.indexationOk, Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(this.getClass().getSimpleName() + " GET", "indexation KO : " + error.getMessage() + error.getCause());
                    Toast.makeText(getApplicationContext(), R.string.indexationKo, Toast.LENGTH_SHORT).show();

                }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(40000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);
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

            Log.d(this.getClass().getSimpleName() + "CAMERA", "Prise de photo et retour avec le bundle sur l'activité main");

            Intent intent = new Intent(this, PhotoActivity.class);
            intent.putExtra("image", uri);
            startActivity(intent);
        }
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK){
            Log.d(this.getClass().getSimpleName() + "CAMERA", "Import d'une photo et retour avec le bundle sur l'activité main");

            Intent intent = new Intent(this, PhotoActivity.class);
            intent.putExtra("image", data.getData());
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        Log.d("dest", "DESTROY");
        deleteRecursive(photoFile);
        super.onDestroy();
    }

    void deleteRecursive(File file) {
        if(file != null) {
            if (file.isDirectory())
                for (File child : file.listFiles())
                    deleteRecursive(child);

            file.delete();
        }
    }
}
