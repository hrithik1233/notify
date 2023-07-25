package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class NotificationRecyclerAdapter extends RecyclerView.Adapter<NotificationRecyclerAdapter.MyView> {
    ArrayList<MessagesReceiver> rm;
    NotificationInterface notificationInterface;
    Context context;
    NotificationRecyclerAdapter(Context context, ArrayList<MessagesReceiver> recievedMessage ){
        this.context=context;
        this.rm=recievedMessage;
        this.notificationInterface= (NotificationInterface) context;

    }
    @NonNull
    @Override
    public MyView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(context).inflate(R.layout.notification_row_item,parent,false);
        return new MyView(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyView holder, int pos ) {

      holder.batch.setText(rm.get(pos ).getReqeustedBatchOFowner());
      holder.batchNameTitle.setText(rm.get(pos ).getReqeustedBatchOFowner());
      holder.dateAndTime.setText(rm.get(pos).getDateAndTime().toString());
      holder.email.setText(rm.get(pos).getRequesterEmail());
      holder.name.setText(rm.get(pos).getRequesterName());
      holder.messageID.setText(rm.get(pos).getMessageId()+"");
      holder.denie.setOnClickListener(view -> {
          if(pos!=RecyclerView.NO_POSITION){
              notificationInterface.onDenieRequest(pos);
          }

      });
      holder.approve.setOnClickListener(view -> {
          if(pos!=RecyclerView.NO_POSITION){
              notificationInterface.onAcceptRequest(pos);
          }

      });
    }

    @Override
    public int getItemCount() {
        return rm.size();
    }

    public class MyView extends RecyclerView.ViewHolder {
        TextView batchNameTitle,name,email,batch,dateAndTime,messageID;
        MaterialButton denie ,approve;
        public MyView(@NonNull View itemView) {
            super(itemView);
            batchNameTitle=itemView.findViewById(R.id.notification_batch_title);
            batch=itemView.findViewById(R.id.requested_batch_notification);
            dateAndTime=itemView.findViewById(R.id.dateandtime_notification);
            name=itemView.findViewById(R.id.requester_name_notification);
            email=itemView.findViewById(R.id.requester_email_notification);
            denie=itemView.findViewById(R.id.denie_notfier);
            approve=itemView.findViewById(R.id.approve_notfier);
            messageID=itemView.findViewById(R.id.requested_ID_notification);

        }
    }
}
