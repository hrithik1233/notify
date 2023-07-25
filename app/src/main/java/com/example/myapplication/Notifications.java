package com.example.myapplication;

import static com.example.myapplication.SignUpActivity.APP_LOGIN;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;

import android.app.ProgressDialog;



import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import android.provider.ContactsContract;
import android.util.Log;

import android.view.DragEvent;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class Notifications extends AppCompatActivity implements NotificationInterface {
    ArrayList<MessagesReceiver> recievedMessage = new ArrayList<>();
    String databaseMain;
    NotificationRecyclerAdapter adapter;
    MaterialButton historyView;
    ImageView backFinishBtn;

    @SuppressLint({"MissingInflatedId", "NotifyDataSetChanged"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        try {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        } catch (Exception ignored) {}
        historyView = findViewById(R.id.notifiaction_history);
        databaseMain = getIntent().getStringExtra("databaseMain");
        RecyclerView recyclerView;


        backFinishBtn = findViewById(R.id.backclickNotificationActivity);
        //finishActivity
        backFinishBtn.setOnClickListener(view -> finish());

        adapter = new NotificationRecyclerAdapter(this, recievedMessage);
        recyclerView = findViewById(R.id.NotifiactionrecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.show();
        historyView.setOnClickListener(view -> historyViewClick());
        if (NetworkUtils.isInternetIsConnected(this)) {
            SharedPreferences preferences = getSharedPreferences(APP_LOGIN, MODE_PRIVATE);
            String databaseMain = preferences.getString(SignUpActivity.MAIN_DATABASE_NAME, "default");

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference(databaseMain);
            ref.child("messageRecieved").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    recievedMessage.clear();
                    for (DataSnapshot data : snapshot.getChildren()) {

                        MessagesReceiver messagesReceiver = data.getValue(MessagesReceiver.class);
                        if (messagesReceiver != null) {
                            recievedMessage.add(messagesReceiver);
                        }
                    }
                    runOnUiThread(dialog::dismiss);
                    adapter.notifyDataSetChanged();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    runOnUiThread(dialog::dismiss);
                    Toast.makeText(Notifications.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }

            });
        }

        recyclerView.setAdapter(adapter);


    }

    private void historyViewClick() {
        Dialog history = new Dialog(this);
        history.setContentView(R.layout.notifcation_history_dialog);
        history.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        history.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TableLayout tb=history.findViewById(R.id._notification_history_table);
        tb.setPadding(10,10,10,10);
        FirebaseDatabase.getInstance().getReference(databaseMain).
                child("MessageHistory").addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot data:snapshot.getChildren()){
                            MessagesReceiver ms=data.getValue(MessagesReceiver.class);

                                TableRow row=new TableRow(Notifications.this);
                                row.setBackground(getDrawable(R.drawable.table_border));
                                row.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
                                TextView txt=new TextView(Notifications.this);
                                txt.setText("Requester Name: "+ms.getRequesterName()+"\n"+
                                        "Requester Email: "+ms.getRequesterEmail()+"\n"+
                                        "Requested Batch: "+ms.getReqeustedBatchOFowner()+"\n"+
                                                "Message ID: "+ms.getMessageId());
                                txt.setPadding(5,5,5,5);
                                txt.setTextColor(Color.BLACK);
                                TextView txt2=new TextView(Notifications.this);
                                txt2.setPadding(5,5,5,5);
                                txt2.setTextColor(Color.BLACK);
                                if(Objects.equals(ms.requestResult, "Accepted")){
                                    txt2.setTextColor(Color.parseColor("#02c71d"));
                                }else if(Objects.equals(ms.requestResult, "Denied")){
                                    txt2.setTextColor(Color.RED);
                                }else{
                                    txt2.setTextColor(Color.BLUE);
                                }
                                txt2.setText(ms.requestResult);
                                 txt2.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                                 txt.setPadding(5,5,5,5);
                                row.addView(txt);
                                row.addView(txt2);

                                row.setOnLongClickListener(view -> {

                                    Toast.makeText(Notifications.this, "fhfgh", Toast.LENGTH_SHORT).show();
                                    return true;

                                });
                                row.setPadding(10,10,10,10);
                                tb.addView(row);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        history.show();
    }


    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onDenieRequest(int pos) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(databaseMain);
        recievedMessage.get(pos).setRequestResult("Denied");
        ref.child("MessageHistory").child(Long.toString(recievedMessage.get(pos).getMessageId())).setValue(recievedMessage.get(pos));
        ref.child("messageRecieved").child(Long.toString(recievedMessage.get(pos).getMessageId())).removeValue();
        recievedMessage.remove(pos);
        adapter.notifyDataSetChanged();

    }

    @Override
    public void onAcceptRequest(int pos) {

        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.CustomAlertDialogTheme));
        builder.setMessage("Accept the reqeust!").setTitle("Send Data").setNegativeButton("Yes", (dialogInterface, i) -> {


            ProgressDialog dialog = new ProgressDialog(Notifications.this);
            dialog.show();
            ArrayList<StudentData> getData = new ArrayList<>();
            //***************************************************
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference(databaseMain);
            recievedMessage.get(pos).setRequestResult("Accepted");
            ref.child("MessageHistory").child(Long.toString(recievedMessage.get(pos)
                    .getMessageId())).setValue(recievedMessage.get(pos));
            //***************************************************
            DatabaseReference reqRef = FirebaseDatabase.getInstance()
                    .getReference(recievedMessage.get(pos).getidOfRequester())
                    .child(recievedMessage.get(pos).getRequesterbatch()).child("studentdata");

            //***************************************************


            DatabaseReference myRef = FirebaseDatabase.getInstance()
                    .getReference(databaseMain).child(recievedMessage.get(pos)
                            .getReqeustedBatchOFowner().toUpperCase()).child("studentdata");

            //**************************************
            // ** To get the last index of the requester id

            Query lafuery = reqRef.orderByKey();
            try {


                lafuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("RestrictedApi")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Log.i("test1", lafuery.getPath() + "");
                        for (DataSnapshot data : snapshot.getChildren()) {
                            StudentData sd = data.getValue(StudentData.class);

                            getData.add(sd);
                        }
                        Collections.reverse(getData);

                        if (getData.size() > 0) {
                            int LastIndex = getData.get(0).getId();
                            getData.clear();
                            // ** TO FETCH THE DATA FROM OWNER
                            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @SuppressLint("NotifyDataSetChanged")
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    int lst = LastIndex;
                                    lst++;
                                    for (DataSnapshot data : snapshot.getChildren()) {
                                        StudentData sd = data.getValue(StudentData.class);
                                        if (sd != null) {
                                            sd.setId(lst);
                                            lst++;
                                            getData.add(sd);
                                        }

                                    }
                                    // ** ADD THE DATA INTO REQUESTER BATCH
                                    for (StudentData data : getData) {
                                        reqRef.child(data.getId() + "").setValue(data);
                                    }
                                    dialog.dismiss();
                                    recievedMessage.remove(pos);
                                    adapter.notifyDataSetChanged();

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        runOnUiThread(() -> runOnUiThread(dialog::dismiss));
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> runOnUiThread(dialog::dismiss));
                Log.i("test1", e.getMessage());
            }

//***************************************************
            DatabaseReference reff = FirebaseDatabase.getInstance().getReference(databaseMain);
            reff.child("messageRecieved")
                    .child(Long.toString(recievedMessage.get(pos).getMessageId())).removeValue();
            dialogInterface.dismiss();

        }).setPositiveButton("No", null).show();


    }
}