package com.example.myapplication;

import static com.example.myapplication.SignUpActivity.APP_LOGIN;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.content.res.ResourcesCompat;
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
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
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

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Notifications extends AppCompatActivity implements NotificationInterface {
    ArrayList<MessagesReceiver> recievedMessage = new ArrayList<>();
    String databaseMain;
    NotificationRecyclerAdapter adapter;
    boolean isOnline;
    MaterialButton historyView;
    ImageView backFinishBtn;
    SharedPreferences meCheck;

    @SuppressLint({"MissingInflatedId", "NotifyDataSetChanged"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ** FOR CHECKING IF THE USER IS SET ONLINE OR OFFLINE MODE
        SharedPreferences settings = getSharedPreferences("Setting", MODE_PRIVATE);
        isOnline=!settings.getBoolean("isOfflineMode",false);
        //****************************************************************
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_notifications);
        meCheck = getSharedPreferences("Messagecount", MODE_PRIVATE);

        try {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        } catch (Exception ignored) {
        }
        historyView = findViewById(R.id.notifiaction_history);
        databaseMain = getIntent().getStringExtra("databaseMain");
        RecyclerView recyclerView;


        backFinishBtn = findViewById(R.id.backclickNotificationActivity);
        //finishActivity
        backFinishBtn.setOnClickListener(view -> finish());

        adapter = new NotificationRecyclerAdapter(this, recievedMessage);
        recyclerView = findViewById(R.id.NotifiactionrecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        if(!isOnline){
            Toast.makeText(this, "offline mode", Toast.LENGTH_SHORT).show();
            return;
        }
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
        TableLayout tb = history.findViewById(R.id._notification_history_table);
        tb.setPadding(10, 10, 10, 10);

        Query qry = FirebaseDatabase.getInstance().getReference(databaseMain).
                child("MessageHistory").orderByChild("resposeTime");
        qry.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Typeface customTypeface = ResourcesCompat.getFont(getApplicationContext(), R.font.allerta);
                if (snapshot.getChildrenCount() > 0) {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        MessagesReceiver ms = data.getValue(MessagesReceiver.class);

                        TableRow row = new TableRow(Notifications.this);

                        row.setBackground(getDrawable(R.drawable.table_border));
                        row.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        TextView txt = new TextView(Notifications.this);

                        TableLayout.LayoutParams tableRowParams = new TableLayout.LayoutParams(
                                TableLayout.LayoutParams.MATCH_PARENT / 2,
                                TableLayout.LayoutParams.WRAP_CONTENT
                        );

                        // Set margins for the TableRow (left, top, right, bottom)
                        tableRowParams.setMargins(0, 20, 0, 20);
                        row.setLayoutParams(tableRowParams);
                        row.setGravity(Gravity.CENTER);

                        txt.setTypeface(customTypeface);


                        txt.setText("Requester Name: " + ms.getRequesterName() + "\n" +
                                "Requester Email: " + ms.getRequesterEmail() + "\n" +
                                "Requested Batch: " + ms.getReqeustedBatchOFowner() + "\n" +
                                "Received : " + ms.dateAndTime.date + " " + ms.dateAndTime.time + "\n" + "Message ID: " + ms.getMessageId());
                        txt.setPadding(5, 5, 5, 5);
                        txt.setTextColor(Color.BLACK);
                        TextView txt2 = new TextView(Notifications.this);

                        txt2.setTextColor(Color.BLACK);
                        if (Objects.equals(ms.requestResult, "Accepted")) {
                            txt2.setTextColor(Color.parseColor("#02c71d"));
                        } else if (Objects.equals(ms.requestResult, "Denied")) {
                            txt2.setTextColor(Color.RED);
                        } else {
                            txt2.setTextColor(Color.BLUE);
                        }
                        txt2.setText(ms.requestResult);
                        txt2.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                        txt.setPadding(5, 5, 5, 5);
                        row.addView(txt);
                        row.addView(txt2);

                        row.setOnLongClickListener(view -> {
                            String val = txt.getText().toString();
                            StringBuilder id = new StringBuilder();
                            int len = val.length() - 1;
                            while (val.charAt(len) != ':' && len >= 0) {
                                if (val.charAt(len) != ' ') {
                                    id.insert(0, val.charAt(len));
                                }
                                len--;
                            }
                            val = String.valueOf(id);
                            FirebaseDatabase.getInstance().getReference(databaseMain).child("MessageHistory").child(val).removeValue();
                            for (MessagesReceiver ns : recievedMessage) {
                                if (ns.getMessageId() == Long.parseLong(val)) {
                                    recievedMessage.remove(ns);
                                }
                            }
                            tb.removeView(view);
                            Toast.makeText(Notifications.this, "item removed", Toast.LENGTH_SHORT).show();
                            return true;

                        });
                        row.setPadding(10, 10, 10, 10);
                        tb.addView(row);
                    }
                } else {
                    TextView txt2 = new TextView(Notifications.this);

                    txt2.setTextColor(Color.BLACK);
                    txt2.setTypeface(customTypeface);
                    txt2.setGravity(Gravity.CENTER);
                    txt2.setText("No history");
                    tb.addView(txt2);

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
        recievedMessage.get(pos).resposeTime = DateAndTime.currentDateAndTime();
        ref.child("MessageHistory").child(Long.toString(recievedMessage.get(pos).getMessageId())).setValue(recievedMessage.get(pos));
        ref.child("messageRecieved").child(Long.toString(recievedMessage.get(pos).getMessageId())).removeValue();
        recievedMessage.remove(pos);
        adapter.notifyDataSetChanged();

    }

    @Override
    public void onAcceptRequest(int pos) {



        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.CustomAlertDialogTheme));
        builder.setMessage("Accept the reqeust!").setTitle("Send Data").setNegativeButton("Yes", (dialogInterface, i) -> {

            recievedMessage.get(pos).resposeTime = DateAndTime.currentDateAndTime();
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


                        int LastIndex;
                        if (getData.size() > 0) {
                            LastIndex = getData.get(0).getId();
                        } else {
                            LastIndex = 0;
                        }
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
                                sendNotifiaction(recievedMessage.get(pos).getRequesterToken(), recievedMessage.get(pos));
                                dialog.dismiss();
                                recievedMessage.remove(pos);
                                adapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences.Editor editor = meCheck.edit();
        editor.putInt("count", recievedMessage.size());
        editor.apply();
    }

    private void sendNotifiaction(String token, MessagesReceiver mr) {
        try {

            JSONObject object = new JSONObject();
            JSONObject notification = new JSONObject();
            notification.put("title", "Access has been accepted");
            notification.put("body", mr.getOwnerName() + " has accepted for batch access " + mr.getReqeustedBatchOFowner());

            JSONObject dataObj = new JSONObject();

            object.put("data", dataObj);
            object.put("notification", notification);
            object.put("to", token);


            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            OkHttpClient client = new OkHttpClient();
            String url = "https://fcm.googleapis.com/fcm/send";
            RequestBody reqestBody = RequestBody.create(object.toString(), JSON);
            Request request = new Request.Builder()
                    .url(url)
                    .post(reqestBody)
                    .addHeader("Authorization", "key=AAAAkEPqclM:APA91bF0Xmf1i-ZSE1K0tcBIb47cWRDSngDS_WZdP9l3SQ4rRIWfR1uAa6M0iQsDlfhyaht1ollp40wpieu_OWGSSgNS40VoqISEb9dGRTbAQR3lcOhUJNjbWX51wJD3C3yg50FAnpmo")
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {

                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {

                }
            });
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}