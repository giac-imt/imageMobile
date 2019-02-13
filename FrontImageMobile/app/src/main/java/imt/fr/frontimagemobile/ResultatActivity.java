package imt.fr.frontimagemobile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import imt.fr.frontimagemobile.adapter.ResultAdapter;
import imt.fr.frontimagemobile.models.ResultModel;

public class ResultatActivity extends AppCompatActivity {

    ImageView imageView;

    ScrollView scrollView;

    Uri imageUri;

    ArrayList<ResultModel> resultats = new ArrayList<>();

    ArrayList<Bitmap> bitmaps = new ArrayList<>();

    Button btn_retour;

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultat);

        imageView = findViewById(R.id.resultat_activity_image_camera);
        scrollView = findViewById(R.id.scrollView_layout_resultat);
        recyclerView = findViewById(R.id.recycler_view);
        btn_retour = findViewById(R.id.btn_retour);

        btn_retour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(imageUri.getPath());
                file.delete();
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

        try {
            for(int i = 0; i < resultats.size(); i++) {
                imageServeur(resultats.get(i).getUrl());
                Thread.sleep(500);
            }
        } catch (Exception e){
            Log.e(this.getClass().getSimpleName() + "ERROR BITMAPS SERVER", e.getMessage());
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new ResultAdapter(resultats, bitmaps));
    }

    /**
     * Fonction qui retourne la base64 d'une image sur le serveur et la transforme en bitmap sur le mobile
     * @param path : path reçu des images du résultat
     */
    public void imageServeur(String path){
        String url = "http://192.168.1.33:8000/image/" + path.replace("\\", "/");
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(this.getClass().getSimpleName() + " GET/PATH IMG SERV B64", "réponse OK");
                        try {
                            String image_base_64_serveur = response.getString("image_base46").replaceAll("\\.", "");

                            byte[] decodedString = Base64.decode(image_base_64_serveur, Base64.DEFAULT);
                            Bitmap bitmapServeur = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            bitmaps.add(bitmapServeur);

                        } catch (Exception e){
                            Log.e(this.getClass().getSimpleName() + " GET/PATH IMG S-B64 EXC", e.getMessage());
                            Toast.makeText(getApplicationContext(), R.string.analyse_ko_get_image, Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(this.getClass().getSimpleName() + " GET/PATH IMG S-B64 ERR", "Réponse KO : " + error.getMessage() + error.getCause());
                    Toast.makeText(getApplicationContext(), R.string.analyse_ko_get_image, Toast.LENGTH_LONG).show();
                }
        });
        queue.add(jsonObjectRequest);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        File file = new File(imageUri.getPath());
        file.delete();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
