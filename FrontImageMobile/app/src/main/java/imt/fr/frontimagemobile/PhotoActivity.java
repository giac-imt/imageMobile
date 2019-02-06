package imt.fr.frontimagemobile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

public class PhotoActivity extends AppCompatActivity {

    ImageView imageView;

    Button btn_analyser;

    Uri imageUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        imageView = findViewById(R.id.image_camera);
        btn_analyser = findViewById(R.id.analyser);

        btn_analyser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postNewImage();
            }
        });

        //Extraire de l'intent l'image et l'afficher
        Intent intent = getIntent();
        if(intent != null) {
            Log.d(this.getClass().getSimpleName() + "INTENT", "Intent présent");
            Bundle intentBundle = intent.getExtras();
            imageUri = (Uri) intentBundle.get("image");
            imageView.setImageURI(imageUri);
        }
    }

    /**
     * GET les infos de l'image (score) après analyse
     */
    private void getImageInfo(){
        String url = "http://172.20.10.2:8000/image/1/";
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("tagggg", "réponse OK");
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("tagggg", "Réponse KO : " + error.getMessage() + error.getStackTrace().toString());
            }
        });
        queue.add(jsonObjectRequest);
    }

    /**
     * POST pour envoyer l'image vers l'analyse avec conversion en base64
     */
    private void postNewImage(){
        String encodeImage = encodeImageToBase64();

        Map<String,String> params = new HashMap<>();
        params.put("image_base64", encodeImage);

        JSONObject jsonObject = new JSONObject(params);

        String url = "http://192.168.1.33:8000/image/";
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(this.getClass().getSimpleName() + " POST", "réponse OK");
                    }
                }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(this.getClass().getSimpleName() + " POST", "Réponse KO : " + error.getMessage() + error.getStackTrace().toString());
                }
        });
        queue.add(jsonObjectRequest);
    }

    /**
     * Méthode pour encoder la photo en base64
     * @return string d'une image en base 64
     */
    private String encodeImageToBase64(){
        File file = new File(imageUri.getPath());
        FileInputStream fis = null;
        try{
            fis = new FileInputStream(file);
        } catch (Exception ex){
            Log.e(this.getClass().getSimpleName(), ex.getMessage() +ex.getCause());
        }
        Bitmap bm = BitmapFactory.decodeStream(fis);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] byteArray = baos.toByteArray();
        String encodeImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
        return encodeImage;
    }

    /**
     * Dès que l'activité se stoppe, effacer les fichiers temporaires
     */
    @Override
    protected void onDestroy() {
        //On efface le fichier temporaire une fois envoyé ou annulé
        super.onDestroy();
        Log.d("destroy", "destroy");
        if(imageUri != null){
            File file = new File(imageUri.getPath());
            deleteTempFiles(file);
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
