package imt.fr.frontimagemobile;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ZipActivity extends AppCompatActivity {

    Button btn_retour;

    Button btn_envoyer;

    EditText editTextUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zip);

        btn_retour = findViewById(R.id.zip_button_retour);
        btn_envoyer = findViewById(R.id.zip_button_envoyer);
        editTextUrl = findViewById(R.id.zip_edittext_url);

        btn_retour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        btn_envoyer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isValidUrl()) {
                    Toast.makeText(getApplicationContext(), R.string.zip_activity_send_zip, Toast.LENGTH_SHORT).show();
                    remplacerDataset();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.zip_activity_url_error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void remplacerDataset(){
        String editText = editTextUrl.getText().toString();
        int positionId = editText.indexOf("id=");
        // le + 3 enlève le id= qui ne doit pas être dans la requête
        String id = editText.substring(positionId+3);
        String url = "http://192.168.1.33:8000/zip/" + id;
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(this.getClass().getSimpleName() + " GET", "zip OK");
                        Toast.makeText(getApplicationContext(), R.string.zip_activity_dataset_replaced, Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(this.getClass().getSimpleName() + " GET", "zip KO : " + error.getMessage() + error.getCause());
                    Toast.makeText(getApplicationContext(), R.string.zip_activity_dataset_error, Toast.LENGTH_SHORT).show();
                }
            });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);
    }

    private boolean isValidUrl() {
        return Patterns.WEB_URL.matcher(editTextUrl.getText()).matches();
    }
}
