package imt.fr.frontimagemobile;

import android.app.VoiceInteractor;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

public class PhotoActivity extends AppCompatActivity {

    ImageView img;

    Button btn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        img = findViewById(R.id.image_camera);
        btn = findViewById(R.id.button);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testImageRequest();
            }
        });

        Intent intent = getIntent();
        if(intent != null) {
            Log.d(this.getClass().getSimpleName() + "INTENT", "Intent présent avec la photo");
            Bundle intentBundle = intent.getExtras();
            Uri imageBitmap = (Uri) intentBundle.get("image");
            img.setImageURI(imageBitmap);
        }
    }
//todo : réussir get simple
    //todo : envoyer photo en base64
    private void testImageRequest(){
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
                try {
                    byte[] htmlBodyBytes = error.networkResponse.data;
                    Log.e("taggggg", new String(htmlBodyBytes), error);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });
        queue.add(jsonObjectRequest);
    }
}
