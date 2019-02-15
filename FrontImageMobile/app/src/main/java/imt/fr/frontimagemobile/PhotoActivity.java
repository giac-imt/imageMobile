package imt.fr.frontimagemobile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import imt.fr.frontimagemobile.models.ResultModel;

public class PhotoActivity extends AppCompatActivity {

    ImageView imageView;

    Button btn_analyser;

    ProgressBar progressBar;

    Uri imageUri;

    String location;

    ArrayList<ResultModel> resultats = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        imageView = findViewById(R.id.image_camera);
        btn_analyser = findViewById(R.id.analyser);
        progressBar = findViewById(R.id.photo_progressbar);

        btn_analyser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), R.string.analyse_en_cours, Toast.LENGTH_LONG).show();
                btn_analyser.setClickable(false);
                btn_analyser.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                postNewImage();
            }
        });

        //Extraire de l'intent l'image et l'afficher
        Intent intent = getIntent();
        if(intent != null) {
            Log.d(this.getClass().getSimpleName() + "INTENT", "Intent présent");
            Bundle intentBundle = intent.getExtras();
            imageUri = (Uri) intentBundle.get("image");
            location = (String) intentBundle.get("location");
            imageView.setImageURI(imageUri);
        }
    }

    /**
     * Fonction qui retourne les resultats d'une recherche dans une nouvelle activité
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
                        Log.d(this.getClass().getSimpleName() + " GET/ID Resultat", "réponse OK" + response);
                        try{
                            Gson gson = new Gson();
                            for(int i = 0; i < 5; i++){
                                String url = response.getJSONObject(i).get("url").toString();
                                float score = Float.parseFloat(response.getJSONObject(i).get("score").toString());
                                resultats.add(new ResultModel(url, score));
                            }

                            //Changement d'activité pour afficher les résultats
                            Intent intent = new Intent(getApplicationContext(), ResultatActivity.class);
                            intent.putExtra("image", imageUri);
                            intent.putParcelableArrayListExtra("resultats", resultats);
                            progressBar.setVisibility(View.GONE);
                            startActivity(intent);

                        } catch (Exception e){
                            Log.e(this.getClass().getSimpleName() + " GET/ID Resultat ERROR", e.getMessage());
                            Toast.makeText(getApplicationContext(), R.string.analyse_ko_get, Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(this.getClass().getSimpleName() + " GET/ID Resultat", "Réponse KO : " + error.getMessage() + error.getCause());
                Toast.makeText(getApplicationContext(), R.string.analyse_ko_get, Toast.LENGTH_LONG).show();
            }
        });
        queue.add(jsonArrayRequest);
    }

    /**
     * POST pour envoyer l'image vers l'analyse avec conversion en base64
     */
    private void postNewImage(){
        String encodeImage = null;

        if(location.equals("camera")) {
            encodeImage = encodeImageToBase64Camera();
        } else if (location.equals("library")){
            encodeImage = encodeImageToBase64Library();
        }

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
                            Toast.makeText(getApplicationContext(), R.string.analyse_ko_post, Toast.LENGTH_LONG).show();
                        }
                        Log.d(this.getClass().getSimpleName() + " POST RESPONSE", "réponse OK/ ID : " + id);
                        resultat(Integer.parseInt(id));

                    }
                }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(this.getClass().getSimpleName() + " POST RESPONSE ERROR", "Réponse KO : " + error.getMessage() + error.getStackTrace().toString());
                    Toast.makeText(getApplicationContext(), R.string.analyse_ko_post, Toast.LENGTH_LONG).show();
                }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonObjectRequest);
    }

    /**
     * Méthode pour encoder la photo en base64 de la caméra
     * @return string d'une image en base 64
     */
    private String encodeImageToBase64Camera(){
        byte[] byteArray = null;

        try{
            File file = new File(imageUri.getPath());
            FileInputStream fis = new FileInputStream(file);
            Bitmap bm = BitmapFactory.decodeStream(fis);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG,100,baos);
            byteArray = baos.toByteArray();
            baos.close();
        } catch (Exception ex){
            Log.e(this.getClass().getSimpleName(), ex.getMessage() +ex.getCause());
            Toast.makeText(getApplicationContext(), R.string.analyse_ko_encodage, Toast.LENGTH_LONG).show();
        }

        String encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);

        return encodedImage;
    }

    /**
     * Méthode pour encoder la photo en base64 de la librairie
     * @return string d'une image en base 64
     */
    private String encodeImageToBase64Library(){
        byte[] byteArray = null;
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
            byteArray = baos.toByteArray();
            baos.close();
        } catch (Exception e){
            Log.e(this.getClass().getSimpleName(), e.getMessage());
        }
        String encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);

        return encodedImage;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        File file = new File(imageUri.getPath());
        file.delete();
    }
}
