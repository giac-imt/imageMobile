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
        Map<String,String> params = new HashMap<>();
        params.put("url", editTextUrl.getText().toString());

        JSONObject jsonObject = new JSONObject(params);

        String url = "http://192.168.1.33:8000/zip/";
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(this.getClass().getSimpleName() + " POST", "zip OK");
                        Toast.makeText(getApplicationContext(), R.string.zip_activity_dataset_replaced, Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(this.getClass().getSimpleName() + " POST", "zip KO : " + error.getMessage() + error.getCause());
                    Toast.makeText(getApplicationContext(), R.string.zip_activity_dataset_error, Toast.LENGTH_SHORT).show();
                }
            });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonObjectRequest);
    }

    // validating email id
    private boolean isValidUrl() {
        return Patterns.WEB_URL.matcher(editTextUrl.getText()).matches();
    }
}
