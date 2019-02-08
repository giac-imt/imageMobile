package imt.fr.frontimagemobile;

import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import java.util.ArrayList;

public class ResultatActivity extends AppCompatActivity {

    ImageView imageView;

    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultat);

        imageView = findViewById(R.id.resultat_activity_image_camera);

        Bundle data = getIntent().getExtras();
        if(data != null){
            imageUri = (Uri) data.get("image");
            ArrayList result = data.getParcelableArrayList("resultats");
            imageView.setImageURI(imageUri);
        }


    }
}
