package imt.fr.frontimagemobile.adapter;

import android.app.AlertDialog;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.StringRequest;

import java.util.ArrayList;

import javax.xml.transform.Result;

import imt.fr.frontimagemobile.R;
import imt.fr.frontimagemobile.models.ResultModel;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.MyViewHolder> {
    private ArrayList<ResultModel> mDataset;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final TextView text1;
        private final TextView text2;

        public MyViewHolder(final View itemView) {
            super(itemView);

            text1 = itemView.findViewById(R.id.text);
            text2 = itemView.findViewById(R.id.text2);
        }
    }

    public ResultAdapter(ArrayList<ResultModel> mDataset) {
        this.mDataset = mDataset;
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
        myViewHolder.text1.setText(result.getUrl());
        myViewHolder.text2.setText(String.valueOf(result.getScore()));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
