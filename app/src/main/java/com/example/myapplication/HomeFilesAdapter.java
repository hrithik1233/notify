package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class HomeFilesAdapter extends RecyclerView.Adapter<HomeFilesAdapter.MyViewHolder> {
    Context context;
    ArrayList<Homefiles> arrayList;
    private final RecyclerBatchInterface recyclerBatchInterface;

    public HomeFilesAdapter(Context context, ArrayList<Homefiles> arrayList, Home recyclerBatchInterface){
        this.context=context;
        this.arrayList=arrayList;
        this.recyclerBatchInterface = recyclerBatchInterface;
    }
    @Override
    public HomeFilesAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater= LayoutInflater.from(context);
        View view=layoutInflater.inflate(R.layout.file_instance,parent,false);

        return new HomeFilesAdapter.MyViewHolder(view,recyclerBatchInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeFilesAdapter.MyViewHolder holder, int position) {
       holder.batch.setText(arrayList.get(position).getBatch());
       holder.year.setText(arrayList.get(position).getYear());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
    public static class MyViewHolder extends  RecyclerView.ViewHolder{
            TextView batch,year;
        public MyViewHolder(@NonNull View itemView, RecyclerBatchInterface recyclerBatchInterface) {
            super(itemView);
            batch=itemView.findViewById(R.id.batch_name);
            year=itemView.findViewById(R.id.batch_year);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(recyclerBatchInterface != null){
                        int pos=getAdapterPosition();
                        if(pos != RecyclerView.NO_POSITION){
                            recyclerBatchInterface.itemOnClick(pos);
                        }
                    }
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if(recyclerBatchInterface != null){
                        int pos=getAdapterPosition();
                        if(pos != RecyclerView.NO_POSITION){
                            recyclerBatchInterface.itemLongPress(pos);
                        }
                    }
                    return true;
                }
            });
        }

    }
}
