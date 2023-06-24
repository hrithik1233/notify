package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class StudentDataAdapter extends RecyclerView.Adapter<StudentDataAdapter.MyViewHolder> {
    Context context;
    int count=0;
    boolean isSelectMode=false;
    ArrayList<StudentData> arrayList;
    ArrayList<StudentData> selectedList;
    StudentResponseInterface responseInterface;


    public StudentDataAdapter(Context context, ArrayList<StudentData> arrayList, StudentResponseInterface responseInterface) {
        this.context = context;
        this.arrayList = arrayList;
        this.responseInterface = responseInterface;
        selectedList=new ArrayList<>();
    }

    @NonNull
    @Override

    public StudentDataAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.studentdatalayout,parent,false);
        return new StudentDataAdapter.MyViewHolder(view,responseInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentDataAdapter.MyViewHolder holder, int pos) {

   holder.lateComesText.setText(Integer.toString(arrayList.get(pos).getNumber_of_late_comes()));
   holder.nameText.setText(arrayList.get(pos).getStdnt_name());
   holder.idtext.setText(Integer.toString(arrayList.get(pos).getId()));
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        RelativeLayout relativeLayout;

        ImageView edit,delete;
        CardView layout;
        TextView nameText,lateComesText,idtext;


        public MyViewHolder(@NonNull View itemView,StudentResponseInterface responseInterface1) {
            super(itemView);
            layout=itemView.findViewById(R.id.maincard);
            relativeLayout=itemView.findViewById(R.id.Colourpanel);
            edit=itemView.findViewById(R.id.editstudetndata);
            delete=itemView.findViewById(R.id.deletestudentdata);
            nameText=itemView.findViewById(R.id.student_name);
            lateComesText=itemView.findViewById(R.id.student_late_comes);
            idtext=itemView.findViewById(R.id.studentId);


            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(responseInterface1!=null){
                        int pos=getBindingAdapterPosition();
                        if(pos!=RecyclerView.NO_POSITION){
                            responseInterface1.onTouchDelete(pos);

                        }
                    }
                }
            });


            lateComesText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(responseInterface1!=null){
                        int pos=getBindingAdapterPosition();
                        if(pos!=RecyclerView.NO_POSITION){
                            Toast.makeText(context,"No of time inspected: " +arrayList.get(pos).getNumber_of_late_comes(), Toast.LENGTH_SHORT).show();

                        }
                    }
                }
            });
            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(responseInterface1!=null){
                        int pos=getBindingAdapterPosition();
                        if(pos!=RecyclerView.NO_POSITION){
                            responseInterface1.onTouchEdit(pos);

                        }
                    }
                }
            });
            layout.setOnLongClickListener(new View.OnLongClickListener() {
                @SuppressLint("ResourceAsColor")
                @Override
                public boolean onLongClick(View view) {
                    if(responseInterface1!=null){
                        int pos=getBindingAdapterPosition();
                        if(pos!=RecyclerView.NO_POSITION){
                            if(!isSelectMode &&selectedList.size()==0){
                                selectedList.add(arrayList.get(pos));
                                relativeLayout.setBackgroundResource(R.color.SELECTED);
                                arrayList.get(pos).setIsselected(true);
                                isSelectMode=true;
                                count++;
                            }

                            responseInterface1.onLongpress(pos,selectedList);

                        }
                    }

                    return true;
                }
            });
            layout.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("ResourceAsColor")
                @Override
                public void onClick(View view) {
                    if(responseInterface1!=null){
                        int pos=getBindingAdapterPosition();
                        if(pos!=RecyclerView.NO_POSITION){

                            if(isSelectMode){
                                if(arrayList.get(pos).getIsselected()){
                                    selectedList.remove(arrayList.get(pos));
                                    relativeLayout.setBackgroundResource(R.color.NOT_SELECTED_DEFAULT);
                                    arrayList.get(pos).setIsselected(false);
                                }else{
                                    selectedList.add(arrayList.get(pos));
                                    relativeLayout.setBackgroundResource(R.color.SELECTED);
                                    arrayList.get(pos).setIsselected(true);
                                }

                            }
                            if(selectedList.size()==0){
                                isSelectMode=false;
                            }

                            responseInterface1.onClick(pos,selectedList);
                        }
                    }
                }
            });

        }

    }
    public  void filter(ArrayList<StudentData> dt){
        arrayList=dt;

        notifyDataSetChanged();
    }
    public void setSelctionMode(boolean selection){
        isSelectMode=selection;
        if(!selection){
            selectedList.clear();
        }

    }



}
