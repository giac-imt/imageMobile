package imt.fr.frontimagemobile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    final int REQUEST_IMAGE_CAPTURE = 61460;

    Button btn_appareil_photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_appareil_photo = findViewById(R.id.photo);

        btn_appareil_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
                }

                Log.d(this.getClass().getSimpleName() + "CAMERA", "Lancement de l'appareil photo effectué");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //todo : envoyer directement le bundle ?
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            Log.d(this.getClass().getSimpleName() + "CAMERA", "Prise de photo et retour avec le bundle sur l'activité 1");

            Intent intent = new Intent(this, PhotoActivity.class);
            intent.putExtra("image", imageBitmap);
            startActivity(intent);
        }
    }
}
