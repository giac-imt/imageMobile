package imt.fr.frontimagemobile;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ImageView;

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

import java.util.ArrayList;

import imt.fr.frontimagemobile.adapter.ResultAdapter;
import imt.fr.frontimagemobile.models.ResultModel;

public class ResultatActivity extends AppCompatActivity {

    ImageView imageView;

    Uri imageUri;

    Bitmap bitmapServeur;

    ArrayList<ResultModel> resultats = new ArrayList<>();

    Button btn_retour;

    RecyclerView recyclerView;

    RecyclerView.LayoutManager layoutManager;

    RecyclerView.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultat);

        imageView = findViewById(R.id.resultat_activity_image_camera);
        recyclerView = findViewById(R.id.recycler_view);
        btn_retour = findViewById(R.id.btn_retour);

        btn_retour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        Bundle data = getIntent().getExtras();
        if(data != null){
            imageUri = (Uri) data.get("image");
            resultats = data.getParcelableArrayList("resultats");
            imageView.setImageURI(imageUri);
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(new ResultAdapter(resultats, getApplicationContext()));

        imageServeur("analyse_image/datasetretr/train\\ukbench00013.jpg", this);


    }

    /**
     * Fonction qui retourne la base64 d'une image sur le serveur
     * @param path : path reçu des images du résultat
     */
    public void imageServeur(String path, Context context){
        String url = "http://192.168.1.33:8000/image/" + path;
        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(this.getClass().getSimpleName() + " GET/PATH IMAGE SERVEUR BASE 64", "réponse OK");
                        try {
                            String image_base_64_serveur = response.getString("image_base46").replaceAll("\\.", "");

                            byte[] decodedString = Base64.decode(image_base_64_serveur, Base64.DEFAULT);
                            bitmapServeur = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                            imageView.setImageBitmap(bitmapServeur);

                        } catch (Exception e){
                            Log.e(this.getClass().getSimpleName() + " GET/PATH IMAGE SERVEUR BASE 64 EXCEPTION", e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(this.getClass().getSimpleName() + " GET/PATH IMAGE SERVEUR BASE 64 ERROR", "Réponse KO : " + error.getMessage() + error.getCause());
                }
        });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(3000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonObjectRequest);
    }
}
