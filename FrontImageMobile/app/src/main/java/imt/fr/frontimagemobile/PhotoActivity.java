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


    /**
     * Fonction qui retourne les resultats d'une recherche
     * @param id : id en réponse du post fait précedemment
     */
    private void resultat(int id){
        String url = "http://192.168.1.33:8000/image/" + id;
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(this.getClass().getSimpleName() + " GET", "réponse OK" + response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(this.getClass().getSimpleName() + " GET", "Réponse KO : " + error.getMessage() + error.getCause());
            }
        });
        queue.add(jsonArrayRequest);
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
                        String id = "";
                        try{
                            id = response.getString("id");
                        } catch (Exception e){
                            Log.e(this.getClass().getSimpleName() + " POST RESPONSE", "L'id n'a pas été retourné dans la réponse");
                        }
                        Log.d(this.getClass().getSimpleName() + " POST", "réponse OK/ ID : " + id);
                        resultat(Integer.parseInt(id));

                    }
                }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(this.getClass().getSimpleName() + " POST", "Réponse KO : " + error.getMessage() + error.getStackTrace().toString());
                }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
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
}
