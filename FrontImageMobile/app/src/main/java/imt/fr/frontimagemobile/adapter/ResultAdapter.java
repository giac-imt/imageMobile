package imt.fr.frontimagemobile.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import imt.fr.frontimagemobile.R;
import imt.fr.frontimagemobile.ResultatActivity;
import imt.fr.frontimagemobile.models.ResultModel;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.MyViewHolder> {
    private ArrayList<ResultModel> mDataset;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final ImageView image;
        private final TextView score;

        public MyViewHolder(final View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image_serveur);
            score = itemView.findViewById(R.id.score);
        }
    }

    public ResultAdapter(ArrayList<ResultModel> mDataset, Context context) {
        this.mDataset = mDataset;
        this.context = context;
    }



    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.recycler_resultat, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {
        //ResultatActivity resultImageServeur = new ResultatActivity();
        ResultModel result = mDataset.get(position);
        //myViewHolder.image.setImageBitmap(resultImageServeur.returnBitmap(result.getUrl(), context));
        myViewHolder.score.setText(String.valueOf(result.getScore()) + "%");
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
