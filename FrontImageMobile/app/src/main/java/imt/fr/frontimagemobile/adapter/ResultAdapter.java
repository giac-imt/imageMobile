package imt.fr.frontimagemobile.adapter;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import imt.fr.frontimagemobile.R;
import imt.fr.frontimagemobile.models.ResultModel;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.MyViewHolder> {
    private ArrayList<ResultModel> mDataset;
    private ArrayList<Bitmap> bitmaps;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final ImageView image;
        private final TextView score;

        public MyViewHolder(final View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image_serveur);
            score = itemView.findViewById(R.id.score);
        }
    }

    public ResultAdapter(ArrayList<ResultModel> mDataset, ArrayList<Bitmap> bitmaps) {
        this.mDataset = mDataset;
        this.bitmaps = bitmaps;
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
        ResultModel result = mDataset.get(position);
        myViewHolder.image.setImageBitmap(bitmaps.get(position));
        myViewHolder.score.setText(String.valueOf(result.getScore()) + "%");
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
