package imt.fr.frontimagemobile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

public class PhotoActivity extends AppCompatActivity {

    ImageView img;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        img = findViewById(R.id.image_camera);

        Intent intent = getIntent();
        if(intent != null) {
            Log.d(this.getClass().getSimpleName() + "INTENT", "Intent présent avec la photo");
            Bundle intentBundle = intent.getExtras();
            Bitmap imageBitmap = (Bitmap) intentBundle.get("image");
            img.setImageBitmap(imageBitmap);
        }
    }
}
