package imt.fr.frontimagemobile;

import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Adapter;
import android.widget.ImageView;

import java.util.ArrayList;

import imt.fr.frontimagemobile.adapter.ResultAdapter;
import imt.fr.frontimagemobile.models.ResultModel;

public class ResultatActivity extends AppCompatActivity {

    ImageView imageView;

    Uri imageUri;

    ArrayList<ResultModel> resultats = new ArrayList<>();

    RecyclerView recyclerView;

    RecyclerView.LayoutManager layoutManager;

    RecyclerView.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultat);

        imageView = findViewById(R.id.resultat_activity_image_camera);
        recyclerView = findViewById(R.id.recycler_view);

        Bundle data = getIntent().getExtras();
        if(data != null){
            imageUri = (Uri) data.get("image");
            resultats = data.getParcelableArrayList("resultats");
            imageView.setImageURI(imageUri);
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(new ResultAdapter(resultats));


    }
}
