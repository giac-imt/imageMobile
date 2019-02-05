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

public class PhotoActivity extends AppCompatActivity {

    ImageView img;

    Button btn;

    Uri imageUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        img = findViewById(R.id.image_camera);
        btn = findViewById(R.id.button);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImageInfo();
            }
        });

        //Extraire de l'intent l'image et l'afficher
        Intent intent = getIntent();
        if(intent != null) {
            Log.d(this.getClass().getSimpleName() + "INTENT", "Intent présent avec la photo");
            Bundle intentBundle = intent.getExtras();
            imageUri = (Uri) intentBundle.get("image");
            img.setImageURI(imageUri);
        }
    }
//todo : réussir get simple
    //todo : envoyer photo en base64

    /**
     * GET les infos de l'image (score) après analyse
     */
    private void getImageInfo(){
        String url = "http://192.168.1.33:8000/image/1/";
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
        File file = new File(imageUri.getPath());
        FileInputStream fis = null;
        try{
            fis = new FileInputStream(file);
        } catch (Exception ex){
            Log.e(this.getClass().getSimpleName(), ex.getMessage());
        }
        Bitmap bm = BitmapFactory.decodeStream(fis);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] byteArray = baos.toByteArray();
        String encodeImage = Base64.encodeToString(byteArray, Base64.DEFAULT);

        /*String url = "http://192.168.1.33:8000/image/";
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
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
        queue.add(jsonObjectRequest);*/
    }
}
